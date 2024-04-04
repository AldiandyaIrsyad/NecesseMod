package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class ExtractOnlyContainerSlot extends ContainerSlot {
   public ExtractOnlyContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      return "";
   }
}
