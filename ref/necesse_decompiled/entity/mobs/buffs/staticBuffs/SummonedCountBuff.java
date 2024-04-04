package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class SummonedCountBuff extends Buff {
   public SummonedCountBuff() {
      this.canCancel = true;
      this.isVisible = true;
      this.isPassive = true;
      this.overrideSync = true;
      this.shouldSave = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public int getStackSize() {
      return 999;
   }

   public boolean overridesStackDuration() {
      return true;
   }
}
