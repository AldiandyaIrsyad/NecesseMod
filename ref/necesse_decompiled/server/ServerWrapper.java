package necesse.server;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.GameAuth;
import necesse.engine.GameInfo;
import necesse.engine.GameLaunch;
import necesse.engine.GameLog;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.network.server.network.ServerOpenNetwork;
import necesse.engine.save.WorldSave;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;

public class ServerWrapper {
   private boolean clearConsole = true;
   private ServerJFrame serverForm;
   private ServerScanThread scanThread = null;
   private ServerSettings serverSettings;
   private Server server;
   private ArrayList<InputRequest> inputRequests;

   public ServerWrapper(String[] var1) {
      HashMap var2 = GameLaunch.parseLaunchOptions(var1);
      if (var2.containsKey("nogui")) {
         this.serverForm = null;
      } else {
         this.serverForm = new ServerJFrame(this);
      }

      GameLog.setServerWrite(this.serverForm);
      if (var2.containsKey("help")) {
         System.out.println("Available parameters:");
         System.out.println("-help - Shows this help menu");
         System.out.println("-nogui - Runs the server in terminal instead of opening the GUI");
         System.out.println("-settings <file> - Settings file path to load server settings from");
         System.out.println("Parameters not given will be loaded from server settings file");
         System.out.println("-world <name> - World to load instead of being asked which to load");
         System.out.println("-port <port> - Port to host at");
         System.out.println("-slots <slots> - Amount of player slots");
         System.out.println("-owner <name> - Anyone that connects with this name, will get owner permissions");
         System.out.println("-motd <message> - Sets the message of the day. Use \\n for new line");
         System.out.println("-password <password> - The password for the server, blank for no password");
         System.out.println("-pausewhenempty <1/0> - Pauses the world when there are no players in server, defaults 0");
         System.out.println("-giveclientspower <1/0> - If the server should check client actions, a kind of anti-cheat. When off it will give a much smoother experience for clients. Defaults off.");
         System.out.println("-logging <1/0> - If on the server will generate a log file for each session, defaults 1");
         System.out.println("-zipsaves <1/0> - If saves should be compressed, defaults to 1");
         System.out.println("-language <language> - Sets the language of the server, only used for occasional messages in log");
         System.out.println("-ip <address> - Binds the server IP to the address");
         if (this.serverForm == null) {
            (new Thread(() -> {
               try {
                  System.in.read();
               } catch (IOException var1) {
               }

            })).start();
            System.out.println();
            System.out.println("You can now exit");
         }

      } else {
         if (var2.containsKey("settings")) {
            if (Settings.loadServerSettings(new File((String)var2.get("settings")), false)) {
               System.out.println("Loaded settings file at " + (String)var2.get("settings"));
            } else {
               GameLog.warn.println("Could not load or find settings file at " + (String)var2.get("settings"));
            }
         } else {
            Settings.loadServerSettings();
         }

         String var3 = (String)var2.getOrDefault("world", Settings.serverWorld);
         if (!var3.isEmpty()) {
            this.clearConsole = false;
         }

         this.handleLaunchArgs(var2);
         String var4;
         if (Settings.serverLogging) {
            var4 = "logs/" + (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date()) + ".txt";
            GameLog.addLoggingPath(var4);
         }

         System.out.println("Loading dedicated server on version " + GameInfo.getFullVersionString() + ".");
         if (this.serverForm == null) {
            this.scanThread = new ServerScanThread(this);
            this.scanThread.start();
         }

         try {
            GlobalData.loadAll(true);
         } catch (ModLoadException var7) {
            System.err.println("The mod " + var7.mod.getModDebugString() + " causes an error on startup.");
            System.err.println("You can try to contact the author with the below error message, or remove the mod.");
            var7.printStackTrace();
            if (this.serverForm == null) {
               (new Thread(() -> {
                  try {
                     System.in.read();
                  } catch (IOException var1) {
                  }

               })).start();
               System.out.println();
               System.out.println("You can now exit");
            }

            return;
         }

         GameAuth.loadAuth();
         var4 = (String)var2.getOrDefault("owner", (Object)null);
         if (var4 != null) {
            try {
               Settings.serverOwnerAuth = Long.parseLong(var4);
            } catch (NumberFormatException var6) {
               Settings.serverOwnerAuth = -1L;
            }

            Settings.serverOwnerName = var4;
            System.out.println("Any client connecting with name \"" + var4 + "\" will automatically get owner permissions");
         }

         if (!var3.isEmpty()) {
            File var5 = World.worldExistsWithName(var3);
            if (var5 != null) {
               System.out.println("Loading existing world at " + var5.getPath());
            } else {
               if (!var3.endsWith(".zip") && Settings.zipSaves) {
                  var3 = var3 + ".zip";
               }

               var5 = new File(World.getWorldsPath() + var3);
               System.out.println("Creating new world at " + var5.getPath());
            }

            this.serverSettings = ServerSettings.HostServer(var5, Settings.serverSlots, Settings.serverPort);
            this.serverSettings.password = Settings.serverPassword;
            this.startServer();
         } else {
            this.inputRequests = new ArrayList();
            final ArrayList var8 = new ArrayList();
            Objects.requireNonNull(var8);
            WorldSave.loadSaves(var8::add, (Supplier)null, (Consumer)null, 5);
            this.inputRequests.add(new InputRequest() {
               public void init() {
                  if (var8.isEmpty()) {
                     System.out.println("Type a name for the world to create");
                  } else {
                     System.out.println("Select a world, or type a new name to create one");

                     for(int var1 = 0; var1 < var8.size(); ++var1) {
                        System.out.println("  " + (var1 + 1) + ". " + ((WorldSave)var8.get(var1)).displayName);
                     }
                  }

               }

               public boolean submitInput(String var1) {
                  try {
                     File var2 = World.getExistingWorldFilePath(var1);

                     try {
                        int var3 = Integer.parseInt(var1);
                        if (var3 >= 1 && var3 <= var8.size()) {
                           var2 = ((WorldSave)var8.get(var3 - 1)).filePath;
                        }
                     } catch (NumberFormatException var4) {
                     }

                     if (var2 == null) {
                        var2 = new File(World.getWorldsPath() + var1 + (Settings.zipSaves ? ".zip" : ""));
                     }

                     if (World.worldExists(var2)) {
                        System.out.println("Selected save: " + var2);
                     } else {
                        System.out.println("Creating new save: " + var2);
                     }

                     ServerWrapper.this.serverSettings = ServerSettings.HostServer(var2, Settings.serverSlots, Settings.serverPort);
                     ServerWrapper.this.serverSettings.password = Settings.serverPassword;
                     return true;
                  } catch (IllegalArgumentException var5) {
                     System.out.println(var5.getMessage());
                     return false;
                  }
               }
            });
            this.inputRequests.add(new InputRequest() {
               public void init() {
                  System.out.println("Custom server options? (y/n)");
               }

               public boolean submitInput(String var1) {
                  if (var1.toLowerCase().startsWith("y")) {
                     ServerWrapper.this.inputRequests.add(new InputRequest() {
                        public void init() {
                           System.out.println("Please specify host port");
                        }

                        public boolean submitInput(String var1) {
                           try {
                              int var2 = Integer.parseInt(var1);
                              if (var2 >= 0 && var2 <= 65535) {
                                 ServerWrapper.this.serverSettings = ServerSettings.HostServer(ServerWrapper.this.serverSettings.worldFilePath, ServerWrapper.this.serverSettings.slots, var2);
                                 Settings.serverPort = var2;
                                 return true;
                              }

                              System.out.println("Port must be between 0 and 65535");
                              return false;
                           } catch (NumberFormatException var3) {
                              System.out.println(var1 + " is not number");
                           } catch (IllegalArgumentException var4) {
                              System.out.println(var4.getMessage());
                           }

                           return false;
                        }
                     });
                     ServerWrapper.this.inputRequests.add(new InputRequest() {
                        public void init() {
                           System.out.println("Please specify player slots (1 - 250)");
                        }

                        public boolean submitInput(String var1) {
                           try {
                              int var2 = Integer.parseInt(var1);
                              ServerWrapper.this.serverSettings = ServerSettings.HostServer(ServerWrapper.this.serverSettings.worldFilePath, var2, ServerWrapper.this.serverSettings.port);
                              Settings.serverSlots = var2;
                              return true;
                           } catch (NumberFormatException var3) {
                              System.out.println(var1 + " is not number");
                           } catch (IllegalArgumentException var4) {
                              System.out.println(var4.getMessage());
                           }

                           return false;
                        }
                     });
                     ServerWrapper.this.inputRequests.add(new InputRequest() {
                        public void init() {
                           System.out.println("Please specify server password (blank for none)");
                        }

                        public boolean submitInput(String var1) {
                           ServerWrapper.this.serverSettings.password = var1;
                           Settings.serverPassword = var1;
                           if (var1.isEmpty()) {
                              System.out.println("> No password selected");
                           }

                           return true;
                        }
                     });
                     if (!World.worldExists(ServerWrapper.this.serverSettings.worldFilePath)) {
                        ServerWrapper.this.inputRequests.add(new InputRequest() {
                           public void init() {
                              System.out.println("Please specify custom spawn island (blank for random, format: <x>,<y>)");
                           }

                           public boolean submitInput(String var1) {
                              if (var1.trim().isEmpty()) {
                                 System.out.println("> Random spawn island selected");
                                 ServerWrapper.this.serverSettings.spawnIsland = null;
                                 ServerWrapper.this.inputRequests.add(ServerWrapper.this.inputRequests.indexOf(this) + 1, new InputRequest() {
                                    public void init() {
                                       System.out.println("Please specify spawn seed (blank for random)");
                                    }

                                    public boolean submitInput(String var1) {
                                       if (var1.isEmpty()) {
                                          System.out.println("> Random spawn seed selected");
                                          ServerWrapper.this.serverSettings.spawnSeed = GameRandom.globalRandom.nextInt();
                                          return true;
                                       } else if (var1.length() > 10) {
                                          System.out.println("Seed too long (max 10 characters)");
                                          return false;
                                       } else {
                                          ServerWrapper.this.serverSettings.spawnSeed = var1.hashCode();
                                          return true;
                                       }
                                    }
                                 });
                                 return true;
                              } else {
                                 String[] var2 = var1.split(",");

                                 try {
                                    int var3 = Integer.parseInt(var2[0].trim());
                                    int var4 = Integer.parseInt(var2[1].trim());
                                    ServerWrapper.this.serverSettings.spawnIsland = new Point(var3, var4);
                                    return true;
                                 } catch (Exception var5) {
                                    System.out.println(var1 + " is not a valid spawn point");
                                    return false;
                                 }
                              }
                           }
                        });
                        ServerWrapper.this.inputRequests.add(new InputRequest() {
                           public void init() {
                              System.out.println("Spawn guide house? (y/n)");
                           }

                           public boolean submitInput(String var1) {
                              if (var1.toLowerCase().startsWith("y")) {
                                 ServerWrapper.this.serverSettings.spawnGuide = true;
                                 return true;
                              } else if (var1.toLowerCase().startsWith("n")) {
                                 ServerWrapper.this.serverSettings.spawnGuide = false;
                                 return true;
                              } else {
                                 return false;
                              }
                           }
                        });
                     }

                     return true;
                  } else {
                     return var1.toLowerCase().startsWith("n");
                  }
               }
            });
            ((InputRequest)this.inputRequests.get(0)).init();

            while(this.inputRequests.size() != 0) {
               threadSleep(100);
            }

            Settings.saveServerSettings();
            this.startServer();
         }

      }
   }

