package necesse.inventory.container.slots;

import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;

public class ArmorContainerSlot extends ContainerSlot {
   private ArmorItem.ArmorType armorType;

   public ArmorContainerSlot(Inventory var1, int var2, ArmorItem.ArmorType var3) {
      super(var1, var2);
      this.armorType = var3;
   }

   public String getItemInvalidError(InventoryItem var1) {
      return var1 != null && (!var1.item.isArmorItem() || ((ArmorItem)var1.item).armorType != this.armorType) ? "" : null;
   }

   public int getItemStackLimit(InventoryItem var1) {
      return 1;
   }
}
