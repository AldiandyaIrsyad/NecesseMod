package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class LeaveTeamServerCommand extends ModularChatCommand {
   public LeaveTeamServerCommand() {
      super("leaveteam", "Leaves your current team", PermissionLevel.USER, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Command cannot be run from server.");
      } else {
         PlayerTeam var7 = var3.getPlayerTeam();
         if (var7 == null) {
            var6.add((GameMessage)(new LocalMessage("ui", "teamnocurrent")));
         } else {
            PlayerTeam.removeMember(var2, var7, var3.authentication, false);
            var6.add((GameMessage)(new LocalMessage("ui", "teamleaved", "name", var7.getName())));
         }
      }
   }
}
