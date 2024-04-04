package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class MinerSettler extends Settler {
   public static double spawnChance = GameMath.getAverageSuccessRuns(120.0);

   public MinerSettler() {
      super("minerhuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.mob_kills.getKills("stonecaveling") > 0 || var2.mob_kills.getKills("snowstonecaveling") > 0 || var2.mob_kills.getKills("swampstonecaveling") > 0 || var2.mob_kills.getKills("sandstonecaveling") > 0;
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "minertip");
   }

   public double getSpawnChance(Server var1, ServerClient var2, Level var3) {
      return !var3.isCave || !var3.isIslandPosition() || var3.getIslandDimension() != -1 && var3.getIslandDimension() != -2 ? 0.0 : spawnChance;
   }

   public void spawnAtClient(Server var1, ServerClient var2, Level var3) {
      if (var2.characterStats().mob_kills.getKills("evilsprotector") > 0) {
         if (!var3.entityManager.mobs.streamInRegionsInTileRange(var2.playerMob.getX() / 32, var2.playerMob.getY() / 32, Settler.SETTLER_SPAWN_AREA.maxSpawnDistance * 2).anyMatch((var1x) -> {
            return var1x.getStringID().equals(this.mobStringID);
         })) {
            MinerHumanMob var4 = (MinerHumanMob)MobRegistry.getMob(this.mobStringID, var3);
            var4.setLost(true);
            Point var5 = this.getSpawnLocation(var2, var3, var4, Settler.SETTLER_SPAWN_AREA);
            if (var5 != null) {
               var3.entityManager.addMob(var4, (float)var5.x, (float)var5.y);
            }

         }
      }
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      if (var2 || !this.doesSettlementHaveThisSettler(var1)) {
         var3.addObject(75, this.getNewRecruitMob(var1));
      }

   }
}
