package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.TrapArrowProjectile;
import necesse.level.maps.Level;

public class ArrowTrapObjectEntity extends TrapObjectEntity {
   public static GameDamage damage = new GameDamage(35.0F, 10.0F, 0.0F, 2.0F, 1.0F);

   public ArrowTrapObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 1000L);
      this.shouldSave = false;
   }

   public void triggerTrap(int var1, int var2) {
      if (!this.isClient() && !this.onCooldown()) {
         if (!this.otherWireActive(var1)) {
            Point var3 = this.getPos(this.getX(), this.getY(), var2);
            Point var4 = this.getDir(var2);
            int var5 = var3.x * 32;
            if (var4.x == 0) {
               var5 += 16;
            } else if (var4.x == -1) {
               var5 += 30;
            } else if (var4.x == 1) {
               var5 += 2;
            }

            int var6 = var3.y * 32;
            if (var4.y == 0) {
               var6 += 16;
            } else if (var4.y == -1) {
               var6 += 30;
            } else if (var4.y == 1) {
               var6 += 2;
            }

            float var10003 = (float)var5;
            float var10004 = (float)var6;
            float var10005 = (float)(var5 + var4.x);
            this.getLevel().entityManager.projectiles.add(new TrapArrowProjectile(var10003, var10004, var10005, (float)(var6 + var4.y), damage, (Mob)null));
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
         return var1 == 3 ? new Point(-1, 0) : new Point();
      }
   }
}
