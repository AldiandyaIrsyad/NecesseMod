package necesse.engine.expeditions;

import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public abstract class FishingTripExpedition extends SettlerExpedition {
   public FishingTripExpedition() {
   }

   public float getSuccessChance(SettlementLevelData var1, HumanMob var2) {
      return 1.0F;
   }
}
