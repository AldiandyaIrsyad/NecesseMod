package necesse.entity.particle.fireworks;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class FireworksRocketParticle extends Particle {
   public GameRandom random;
   protected float currentHeight;
   public float heightTrajectory;
   public float height;
   public FireworksExplosion explosion;

   public FireworksRocketParticle(Level var1, float var2, float var3, long var4, int var6, FireworksExplosion var7, GameRandom var8) {
      super(var1, var2, var3, var4);
      this.heightTrajectory = 0.5F;
      this.explosion = var7;
      this.height = (float)var6;
      this.random = var8;
      this.dx = var8.floatGaussian() * 10.0F;
      this.dy = var8.floatGaussian() * 10.0F;
      this.friction = 0.3F;
   }

   public FireworksRocketParticle(Level var1, float var2, float var3, long var4, int var6, ParticleGetter<FireworksPath> var7, GameRandom var8) {
      this(var1, var2, var3, var4, var6, new FireworksExplosion(var7), var8);
   }

   public void init() {
      super.init();
      Screen.playSound(GameResources.fireworkFuse, SoundEffect.effect(this.x, this.y).pitch((Float)this.random.getOneOf((Object[])(0.95F, 1.0F, 1.05F))).volume(0.7F));
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      this.currentHeight = (float)Math.pow((double)this.getLifeCyclePercent(), (double)this.heightTrajectory) * this.height;
   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().entityManager.addParticle(this.x, this.y, (Particle.GType)null).height(this.currentHeight).flameColor().givesLight(ParticleOption.defaultFlameHue, 0.7F);
   }

   public void remove() {
      if (!this.removed()) {
         this.explosion.spawnExplosion(this.getLevel(), this.x, this.y, this.currentHeight, this.random);
      }

      super.remove();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public interface ParticleGetter<T> {
      T get(int var1, float var2, GameRandom var3);
   }

   public interface ExplosionModifier {
      void play(FireworksExplosion var1, float var2, GameRandom var3);
   }

   public interface RandomSoundPlayer {
      void play(Point2D.Float var1, float var2, GameRandom var3);
   }
}
