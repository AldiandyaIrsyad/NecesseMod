package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;

public class AblazeBuff extends Buff {
   public AblazeBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.FIRE_DAMAGE_FLAT, 5.0F);
   }

   public void clientTick(ActiveBuff var1) {
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(226, 57, 0)).givesLight(0.0F, 0.5F).height(16.0F);
      }

   }

   public Attacker getSource(Attacker var1) {
      return new OnFireBuff.FireAttacker(var1);
   }
}
