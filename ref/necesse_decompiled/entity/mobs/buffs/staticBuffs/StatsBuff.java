package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class StatsBuff extends Buff {
   public StatsBuff() {
      this.canCancel = false;
      this.isVisible = false;
      this.isPassive = true;
      this.shouldSave = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }
}
