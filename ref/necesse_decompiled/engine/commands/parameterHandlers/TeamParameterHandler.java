package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.engine.team.TeamManager;

public class TeamParameterHandler extends ParameterHandler<PlayerTeam> {
   public TeamParameterHandler() {
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      if (var2 != null) {
         TeamManager var5 = var2.world.getTeams();
         return autocompleteFromArray((PlayerTeam[])var5.streamTeams().filter(PlayerTeam::hasMembers).toArray((var0) -> {
            return new PlayerTeam[var0];
         }), (var0) -> {
            return true;
         }, PlayerTeam::getName, var4);
      } else {
         return Collections.emptyList();
      }
   }

   public PlayerTeam parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      try {
         int var6 = Integer.parseInt(var4);
         PlayerTeam var7 = var2.world.getTeams().getTeam(var6);
         if (var7 != null) {
            return var7;
         }
      } catch (NumberFormatException var8) {
      }

      PlayerTeam var9 = (PlayerTeam)var2.world.getTeams().streamTeams().filter(PlayerTeam::hasMembers).filter((var1x) -> {
         return var1x.getName().equals(var4);
      }).findFirst().orElse((Object)null);
      if (var9 != null) {
         return var9;
      } else {
         throw new IllegalArgumentException("Could not find player team with name/ID \"" + var4 + "\"");
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      try {
         Integer.parseInt(var4);
         return true;
      } catch (NumberFormatException var7) {
         return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
      }
   }

   public PlayerTeam getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return null;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.getDefault(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      return this.parse(var1, var2, var3, var4, var5);
   }
}
