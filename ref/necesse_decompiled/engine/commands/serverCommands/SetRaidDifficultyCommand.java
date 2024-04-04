package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SetRaidDifficultyCommand extends ModularChatCommand {
   public SetRaidDifficultyCommand() {
      super("setraiddifficulty", "Set the next raid difficulty of the settlement at players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("percent", new IntParameterHandler(100)));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      int var8 = (Integer)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         Level var9 = var7.getLevel();
         SettlementLevelData var10 = SettlementLevelData.getSettlementData(var9);
         if (var10 == null) {
            var6.add(var7.getName() + " is not at a location with a settlement");
         } else {
            int var11 = GameMath.limit(var8, 50, 150);
            if (var8 != var11) {
               var6.add("Limited difficulty to " + var11 + "%");
            }

            float var12 = (float)var11 / 100.0F;
            var10.setRaidDifficultyMod(var12);
            String var13 = var9.settlementLayer.getSettlementName().translate() + " (Owner: " + var9.settlementLayer.getOwnerName() + ")";
            var6.add("Set raid difficulty modifier to " + (int)(var10.getNextRaidDifficultyMod() * 100.0F) + "% at " + var13);
         }

      }
   }
}
