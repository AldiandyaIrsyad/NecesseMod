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
import necesse.engine.team.TeamManager;

public class GetTeamServerCommand extends ModularChatCommand {
   public GetTeamServerCommand() {
      super("getteam", "Gets the current team of the player", PermissionLevel.ADMIN, false, new CmdParameter("player", new StoredPlayerParameterHandler()));
   }

   public boolean autocompleteOnServer() {
      return true;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      StoredPlayerParameterHandler.StoredPlayer var7 = (StoredPlayerParameterHandler.StoredPlayer)var4[0];
      if (var7 != null) {
         ServerClient var8 = var2.getClientByAuth(var7.authentication);
         if (var8 != null) {
            PlayerTeam var9 = var8.getPlayerTeam();
            if (var9 == null) {
               var6.add(var8.getName() + " is not part of any team");
            } else {
               var6.add(var8.getName() + " is part of team: " + var9.getName() + " (ID " + var9.teamID + ")");
            }
         } else {
            TeamManager var11 = var2.world.getTeams();
            PlayerTeam var10 = var11.getPlayerTeam(var7.authentication);
            if (var10 != null) {
               var6.add(var7.name + " is part of team: " + var10.getName() + " (ID " + var10.teamID + ")");
            } else {
               var6.add(var7.name + " is not part of any team");
            }
         }
      } else {
         var6.add("Could not find player");
      }

   }
}
