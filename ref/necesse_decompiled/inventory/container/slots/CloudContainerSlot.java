package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class CloudContainerSlot extends ContainerSlot {
   public final int itemID;

   public CloudContainerSlot(Inventory var1, int var2, int var3) {
      super(var1, var2);
      this.itemID = var3;
   }

   public String getItemInvalidError(InventoryItem var1) {
      return var1 != null && var1.item.getID() == this.itemID ? "" : super.getItemInvalidError(var1);
   }
}
