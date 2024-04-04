package necesse.entity.particle.fireworks;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class FireworksExplosion {
   public static FireworksRocketParticle.ParticleGetter<FireworksPath> popPath = (var0, var1, var2) -> {
      float var3 = var2.floatGaussian() * 10.0F;
      float var4 = var2.floatGaussian() * 10.0F;
      float var5 = 0.25F;
      return new FireworksPath(var3, var4 * (1.0F - var5), var4 * var5);
   };
   public static FireworksExplosion simplePopExplosion;
   public int particles = 300;
   public int lifetime = 2000;
   public int minSize = 10;
   public int maxSize = 18;
   public float particleLightHue;
   public float particleLightSaturation;
   public float trailChance;
   public float trailSize;
   public int trailFadeTime;
   public FireworksRocketParticle.ParticleGetter<FireworksPath> pathGetter;
   public FireworksRocketParticle.ParticleGetter<Color> colorGetter;
   public float popChance;
   public FireworksExplosion popOptions;
   public FireworksRocketParticle.RandomSoundPlayer explosionSound;

   public FireworksExplosion(FireworksRocketParticle.ParticleGetter<FireworksPath> var1) {
      this.particleLightHue = ParticleOption.defaultFlameHue;
      this.particleLightSaturation = 0.7F;
      this.trailChance = 0.2F;
      this.trailSize = 5.0F;
      this.trailFadeTime = 500;
      this.colorGetter = (var0, var1x, var2) -> {
         return Color.getHSBColor(var2.nextFloat(), 1.0F, 1.0F);
      };
      this.popChance = 0.5F;
      this.popOptions = simplePopExplosion;
      this.explosionSound = (var0, var1x, var2) -> {
         Screen.playSound(GameResources.fireworkExplosion, SoundEffect.effect(var0.x, var0.y).pitch((Float)var2.getOneOf((Object[])(0.95F, 1.0F, 1.05F))).volume(Math.max(0.0F, 1.0F - var1x / 1000.0F) * 1.5F).falloffDistance(2000));
      };
      this.pathGetter = var1;
   }

   public void spawnExplosion(Level var1, float var2, float var3, float var4, GameRandom var5) {
      if (this.explosionSound != null) {
         this.explosionSound.play(new Point2D.Float(var2, var3), var4, var5);
      }

      for(int var6 = 0; var6 <= this.particles; ++var6) {
         float var7 = (float)var6 / (float)this.particles;
         FireworksPath var8 = (FireworksPath)this.pathGetter.get(var6, var7, var5);
         Color var9 = (Color)this.colorGetter.get(var6, var7, var5);
         Trail var10;
         if (var5.getChance(this.trailChance)) {
            var10 = new Trail(new TrailVector(var2, var3, var2, var3, this.trailSize, var4), var1, var9, this.trailFadeTime);
            var1.entityManager.addTrail(var10);
         } else {
            var10 = null;
         }

         ParticleOption var11 = var1.entityManager.addParticle(var2, var3, (Particle.GType)null).moves((var3x, var4x, var5x, var6x, var7x) -> {
            float var8x = var8.delta.get(var5x, var6x, var7x);
            var3x.x = var2 + var8.dx * var8x;
            var3x.y = var3 + var8.dy * var8x;
         }).height((var2x, var3x, var4x) -> {
            float var5 = var8.delta.get(var2x, var3x, var4x);
            return var4 + var8.dh * var5;
         }).color(var9).rotates().sizeFades(this.minSize, this.maxSize).givesLight(this.particleLightHue, this.particleLightSaturation).rotates(200.0F, 300.0F).lifeTime(this.lifetime);
         if (var5.getChance(this.popChance) && this.popOptions != null) {
            var11.onProgress(var5.getFloatBetween(0.65F, 0.95F), (var4x) -> {
               float var5x = var11.getCurrentHeight();
               this.popOptions.spawnExplosion(var1, var4x.x, var4x.y, var5x, var5);
            });
         }

         if (var10 != null) {
            var11.onMoveTick((var5x, var6x, var7x, var8x) -> {
               Point2D.Float var9 = var11.getLevelPos();
               var10.addPoint(new TrailVector(var9.x, var9.y, var2, var3, this.trailSize * Math.abs(var8x - 1.0F), var11.getCurrentHeight()), 0);
            });
         }
      }

   }

   static {
      simplePopExplosion = new FireworksExplosion(popPath);
      simplePopExplosion.particles = 1;
      simplePopExplosion.lifetime = 300;
      simplePopExplosion.minSize = 6;
      simplePopExplosion.maxSize = 10;
      simplePopExplosion.trailChance = 0.0F;
      simplePopExplosion.popChance = 0.0F;
      simplePopExplosion.colorGetter = (var0, var1, var2) -> {
         return ParticleOption.randomFlameColor(var2);
      };
      simplePopExplosion.explosionSound = null;
      simplePopExplosion.explosionSound = (var0, var1, var2) -> {
         if (var2.getChance(0.2F)) {
            Screen.playSound(GameResources.fireworkCrack, SoundEffect.effect(var0.x, var0.y).pitch((Float)var2.getOneOf((Object[])(1.0F, 1.2F, 1.4F))).volume(Math.max(0.0F, 1.0F - var1 / 1000.0F) / 4.0F).falloffDistance(2500));
         }

      };
   }
}
