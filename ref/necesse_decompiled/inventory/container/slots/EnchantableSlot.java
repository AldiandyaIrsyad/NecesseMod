package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class EnchantableSlot extends ContainerSlot {
   public EnchantableSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else if (!var1.item.isEnchantable(var1)) {
         String var2 = var1.item.getIsEnchantableError(var1);
         return var2 == null ? "" : var2;
      } else {
         return null;
      }
   }
}
