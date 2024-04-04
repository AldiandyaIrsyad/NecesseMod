package necesse.entity.projectile;

import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class DynamiteStickProjectile extends BombProjectile {
   public DynamiteStickProjectile() {
   }

   public DynamiteStickProjectile(float var1, float var2, float var3, float var4, int var5, int var6, GameDamage var7, Mob var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void init() {
      super.init();
      this.stopsRotatingOnStationary = true;
   }

   public int getFuseTime() {
      return 4000;
   }

   public float getParticleAngle() {
      return 220.0F;
   }

   public float getParticleDistance() {
      return 14.0F;
   }

   public ExplosionEvent getExplosionEvent(float var1, float var2) {
      int var3 = Math.max(2, this.getOwnerToolTier() + 1);
      return new BombExplosionEvent(var1, var2, 200, new GameDamage(400.0F, 1000.0F), true, var3, this.getOwner());
   }
}
