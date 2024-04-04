package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class HiddenCooldownBuff extends Buff {
   public HiddenCooldownBuff() {
      this.canCancel = false;
      this.isVisible = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }
}
