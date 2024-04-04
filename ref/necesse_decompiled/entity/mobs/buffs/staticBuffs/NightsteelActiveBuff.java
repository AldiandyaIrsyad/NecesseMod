package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.drawOptions.human.HumanDrawOptions;

public class NightsteelActiveBuff extends Buff implements HumanDrawBuff {
   public NightsteelActiveBuff() {
      this.shouldSave = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.UNTARGETABLE, true);
      var1.setModifier(BuffModifiers.INTIMIDATED, true);
      var1.setModifier(BuffModifiers.SPEED, 1.0F);
      var1.setMaxModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, 0.0F);
   }

   public void addHumanDraw(ActiveBuff var1, HumanDrawOptions var2) {
      float var3 = GameUtils.getAnimFloatContinuous((long)var1.getDurationLeft(), 1000);
      var2.alpha(var3);
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.isVisible()) {
         Mob var2 = var1.owner;
         var2.getLevel().entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(var2.dx / 10.0F, var2.dy / 10.0F).color(new Color(108, 37, 92)).givesLight(0.0F, 0.5F).height(16.0F);
      }

   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      if (var1.owner.getLevel().tickManager().isGameTickInSecond(5)) {
         var1.owner.getLevel().entityManager.mobs.stream().forEach((var0) -> {
            var0.ai.blackboard.submitEvent("refreshBossDespawn", new AIEvent());
         });
      }

   }
}
