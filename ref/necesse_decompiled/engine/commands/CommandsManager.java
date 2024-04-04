package necesse.engine.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.commands.clientCommands.BoolClientCommand;
import necesse.engine.commands.clientCommands.DebugClientClientCommand;
import necesse.engine.commands.clientCommands.HelpClientCommand;
import necesse.engine.commands.clientCommands.MaxFPSClientCommand;
import necesse.engine.commands.clientCommands.ObjectHitboxesClientCommand;
import necesse.engine.commands.clientCommands.PanningCameraClientCommand;
import necesse.engine.commands.clientCommands.SteamNetworkSendFlagClientCommand;
import necesse.engine.commands.clientCommands.VoidClientCommand;
import necesse.engine.commands.clientCommands.ZoomClientCommand;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.serverCommands.AllowCheatsServerCommand;
import necesse.engine.commands.serverCommands.BanServerCommand;
import necesse.engine.commands.serverCommands.BansServerCommand;
import necesse.engine.commands.serverCommands.BuffServerCommand;
import necesse.engine.commands.serverCommands.ChangeNameServerCommand;
import necesse.engine.commands.serverCommands.ClearBuffServerCommand;
import necesse.engine.commands.serverCommands.ClearConnectionServerCommand;
import necesse.engine.commands.serverCommands.ClearEventsServerCommand;
import necesse.engine.commands.serverCommands.ClearMobsServerCommand;
import necesse.engine.commands.serverCommands.ClearTeamServerCommand;
import necesse.engine.commands.serverCommands.ClearallServerCommand;
import necesse.engine.commands.serverCommands.CopyInventoryServerCommand;
import necesse.engine.commands.serverCommands.CopyItemCommand;
import necesse.engine.commands.serverCommands.CreateTeamServerCommand;
import necesse.engine.commands.serverCommands.DeathPenaltyServerCommand;
import necesse.engine.commands.serverCommands.DeletePlayerServerCommand;
import necesse.engine.commands.serverCommands.DieServerCommand;
import necesse.engine.commands.serverCommands.DifficultyServerCommand;
import necesse.engine.commands.serverCommands.EnchantServerCommand;
import necesse.engine.commands.serverCommands.EndRaidCommand;
import necesse.engine.commands.serverCommands.GetTeamServerCommand;
import necesse.engine.commands.serverCommands.GiveServerCommand;
import necesse.engine.commands.serverCommands.HealServerCommand;
import necesse.engine.commands.serverCommands.HealthServerCommand;
import necesse.engine.commands.serverCommands.HelpServerCommand;
import necesse.engine.commands.serverCommands.HungerServerCommand;
import necesse.engine.commands.serverCommands.InviteTeamServerCommand;
import necesse.engine.commands.serverCommands.ItemGNDCommand;
import necesse.engine.commands.serverCommands.KickServerCommand;
import necesse.engine.commands.serverCommands.LeaveTeamServerCommand;
import necesse.engine.commands.serverCommands.LevelsServerCommand;
import necesse.engine.commands.serverCommands.MOTDServerCommand;
import necesse.engine.commands.serverCommands.ManaServerCommand;
import necesse.engine.commands.serverCommands.MaxHealthServerCommand;
import necesse.engine.commands.serverCommands.MaxLatencyServerCommand;
import necesse.engine.commands.serverCommands.MaxManaServerCommand;
import necesse.engine.commands.serverCommands.MeServerCommand;
import necesse.engine.commands.serverCommands.MowServerCommand;
import necesse.engine.commands.serverCommands.MyPermissionsServerCommand;
import necesse.engine.commands.serverCommands.NetworkServerCommand;
import necesse.engine.commands.serverCommands.PasswordServerCommand;
import necesse.engine.commands.serverCommands.PauseWhenEmptyServerCommand;
import necesse.engine.commands.serverCommands.PerformanceServerCommand;
import necesse.engine.commands.serverCommands.PermissionsServerCommand;
import necesse.engine.commands.serverCommands.PlayerNamesServerCommand;
import necesse.engine.commands.serverCommands.PlayersServerCommand;
import necesse.engine.commands.serverCommands.PlaytimeServerCommand;
import necesse.engine.commands.serverCommands.PrintServerCommand;
import necesse.engine.commands.serverCommands.RaidsServerCommand;
import necesse.engine.commands.serverCommands.RainServerCommand;
import necesse.engine.commands.serverCommands.RegenServerCommand;
import necesse.engine.commands.serverCommands.RevealServerCommand;
import necesse.engine.commands.serverCommands.SaveServerCommand;
import necesse.engine.commands.serverCommands.SayServerCommand;
import necesse.engine.commands.serverCommands.SetDimensionServerCommand;
import necesse.engine.commands.serverCommands.SetIslandServerCommand;
import necesse.engine.commands.serverCommands.SetLanguageServerCommand;
import necesse.engine.commands.serverCommands.SetRaidDifficultyCommand;
import necesse.engine.commands.serverCommands.SetRaidTierCommand;
import necesse.engine.commands.serverCommands.SetTeamOwnerServerCommand;
import necesse.engine.commands.serverCommands.SetTeamServerCommand;
import necesse.engine.commands.serverCommands.SettingsServerCommand;
import necesse.engine.commands.serverCommands.ShareMapServerCommand;
import necesse.engine.commands.serverCommands.StartRaidCommand;
import necesse.engine.commands.serverCommands.StaticDamageServerCommand;
import necesse.engine.commands.serverCommands.StopHelpServerCommand;
import necesse.engine.commands.serverCommands.StopServerCommand;
import necesse.engine.commands.serverCommands.TeleportServerCommand;
import necesse.engine.commands.serverCommands.TimeServerCommand;
import necesse.engine.commands.serverCommands.UnbanServerCommand;
import necesse.engine.commands.serverCommands.WhisperHelpServerCommand;
import necesse.engine.commands.serverCommands.WhisperServerCommand;
import necesse.engine.commands.serverCommands.setupCommand.DemoServerCommand;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCmdAutocomplete;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;

