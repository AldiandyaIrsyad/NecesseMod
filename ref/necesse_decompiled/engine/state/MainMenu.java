package necesse.engine.state;

import com.codedisaster.steamworks.SteamID;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipError;
import necesse.engine.AbstractMusicList;
import necesse.engine.GameCache;
import necesse.engine.GameCrashLog;
import necesse.engine.GlobalData;
import necesse.engine.MusicList;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.camera.PanningCamera;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainMenuFormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.presets.ModSaveListMismatchForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;

public class MainMenu extends State {
   private Client client;
   private Level level;
   private WorldEntity worldEntity;
   public ConnectFrom connectedFrom;
   private PanningCamera camera;
   private InputEvent cameraInput;
   private MainMenuFormManager formManager;
   private NoticeForm waitingForServerShutdown;

   public MainMenu(String var1) {
      this((String)var1, (Client)null);
   }

   public MainMenu(String var1, Client var2) {
      this((GameMessage)(var1 == null ? null : new StaticMessage(var1)), var2);
   }

   public MainMenu(GameMessage var1) {
      this((GameMessage)var1, (Client)null);
   }

   public MainMenu(GameMessage var1, Client var2) {
      this.client = var2;
      this.connectedFrom = null;
      Settings.hideUI = false;
      this.init();
      if (var1 != null) {
         this.notice(var1);
      }

   }

   public MainMenu(ContinueComponent var1) {
      this((ContinueComponent)var1, (Client)null);
   }

   public MainMenu(ContinueComponent var1, Client var2) {
      this.client = var2;
      this.connectedFrom = null;
      Settings.hideUI = false;
      this.init();
      if (var1 != null) {
         this.continueForm(var1);
      }

   }

   public MainMenu(Client var1) {
      this.client = var1;
      this.init();
      this.changeLevel(var1);
   }

   public void init() {
      this.setupFormManager();
      SteamData.storeStatsAndAchievements();
      this.connectedFrom = null;
      this.worldEntity = WorldEntity.getDebugWorldEntity();
      ControllerInput.MENU_SET_LAYER.setActive(true);
      ControllerInput.setMoveAsMenuNavigation(true);
      FormManager.cleanUpLastControllerFocuses();
      this.updateSteamRichPresence();
      if (this.client != null && !this.client.loading.isDone()) {
         this.formManager.startConnection(this.client);
      }

      this.isInitialized = true;
   }

   public void frameTick(TickManager var1) {
      Client var2 = this.client;
      if (this.client != null) {
         if (!this.client.hasDisconnected()) {
            if (var1.isGameTick()) {
               Client var10002 = this.client;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "clientTick", (Runnable)(var10002::tick));
            }

            Performance.record(var1, "clientMove", (Runnable)(() -> {
               if (this.client != null) {
                  this.client.frameTick(var1);
               }

            }));
            if (this.isDisposed()) {
               return;
            }
         }

         if (this.client.isDisconnected() && this.client.getLocalServer() != null) {
            if (this.client.getLocalServer().serverThread.isAlive()) {
               if (this.waitingForServerShutdown == null) {
                  this.waitingForServerShutdown = new NoticeForm("waitserver");
                  this.waitingForServerShutdown.setupNotice((GameMessage)(new LocalMessage("ui", "waitserver")));
                  this.waitingForServerShutdown.setButtonCooldown(-2);
                  this.formManager.addContinueForm(this.waitingForServerShutdown);
               }
            } else {
               if (this.waitingForServerShutdown != null) {
                  this.waitingForServerShutdown.applyContinue();
                  this.waitingForServerShutdown = null;
               }

               if (this.client.hasDisconnected()) {
                  if (this.client.getLocalServer().stopErrors != null) {
                     Iterator var3 = this.client.getLocalServer().stopErrors.iterator();

                     while(var3.hasNext()) {
                        ContinueComponent var4 = (ContinueComponent)var3.next();
                        this.continueForm(var4);
                     }
                  }

                  this.client = null;
               }
            }
         } else if (this.client.hasDisconnected() && this.client.getLocalServer() == null) {
            this.client = null;
         }
      }

