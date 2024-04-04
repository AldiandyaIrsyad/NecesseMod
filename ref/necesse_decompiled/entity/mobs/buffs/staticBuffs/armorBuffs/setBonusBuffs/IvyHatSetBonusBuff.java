package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class IvyHatSetBonusBuff extends SetBonusBuff {
   public IntUpgradeValue maxMana = (new IntUpgradeValue(0, 0.1F)).setBaseValue(150).setUpgradedValue(1.0F, 250);

   public IvyHatSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(this.getUpgradeTier(var1)));
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
   }

   public void onHasAttacked(ActiveBuff var1, MobWasHitEvent var2) {
      super.onHasAttacked(var1, var2);
      if (!var2.wasPrevented) {
         var2.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.IVY_POISON, var2.target, 5.0F, var2.attacker), var2.target.isServer());
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "ivyhatset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
