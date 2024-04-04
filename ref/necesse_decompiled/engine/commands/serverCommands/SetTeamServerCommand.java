package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StoredPlayerParameterHandler;
import necesse.engine.commands.parameterHandlers.TeamParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class SetTeamServerCommand extends ModularChatCommand {
   public SetTeamServerCommand() {
      super("setteam", "Sets the team of the player.", PermissionLevel.ADMIN, false, new CmdParameter("player", new StoredPlayerParameterHandler()), new CmdParameter("team", new TeamParameterHandler()));
   }

   public boolean autocompleteOnServer() {
      return true;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      StoredPlayerParameterHandler.StoredPlayer var7 = (StoredPlayerParameterHandler.StoredPlayer)var4[0];
      PlayerTeam var8 = (PlayerTeam)var4[1];
      if (var7 != null) {
         if (var8 != null) {
            PlayerTeam var9 = var2.world.getTeams().getPlayerTeam(var7.authentication);
            if (var9 != null) {
               PlayerTeam.removeMember(var2, var9, var7.authentication, true);
               var6.add("Removed " + var7.name + " from old team: " + var9.getName() + " (ID " + var9.teamID + ")");
            }

            PlayerTeam.addMember(var2, var8, var7.authentication);
            var6.add("Added " + var7.name + " to team: " + var8.getName() + " (ID " + var8.teamID + ")");
         } else {
            var6.add("Could not find team");
         }
      } else {
         var6.add("Could not find player");
      }

   }
}
