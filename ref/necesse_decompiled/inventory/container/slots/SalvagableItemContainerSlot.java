package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.upgradeUtils.SalvageableItem;

public class SalvagableItemContainerSlot extends ContainerSlot {
   public SalvagableItemContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else {
         return var1.item instanceof SalvageableItem ? ((SalvageableItem)var1.item).getCanBeSalvagedError(var1) : Localization.translate("ui", "itemnotsalvageable");
      }
   }
}
