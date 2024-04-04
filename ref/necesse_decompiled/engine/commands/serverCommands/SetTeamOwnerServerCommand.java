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

public class SetTeamOwnerServerCommand extends ModularChatCommand {
   public SetTeamOwnerServerCommand() {
      super("setteamowner", "Sets the owner of the team. The new owner must be part of the team already", PermissionLevel.ADMIN, false, new CmdParameter("team", new TeamParameterHandler()), new CmdParameter("player", new StoredPlayerParameterHandler()));
   }

   public boolean autocompleteOnServer() {
      return true;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      PlayerTeam var7 = (PlayerTeam)var4[0];
      StoredPlayerParameterHandler.StoredPlayer var8 = (StoredPlayerParameterHandler.StoredPlayer)var4[1];
      if (var7 != null) {
         if (var8 != null) {
            if (var7.isMember(var8.authentication)) {
               if (var7.getOwner() != var8.authentication) {
                  PlayerTeam.changeOwner(var2, var7, var8.authentication);
                  var6.add("Made " + var8.name + " the owner of team: " + var7.getName() + " (ID " + var7.teamID + ")");
               } else {
                  var6.add(var8.name + " is already owner of team: " + var7.getName() + " (ID " + var7.teamID + ")");
               }
            } else {
               var6.add(var8.name + " is not part of team: " + var7.getName() + " (ID " + var7.teamID + ")");
            }
         } else {
            var6.add("Could not find player");
         }
      } else {
         var6.add("Could not find team");
      }

   }
}
