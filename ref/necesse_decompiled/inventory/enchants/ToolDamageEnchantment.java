package necesse.inventory.enchants;

import necesse.engine.modifiers.ModifierValue;

public class ToolDamageEnchantment extends ToolItemEnchantment {
   public static final ToolDamageEnchantment noEnchant = new ToolDamageEnchantment(0, new ModifierValue[0]);

   public ToolDamageEnchantment(int var1, ModifierValue<?>... var2) {
      super(ToolItemModifiers.LIST, var1, var2);
   }

   static {
      noEnchant.idData.setData(0, "noenchant");
   }
}
