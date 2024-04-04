package necesse.inventory.item.bulletItem;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;

public class SimpleBulletItem extends BulletItem {
   public SimpleBulletItem() {
      super(1000);
      this.damage = 5;
   }

   public Projectile getProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return new HandGunBulletProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
