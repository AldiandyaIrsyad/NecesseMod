package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class HumanAngryBuff extends Buff {
   public HumanAngryBuff() {
      this.shouldSave = false;
      this.isVisible = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SPEED_FLAT, 10.0F);
   }
}
