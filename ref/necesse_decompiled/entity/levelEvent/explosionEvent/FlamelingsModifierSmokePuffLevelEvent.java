package necesse.entity.levelEvent.explosionEvent;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class FlamelingsModifierSmokePuffLevelEvent extends ExplosionEvent implements Attacker {
   public FlamelingsModifierSmokePuffLevelEvent() {
      this(0.0F, 0.0F, 100, new GameDamage(100.0F), false, 0, (Mob)null);
   }

   public FlamelingsModifierSmokePuffLevelEvent(float var1, float var2, int var3, GameDamage var4, boolean var5, int var6, Mob var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   protected GameDamage getTotalObjectDamage(float var1) {
      return super.getTotalObjectDamage(var1).modDamage(10.0F);
   }

   protected void playExplosionEffects() {
      Screen.playSound(GameResources.swoosh, SoundEffect.effect(this.x, this.y).volume(1.0F));
   }

   public float getParticleCount(float var1, float var2) {
      return super.getParticleCount(var1, var2) * 1.5F;
   }

   protected float getDistanceMod(float var1) {
      return 1.0F;
   }

   public void spawnExplosionParticle(float var1, float var2, float var3, float var4, int var5, float var6) {
      if (GameRandom.globalRandom.getChance(0.5F)) {
         Point2D.Float var7 = GameMath.normalize(var3, var4);
         this.level.entityManager.addParticle(var1 + var7.x * var6, var2 + var7.y * var6, Particle.GType.IMPORTANT_COSMETIC).movesConstant(var3, var4).smokeColor().heightMoves(10.0F, 40.0F).lifeTime(var5);
      }

   }
}
