package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class ForceOfWindActiveBuff extends Buff {
   public ForceOfWindActiveBuff() {
      this.isVisible = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setMaxModifier(BuffModifiers.FRICTION, 0.01F, 1000);
   }
}
