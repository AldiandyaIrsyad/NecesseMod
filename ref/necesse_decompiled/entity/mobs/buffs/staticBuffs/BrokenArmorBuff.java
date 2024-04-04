package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class BrokenArmorBuff extends Buff {
   public BrokenArmorBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.ARMOR, -0.5F);
   }
}
