package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class InviteTeamServerCommand extends ModularChatCommand {
   public InviteTeamServerCommand() {
      super("invite", "Invites a player to your team", PermissionLevel.USER, false, new CmdParameter("player", new ServerClientParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Command cannot be run from server.");
      } else {
         ServerClient var7 = (ServerClient)var4[0];
         if (var7 != null) {
            if (var3 == var7) {
               var6.add("Cannot invite yourself");
            } else {
               PlayerTeam var8 = var3.getPlayerTeam();
               if (var8 == null) {
                  var8 = var2.world.getTeams().createNewTeam(var3);
                  var6.add((GameMessage)(new LocalMessage("ui", "teamcreated", "name", var8.getName())));
               }

               var6.add((GameMessage)(new LocalMessage("ui", "teaminvited", new String[]{"name", var7.getName(), "team", var8.getName()})));
               PlayerTeam.invitePlayer(var2, var8, var7);
            }
         } else {
            var6.add("Could not find player");
         }

      }
   }
}
