package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class StartRaidCommand extends ModularChatCommand {
   public StartRaidCommand() {
      super("startraid", "Starts a raid at the players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("direction", new EnumParameterHandler(SettlementRaidLevelEvent.RaidDir.values()), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      SettlementRaidLevelEvent.RaidDir var8 = (SettlementRaidLevelEvent.RaidDir)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         if (var8 == null) {
            var8 = (SettlementRaidLevelEvent.RaidDir)GameRandom.globalRandom.getOneOf((Object[])SettlementRaidLevelEvent.RaidDir.values());
         }

         Level var9 = var7.getLevel();
         SettlementLevelData var10 = SettlementLevelData.getSettlementData(var9);
         if (var10 == null) {
            var6.add(var7.getName() + " is not at a location with a settlement");
         } else {
            String var11 = var9.settlementLayer.getSettlementName().translate() + " (Owner: " + var9.settlementLayer.getOwnerName() + ")";
            if (var10.spawnRaid(var8)) {
               var6.add("Spawned a raid at " + var11 + " from " + var8.displayName.translate());
            } else {
               var6.add("Could not spawn a raid at " + var11);
               var6.add("This is usually because a criteria is not met, like a minimum of 3 or more settlers");
            }

         }
      }
   }
}
