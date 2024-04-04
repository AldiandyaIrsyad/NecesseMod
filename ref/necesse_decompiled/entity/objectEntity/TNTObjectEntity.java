package necesse.entity.objectEntity;

import necesse.entity.levelEvent.explosionEvent.TNTExplosionEvent;
import necesse.level.maps.Level;

public class TNTObjectEntity extends ObjectEntity {
   public boolean exploded;

   public TNTObjectEntity(Level var1, int var2, int var3) {
      super(var1, "tnt", var2, var3);
      this.shouldSave = false;
      this.exploded = false;
   }

   public boolean shouldRequestPacket() {
      return false;
   }

   public void explode() {
      if (!this.exploded && !this.isClient()) {
         TNTExplosionEvent var1 = new TNTExplosionEvent((float)this.getX(), (float)this.getY());
         this.getLevel().entityManager.addLevelEvent(var1);
         this.exploded = true;
      }
   }
}
