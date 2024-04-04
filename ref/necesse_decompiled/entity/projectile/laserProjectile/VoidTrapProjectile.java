package necesse.entity.projectile.laserProjectile;

import java.awt.Color;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.trails.Trail;

public class VoidTrapProjectile extends LaserProjectile {
   public VoidTrapProjectile() {
   }

   public VoidTrapProjectile(float var1, float var2, float var3, float var4, GameDamage var5, Mob var6) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.setDamage(var5);
      this.setOwner(var6);
      this.setDistance(250);
   }

   public void init() {
      super.init();
      this.width = 20.0F;
      this.givesLight = true;
      this.height = 18.0F;
      this.piercing = 1000;
      this.clientHandlesHit = false;
      this.canBreakObjects = true;
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 3;
   }

   public Color getParticleColor() {
      return new Color(50, 0, 102);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(50, 0, 102), 20.0F, 500, this.getHeight());
   }
}
