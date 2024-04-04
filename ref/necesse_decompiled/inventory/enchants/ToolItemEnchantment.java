package necesse.inventory.enchants;

import necesse.engine.modifiers.ModifierList;
import necesse.engine.modifiers.ModifierValue;

public class ToolItemEnchantment extends ItemEnchantment {
   public static final ToolItemEnchantment noEnchant = new ToolItemEnchantment(0, new ModifierValue[0]);

   public ToolItemEnchantment(ModifierList var1, int var2, ModifierValue<?>... var3) {
      super(var1, var2);
      if (var1 != ToolItemModifiers.LIST) {
         throw new IllegalArgumentException("Modifier list must be child of ToolItemModifiers list");
      } else {
         ModifierValue[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ModifierValue var7 = var4[var6];
            var7.apply(this);
         }

      }
   }

   public ToolItemEnchantment(int var1, ModifierValue<?>... var2) {
      this(ToolItemModifiers.LIST, var1, var2);
   }

   static {
      noEnchant.idData.setData(0, "noenchant");
   }
}
