package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class BloodClawStacksBuff extends Buff {
   public BloodClawStacksBuff() {
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public int getStackSize() {
      return 10;
   }

   public boolean overridesStackDuration() {
      return true;
   }
}
