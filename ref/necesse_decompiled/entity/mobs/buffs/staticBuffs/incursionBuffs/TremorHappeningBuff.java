package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class TremorHappeningBuff extends Buff {
   public TremorHappeningBuff() {
      this.isImportant = true;
      this.canCancel = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SLOW, 0.5F);
   }
}
