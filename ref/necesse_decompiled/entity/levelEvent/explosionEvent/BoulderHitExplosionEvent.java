package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.SwampGuardianHead;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BoulderHitExplosionEvent extends ExplosionEvent implements Attacker {
   public BoulderHitExplosionEvent() {
      this(0.0F, 0.0F, (Mob)null);
   }

   public BoulderHitExplosionEvent(float var1, float var2, Mob var3) {
      super(var1, var2, SwampGuardianHead.boulderExplosionRange, SwampGuardianHead.boulderExplosionDamage, false, 0, var3);
      this.sendCustomData = false;
      this.sendOwnerData = true;
      this.hitsOwner = false;
      this.knockback = 50;
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(this.x, this.y).pitch(1.0F).volume(0.1F));
      Screen.playSound(GameResources.punch, SoundEffect.effect(this.x, this.y).pitch(1.0F).volume(0.4F));
   }

   public void spawnExplosionParticle(float var1, float var2, float var3, float var4, int var5, float var6) {
      this.level.entityManager.addParticle(var1, var2, Particle.GType.CRITICAL).movesConstant(var3, var4).color(new Color(50, 50, 50)).lifeTime(var5);
   }
}
