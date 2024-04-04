package necesse.level.gameObject;

import necesse.engine.util.GameRandom;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class WallCandleObject extends WallTorchObject {
   public WallCandleObject() {
   }

   public void loadTextures() {
      this.texture = GameTexture.fromFile("objects/" + this.getStringID());
   }

   public void tickEffect(Level var1, int var2, int var3) {
      if (GameRandom.globalRandom.getChance(40) && this.isActive(var1, var2, var3)) {
         byte var4 = var1.getObjectRotation(var2, var3);
         int var5 = this.getSprite(var1, var2, var3, var4);
         int var6 = 10;
         if (var5 == 0) {
            var6 += 32;
         } else if (var5 == 1 || var5 == 3) {
            var6 += 14;
         }

         BombProjectile.spawnFuseParticle(var1, (float)(var2 * 32 + 16), (float)(var3 * 32 + 16 + 2), (float)var6, this.lightHue, ParticleOption.defaultSmokeHue);
      }

   }
}
