package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.SunlightChampionMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class SupernovaExplosionEvent extends ExplosionEvent implements Attacker {
   private int particleBuffer;
   protected ParticleTypeSwitcher explosionTypeSwitcher;

   public SupernovaExplosionEvent() {
      this(0.0F, 0.0F, 650, SunlightChampionMob.supernovaDamage, 0, (Mob)null);
   }

   public SupernovaExplosionEvent(float var1, float var2, int var3, GameDamage var4, int var5, Mob var6) {
      super(var1, var2, var3, var4, false, var5, var6);
      this.explosionTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
      this.targetRangeMod = 0.0F;
   }

   protected GameDamage getTotalObjectDamage(float var1) {
      return super.getTotalObjectDamage(var1).modDamage(10.0F);
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.5F).pitch(1.5F));
      this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0F, 3.0F, true);
   }

   public float getParticleCount(float var1, float var2) {
      return super.getParticleCount(var1, var2) * 1.5F;
   }

   public void spawnExplosionParticle(float var1, float var2, float var3, float var4, int var5, float var6) {
      if (this.particleBuffer < 10) {
         ++this.particleBuffer;
      } else {
         this.particleBuffer = 0;
         if (var6 <= (float)Math.max(this.range - 125, 25)) {
            float var7 = var3 * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            float var8 = var4 * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8F;
            this.getLevel().entityManager.addParticle(var1, var2, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 100).givesLight(53.0F, 1.0F).movesFriction(var7 * 0.05F, var8 * 0.05F, 0.8F).color((var0, var1x, var2x, var3x) -> {
               float var4 = Math.max(0.0F, Math.min(1.0F, var3x));
               var0.color(new Color((int)(255.0F - 55.0F * var4), (int)(225.0F - 200.0F * var4), (int)(155.0F - 125.0F * var4)));
            }).heightMoves(0.0F, 10.0F).lifeTime(var5 * 3);
         }
      }

   }
}
