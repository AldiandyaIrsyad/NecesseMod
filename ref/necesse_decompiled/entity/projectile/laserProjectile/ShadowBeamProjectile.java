package necesse.entity.projectile.laserProjectile;

import java.awt.Color;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;

public class ShadowBeamProjectile extends LaserProjectile {
   public ShadowBeamProjectile() {
   }

   public ShadowBeamProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.distance = var7;
      this.setDamage(var8);
      this.knockback = var9;
   }

   public void init() {
      super.init();
      this.setWidth(10.0F);
      this.givesLight = true;
      this.height = 18.0F;
      this.bouncing = 1000;
      this.piercing = 1000;
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 3;
   }

   public Color getParticleColor() {
      return new Color(45, 47, 55);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(45, 47, 55), 15.0F, 500, 18.0F);
   }
}
