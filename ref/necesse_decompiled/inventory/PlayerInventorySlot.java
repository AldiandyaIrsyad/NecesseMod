package necesse.inventory;

public class PlayerInventorySlot {
   public final int inventoryID;
   public final int slot;

   public PlayerInventorySlot(int var1, int var2) {
      this.inventoryID = var1;
      this.slot = var2;
   }

   public PlayerInventorySlot(PlayerInventory var1, int var2) {
      this(var1.getInventoryID(), var2);
   }

   public PlayerInventory getInv(PlayerInventoryManager var1) {
      return var1.getInventoryByID(this.inventoryID);
   }

   public void setItem(PlayerInventoryManager var1, InventoryItem var2) {
      PlayerInventory var3 = this.getInv(var1);
      if (var3 != null) {
         if (this.slot >= var3.getSize()) {
            var3.changeSize(this.slot + 1);
         }

         var3.setItem(this.slot, var2);
      }

   }

   public void setItem(PlayerInventoryManager var1, InventoryItem var2, boolean var3) {
      PlayerInventory var4 = this.getInv(var1);
      if (var4 != null) {
         if (this.slot >= var4.getSize()) {
            var4.changeSize(this.slot + 1);
         }

         var4.setItem(this.slot, var2, var3);
      }

   }

   public InventoryItem getItem(PlayerInventoryManager var1) {
      PlayerInventory var2 = this.getInv(var1);
      return var2 != null ? var2.getItem(this.slot) : null;
   }

   public boolean isSlotClear(PlayerInventoryManager var1) {
      PlayerInventory var2 = this.getInv(var1);
      return var2 != null && var2.isSlotClear(this.slot);
   }

   public boolean isItemLocked(PlayerInventoryManager var1) {
      PlayerInventory var2 = this.getInv(var1);
      return var2 != null && var2.isItemLocked(this.slot);
   }

   public void markDirty(PlayerInventoryManager var1) {
      PlayerInventory var2 = this.getInv(var1);
      if (var2 != null) {
         var2.markDirty(this.slot);
      }

   }

   public boolean equals(PlayerInventorySlot var1) {
      return var1 != null && var1.inventoryID == this.inventoryID && var1.slot == this.slot;
   }
}
