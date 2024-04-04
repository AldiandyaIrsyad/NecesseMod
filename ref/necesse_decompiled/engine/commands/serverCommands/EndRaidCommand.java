package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.Level;

public class EndRaidCommand extends ModularChatCommand {
   public EndRaidCommand() {
      super("endraid", "Ends a raid at the players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         Level var8 = var7.getLevel();
         boolean var9 = false;
         Iterator var10 = var8.entityManager.getLevelEvents().iterator();

         while(var10.hasNext()) {
            LevelEvent var11 = (LevelEvent)var10.next();
            if (var11 instanceof SettlementRaidLevelEvent && !var11.isOver()) {
               var11.over();
               var9 = true;
            }
         }

         if (var9) {
            var6.add("Ended raid events at " + var7.getName() + "'s location");
         } else {
            var6.add("Could not find any raid events at " + var7.getName() + "'s location");
         }

      }
   }
}
