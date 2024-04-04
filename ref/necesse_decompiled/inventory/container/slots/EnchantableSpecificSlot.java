package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;

public class EnchantableSpecificSlot extends EnchantableSlot {
   public ItemEnchantment enchantment;

   public EnchantableSpecificSlot(Inventory var1, int var2, ItemEnchantment var3) {
      super(var1, var2);
      this.enchantment = var3;
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (this.enchantment == null) {
         return "";
      } else {
         String var2 = super.getItemInvalidError(var1);
         if (var2 != null) {
            return var2;
         } else {
            return var1 != null && !((Enchantable)var1.item).isValidEnchantment(var1, this.enchantment) ? Localization.translate("ui", "enchantingscrollwrongtype", "item", ItemRegistry.getLocalization(var1.item.getID()), "enchantment", this.enchantment.getLocalization()) : null;
         }
      }
   }
}
