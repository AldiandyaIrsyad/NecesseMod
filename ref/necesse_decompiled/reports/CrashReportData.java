package necesse.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.GameSystemInfo;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.network.ClientNetwork;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.network.ServerNetwork;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

public class CrashReportData extends BasicsData {
   public final List<Throwable> errors;

   public CrashReportData(List<Throwable> var1, Client var2, Server var3, String var4) {
      super(var4);
      this.errors = var1;
      this.data.put("system_user_name", this.getString(() -> {
         return System.getProperty("user.name");
      }));
      this.data.put("system_os", this.getString(GameSystemInfo::getOSName));
      this.data.put("system_os_arch", this.getString(() -> {
         return System.getProperty("os.arch");
      }));
      this.data.put("system_cpu", this.getString(GameSystemInfo::getCPUName));
      this.data.put("system_memory_total", this.getString(GameSystemInfo::getTotalMemoryString));
      this.data.put("system_memory_used", this.getString(GameSystemInfo::getUsedMemoryString));
      this.data.put("system_java_path", this.getString(() -> {
         return System.getProperty("java.home");
      }));
      this.data.put("system_java_version", this.getString(() -> {
         return System.getProperty("java.version");
      }));
      this.addList("system_java_total_arguments", "system_java_argument", () -> {
         return ManagementFactory.getRuntimeMXBean().getInputArguments();
      }, this.data, (var0) -> {
         return var0;
      });
      this.data.put("system_lwjgl_version", this.getString(Version::getVersion));
      this.data.put("system_appdata_path", this.getString(GlobalData::appDataPath));
      this.data.put("system_working_dir", this.getString(() -> {
         return System.getProperty("user.dir");
      }));
      this.data.put("system_natives_path", this.getString(() -> {
         String var0 = System.getProperty("org.lwjgl.librarypath");
         if (var0 == null) {
            var0 = "INTERNAL";
         }

         return var0;
      }));
      this.data.put("system_jvm_memory_max", this.getString(() -> {
         return GameUtils.getByteString(Runtime.getRuntime().maxMemory());
      }));
      this.data.put("system_jvm_memory_used", this.getString(() -> {
         long var0 = Runtime.getRuntime().totalMemory();
         long var2 = var0 - Runtime.getRuntime().freeMemory();
         double var4 = (double)var2 / (double)Runtime.getRuntime().totalMemory();
         return GameUtils.getByteString(var2) + " / " + GameUtils.getByteString(var0) + " (" + GameMath.toDecimals(var4 * 100.0, 2) + "%)";
      }));
      if (GLFW.glfwGetCurrentContext() != 0L) {
         this.data.put("system_graphics_card", this.getString(GameSystemInfo::getGraphicsCard));
         this.data.put("system_opengl_version", this.getString(GameSystemInfo::getOpenGLVersion));
         this.addList("system_total_displays", "system_display", GameSystemInfo::getDisplays, this.data, (var0) -> {
            return var0;
         });
         this.data.put("system_fbo", this.getString(Screen::getFBOCapabilities));
      }

      this.addList("total_errors", "error", () -> {
         return var1;
      }, this.data, (var0) -> {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         var0.printStackTrace(new PrintStream(var1));
         return var1.toString().trim();
      });
      HashMap var10000;
      if (var3 != null) {
         this.data.put("running_server", "true");
         var10000 = this.data;
         ServerNetwork var10003 = var3.network;
         Objects.requireNonNull(var10003);
         var10000.put("server_network", this.getString(var10003::getDebugString));
         this.data.put("server_network_in", this.getString(() -> {
            return var3.packetManager.getAverageIn() + "/s (" + var3.packetManager.getAverageInPackets() + "), Total: " + var3.packetManager.getTotalIn() + " (" + var3.packetManager.getTotalInPackets() + ")";
         }));
         this.data.put("server_network_out", this.getString(() -> {
            return var3.packetManager.getAverageOut() + "/s (" + var3.packetManager.getAverageOutPackets() + "), Total: " + var3.packetManager.getTotalOut() + " (" + var3.packetManager.getTotalOutPackets() + ")";
         }));
         if (var3.world != null) {
            this.data.put("server_save_name", this.getString(() -> {
               return var3.world.filePath;
            }));
            if (var3.world.worldEntity != null) {
               this.data.put("server_world_time", this.getString(() -> {
                  return var3.world.worldEntity.getDayTimeInt() + ", day " + var3.world.worldEntity.getDay();
               }));
               this.data.put("server_game_time", this.getString(() -> {
                  return var3.world.worldEntity.getTime();
               }));
               this.data.put("server_local_time", this.getString(() -> {
                  return var3.world.worldEntity.getLocalTime();
               }));
            }
         }

         this.data.put("server_ticks", this.getString(() -> {
            return var3.tickManager().getTotalTicks();
         }));
         Objects.requireNonNull(var3);
         this.addStream("total_server_players", "server_player", var3::streamClients, this.data, (var0) -> {
            return "Slot " + var0.slot + ": " + var0.getName() + " at " + var0.getLevelIdentifier();
         });
         this.addList("total_server_levels", "server_level", () -> {
            return var3.world.levelManager.getLoadedLevels();
         }, this.data, (var0) -> {
            return var0.getIdentifier() + ", Size: " + var0.width + "x" + var0.height + ", Biome: " + var0.biome.getStringID() + ", Entities: " + var0.entityManager.getSize() + ", Mobs: " + var0.entityManager.mobs.count() + ", Projectiles: " + var0.entityManager.projectiles.count() + ", Pickups: " + var0.entityManager.pickups.count();
         });
      }

      if (var2 != null) {
         this.data.put("running_client", "true");
         var10000 = this.data;
         ClientNetwork var6 = var2.network;
         Objects.requireNonNull(var6);
         var10000.put("client_network", this.getString(var6::getDebugString));
         this.data.put("client_network_in", this.getString(() -> {
            return var2.packetManager.getAverageIn() + "/s (" + var2.packetManager.getAverageInPackets() + "), Total: " + var2.packetManager.getTotalIn() + " (" + var2.packetManager.getTotalInPackets() + ")";
         }));
         this.data.put("client_network_out", this.getString(() -> {
            return var2.packetManager.getAverageOut() + "/s (" + var2.packetManager.getAverageOutPackets() + "), Total: " + var2.packetManager.getTotalOut() + " (" + var2.packetManager.getTotalOutPackets() + ")";
         }));
         if (var2.worldEntity != null) {
            this.data.put("client_world_time", this.getString(() -> {
               return var2.worldEntity.getDayTimeInt() + ", day " + var2.worldEntity.getDay();
            }));
            this.data.put("client_game_time", this.getString(() -> {
               return var2.worldEntity.getTime();
            }));
            this.data.put("client_local_time", this.getString(() -> {
               return var2.worldEntity.getLocalTime();
            }));
         }

         this.data.put("client_slot", this.getString(() -> {
            return var2.getSlot() + "/" + var2.getSlots();
         }));
         Level var5 = var2.getLevel();
         if (var5 != null) {
            this.data.put("client_level", this.getString(() -> {
               return var5.getIdentifier() + ", Size: " + var5.width + "x" + var5.height + ", Biome: " + var5.biome.getStringID() + ", Entities: " + var5.entityManager.getSize() + ", Mobs: " + var5.entityManager.mobs.count() + ", Projectiles: " + var5.entityManager.projectiles.count() + ", Pickups: " + var5.entityManager.pickups.count();
            }));
         }
      }

      this.addList("total_log_errors", "log_error", () -> {
         return this.getErrors;
      }, this.data, (var0) -> {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         var0.printStackTrace(new PrintStream(var1));
         return var1.toString().trim();
      });
   }

