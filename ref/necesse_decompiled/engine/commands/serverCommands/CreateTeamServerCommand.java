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

public class CreateTeamServerCommand extends ModularChatCommand {
   public CreateTeamServerCommand() {
      super("createteam", "Creates a new team for yourself", PermissionLevel.USER, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Command cannot be run from server.");
      } else if (var3.getPlayerTeam() != null) {
         var6.add((GameMessage)(new LocalMessage("ui", "teamcreateleave")));
      } else {
         PlayerTeam var7 = var2.world.getTeams().createNewTeam(var3);
         var6.add((GameMessage)(new LocalMessage("ui", "teamcreated", "name", var7.getName())));
      }
   }
}
