package necesse.entity.objectEntity;

import necesse.entity.levelEvent.SpikeTrapEvent;
import necesse.level.maps.Level;

public class SpikeTrapObjectEntity extends TrapObjectEntity {
   public SpikeTrapObjectEntity(Level var1, int var2, int var3, int var4) {
      super(var1, var2, var3, (long)var4);
      this.shouldSave = false;
   }

   public void triggerTrap(int var1, int var2) {
      if (!this.isClient() && !this.onCooldown()) {
         if (!this.otherWireActive(var1)) {
            SpikeTrapEvent var3 = new SpikeTrapEvent(this.getX(), this.getY());
            this.getLevel().entityManager.addLevelEvent(var3);
            this.sendClientTriggerPacket();
            this.startCooldown();
         }
      }
   }

   public void onClientTrigger() {
      super.onClientTrigger();
      this.startCooldown();
   }
}