      if (this.level == null) {
         this.initLevel();
         this.initCamera();
      }

      InputEvent var9;
      InputEvent var10;
      if (GlobalData.isDevMode()) {
         var9 = Screen.input().getEvent(297);
         var10 = Screen.input().getEvent(298);
         if (var9 != null && var9.state || var10 != null && var10.state) {
            float[] var5 = new float[]{0.5F, 1.0F, 2.0F, 4.0F, 8.0F, 16.0F, 32.0F};
            int var6 = 0;

            for(int var7 = 0; var7 < var5.length; ++var7) {
               if (TickManager.globalTimeMod == var5[var7]) {
                  var6 = var7;
                  break;
               }
            }

            if (var9 != null) {
               TickManager.globalTimeMod = var5[Math.floorMod(var6 - 1, var5.length)];
            }

            if (var10 != null) {
               TickManager.globalTimeMod = var5[(var6 + 1) % var5.length];
            }

            TickManager.skipDrawIfBehind = TickManager.globalTimeMod > 1.0F;
            System.out.println("Time modifier: x" + TickManager.globalTimeMod);
         }
      }

      var9 = Screen.input().getEvent(-100);
      if (var9 != null) {
         if (var9.state) {
            if (!this.formManager.isMouseOver(var9)) {
               var9.use();
               this.cameraInput = var9;
            }
         } else if (this.cameraInput != null) {
            var9.use();
            this.cameraInput = null;
         }
      }

      if (this.cameraInput != null) {
         int var11 = Screen.input().mousePos().windowX - this.cameraInput.pos.windowX;
         int var12 = Screen.input().mousePos().windowY - this.cameraInput.pos.windowY;
         float var14 = (float)(new Point(var11, var12)).distance(0.0, 0.0) / 10.0F;
         this.camera.setDirection((float)var11, (float)var12);
         this.camera.setSpeed(var14);
      }

      try {
         Screen.input().getEvents().forEach((var2x) -> {
            this.formManager.submitInputEvent(var2x, var1, (PlayerMob)null);
         });
         ControllerInput.getEvents().forEach((var2x) -> {
            this.formManager.submitControllerEvent(var2x, var1, (PlayerMob)null);
         });
      } catch (ConcurrentModificationException var8) {
         System.err.println("ConcurrentModificationException likely caused by opening of url/file");
         var8.printStackTrace();
      }

      var10 = Screen.input().getEvent(256);
      ControllerEvent var13;
      if (var10 == null) {
         var13 = ControllerInput.getEvent(ControllerInput.MENU_BACK);
         if (var13 != null) {
            var10 = InputEvent.ControllerButtonEvent(var13, var1);
         }
      }

      if (var10 == null) {
         var13 = ControllerInput.getEvent(ControllerInput.MAIN_MENU);
         if (var13 != null) {
            var10 = InputEvent.ControllerButtonEvent(var13, var1);
         }
      }

      if (var10 != null && !var10.isUsed() && var10.state) {
         this.formManager.submitEscapeEvent(var10);
      }

