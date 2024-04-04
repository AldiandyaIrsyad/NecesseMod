package necesse.level.maps.levelData.settlementData;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.friendly.human.HumanMob;

public class TravelingHumanArrive {
   public final TravelingHumanOdds odds;
   public final HumanMob mob;

   public TravelingHumanArrive(TravelingHumanOdds var1, HumanMob var2) {
      this.odds = var1;
      this.mob = var2;
   }

   public GameMessage getArriveMessage(SettlementLevelData var1) {
      return new LocalMessage("settlement", "travelingarrive", new Object[]{"mob", this.mob.getDisplayName(), "settlement", var1.getLevel().settlementLayer.getSettlementName()});
   }
}
