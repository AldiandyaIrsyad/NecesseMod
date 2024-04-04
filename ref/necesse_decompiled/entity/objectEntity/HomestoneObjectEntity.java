package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HomestoneObjectEntity extends ObjectEntity {
   public HomestoneObjectEntity(Level var1, int var2, int var3) {
      super(var1, "homestone", var2, var3);
   }

   public void serverTick() {
      if (this.getLevel().tickManager().getTick() == 1) {
         SettlementLevelData var1 = SettlementLevelData.getSettlementData(this.getLevel());
         if (var1 != null) {
            Point var2 = var1.getHomestonePos();
            if (var2 == null) {
               var1.setHomestonePos(new Point(this.getX(), this.getY()));
            }
         }
      }

   }
}
