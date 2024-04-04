package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class PirateEscapeBuff extends Buff {
   public PirateEscapeBuff() {
      this.shouldSave = false;
      this.isVisible = false;
      this.isPassive = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SPEED_FLAT, 20.0F);
   }
}