   private void handleLaunchArgs(HashMap<String, String> var1) {
      int var2;
      if (var1.containsKey("port")) {
         try {
            var2 = Integer.parseInt((String)var1.get("port"));
            Settings.serverPort = GameMath.limit(var2, 0, 65535);
            if (var2 != Settings.serverPort) {
               System.out.println("Limited port to " + Settings.serverPort);
            }
         } catch (NumberFormatException var11) {
            GameLog.warn.println("Could not parse port parameter: " + (String)var1.get("port"));
         }
      }

      if (var1.containsKey("slots")) {
         try {
            var2 = Integer.parseInt((String)var1.get("slots"));
            Settings.serverSlots = GameMath.limit(var2, 1, 250);
            if (var2 != Settings.serverSlots) {
               System.out.println("Limited slots to " + Settings.serverSlots);
            }
         } catch (NumberFormatException var10) {
            GameLog.warn.println("Could not parse slots parameter: " + (String)var1.get("slots"));
         }
      }

      Settings.serverPassword = (String)var1.getOrDefault("password", Settings.serverPassword);
      Settings.serverMOTD = (String)var1.getOrDefault("motd", Settings.serverMOTD);
      Settings.serverMOTD = Settings.serverMOTD.replace("\\n", "\n");
      String var12;
      if (var1.containsKey("pausewhenempty")) {
         var12 = (String)var1.get("pausewhenempty");
         if (!var12.equalsIgnoreCase("true") && !var12.equalsIgnoreCase("1")) {
            if (!var12.equalsIgnoreCase("false") && !var12.equalsIgnoreCase("0")) {
               GameLog.warn.println("Could not parse pausewhenempty parameter: " + var12);
            } else {
               Settings.pauseWhenEmpty = false;
            }
         } else {
            Settings.pauseWhenEmpty = true;
         }
      }

      if (var1.containsKey("giveclientspower")) {
         var12 = (String)var1.get("giveclientspower");
         if (!var12.equalsIgnoreCase("true") && !var12.equalsIgnoreCase("1")) {
            if (!var12.equalsIgnoreCase("false") && !var12.equalsIgnoreCase("0")) {
               GameLog.warn.println("Could not parse giveclientspower parameter: " + var12);
            } else {
               Settings.giveClientsPower = false;
            }
         } else {
            Settings.giveClientsPower = true;
         }
      }

      if (var1.containsKey("logging")) {
         var12 = (String)var1.get("logging");
         if (!var12.equalsIgnoreCase("true") && !var12.equalsIgnoreCase("1")) {
            if (!var12.equalsIgnoreCase("false") && !var12.equalsIgnoreCase("0")) {
               GameLog.warn.println("Could not parse logging parameter: " + var12);
            } else {
               Settings.serverLogging = false;
            }
         } else {
            Settings.serverLogging = true;
         }
      }

      if (var1.containsKey("zipsaves")) {
         var12 = (String)var1.get("zipsaves");
         Settings.zipSaves = var12.equals("1") || var12.equals("true");
      }

      if (var1.containsKey("language")) {
         Settings.language = (String)var1.get("language");
         Language var13 = Localization.getLanguageStringID(Settings.language);
         if (var13 == null) {
            var13 = Localization.defaultLang;
            GameLog.warn.println("Could not find language " + Settings.language);
            Settings.language = Localization.defaultLang.stringID;
         }

         var13.setCurrent();
      }

      ServerOpenNetwork.bindIP = (String)var1.getOrDefault("ip", ServerOpenNetwork.bindIP);
      if (var1.containsKey("unloadlevels")) {
         try {
            var2 = Integer.parseInt((String)var1.get("unloadlevels"));
            Settings.unloadLevelsCooldown = Math.max(2, var2);
            if (var2 != Settings.unloadLevelsCooldown) {
               System.out.println("Limited unload levels cooldown to " + Settings.unloadLevelsCooldown + " seconds");
            }
         } catch (NumberFormatException var9) {
            GameLog.warn.println("Could not parse unloadlevels parameter: " + (String)var1.get("unloadlevels"));
         }
      }

      if (var1.containsKey("worldborder")) {
         try {
            var2 = Integer.parseInt((String)var1.get("worldborder"));
            Settings.worldBorderSize = Math.max(-1, var2);
         } catch (NumberFormatException var8) {
            GameLog.warn.println("Could not parse worldborder parameter: " + (String)var1.get("worldborder"));
         }
      }

      if (var1.containsKey("itemslife")) {
         try {
            var2 = Integer.parseInt((String)var1.get("itemslife"));
            Settings.droppedItemsLifeMinutes = Math.max(0, var2);
         } catch (NumberFormatException var7) {
            GameLog.warn.println("Could not parse itemslife parameter: " + (String)var1.get("itemslife"));
         }
      }

      if (var1.containsKey("unloadsettlements")) {
         var12 = (String)var1.get("unloadsettlements");
         Settings.unloadSettlements = var12.equals("1") || var12.equals("true");
      }

      if (var1.containsKey("maxsettlements")) {
         try {
            var2 = Integer.parseInt((String)var1.get("maxsettlements"));
            Settings.maxSettlementsPerPlayer = Math.max(-1, var2);
         } catch (NumberFormatException var6) {
            GameLog.warn.println("Could not parse maxsettlements parameter: " + (String)var1.get("maxsettlements"));
         }
      }

      if (var1.containsKey("maxsettlers")) {
         try {
            var2 = Integer.parseInt((String)var1.get("maxsettlers"));
            Settings.maxSettlersPerSettlement = Math.max(-1, var2);
         } catch (NumberFormatException var5) {
            GameLog.warn.println("Could not parse maxsettlers parameter: " + (String)var1.get("maxsettlers"));
         }
      }

      if (var1.containsKey("jobrange")) {
         try {
            var2 = Integer.parseInt((String)var1.get("jobrange"));
            int var3 = GameMath.limit(var2, 1, 300);
            if (Settings.jobSearchRange.maxRange != var2) {
               if (var2 != var3) {
                  System.out.println("Limited job search range to " + var3 + " tiles");
               }

               Settings.jobSearchRange = new GameTileRange(var3, new Point[0]);
            }
         } catch (NumberFormatException var4) {
            GameLog.warn.println("Could not parse jobrange parameter: " + (String)var1.get("jobrange"));
         }
      }

   }

