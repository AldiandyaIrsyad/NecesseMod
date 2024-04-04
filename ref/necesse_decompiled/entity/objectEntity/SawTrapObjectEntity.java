package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.summon.SawBladeMob;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;

public class SawTrapObjectEntity extends TrapObjectEntity {
   public SawTrapObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 900L);
      this.shouldSave = false;
   }

   public void triggerTrap(int var1, int var2) {
      if (!this.isClient() && !this.onCooldown()) {
         Point var3 = this.getDir(var2);
         GameObject var4 = this.getLevel().getObject(this.getTileX() + var3.x, this.getTileY() + var3.y);
         if (var4 instanceof TrapTrackObject) {
            Mob var5 = MobRegistry.getMob("sawblade", this.getLevel());
            var5.setFacingDir((float)var3.x, (float)var3.y);
            ((SawBladeMob)var5).sawDir = var2;
            ((SawBladeMob)var5).sawSpeed = 10.0F;
            this.getLevel().entityManager.addMob(var5, (float)((this.getTileX() + var3.x) * 32 + 16), (float)((this.getTileY() + var3.y) * 32 + 16));
            this.sendClientTriggerPacket();
            this.startCooldown();
         }

      }
   }

   public Point getDir(int var1) {
      if (var1 == 0) {
         return new Point(0, -1);
      } else if (var1 == 1) {
         return new Point(1, 0);
      } else if (var1 == 2) {
         return new Point(0, 1);
      } else {
         return var1 == 3 ? new Point(-1, 0) : new Point(0, 0);
      }
   }

   public void onClientTrigger() {
      super.onClientTrigger();
      this.startCooldown();
   }
}
