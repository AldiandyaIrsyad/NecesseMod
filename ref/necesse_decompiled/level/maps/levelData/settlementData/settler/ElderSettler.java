package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.playerStats.PlayerStats;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ElderSettler extends Settler {
   public ElderSettler() {
      super("elderhuman");
   }

   public float getArriveAsRecruitAfterDeathChance(SettlementLevelData var1) {
      return 0.0F;
   }

   public boolean canSpawnInSettlement(SettlementLevelData var1, PlayerStats var2) {
      return var1.countSettlersWithoutBed() < 2 && !var1.hasSettler(this);
   }

   public boolean canMoveOut(LevelSettler var1, SettlementLevelData var2) {
      return false;
   }

   public boolean canBanish(LevelSettler var1, SettlementLevelData var2) {
      return false;
   }

   public SettlerMob getNewSettlerMob(Level var1) {
      return (SettlerMob)var1.entityManager.mobs.stream().filter((var1x) -> {
         return var1x.getStringID().equals(this.mobStringID);
      }).map((var0) -> {
         return (SettlerMob)var0;
      }).findFirst().filter((var0) -> {
         return !var0.isSettler();
      }).orElseGet(() -> {
         return super.getNewSettlerMob(var1);
      });
   }
}
