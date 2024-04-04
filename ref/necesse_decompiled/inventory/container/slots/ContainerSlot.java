package necesse.inventory.container.slots;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.Container;
import necesse.level.maps.Level;

public class ContainerSlot {
   private Container container;
   private int containerIndex = -1;
   private Inventory inventory;
   private int inventorySlot;

   public ContainerSlot(Inventory var1, int var2) {
      this.inventory = var1;
      this.inventorySlot = var2;
   }

   public void init(Container var1, int var2) {
      if (this.containerIndex != -1 && this.containerIndex != var2) {
         throw new IllegalStateException("Container index already set");
      } else {
         this.container = var1;
         this.containerIndex = var2;
      }
   }

   public Container getContainer() {
      return this.container;
   }

   public int getContainerIndex() {
      return this.containerIndex;
   }

   public String getItemInvalidError(InventoryItem var1) {
      return this.inventory.isItemValid(this.inventorySlot, var1) ? null : "";
   }

   public int getItemStackLimit(InventoryItem var1) {
      return this.inventory.getItemStackLimit(this.inventorySlot, var1);
   }

   public int getInventorySlot() {
      return this.inventorySlot;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public boolean isClear() {
      return this.getInventory().isSlotClear(this.getInventorySlot());
   }

   public InventoryItem getItem() {
      return this.getInventory().getItem(this.getInventorySlot());
   }

   public int getItemAmount() {
      return this.isClear() ? 0 : this.getItem().getAmount();
   }

   public void setItem(InventoryItem var1) {
      this.getInventory().setItem(this.getInventorySlot(), var1);
      this.markDirty();
   }

   public void setAmount(int var1) {
      if (!this.isClear()) {
         this.getInventory().setAmount(this.getInventorySlot(), var1);
         this.markDirty();
      }
   }

   public void setItemLocked(boolean var1) {
      if (!this.isClear()) {
         this.getInventory().setItemLocked(this.getInventorySlot(), var1);
      }
   }

   public boolean isItemLocked() {
      return !this.isClear() && this.getInventory().isItemLocked(this.getInventorySlot());
   }

   public ItemCombineResult combineSlots(Level var1, PlayerMob var2, ContainerSlot var3, int var4, boolean var5, boolean var6, String var7) {
      if (var3.isClear()) {
         return ItemCombineResult.failure();
      } else {
         int var8 = Math.min(var4, var3.getItemAmount());
         InventoryItem var9 = this.getItem();
         if (var9 == null || !var9.item.ignoreCombineStackLimit(var1, var2, var9, var3.getItem(), var7)) {
            var8 = Math.min(var8, this.getItemStackLimit(var3.getItem()) - this.getItemAmount());
         }

         if (var8 <= 0) {
            return ItemCombineResult.failure();
         } else {
            InventoryItem var10 = var3.getItem().copy(var8, var5 && var3.getItem().isLocked());
            String var11 = this.getItemInvalidError(var10);
            if (var11 != null) {
               return ItemCombineResult.failure(var11);
            } else {
               ItemCombineResult var12;
               if (this.isClear()) {
                  var12 = ItemCombineResult.success();
                  this.setItem(var10);
                  var3.setAmount(var3.getItemAmount() - var8);
               } else {
                  var12 = this.getInventory().combineItem(var1, var2, this.getInventorySlot(), var3.getItem(), var4, var6, var7, (InventoryAddConsumer)null);
                  if (var12.success) {
                     var3.inventory.updateSlot(var3.getInventorySlot());
                  }
               }

               if (var3.getItemAmount() <= 0) {
                  var3.setItem((InventoryItem)null);
               }

               if (var12.success) {
                  this.markDirty();
                  var3.markDirty();
               }

               return var12;
            }
         }
      }
   }

   public ItemCombineResult combineSlots(Level var1, PlayerMob var2, ContainerSlot var3, boolean var4, boolean var5, String var6) {
      return this.combineSlots(var1, var2, var3, var3.getItemAmount(), var4, false, var6);
   }

   public ItemCombineResult swapItems(ContainerSlot var1) {
      String var2 = var1.getItemInvalidError(this.getItem());
      if (var2 != null) {
         return ItemCombineResult.failure(var2);
      } else if (var1.getItemStackLimit(this.getItem()) < this.getItemAmount()) {
         return ItemCombineResult.failure();
      } else {
         String var3 = this.getItemInvalidError(var1.getItem());
         if (var3 != null) {
            return ItemCombineResult.failure(var3);
         } else if (this.getItemStackLimit(var1.getItem()) < var1.getItemAmount()) {
            return ItemCombineResult.failure();
         } else {
            InventoryItem var4 = this.getItem();
            this.setItem(var1.getItem());
            var1.setItem(var4);
            return ItemCombineResult.success();
         }
      }
   }

   public boolean canLockItem() {
      return this.getInventory().canLockItem(this.getInventorySlot());
   }

   public void markDirty() {
      this.inventory.markDirty(this.getInventorySlot());
   }

   public boolean isDirty() {
      return this.inventory.isDirty(this.getInventorySlot());
   }
}
