package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;

public class WidowPoisonBuff extends Buff {
   public WidowPoisonBuff() {
      this.shouldSave = false;
      this.isImportant = true;
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      Mob var2 = var1.owner;
      if (var2.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(197, 218, 5)).height(16.0F);
      }

   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, 5.0F);
   }
}
