package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class BloodPlateSetBonusBuff extends SetBonusBuff {
   public BloodPlateSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "bloodplateset"), 400);
      return var3;
   }
}
