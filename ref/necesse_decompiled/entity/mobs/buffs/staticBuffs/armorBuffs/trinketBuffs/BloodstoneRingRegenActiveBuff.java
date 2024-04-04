package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class BloodstoneRingRegenActiveBuff extends Buff {
   public BloodstoneRingRegenActiveBuff() {
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.COMBAT_REGEN_FLAT, 4.0F);
   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return false;
   }
}
