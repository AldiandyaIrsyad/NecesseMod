package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;

public abstract class OutOfCombatBuff extends ToggleActiveBuff {
   public OutOfCombatBuff() {
   }

   protected boolean isNextActive(ActiveBuff var1) {
      return var1.owner.isInCombat() || var1.owner.getWorldEntity() != null && var1.owner.getLastAttackTime() + 2000L > var1.owner.getWorldEntity().getTime();
   }
}
