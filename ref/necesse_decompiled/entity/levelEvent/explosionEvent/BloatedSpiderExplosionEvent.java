package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.BloatedSpiderMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BloatedSpiderExplosionEvent extends ExplosionEvent implements Attacker {
   protected ParticleTypeSwitcher explosionTypeSwitcher;

   public BloatedSpiderExplosionEvent() {
      this(0.0F, 0.0F, 150, BloatedSpiderMob.explosionDamage, false, 0, (Mob)null);
   }

   public BloatedSpiderExplosionEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, int var6, Mob var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
      this.explosionTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
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
      float var7;
      float var8;
      if (GameRandom.globalRandom.getChance(0.7F) && var6 < 25.0F) {
         var7 = var3 * (float)GameRandom.globalRandom.getIntBetween(20, 70);
         var8 = var4 * (float)GameRandom.globalRandom.getIntBetween(10, 60) * 0.8F;
         this.getLevel().entityManager.addParticle(var1, var2, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 80).movesFriction(var7 * 0.05F, var8 * 0.05F, 0.8F).color(new Color(89, 89, 89)).heightMoves(0.0F, 70.0F).lifeTime(var5 * 4);
      }

      if (var6 <= (float)Math.max(this.range - 125, 25)) {
         var7 = var3 * (float)GameRandom.globalRandom.getIntBetween(140, 150);
         var8 = var4 * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8F;
         this.getLevel().entityManager.addParticle(var1, var2, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).movesFriction(var7 * 0.05F, var8 * 0.05F, 0.8F).color(new Color(255, 255, 255)).heightMoves(0.0F, 10.0F).lifeTime(var5 * 3);
      }

      if (GameRandom.globalRandom.getChance(0.5F)) {
         this.level.entityManager.addParticle(var1, var2, this.explosionTypeSwitcher.next()).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(25, 40).movesConstant(var3 * 0.8F, var4 * 0.8F).flameColor(75.0F).height(10.0F).givesLight(75.0F, 0.5F).onProgress(0.4F, (var4x) -> {
            Point2D.Float var5x = GameMath.normalize(var3, var4);
            this.level.entityManager.addParticle(var4x.x + var5x.x * 20.0F, var4x.y + var5x.y * 20.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(var3, var4).smokeColor().heightMoves(10.0F, 40.0F).lifeTime(var5);
         }).lifeTime(var5);
      }

   }
}