public class CommandsManager {
   private static boolean registryOpen = true;
   private static boolean registeredCore = false;
   private static List<ChatCommand> serverCommands = new ArrayList();
   private static List<ChatCommand> clientCommands = new ArrayList();
   public final Server server;
   public final Client client;
   private String lastCheatCommand;

   public static void registerCoreCommands() {
      if (!registryOpen) {
         throw new IllegalStateException("Command registration is closed");
      } else if (registeredCore) {
         throw new IllegalStateException("Cannot register core commands twice");
      } else {
         registeredCore = true;
         registerServerCommand(new StopHelpServerCommand());
         registerServerCommand(new StopServerCommand("quit"));
         registerServerCommand(new StopServerCommand("stop"));
         registerServerCommand(new StopServerCommand("exit"));
         registerServerCommand(new HelpServerCommand());
         registerServerCommand(new PlaytimeServerCommand());
         registerServerCommand(new MeServerCommand());
         registerServerCommand(new WhisperHelpServerCommand());
         registerServerCommand(new WhisperServerCommand("whisper"));
         registerServerCommand(new WhisperServerCommand("w"));
         registerServerCommand(new WhisperServerCommand("pm"));
         registerServerCommand(new NetworkServerCommand());
         registerServerCommand(new PlayersServerCommand());
         registerServerCommand(new PlayerNamesServerCommand());
         registerServerCommand(new LevelsServerCommand());
         registerServerCommand(new SaveServerCommand());
         registerServerCommand(new MowServerCommand());
         registerServerCommand(new TimeServerCommand());
         registerServerCommand(new ClearallServerCommand());
         registerServerCommand(new ClearMobsServerCommand());
         registerServerCommand(new ClearEventsServerCommand());
         registerServerCommand(new TeleportServerCommand("tp"));
         registerServerCommand(new KickServerCommand());
         registerServerCommand(new SayServerCommand());
         registerServerCommand(new PrintServerCommand());
         registerServerCommand(new GiveServerCommand());
         registerServerCommand(new BuffServerCommand());
         registerServerCommand(new ClearBuffServerCommand());
         registerServerCommand(new RevealServerCommand());
         registerServerCommand(new SetIslandServerCommand());
         registerServerCommand(new SetDimensionServerCommand());
         registerServerCommand(new HealthServerCommand("hp"));
         registerServerCommand(new MaxHealthServerCommand());
         registerServerCommand(new ManaServerCommand());
         registerServerCommand(new MaxManaServerCommand());
         registerServerCommand(new HungerServerCommand());
         registerServerCommand(new DeletePlayerServerCommand());
         registerServerCommand(new SettingsServerCommand());
         registerServerCommand(new DifficultyServerCommand());
         registerServerCommand(new DeathPenaltyServerCommand());
         registerServerCommand(new RaidsServerCommand());
         registerServerCommand(new PauseWhenEmptyServerCommand());
         registerServerCommand(new MaxLatencyServerCommand());
         registerServerCommand(new PasswordServerCommand());
         registerServerCommand(new PermissionsServerCommand());
         registerServerCommand(new MyPermissionsServerCommand());
         registerServerCommand(new BanServerCommand());
         registerServerCommand(new UnbanServerCommand());
         registerServerCommand(new BansServerCommand());
         registerServerCommand(new RegenServerCommand());
         registerServerCommand(new RainServerCommand());
         registerServerCommand(new EnchantServerCommand());
         registerServerCommand(new AllowCheatsServerCommand());
         registerServerCommand(new SetLanguageServerCommand());
         registerServerCommand(new DieServerCommand());
         registerServerCommand(new ItemGNDCommand());
         registerServerCommand(new CopyItemCommand());
         registerServerCommand(new HealServerCommand());
         registerServerCommand(new CopyInventoryServerCommand());
         registerServerCommand(new DemoServerCommand());
         registerServerCommand(new PerformanceServerCommand());
         registerServerCommand(new GetTeamServerCommand());
         registerServerCommand(new ClearTeamServerCommand());
         registerServerCommand(new SetTeamServerCommand());
         registerServerCommand(new SetTeamOwnerServerCommand());
         registerServerCommand(new CreateTeamServerCommand());
         registerServerCommand(new LeaveTeamServerCommand());
         registerServerCommand(new InviteTeamServerCommand());
         registerServerCommand(new MOTDServerCommand());
         registerServerCommand(new ChangeNameServerCommand());
         registerServerCommand(new ShareMapServerCommand());
         registerServerCommand(new StartRaidCommand());
         registerServerCommand(new EndRaidCommand());
         registerServerCommand(new SetRaidTierCommand());
         registerServerCommand(new SetRaidDifficultyCommand());
         registerClientCommand(new HelpClientCommand());
         registerClientCommand(new VoidClientCommand("reload", "Reloads the map", PermissionLevel.USER, (var0, var1) -> {
            var0.reloadMap();
         }));
         registerClientCommand(new PanningCameraClientCommand());
         registerClientCommand(new VoidClientCommand("mapshot", "Takes a map shot", PermissionLevel.USER, (var0, var1) -> {
            Screen.takeMapshot(var0.getLevel(), var0);
         }));
         registerClientCommand(new ZoomClientCommand());
         registerClientCommand(new ObjectHitboxesClientCommand());
         registerClientCommand(new VoidClientCommand("reloadlang", "Reloads language files", PermissionLevel.USER, (var0, var1) -> {
            Localization.reloadLanguageFiles();
            var0.chat.addMessage("Reloaded language files");
         }));
         registerClientCommand(new SteamNetworkSendFlagClientCommand());
         if (GlobalData.isDevMode()) {
            registerClientCommand(BoolClientCommand.create("alwaysrain", "Toggles show rain", PermissionLevel.OWNER, (Class)Settings.class, (String)"alwaysRain", (Object)null));
            registerClientCommand(BoolClientCommand.create("alwayslight", "Toggles always light", PermissionLevel.OWNER, (Class)Settings.class, (String)"alwaysLight", (Object)null));
            registerClientCommand(new MaxFPSClientCommand());
            registerServerCommand(new ClearConnectionServerCommand());
            registerServerCommand(new StaticDamageServerCommand());
         }

         registerClientCommand(new DebugClientClientCommand());
      }
   }