   private void startServer() {
      if (this.serverForm != null && this.clearConsole) {
         this.serverForm.clearConsole();
      }

      try {
         this.server = GlobalData.startServer(this.serverSettings, (ServerHostSettings)null);
      } catch (FileSystemClosedException | IOException var2) {
         System.err.println("Error loading server world:");
         var2.printStackTrace();
         if (this.server != null) {
            this.server.stop();
         }

         System.out.println("Server has stopped.");
         return;
      }

      if (this.scanThread != null) {
         this.scanThread.setServer(this.server);
      }

      if (this.serverForm != null) {
         this.serverForm.setServer(this.server);
      }

      TickManager var1 = new TickManager("main", 2) {
         public void update() {
            if (ServerWrapper.this.server != null) {
               ServerWrapper.this.updateGUI(ServerWrapper.this.server, ServerWrapper.this.server.tickManager());
            }
         }
      };
      if (this.server != null) {
         this.updateGUI(this.server, this.server.tickManager());
      }

      var1.init();

      while(this.server != null && !this.server.hasClosed()) {
         var1.tickLogic();
      }

      System.out.println("Server has stopped");
      System.out.println("Exiting in 2 seconds...");
      threadSleep(2000);
      if (this.serverForm != null) {
         this.serverForm.dispose();
      }

      ModLoader.getAllMods().forEach(LoadedMod::dispose);
      if (this.scanThread != null) {
         System.exit(0);
      }

   }