      this.formManager.frameTick(var1);
      if (!this.isDisposed()) {
         if (Control.SCREENSHOT.isPressed()) {
            Screen.takeScreenshot(this.client == null ? null : this.client.chat);
         }

         this.worldEntity.serverFrameTick(var1);
         if (var1.isGameTick()) {
            this.worldEntity.serverTick();
            this.level.clientTick();
            this.level.serverTick();
            if (this.client == null) {
               Screen.setMusic((AbstractMusicList)(new MusicList(new GameMusic[]{MusicRegistry.AdventureBegins})), (Screen.MusicPriority)Screen.MusicPriority.BIOME);
            }
         }

         this.level.frameTick(var1);
         if (var1.isGameTick()) {
            Performance.recordConstant(var1, "levelEffectTick", (Runnable)(() -> {
               if (this.level != null) {
                  this.level.tickEffect(this.camera, (PlayerMob)null);
               }

            }));
         }

      }
   }

   public void drawScene(TickManager var1, boolean var2) {
      if (this.level == null) {
         this.initLevel();
         this.initCamera();
      }

      this.camera.updateToSceneDimensions();
      this.camera.tickMovement(var1);
      if (this.camera.getX() < 32 * this.level.width / 20) {
         this.camera.setPosition(32 * this.level.width / 20, this.camera.getY());
         if (this.camera.getXDir() < 0.0F) {
            this.camera.invertXDir();
         }
      } else if (this.camera.getX() > this.level.width * 32 - this.camera.getWidth() - 32 * this.level.width / 20) {
         this.camera.setPosition(this.level.width * 32 - this.camera.getWidth() - 32 * this.level.width / 20, this.camera.getY());
         if (this.camera.getXDir() > 0.0F) {
            this.camera.invertXDir();
         }
      }

      if (this.camera.getY() < 32 * this.level.height / 20) {
         this.camera.setPosition(this.camera.getX(), 32 * this.level.height / 20);
         if (this.camera.getYDir() < 0.0F) {
            this.camera.invertYDir();
         }
      } else if (this.camera.getY() > this.level.height * 32 - this.camera.getHeight() - 32 * this.level.height / 20) {
         this.camera.setPosition(this.camera.getX(), this.level.height * 32 - this.camera.getHeight() - 32 * this.level.height / 20);
         if (this.camera.getYDir() > 0.0F) {
            this.camera.invertYDir();
         }
      }

      this.level.draw(this.camera, (PlayerMob)null, var1, var2);
   }

   public void drawSceneOverlay(TickManager var1) {
      this.level.drawHud(this.camera, (PlayerMob)null, var1);
   }

   public void drawHud(TickManager var1) {
      this.formManager.draw(var1, (PlayerMob)null);
      Screen.addControllerGlyph(Localization.translate("controls", "navigatetip"), ControllerInput.MENU_UP, ControllerInput.MENU_RIGHT, ControllerInput.MENU_DOWN, ControllerInput.MENU_LEFT);
      Screen.addControllerGlyph(Localization.translate("ui", "backbutton"), ControllerInput.MENU_BACK);
   }

   public void onWindowResized() {
      if (this.formManager != null) {
         this.formManager.onWindowResized();
      }

   }

   public void setupFormManager() {
      this.formManager = new MainMenuFormManager(this);
      this.formManager.setup();
   }

   public FormManager getFormManager() {
      return this.formManager;
   }

   public void reloadInterfaceFromSettings(boolean var1) {
      this.formManager = new MainMenuFormManager(this);
      this.formManager.setup();
      this.formManager.mainForm.makeCurrent(this.formManager.mainForm.settings);
      if (var1) {
         this.formManager.mainForm.settings.makeInterfaceCurrent();
         this.formManager.mainForm.settings.setSaveActive(true);
         this.formManager.mainForm.settings.reloadedInterface = true;
      }

   }

   public void toggleCameraPanSetting() {
      this.initCamera();
   }

   public void updateSteamRichPresence() {
      if (this.client != null) {
         this.client.updateSteamRichPresence();
      } else {
         SteamData.setRichPresence("steam_display", "#richpresence_atmainmenu");
      }

   }

   public GameCamera getCamera() {
      return this.camera;
   }

   public void connect(SteamID var1, ConnectFrom var2) {
      this.startConnection(GlobalData.startMultiplayerClient(Screen.tickManager, var1), var2);
   }

   public void connect(String var1, String var2, int var3, ConnectFrom var4) {
      if (var2 == null) {
         this.notice("Could not connect to null address");
      } else {
         this.startConnection(GlobalData.startMultiplayerClient(Screen.tickManager, var2, var3, new StaticMessage(var1 == null ? var2 : var1)), var4);
         SaveData var5 = getContinueCacheSaveBase(var2, MainMenu.ContinueMode.JOIN);
         if (var1 != null) {
            var5.addSafeString("name", var1);
         }

         var5.addInt("port", var3);
         GameCache.cacheSave(var5, "continueLast");
      }
   }

   public void startSingleplayer(WorldSave var1, final ServerSettings var2, final ConnectFrom var3) {
      if (var1 != null && !ModLoader.matchesCurrentMods(var1)) {
         this.connectedFrom = var3;
         ModSaveListMismatchForm var7 = new ModSaveListMismatchForm() {
            public void loadAnywayPressed() {
               MainMenu.this.startSingleplayer((WorldSave)null, var2, var3);
            }

            public void backPressed() {
               MainMenu.this.formManager.mainForm.setConnectedFromCurrent();
            }
         };
         this.formManager.mainForm.addComponent(var7, (var1x, var2x) -> {
            if (!var2x) {
               this.formManager.mainForm.removeComponent(var1x);
            }

         });
         var7.setup(var1.getWorldEntity().lastMods);
         var7.onWindowResized();
         this.formManager.mainForm.makeCurrent(var7);
      } else {
         try {
            this.startConnection(GlobalData.startSingleplayerClient(Screen.tickManager, var2), var3);
            SaveData var4 = getContinueCacheSaveBase(var2.worldFilePath.getAbsolutePath(), MainMenu.ContinueMode.OPEN);
            GameCache.cacheSave(var4, "continueLast");
         } catch (ZipError | IOException var5) {
            this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + var5.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
            var5.printStackTrace();
         } catch (FileSystemClosedException var6) {
            this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
            var6.printStackTrace();
         }

      }
   }

   public void host(WorldSave var1, final ServerSettings var2, final ServerHostSettings var3, final ConnectFrom var4) {
      if (var1 != null && !ModLoader.matchesCurrentMods(var1)) {
         this.connectedFrom = var4;
         ModSaveListMismatchForm var9 = new ModSaveListMismatchForm() {
            public void loadAnywayPressed() {
               MainMenu.this.host((WorldSave)null, var2, var3, var4);
            }

            public void backPressed() {
               MainMenu.this.formManager.mainForm.setConnectedFromCurrent();
            }
         };
         this.formManager.mainForm.addComponent(var9, (var1x, var2x) -> {
            if (!var2x) {
               this.formManager.mainForm.removeComponent(var1x);
            }

         });
         var9.setup(var1.getWorldEntity().lastMods);
         var9.onWindowResized();
         this.formManager.mainForm.makeCurrent(var9);
      } else {
         try {
            this.startConnection(GlobalData.startHostClient(Screen.tickManager, var2, var3), var4);
            SaveData var5 = getContinueCacheSaveBase(var2.worldFilePath.getAbsolutePath(), MainMenu.ContinueMode.HOST);
            SaveData var6 = new SaveData("host");
            var2.addSaveData(var6);
            var5.addSaveData(var6);
            GameCache.cacheSave(var5, "continueLast");
         } catch (ZipError | IOException var7) {
            this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + var7.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
            var7.printStackTrace();
         } catch (FileSystemClosedException var8) {
            this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
            var8.printStackTrace();
         }

      }
   }

   public ObjectValue<GameMessage, Runnable> loadContinueCacheSave() {
      try {
         LoadData var1 = GameCache.getSave("continueLast");
         if (var1 != null) {
            ContinueMode var2 = (ContinueMode)var1.getEnum(ContinueMode.class, "mode", (Enum)null);
            if (var2 != null) {
               String var3 = var1.getSafeString("value", (String)null);
               File var8;
               switch (var2) {
                  case OPEN:
                     if (var3 != null) {
                        var8 = new File(var3);
                        if (!World.worldExists(var8)) {
                           return null;
                        }

                        return new ObjectValue(new LocalMessage("ui", "continuetip", "world", World.getWorldDisplayName(var8.getName())), () -> {
                           try {
                              WorldSave var2 = new WorldSave(var8, true, true);
                              this.startSingleplayer(var2, ServerSettings.SingleplayerServer(var8), (ConnectFrom)null);
                           } catch (ZipError | IOException var3) {
                              this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + var3.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
                              var3.printStackTrace();
                           } catch (FileSystemClosedException var4) {
                              this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                              var4.printStackTrace();
                           }

                        });
                     }
                     break;
                  case HOST:
                     if (var3 != null) {
                        var8 = new File(var3);
                        if (!World.worldExists(var8)) {
                           return null;
                        }

                        LoadData var9 = var1.getFirstLoadDataByName("host");
                        if (var9 != null) {
                           ServerSettings var10 = ServerSettings.FromSave(var8, var9);
                           return new ObjectValue(new LocalMessage("ui", "continuehost", "world", World.getWorldDisplayName(var8.getName())), () -> {
                              try {
                                 WorldSave var3 = new WorldSave(var8, true, true);
                                 this.host(var3, var10, (ServerHostSettings)null, (ConnectFrom)null);
                              } catch (ZipError | IOException var4) {
                                 this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + var4.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
                                 var4.printStackTrace();
                              } catch (FileSystemClosedException var5) {
                                 this.notice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                                 var5.printStackTrace();
                              }

                           });
                        }
                     }
                     break;
                  case JOIN:
                     if (var3 != null) {
                        int var4 = var1.getInt("port", -1);
                        if (var4 != -1) {
                           String var5 = var1.getSafeString("name", (String)null);
                           String var6 = var5;
                           if (var5 == null) {
                              if (var4 == 14159) {
                                 var6 = var3;
                              } else {
                                 var6 = var3 + ":" + var4;
                              }
                           }

                           return new ObjectValue(new LocalMessage("ui", "continuejoin", "name", var6), () -> {
                              this.connect(var5, var3, var4, (ConnectFrom)null);
                           });
                        }
                     }
               }
            }
         }
      } catch (Exception var7) {
      }

      return null;
   }

   public static SaveData getContinueCacheSaveBase(String var0, ContinueMode var1) {
      SaveData var2 = new SaveData("");
      var2.addSafeString("value", var0);
      var2.addEnum("mode", var1);
      return var2;
   }

   public void startConnection(Client var1, ConnectFrom var2) {
      if (this.client != null) {
         var1.instantDisconnect("Client error");
      }

      this.client = var1;
      this.connectedFrom = var2;
      GlobalData.resetDebug();
      this.formManager.startConnection(var1);
   }

   public void changeLevel(Client var1) {
      var1.reset();
      this.formManager.startConnection(var1);
   }

   public Client getClient() {
      return this.client;
   }

   public void initLevel() {
      if (this.level != null && !this.level.isDisposed()) {
         this.level.dispose();
      }

      Biome var1 = BiomeRegistry.getRandomBiome(GameRandom.globalRandom.nextLong());
      this.level = var1.getNewSurfaceLevel(GameRandom.globalRandom.nextInt(10000), GameRandom.globalRandom.nextInt(10000), 1.15F, (Server)null, this.worldEntity);
      this.level.biome = var1;
      this.level.onLoadingComplete();
      this.level.lightManager.ambientLightOverride = this.level.lightManager.newLight(150.0F);
      int var2 = this.level.width * this.level.height / 1000;

      for(int var3 = 0; var3 < var2; ++var3) {
         MobSpawnTable var4 = var1.getCritterSpawnTable(this.level);
         Point var5 = new Point(GameRandom.globalRandom.getIntBetween(5, this.level.width - 10), GameRandom.globalRandom.getIntBetween(5, this.level.height - 10));

         while(true) {
            MobChance var6 = var4.getRandomMob(this.level, (ServerClient)null, new Point(var5), GameRandom.globalRandom);
            if (var6 == null) {
               break;
            }

            Mob var7 = var6.getMob(this.level, (ServerClient)null, var5);
            if (var7 != null) {
               Point var8 = var7.getPathMoveOffset();
               boolean var9 = (new MobSpawnLocation(var7, var5.x * 32 + var8.x, var5.y * 32 + var8.y)).checkMobSpawnLocation().validAndApply();
               if (var9) {
                  var7.canDespawn = false;
                  this.level.entityManager.addMob(var7, (float)(var5.x * 32 + var8.x), (float)(var5.y * 32 + var8.y));
                  break;
               }
            }

            var4 = var4.withoutRandomMob(var6);
         }
      }

   }

   public void initCamera() {
      PanningCamera var1 = this.camera;
      this.camera = new PanningCamera();
      if (var1 != null) {
         this.camera.setPosition(var1.getX(), var1.getY());
         this.camera.setDirection(var1.getXDir(), var1.getYDir());
      } else {
         int var2 = this.level.width / 2 * 32 + GameRandom.globalRandom.nextInt(500) - 250;
         int var3 = this.level.height / 2 * 32 + GameRandom.globalRandom.nextInt(500) - 250;
         this.camera.centerCamera(var2, var3);
         int var4 = (int)Math.signum((float)GameRandom.globalRandom.nextInt());
         if (var4 == 0) {
            var4 = -1;
         }

         int var5 = (int)Math.signum((float)GameRandom.globalRandom.nextInt());
         if (var5 == 0) {
            var5 = -1;
         }

         this.camera.setDirection((float)var4, (float)var5);
      }

      if (var1 != null) {
         Settings.menuCameraPan = var1.getSpeed() == 0.0F;
      }

      if (Settings.menuCameraPan) {
         this.camera.setSpeed(10.0F);
      } else {
         this.camera.setSpeed(0.0F);
      }

   }

   public void notice(String var1) {
      this.notice((GameMessage)(var1 == null ? null : new StaticMessage(var1)));
   }

   public void notice(GameMessage var1) {
      NoticeForm var2 = new NoticeForm("notice");
      var2.setupNotice(var1);
      this.continueForm(var2);
   }

   public void continueForm(ContinueComponent var1) {
      if (var1 != null) {
         this.formManager.addContinueForm(var1);
      }

   }

   public void cancelConnection() {
      this.formManager.cancelConnection();
   }

   public void dispose() {
      super.dispose();
      this.formManager.dispose();
      if (this.level != null) {
         this.level.dispose();
      }

   }

   public void onClose() {
      if (this.client != null) {
         this.client.saveAndClose("Closed client", PacketDisconnect.Code.SERVER_STOPPED);
      }

   }

   public void onCrash(List<Throwable> var1) {
      GameCrashLog.printCrashLog(var1, this.client, this.client == null ? null : this.client.getLocalServer(), "MainMenu", this.client == null);
      if (this.client != null) {
         this.client.error(Localization.translate("disconnect", "clienterror"), true);
      } else {
         Screen.dispose();
      }

   }

   public SoundEmitter getALListener() {
      return SoundPlayer.SimpleEmitter(0.0F, 0.0F);
   }

   public static enum ConnectFrom {
      Load,
      Multiplayer,
      Host;

      private ConnectFrom() {
      }

      // $FF: synthetic method
      private static ConnectFrom[] $values() {
         return new ConnectFrom[]{Load, Multiplayer, Host};
      }
   }

   public static enum ContinueMode {
      OPEN,
      HOST,
      JOIN;

      private ContinueMode() {
      }

      // $FF: synthetic method
      private static ContinueMode[] $values() {
         return new ContinueMode[]{OPEN, HOST, JOIN};
      }
   }
}
