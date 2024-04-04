package necesse.inventory.container.slots;

import necesse.entity.objectEntity.interfaces.OEInventory;

public class OEInventoryContainerSlot extends ContainerSlot {
   public OEInventoryContainerSlot(OEInventory var1, int var2) {
      super(var1.getInventory(), var2);
   }
}
