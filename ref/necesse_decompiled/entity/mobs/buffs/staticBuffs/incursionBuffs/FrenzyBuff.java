package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class FrenzyBuff extends Buff {
   public FrenzyBuff() {
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.ATTACK_SPEED, 0.5F);
      var1.setModifier(BuffModifiers.SPEED, 0.5F);
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         Rectangle var3 = var2.getSelectBox();
         double var4 = Math.floor((double)((float)(var3.width + var3.height) / 100.0F));
         double var6 = 1.0;
         if (var4 >= 1.0) {
            var6 = var4;
         }

         for(int var8 = 0; (double)var8 < var6; ++var8) {
            int var9 = GameRandom.globalRandom.getIntBetween(-var3.width / 2, var3.width / 2);
            int var10 = GameRandom.globalRandom.getIntBetween(-var3.height / 2, var3.height / 2);
            float var10001 = var2.x + (float)var9;
            var2.getLevel().entityManager.addParticle(var10001, var2.y - 32.0F + (float)var10, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particles.sprite(0, 0, 8)).color(new Color(182, 39, 20)).fadesAlphaTimeToCustomAlpha(100, 100, 0.65F).size(new ParticleOption.DrawModifier() {
               public void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4) {
                  var1.size(10, 10);
               }
            }).lifeTime(500);
         }
      }

   }
}