   public static <T extends ChatCommand> T registerServerCommand(T var0) {
      if (!registryOpen) {
         throw new IllegalStateException("Command registration is closed");
      } else {
         serverCommands.add(var0);
         return var0;
      }
   }

   public static <T extends ChatCommand> T registerClientCommand(T var0) {
      if (!registryOpen) {
         throw new IllegalStateException("Command registration is closed");
      } else {
         clientCommands.add(var0);
         return var0;
      }
   }

   public static void closeRegistry() {
      registryOpen = false;
   }

   public CommandsManager(Server var1) {
      this.server = var1;
      this.client = null;
      this.lastCheatCommand = null;
   }

   public CommandsManager(Client var1) {
      this.client = var1;
      this.server = null;
   }

   public List<ChatCommand> getServerCommands() {
      return serverCommands;
   }

   public List<ChatCommand> getClientCommands() {
      return clientCommands;
   }

   public List<ChatCommand> getCommands() {
      ArrayList var1 = new ArrayList(this.getServerCommands());
      if (this.client != null) {
         var1.addAll(this.getClientCommands());
      }

      return var1;
   }

   public boolean runServerCommand(ParsedCommand var1, ServerClient var2) {
      ArrayList var3 = new ArrayList(Arrays.asList(var1.args));
      CommandLog var4 = null;
      Iterator var5 = serverCommands.iterator();

      while(var5.hasNext()) {
         ChatCommand var6 = (ChatCommand)var5.next();
         if (!var6.onlyForHelp() && var6.name.equalsIgnoreCase(var1.commandName)) {
            CommandLog var7 = new CommandLog(this.client, var2);
            if (var4 == null) {
               var4 = var7;
            }

            if (var6.havePermissions(this.client, this.server, var2)) {
               String var8 = null;
               if (var6.isCheat() && !this.server.world.settings.allowCheats) {
                  if (getPermissionLevel(this.client, var2).getLevel() < PermissionLevel.OWNER.getLevel()) {
                     var8 = GameColor.RED.getColorCode() + "Cheats are not allowed in this world.";
                  } else {
                     var8 = GameColor.CYAN.getColorCode() + "Running this command will disable achievements. Run it again to accept this.";
                     String var9 = var1.commandName + " " + String.join(" ", var3);
                     if (var9.equals(this.lastCheatCommand)) {
                        var8 = null;
                        this.lastCheatCommand = null;
                        this.server.world.settings.enableCheats();
                     } else {
                        this.lastCheatCommand = var9;
                     }
                  }
               }

               if (var8 != null) {
                  var7.add(var8);
               } else {
                  try {
                     if (var6.run(this.client, this.server, var2, var3, var7)) {
                        var7.printLog();
                        return true;
                     }
                  } catch (Exception var10) {
                     var7.add(GameColor.RED.getColorCode() + "Error executing command " + var1.commandName);
                     var10.printStackTrace();
                  }
               }
            } else {
               var7.add(GameColor.RED.getColorCode() + "You do not have permissions for that command.");
            }
         }
      }

      if (var4 != null) {
         var4.printLog();
         return true;
      } else {
         this.lastCheatCommand = null;
         CommandLog.print((Client)null, var2, (String)("Could not find command: \"" + var1.commandName + "\", use command \"help\"."));
         return false;
      }
   }

