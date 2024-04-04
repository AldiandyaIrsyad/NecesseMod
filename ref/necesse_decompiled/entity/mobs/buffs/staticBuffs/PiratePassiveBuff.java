package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class PiratePassiveBuff extends Buff {
   public PiratePassiveBuff() {
      this.shouldSave = false;
      this.isVisible = false;
      this.isPassive = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, (float)var1.owner.getMaxHealth() / 10.0F);
   }
}
