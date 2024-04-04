package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public class SetRaidTierCommand extends ModularChatCommand {
   public SetRaidTierCommand() {
      super("setraidtier", "Set the next raid tier of the settlement at players current location", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("tier", new PresetStringParameterHandler((String[])GameUtils.concat(SettlementQuestTier.questTiers.stream().map((var0) -> {
         return var0.stringID;
      }), Stream.of("all")).toArray((var0) -> {
         return new String[var0];
      }))));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      String var8 = (String)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         SettlementQuestTier var9 = null;
         Iterator var10 = SettlementQuestTier.questTiers.iterator();

         while(var10.hasNext()) {
            SettlementQuestTier var11 = (SettlementQuestTier)var10.next();
            if (var11.stringID.equalsIgnoreCase(var8)) {
               var9 = var11;
               break;
            }
         }

         Level var14 = var7.getLevel();
         SettlementLevelData var15 = SettlementLevelData.getSettlementData(var14);
         if (var15 == null) {
            var6.add(var7.getName() + " is not at a location with a settlement");
         } else {
            var15.setCurrentQuestTierDebug(var7, var9);
            var15.resetQuestsDebug();
            SettlementQuestTier var12 = var15.getCurrentQuestTier();
            String var13 = var14.settlementLayer.getSettlementName().translate() + " (Owner: " + var14.settlementLayer.getOwnerName() + ")";
            if (var12 != null) {
               var6.add("Set quest tier to " + var12.stringID + " at " + var13);
            } else {
               var6.add("Completed all quests at " + var13);
            }
         }

      }
   }
}