   public void printFullReport(PrintStream var1, File var2) {
      var1.println("--- Necesse crash log ---");
      var1.println("Generated on: " + (String)this.data.get("generated_on"));
      if (var2 != null) {
         var1.println("At " + var2.getAbsolutePath());
      }

      var1.println("Game state: " + (String)this.data.get("game_state"));
      var1.println("Game version: " + (String)this.data.get("game_version"));
      var1.println("Game language: " + (String)this.data.get("game_language"));
      var1.println("Steam build: " + (String)this.data.get("steam_build"));
      var1.println("Steam name: " + (String)this.data.get("steam_name"));
      var1.println("Authentication: " + (String)this.data.get("authentication"));
      if (!((String)this.data.get("launch_parameters")).isEmpty()) {
         var1.println("Launch parameters: " + (String)this.data.get("launch_parameters"));
      }

      this.printList(var1, "total_loaded_mods", "loaded_mod", (var0) -> {
         return "Found " + var0 + " loaded mods:";
      });
      this.printList(var1, "total_found_mods", "found_mod", (var0) -> {
         return "Found " + var0 + " not enabled mods:";
      });
      var1.println();
      String[] var3 = this.getList("total_errors", "error");
      var1.println("Exceptions:");
      String[] var4 = var3;
      int var5 = var3.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         var1.println(var7);
      }

