package necesse.inventory.item;

import necesse.inventory.InventoryItem;

public class ItemUsed {
   public final boolean used;
   public final InventoryItem item;

   public ItemUsed(boolean var1, InventoryItem var2) {
      this.used = var1;
      this.item = var2;
   }
}
