package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class SnowCoveredSlowBuff extends Buff {
   public SnowCoveredSlowBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SLOW, 0.1F * (float)var1.getStacks());
   }

   public void onStacksUpdated(ActiveBuff var1) {
      super.onStacksUpdated(var1);
      var1.setModifier(BuffModifiers.SLOW, 0.1F * (float)var1.getStacks());
   }

   public int getStackSize() {
      return 5;
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 5.0), var2.y + 23.0F + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particles.sprite(0, 0, 8)).color(new Color(227, 241, 240)).height(46.0F).fadesAlphaTimeToCustomAlpha(100, 500, 0.35F).size(new ParticleOption.DrawModifier() {
            public void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4) {
               var1.size(10, 10);
            }
         }).lifeTime(1000);
      }

   }
}
