package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

public class WebbedGunBulletProjectile extends BulletProjectile {
   public WebbedGunBulletProjectile() {
   }

   public WebbedGunBulletProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void init() {
      super.init();
      this.particleSpeedMod = 0.03F;
   }

   public void onMaxMoveTick() {
      if (this.isClient()) {
         this.spawnSpinningParticle();
      }

   }

   public Color getParticleColor() {
      return new Color(48, 47, 50);
   }

   protected Color getWallHitColor() {
      return this.getParticleColor();
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(48, 47, 50), 22.0F, 100, this.getHeight());
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   public void refreshParticleLight() {
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.getParticleColor(), this.lightSaturation);
   }
}
