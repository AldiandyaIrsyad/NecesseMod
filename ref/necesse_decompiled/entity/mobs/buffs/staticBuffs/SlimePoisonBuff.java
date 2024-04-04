package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;

public class SlimePoisonBuff extends Buff {
   public SlimePoisonBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      this.tickDamage(var1, true);
      Mob var2 = var1.owner;
      if (var2.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(56, 173, 45)).height(16.0F);
      }

   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      this.tickDamage(var1, true);
   }

   public void tickDamage(ActiveBuff var1, boolean var2) {
      float var3 = (Float)var1.getModifier(BuffModifiers.POISON_DAMAGE_FLAT);
      int var4 = var1.owner.getMaxHealth();
      float var5 = Math.max((float)Math.pow((double)var4, 0.5) / 10.0F + (float)var4 / 50.0F, 5.0F);
      if (var3 != var5) {
         var1.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, var5);
         if (var2) {
            var1.forceManagerUpdate();
         }
      }

   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      this.tickDamage(var1, false);
   }
}
