package necesse.inventory;

public abstract class InventoryItemsRemoved {
   public final Inventory inventory;
   public final int inventorySlot;
   public final InventoryItem invItem;
   public final int amount;

   public InventoryItemsRemoved(Inventory var1, int var2, InventoryItem var3, int var4) {
      this.inventory = var1;
      this.inventorySlot = var2;
      this.invItem = var3;
      this.amount = var4;
   }

   public abstract void revert();
}