   private static void threadSleep(int var0) {
      try {
         Thread.sleep((long)var0);
      } catch (InterruptedException var2) {
      }

   }

   public Server getServer() {
      return this.server;
   }

   public void submitCommand(Server var1, String var2) {
      if (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      if (!var2.equals("")) {
         System.out.println("> " + var2);
      }

      if (this.inputRequests != null && this.inputRequests.size() != 0) {
         if (((InputRequest)this.inputRequests.get(0)).submitInput(var2)) {
            this.inputRequests.remove(0);
            if (this.inputRequests.size() != 0) {
               ((InputRequest)this.inputRequests.get(0)).init();
            }
         }

      } else {
         if (!var2.equals("") && var1 != null) {
            var1.sendCommand(var2, (ServerClient)null);
         }

      }
   }

   public void updateGUI(Server var1, TickManager var2) {
      if (this.serverForm != null) {
         if (var2 != null && var1 != null) {
            ArrayList var3 = new ArrayList();
            var3.add("World: " + var1.world.filePath.getName());
            var3.add("Players: " + var1.getPlayersOnline() + "/" + var1.getSlots());
            var3.add("Loaded levels: " + var1.world.levelManager.getLoadedLevelsNum());
            var3.add("World time: " + var1.world.worldEntity.getDayTimeInt() + ", day " + var1.world.worldEntity.getDay());
            var3.add("Game time: " + var1.world.worldEntity.getTime() / 1000L);
            var3.add("");
            var3.add("TickTime: " + GameUtils.getTimeStringNano(var1.serverThread.getTickTimeAverage()));
            var3.add("TPS: " + var2.getTPS() + ", " + var2.getFPS());
            var3.add("TotalTicks: " + var2.getTotalTicks());
            var3.add("SkippedTicks: " + var2.getSkippedTicks());
            var3.add("");
            var3.add("Received: " + var1.packetManager.getAverageIn() + "/s (" + var1.packetManager.getAverageInPackets() + "), Total: " + var1.packetManager.getTotalIn() + " (" + var1.packetManager.getTotalInPackets() + ")");
            var3.add("Sent: " + var1.packetManager.getAverageOut() + "/s (" + var1.packetManager.getAverageOutPackets() + "), Total: " + var1.packetManager.getTotalOut() + " (" + var1.packetManager.getTotalOutPackets() + ")");
            var3.add("");
            long var4 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            float var6 = (float)var4 / (float)Runtime.getRuntime().totalMemory();
            var3.add("Memory: " + GameUtils.getByteString(var4) + " (" + (float)((int)(var6 * 10000.0F)) / 100.0F + " %)");
            var3.add("Max: " + GameUtils.getByteString(Runtime.getRuntime().maxMemory()));
            this.serverForm.setInfoLines((List)var3);
         }
      }
   }

   private abstract static class InputRequest {
      private InputRequest() {
      }

      public abstract void init();

      public abstract boolean submitInput(String var1);

      // $FF: synthetic method
      InputRequest(Object var1) {
         this();
      }
   }
}
