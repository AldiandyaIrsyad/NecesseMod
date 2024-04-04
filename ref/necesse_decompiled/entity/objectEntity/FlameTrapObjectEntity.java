package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.entity.levelEvent.FlameTrapEvent;
import necesse.level.maps.Level;

public class FlameTrapObjectEntity extends TrapObjectEntity {
   public FlameTrapObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 1000L);
      this.shouldSave = false;
   }

   public void triggerTrap(int var1, int var2) {
      if (!this.isClient() && !this.onCooldown()) {
         if (!this.otherWireActive(var1)) {
            Point var3 = this.getPos(this.getX(), this.getY(), var2);
            FlameTrapEvent var4 = new FlameTrapEvent(var3.x, var3.y, var2);
            this.getLevel().entityManager.addLevelEvent(var4);
            this.startCooldown();
         }
      }
   }
}
