package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.Screen;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.hostile.bosses.MoonlightDancerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class CrushingDarknessBuff extends Buff {
   public CrushingDarknessBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void clientTick(ActiveBuff var1) {
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         GameRandom var3 = GameRandom.globalRandom;
         AtomicReference var4 = new AtomicReference(var3.nextFloat() * 360.0F);
         float var5 = 75.0F;

         for(int var6 = 0; var6 < 4; ++var6) {
            var2.getLevel().entityManager.addParticle(var2.x + GameMath.sin((Float)var4.get()) * var5 + (float)var3.getIntBetween(-5, 5), var2.y + GameMath.cos((Float)var4.get()) * var5 + (float)var3.getIntBetween(-5, 5) * 0.85F, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(var3.getIntBetween(0, 4), 0, 12)).height(0.0F).moves((var3x, var4x, var5x, var6x, var7) -> {
               float var8 = (Float)var4.accumulateAndGet(var4x * 30.0F / 250.0F, Float::sum);
               float var9 = (var5 - 20.0F) * 0.85F;
               var3x.x = var2.x + GameMath.sin(var8) * (var5 - var5 / 2.0F * var7);
               var3x.y = var2.y + GameMath.cos(var8) * var9 - 20.0F * var7;
            }).color((var0, var1x, var2x, var3x) -> {
               var0.color(new Color(0, 0, 0));
               if (var3x > 0.5F) {
                  var0.alpha(2.0F * (1.0F - var3x));
               }

            }).size((var0, var1x, var2x, var3x) -> {
               var0.size(22, 22);
            }).lifeTime(1000);
         }
      }

   }

   public void onRemoved(ActiveBuff var1) {
      super.onRemoved(var1);
      this.inflictDamage(var1);
   }

   private void inflictDamage(ActiveBuff var1) {
      int var2 = 0;
      if (var1.owner.buffManager.hasBuff(BuffRegistry.STAR_BARRIER_BUFF)) {
         if (var1.owner.isClient()) {
            this.spawnBrokenBarrierParticles(var1);
            Screen.playSound(GameResources.shatter2, SoundEffect.effect(var1.owner).volume(3.0F).pitch(0.8F));
         } else {
            var2 = var1.owner.buffManager.getStacks(BuffRegistry.STAR_BARRIER_BUFF);
            var1.owner.buffManager.removeBuff(BuffRegistry.STAR_BARRIER_BUFF, true);
         }
      }

      if (var1.owner.isClient()) {
         Screen.playSound(GameResources.magicroar, SoundEffect.effect(var1.owner).volume(5.0F).pitch(1.75F));
         this.spawnDarknessDamageParticles(var1);
      } else {
         GameDamage var3 = MoonlightDancerMob.crushingDarknessDamage.modDamage(1.0F - (float)var2 * 0.25F);
         int var4 = var1.getGndData().getInt("uniqueID");
         Mob var5 = (Mob)var1.owner.getLevel().entityManager.mobs.get(var4, false);
         if (var5 != null) {
            var1.owner.isServerHit(var3, 0.0F, 0.0F, 0.0F, var5);
         }
      }

   }

   private void spawnDarknessDamageParticles(ActiveBuff var1) {
      Mob var2 = var1.owner;
      GameRandom var3 = GameRandom.globalRandom;
      int var4 = var3.nextInt(360);

      for(int var5 = 0; var5 < 10; ++var5) {
         for(int var6 = 0; var6 < 4; ++var6) {
            var2.getLevel().entityManager.addTopParticle((Entity)var2, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(var3.nextInt(5), 0, 12)).sizeFades(30, 40).rotates().movesFrictionAngle((float)(var4 + var3.getIntBetween(-10, 10) + var6 * 90), (float)((var6 % 2 == 0 ? 100 : 50) + var3.getIntBetween(-50, 20)), 0.8F).color(new Color(0, 0, 0)).givesLight().lifeTime(1000);
         }
      }

   }

   private void spawnBrokenBarrierParticles(ActiveBuff var1) {
      byte var2 = 25;
      Mob var3 = var1.owner;
      GameRandom var4 = GameRandom.globalRandom;
      ParticleTypeSwitcher var5 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      float var6 = 360.0F / (float)var2;

      for(int var7 = 0; var7 < var2; ++var7) {
         int var8 = (int)((float)var7 * var6 + var4.nextFloat() * var6);
         float var9 = (float)Math.sin(Math.toRadians((double)var8)) * 50.0F;
         float var10 = (float)Math.cos(Math.toRadians((double)var8)) * 50.0F;
         var3.getLevel().entityManager.addParticle((Entity)var3, var5.next()).sprite(GameResources.magicSparkParticles.sprite(var4.nextInt(4), 0, 22)).sizeFades(22, 44).movesFriction(var9 * 2.0F, var10 * 2.0F, 0.8F).color(new Color(184, 174, 255)).givesLight(247.0F, 0.3F).heightMoves(0.0F, 30.0F).lifeTime(1500);
      }

   }
}
