package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierUpgradeValue;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.inventory.item.ItemStatTip;

public class SimpleUpgradeSetBonusBuff extends SetBonusBuff {
   protected ModifierUpgradeValue<?>[] modifiers;

   public SimpleUpgradeSetBonusBuff(ModifierUpgradeValue<?>... var1) {
      this.modifiers = var1;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      ModifierUpgradeValue[] var3 = this.modifiers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModifierUpgradeValue var6 = var3[var5];
         var1.setModifier(var6.modifier, var6.value.getValue(this.getUpgradeTier(var1)));
         if (var6.limits.hasMin()) {
            var1.setMinModifier(var6.modifier, var6.limits.min().getValue(this.getUpgradeTier(var1)));
         }

         if (var6.limits.hasMax()) {
            var1.setMaxModifier(var6.modifier, var6.limits.max().getValue(this.getUpgradeTier(var1)));
         }
      }

   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      ModifierUpgradeValue[] var4 = this.modifiers;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ModifierUpgradeValue var7 = var4[var6];
         ModifierTooltip var8 = var2.getModifierTooltip(var7.modifier, var3);
         if (var8 != null) {
            var1.add(var8.tip);
         }

         ModifierTooltip var9 = var2.getMinLimitTooltip(var7.modifier, var3, false);
         if (var9 != null) {
            var1.add(var9.tip);
         }

         ModifierTooltip var10 = var2.getMaxLimitTooltip(var7.modifier, var3, false);
         if (var10 != null) {
            var1.add(var10.tip);
         }
      }

   }
}
