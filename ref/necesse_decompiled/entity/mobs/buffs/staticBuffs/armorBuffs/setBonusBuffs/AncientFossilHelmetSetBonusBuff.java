package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class AncientFossilHelmetSetBonusBuff extends AncientFossilSetBonusBuff implements BuffAbility {
   public IntUpgradeValue maxResilience = (new IntUpgradeValue()).setBaseValue(10).setUpgradedValue(1.0F, 10);

   public AncientFossilHelmetSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      super.init(var1, var2);
      var1.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(this.getUpgradeTier(var1)));
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
