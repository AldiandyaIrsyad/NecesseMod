package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class TremorsIncomingBuff extends Buff {
   public TremorsIncomingBuff() {
      this.isImportant = true;
      this.canCancel = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }
}