   public boolean runClientCommand(ParsedCommand var1) {
      if (this.client == null) {
         throw new IllegalStateException("Cannot run client commands on server");
      } else {
         ArrayList var2 = new ArrayList(Arrays.asList(var1.args));
         CommandLog var3 = null;
         Iterator var4 = clientCommands.iterator();

         while(var4.hasNext()) {
            ChatCommand var5 = (ChatCommand)var4.next();
            if (!var5.onlyForHelp() && var5.name.equalsIgnoreCase(var1.commandName)) {
               CommandLog var6 = new CommandLog(this.client, (ServerClient)null);
               if (var3 == null) {
                  var3 = var6;
               }

               if (var5.havePermissions(this.client, this.server, (ServerClient)null)) {
                  try {
                     if (var5.run(this.client, this.server, (ServerClient)null, var2, var6)) {
                        var6.printLog();
                        return true;
                     }
                  } catch (Exception var8) {
                     var6.add(GameColor.RED.getColorCode() + "Error executing command " + var1.commandName);
                     var8.printStackTrace();
                  }
               } else {
                  var6.add(GameColor.RED.getColorCode() + "You do not have permissions for that command.");
               }
            }
         }

         if (var3 != null) {
            var3.printLog();
            return true;
         } else {
            return false;
         }
      }
   }

