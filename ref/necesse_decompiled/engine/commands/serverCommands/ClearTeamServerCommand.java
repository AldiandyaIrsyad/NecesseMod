package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StoredPlayerParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;

public class ClearTeamServerCommand extends ModularChatCommand {
   public ClearTeamServerCommand() {
      super("clearteam", "Removes the player from his current team", PermissionLevel.ADMIN, false, new CmdParameter("player", new StoredPlayerParameterHandler()));
   }

   public boolean autocompleteOnServer() {
      return true;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      StoredPlayerParameterHandler.StoredPlayer var7 = (StoredPlayerParameterHandler.StoredPlayer)var4[0];
      if (var7 != null) {
         PlayerTeam var8 = var2.world.getTeams().getPlayerTeam(var7.authentication);
         if (var8 != null) {
            PlayerTeam.removeMember(var2, var8, var7.authentication, true);
            var6.add("Removed " + var7.name + " from team: " + var8.getName() + " (ID " + var8.teamID + ")");
         } else {
            var6.add(var7.name + " is not part of any team");
         }
      } else {
         var6.add("Could not find player");
      }

   }
}
