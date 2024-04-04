package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class BouncingBulletProjectile extends BulletProjectile {
   public BouncingBulletProjectile() {
   }

   public BouncingBulletProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void init() {
      super.init();
      this.bouncing = 20;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (var1 == null) {
         int var5 = (new GameRandom((long)(this.getUniqueID() + this.bounced * 1337))).getIntBetween(-5, 5);
         this.setAngle(this.getAngle() + (float)var5);
      }

   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(56, 53, 172), 22.0F, 100, this.getHeight());
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   protected Color getWallHitColor() {
      return new Color(56, 53, 172);
   }

   public void refreshParticleLight() {
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 220.0F, this.lightSaturation);
   }
}
