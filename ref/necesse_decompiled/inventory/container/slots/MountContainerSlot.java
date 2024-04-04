package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class MountContainerSlot extends ContainerSlot {
   public MountContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      return var1 != null && !var1.item.isMountItem() ? "" : null;
   }

   public int getItemStackLimit(InventoryItem var1) {
      return 1;
   }
}
