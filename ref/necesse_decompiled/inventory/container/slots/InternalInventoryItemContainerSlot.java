package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class InternalInventoryItemContainerSlot extends ContainerSlot {
   private InternalInventoryItemInterface internalInventoryItemInterface;

   public InternalInventoryItemContainerSlot(Inventory var1, int var2, InternalInventoryItemInterface var3) {
      super(var1, var2);
      this.internalInventoryItemInterface = var3;
   }

   public String getItemInvalidError(InventoryItem var1) {
      return var1 != null && !this.internalInventoryItemInterface.isValidItem(var1) ? "" : null;
   }
}