   public List<AutoComplete> autocomplete(ParsedCommand var1, ServerClient var2) {
      if (var1.args.length == 0) {
         return ParameterHandler.autocompleteFromList(this.getCommands(), (var2x) -> {
            return !var2x.onlyForHelp() && var2x.havePermissions(this.client, this.server, var2);
         }, (var0) -> {
            return var0.name;
         }, new CmdArgument((CmdParameter)null, var1.commandName, 1));
      } else {
         Iterator var3 = clientCommands.iterator();

         ChatCommand var4;
         List var5;
         while(var3.hasNext()) {
            var4 = (ChatCommand)var3.next();
            if (!var4.onlyForHelp() && var4.havePermissions(this.client, this.server, var2) && var4.name.equalsIgnoreCase(var1.commandName)) {
               var5 = this.clearDuplicates(var4.autocomplete(this.client, this.server, var2, var1.args));
               if (!var5.isEmpty()) {
                  return var5;
               }
            }
         }

         var3 = serverCommands.iterator();

         while(var3.hasNext()) {
            var4 = (ChatCommand)var3.next();
            if (!var4.onlyForHelp() && var4.havePermissions(this.client, this.server, var2) && var4.name.equalsIgnoreCase(var1.commandName)) {
               if (this.client != null && var4.autocompleteOnServer()) {
                  this.client.network.sendPacket(new PacketCmdAutocomplete(var1.fullCommand));
                  return Collections.emptyList();
               }

               var5 = this.clearDuplicates(var4.autocomplete(this.client, this.server, var2, var1.args));
               if (!var5.isEmpty()) {
                  return var5;
               }
            }
         }

         return Collections.emptyList();
      }
   }

   protected List<AutoComplete> clearDuplicates(List<AutoComplete> var1) {
      ListIterator var2 = var1.listIterator();

      while(var2.hasNext()) {
         AutoComplete var3 = (AutoComplete)var2.next();
         boolean var4 = false;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            AutoComplete var6 = (AutoComplete)var5.next();
            if (var3 != var6 && var3.equals(var6)) {
               var4 = true;
               break;
            }
         }

         if (var4) {
            var2.remove();
         }
      }

      return var1;
   }

   public String getCurrentUsage(ParsedCommand var1, ServerClient var2) {
      if (var1.args.length == 0) {
         return null;
      } else {
         Iterator var3 = clientCommands.iterator();

         ChatCommand var4;
         do {
            if (!var3.hasNext()) {
               var3 = serverCommands.iterator();

               do {
                  if (!var3.hasNext()) {
                     return null;
                  }

                  var4 = (ChatCommand)var3.next();
               } while(var4.onlyForHelp() || !var4.havePermissions(this.client, this.server, var2) || !var4.name.equalsIgnoreCase(var1.commandName));

               return var4.getCurrentUsage(this.client, this.server, var2, var1.args);
            }

            var4 = (ChatCommand)var3.next();
         } while(var4.onlyForHelp() || !var4.havePermissions(this.client, this.server, var2) || !var4.name.equalsIgnoreCase(var1.commandName));

         return var4.getCurrentUsage(this.client, this.server, var2, var1.args);
      }
   }

   public static PermissionLevel getPermissionLevel(Client var0, ServerClient var1) {
      if (var0 != null) {
         return var0.getPermissionLevel();
      } else {
         return var1 != null ? var1.getPermissionLevel() : PermissionLevel.SERVER;
      }
   }
}