      var1.println();
      var1.println("Username: " + (String)this.data.get("system_user_name"));
      var1.println("OS: " + (String)this.data.get("system_os"));
      var1.println("OS arch: " + (String)this.data.get("system_os_arch"));
      var1.println("CPU: " + (String)this.data.get("system_cpu"));
      var1.println("Memory total: " + (String)this.data.get("system_memory_total"));
      var1.println("Memory used: " + (String)this.data.get("system_memory_used"));
      var1.println("Java path: " + (String)this.data.get("system_java_path"));
      var1.println("Java version: " + (String)this.data.get("system_java_version"));
      this.printList(var1, "system_java_total_arguments", "system_java_argument", (var0) -> {
         return "JVM arguments:";
      });
      var1.println("LWJGL version: " + (String)this.data.get("system_lwjgl_version"));
      var1.println("AppData path: " + (String)this.data.get("system_appdata_path"));
      var1.println("Working dir: " + (String)this.data.get("system_working_dir"));
      var1.println("Natives path: " + (String)this.data.get("system_natives_path"));
      var1.println("JVM memory max: " + (String)this.data.get("system_jvm_memory_max"));
      var1.println("JVM memory used: " + (String)this.data.get("system_jvm_memory_used"));
      var1.println();
      if (this.data.get("system_graphics_card") != null) {
         var1.println("Graphics card: " + (String)this.data.get("system_graphics_card"));
         var1.println("OpenGL version: " + (String)this.data.get("system_opengl_version"));
         this.printList(var1, "system_total_displays", "system_display", (var0) -> {
            return "Found " + var0 + " displays:";
         });
         var1.println("FBO: " + (String)this.data.get("system_fbo"));
      }

      var1.println();
      if (this.data.get("running_server") != null) {
         var1.println("Running server: Yes");
         var1.println("Server network: " + (String)this.data.get("server_network"));
         var1.println("Server network in: " + (String)this.data.get("server_network_in"));
         var1.println("Server network out: " + (String)this.data.get("server_network_out"));
         var1.println("Server save name: " + (String)this.data.get("server_save_name"));
         var1.println("Server world time: " + (String)this.data.get("server_world_time"));
         var1.println("Server game time: " + (String)this.data.get("server_game_time"));
         var1.println("Server local time: " + (String)this.data.get("server_local_time"));
         var1.println("Server ticks: " + (String)this.data.get("server_ticks"));
         this.printList(var1, "total_server_players", "server_player", (var0) -> {
            return "Players online: " + var0;
         });
         this.printList(var1, "total_server_levels", "server_level", (var0) -> {
            return "Loaded levels: " + var0;
         });
      } else {
         var1.println("Running server: No");
      }

      var1.println();
      if (this.data.get("running_client") != null) {
         var1.println("Running client: Yes");
         var1.println("Client network: " + (String)this.data.get("client_network"));
         var1.println("Client network in: " + (String)this.data.get("client_network_in"));
         var1.println("Client network out: " + (String)this.data.get("client_network_out"));
         var1.println("Client world time: " + (String)this.data.get("client_world_time"));
         var1.println("Client game time: " + (String)this.data.get("client_game_time"));
         var1.println("Client local time: " + (String)this.data.get("client_local_time"));
         var1.println("Client slot: " + (String)this.data.get("client_slot"));
         var1.println("Client level: " + (String)this.data.get("client_level"));
      } else {
         var1.println("Running client: No");
      }

      var4 = this.getList("total_log_errors", "log_error");
      if (var4.length > 0) {
         var1.println();
         var1.println("Log exceptions:");
         String[] var9 = var4;
         var6 = var4.length;

         for(int var10 = 0; var10 < var6; ++var10) {
            String var8 = var9[var10];
            var1.println(var8);
         }
      }

   }

   public String getFullReport(File var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      PrintStream var3 = new PrintStream(var2);
      this.printFullReport(var3, var1);
      return var2.toString();
   }

   private void printList(PrintStream var1, String var2, String var3, Function<Integer, String> var4) {
      this.printList(var1, var2, var3, var4, (var0, var1x) -> {
         return "\t" + var1x;
      });
   }

   private void printList(PrintStream var1, String var2, String var3, Function<Integer, String> var4, BiFunction<Integer, String, String> var5) {
      String[] var6 = this.getList(var2, var3);
      if (var4 != null) {
         var1.println((String)var4.apply(var6.length));
      }

      for(int var7 = 0; var7 < var6.length; ++var7) {
         String var8 = (String)this.data.get(var3 + var7);
         var1.println((String)var5.apply(var7, var8));
      }

   }

   private String[] getList(String var1, String var2) {
      String var3 = (String)this.data.get(var1);
      if (var3 != null) {
         try {
            int var4 = Integer.parseInt(var3);
            String[] var5 = new String[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = (String)this.data.get(var2 + var6);
            }

            return var5;
         } catch (NumberFormatException var7) {
            var7.printStackTrace();
         }
      }

      return new String[0];
   }
}
