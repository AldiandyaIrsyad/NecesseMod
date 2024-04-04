package necesse.engine.commands.serverCommands;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GameTileRange;
import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class SettingsServerCommand extends ModularChatCommand {
   public static ArrayList<ServerSettingCommand<?>> settings = new ArrayList();

   private static String[] presetParams() {
      return (String[])settings.stream().map((var0) -> {
         return var0.commandString;
      }).toArray((var0) -> {
         return new String[var0];
      });
   }

   private static ParameterHandler<?>[] handlers() {
      return (ParameterHandler[])settings.stream().map((var0) -> {
         return var0.handler;
      }).toArray((var0) -> {
         return new ParameterHandler[var0];
      });
   }

   public SettingsServerCommand() {
      super("settings", "Change server world settings", PermissionLevel.ADMIN, false, new CmdParameter("list/setting", new PresetStringParameterHandler((String[])GameUtils.concat(new String[]{"list"}, presetParams()))), new CmdParameter("arg", new RestStringParameterHandler(), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      String var8 = (String)var4[1];
      var8 = var8.trim();
      if (var7.equals("list")) {
         var6.add("Settings list:");
         var6.add(GameUtils.join(presetParams(), ", "));
      } else {
         Iterator var9 = settings.iterator();

         while(var9.hasNext()) {
            ServerSettingCommand var10 = (ServerSettingCommand)var9.next();
            if (var7.equalsIgnoreCase(var10.commandString)) {
               if (var8.isEmpty() && var10.statusMessage != null) {
                  String var11 = var10.statusMessage.get(var2, var3);
                  if (var11 != null) {
                     var6.add(var11);
                     break;
                  }
               }

               try {
                  Object var13 = var10.handler.parse(var1, var2, var3, var8, new CmdParameter(var10.commandString, new RestStringParameterHandler()));
                  var10.apply(var6, var2, var3, var13);
               } catch (IllegalArgumentException var12) {
                  var6.add(var12.getMessage());
               }
               break;
            }
         }
      }

   }

   private static void saveWorldSettings(Server var0) {
      var0.world.settings.saveSettings();
      var0.world.settings.sendSettingsPacket();
   }

   private static void saveClientAndServerSettings() {
      Settings.saveClientSettings();
      Settings.saveServerSettings();
   }

   static {
      settings.add(new ServerSettingCommand("DisableMobSpawns", new BoolParameterHandler(), (var0, var1, var2, var3) -> {
         if (!var1.world.settings.allowCheats && var3) {
            var0.add("Allow cheats to change that setting");
         } else {
            var1.world.settings.disableMobSpawns = var3;
            saveWorldSettings(var1);
            var0.add("Disable mob spawns set to " + var1.world.settings.disableMobSpawns);
         }

      }, (var0, var1) -> {
         return "Disable mob spawns currently set to " + var0.world.settings.disableMobSpawns;
      }));
      settings.add(new ServerSettingCommand("ForcedPvP", new BoolParameterHandler(), (var0, var1, var2, var3) -> {
         var1.world.settings.forcedPvP = var3;
         saveWorldSettings(var1);
         var0.add("Forced PvP set to " + var1.world.settings.forcedPvP);
      }, (var0, var1) -> {
         return "Forced PvP currently set to " + var0.world.settings.forcedPvP;
      }));
      settings.add(new ServerSettingCommand("SurvivalMode", new BoolParameterHandler(), (var0, var1, var2, var3) -> {
         var1.world.settings.survivalMode = var3;
         saveWorldSettings(var1);
         var0.add("Survival mode set to " + var1.world.settings.survivalMode);
      }, (var0, var1) -> {
         return "Survival mode currently set to " + var0.world.settings.survivalMode;
      }));
      settings.add(new ServerSettingCommand("AllowOutsideCharacters", new BoolParameterHandler(), (var0, var1, var2, var3) -> {
         var1.world.settings.allowOutsideCharacters = var3;
         saveWorldSettings(var1);
         var0.add("Allow outside characters set to: " + var1.world.settings.allowOutsideCharacters);
      }, (var0, var1) -> {
         return "Allow outside characters currently set to " + var0.world.settings.allowOutsideCharacters;
      }));
      settings.add(new ServerSettingCommand("PlayerHunger", new BoolParameterHandler(), (var0, var1, var2, var3) -> {
         var1.world.settings.playerHunger = var3;
         saveWorldSettings(var1);
         var0.add("Player hunger set to " + var1.world.settings.playerHunger);
      }, (var0, var1) -> {
         return "Player hunger currently set to " + var0.world.settings.playerHunger;
      }));
      settings.add(new ServerSettingCommand("UnloadLevelsCooldown", new IntParameterHandler(), (var0, var1, var2, var3) -> {
         Settings.unloadLevelsCooldown = Math.max(2, var3);
         saveClientAndServerSettings();
         var0.add("Unload levels cooldown set to " + Settings.unloadLevelsCooldown + " seconds");
      }, (var0, var1) -> {
         return "Unload levels cooldown currently set to " + Settings.unloadLevelsCooldown + " seconds";
      }));
      settings.add(new ServerSettingCommand("WorldBorderSize", new IntParameterHandler(), (var0, var1, var2, var3) -> {
         Settings.worldBorderSize = var3;
         saveClientAndServerSettings();
         var0.add("World border size set to " + (Settings.worldBorderSize < 0 ? "infinite" : Settings.worldBorderSize));
      }, (var0, var1) -> {
         return "World border size currently set to " + (Settings.worldBorderSize < 0 ? "infinite" : Settings.worldBorderSize);
      }));
      settings.add(new ServerSettingCommand("DroppedItemsLifeMinutes", new IntParameterHandler(), (var0, var1, var2, var3) -> {
         Settings.droppedItemsLifeMinutes = var3;
         var1.world.settings.droppedItemsLifeMinutes = Settings.droppedItemsLifeMinutes;
         saveWorldSettings(var1);
         saveClientAndServerSettings();
         var0.add("Dropped items lifetime set to " + (Settings.droppedItemsLifeMinutes <= 0 ? "Infinite" : Settings.droppedItemsLifeMinutes) + " minutes");
      }, (var0, var1) -> {
         return "Dropped items lifetime currently set to " + (var0.world.settings.droppedItemsLifeMinutes <= 0 ? "Infinite" : var0.world.settings.droppedItemsLifeMinutes) + " minutes";
      }));
      settings.add(new ServerSettingCommand("UnloadSettlements", new BoolParameterHandler(), (var0, var1, var2, var3) -> {
         Settings.unloadSettlements = var3;
         var1.world.settings.unloadSettlements = Settings.unloadSettlements;
         saveWorldSettings(var1);
         saveClientAndServerSettings();
         var0.add("Unload settlements set to " + Settings.unloadSettlements);
      }, (var0, var1) -> {
         return "Unload settlements currently set to " + var0.world.settings.unloadSettlements;
      }));
      settings.add(new ServerSettingCommand("MaxSettlementsPerPlayer", new IntParameterHandler(), (var0, var1, var2, var3) -> {
         Settings.maxSettlementsPerPlayer = var3;
         var1.world.settings.maxSettlementsPerPlayer = Settings.maxSettlementsPerPlayer;
         saveWorldSettings(var1);
         saveClientAndServerSettings();
         var0.add("Max settlements per player set to " + (Settings.maxSettlementsPerPlayer < 0 ? "Unlimited" : Settings.maxSettlementsPerPlayer));
      }, (var0, var1) -> {
         return "Max settlements per player currently set to " + (var0.world.settings.maxSettlementsPerPlayer < 0 ? "Unlimited" : var0.world.settings.maxSettlementsPerPlayer);
      }));
      settings.add(new ServerSettingCommand("MaxSettlersPerSettlement", new IntParameterHandler(), (var0, var1, var2, var3) -> {
         Settings.maxSettlersPerSettlement = var3;
         var1.world.settings.maxSettlersPerSettlement = Settings.maxSettlersPerSettlement;
         saveWorldSettings(var1);
         saveClientAndServerSettings();
         var0.add("Max settlers per settlement set to " + (Settings.maxSettlersPerSettlement < 0 ? "Unlimited" : Settings.maxSettlersPerSettlement));
      }, (var0, var1) -> {
         return "Max settlers per settlement currently set to " + (var0.world.settings.maxSettlersPerSettlement < 0 ? "Unlimited" : var0.world.settings.maxSettlersPerSettlement);
      }));
      settings.add(new ServerSettingCommand("JobSearchRange", new IntParameterHandler(), (var0, var1, var2, var3) -> {
         if (var3 >= 1 && var3 <= 300) {
            if (var1.world.settings.jobSearchRange.maxRange != var3) {
               Settings.jobSearchRange = new GameTileRange(var3, new Point[0]);
               var1.world.settings.jobSearchRange = Settings.jobSearchRange;
               saveWorldSettings(var1);
               saveClientAndServerSettings();
            }

            var0.add("Settlers job search range set to " + Settings.jobSearchRange.maxRange + " tiles");
         } else {
            var0.add("Range must be between 1 and 300");
         }
      }, (var0, var1) -> {
         return "Settlers job search range currently set to " + var0.world.settings.jobSearchRange.maxRange + " tiles";
      }));
   }

   public static class ServerSettingCommand<T> {
      public final String commandString;
      public final ParameterHandler<T> handler;
      public final ServerSettingApply<T> applier;
      public final ServerSettingStatus statusMessage;

      public ServerSettingCommand(String var1, ParameterHandler<T> var2, ServerSettingApply<T> var3, ServerSettingStatus var4) {
         this.commandString = var1;
         this.handler = var2;
         this.applier = var3;
         this.statusMessage = var4;
      }

      public void apply(CommandLog var1, Server var2, ServerClient var3, Object var4) {
         this.applier.apply(var1, var2, var3, var4);
      }
   }

   public interface ServerSettingStatus {
      String get(Server var1, ServerClient var2);
   }

   @FunctionalInterface
   public interface ServerSettingApply<T> {
      void apply(CommandLog var1, Server var2, ServerClient var3, T var4);
   }
}
