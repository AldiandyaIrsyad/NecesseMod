package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class TungstenSetBonusBuff extends SetBonusBuff {
   public IntUpgradeValue maxResilience = (new IntUpgradeValue()).setBaseValue(30).setUpgradedValue(1.0F, 30);

   public TungstenSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(this.getUpgradeTier(var1)));
      var1.setMaxModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, 0.0F);
      var1.setModifier(BuffModifiers.KNOCKBACK_OUT, 0.5F);
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "tungstenset1"));
      var3.add(Localization.translate("itemtooltip", "tungstenset2"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).excludeModifiers(BuffModifiers.KNOCKBACK_OUT).excludeLimits(BuffModifiers.KNOCKBACK_INCOMING_MOD).buildToStatList(var1);
   }
}
