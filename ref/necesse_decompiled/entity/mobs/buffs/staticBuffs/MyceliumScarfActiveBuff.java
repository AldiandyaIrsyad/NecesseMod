package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class MyceliumScarfActiveBuff extends Buff {
   public MyceliumScarfActiveBuff() {
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SPEED, 0.005F);
   }

   public int getStackSize() {
      return 100;
   }

   public boolean overridesStackDuration() {
      return true;
   }

   public boolean showsFirstStackDurationText() {
      return super.showsFirstStackDurationText();
   }
}
