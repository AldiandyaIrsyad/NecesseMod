package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class AlchemicalInterferenceBuff extends Buff {
   public AlchemicalInterferenceBuff() {
      this.isImportant = true;
      this.canCancel = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 5.0), var2.y + 23.0F + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(new Color(33, 89, 15)).height(46.0F).fadesAlphaTimeToCustomAlpha(50, 50, 0.55F).size(new ParticleOption.DrawModifier() {
            public void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4) {
               var1.size(10, 10);
            }
         }).lifeTime(500);
      }

   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return false;
   }
}
