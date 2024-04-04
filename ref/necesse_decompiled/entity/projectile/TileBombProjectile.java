package necesse.entity.projectile;

import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class TileBombProjectile extends BombProjectile {
   public TileBombProjectile() {
   }

   public TileBombProjectile(float var1, float var2, float var3, float var4, int var5, int var6, GameDamage var7, Mob var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public int getFuseTime() {
      return 3000;
   }

   public float getParticleAngle() {
      return 290.0F;
   }

   public float getParticleDistance() {
      return 12.0F;
   }

   public ExplosionEvent getExplosionEvent(float var1, float var2) {
      int var3 = Math.max(1, this.getOwnerToolTier());
      return new BombExplosionEvent(var1, var2, 120, new GameDamage(200.0F, 1000.0F), false, true, var3, this.getOwner());
   }
}
