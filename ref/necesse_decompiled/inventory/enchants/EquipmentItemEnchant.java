package necesse.inventory.enchants;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.buffs.BuffModifiers;

public class EquipmentItemEnchant extends ItemEnchantment {
   public static final EquipmentItemEnchant noEnchant = new EquipmentItemEnchant(0, new ModifierValue[0]);

   public EquipmentItemEnchant(int var1, ModifierValue... var2) {
      super(BuffModifiers.LIST, var1);
      ModifierValue[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModifierValue var6 = var3[var5];
         var6.apply(this);
      }

   }

   static {
      noEnchant.idData.setData(0, "noenchant");
   }
}
