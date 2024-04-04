package necesse.inventory.container.slots;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.level.maps.Level;

public class TrashContainerSlot extends ContainerSlot {
   public TrashContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public ItemCombineResult combineSlots(Level var1, PlayerMob var2, ContainerSlot var3, int var4, boolean var5, boolean var6, String var7) {
      if (var3.isClear()) {
         return ItemCombineResult.failure();
      } else {
         int var8 = Math.min(var3.getItemAmount(), var4);
         if (this.getItem() != null && this.getItem().canCombine(var1, var2, var3.getItem(), "trash")) {
            this.getItem().item.onCombine(var1, var2, this.getInventory(), this.getInventorySlot(), this.getItem(), var3.getItem(), Integer.MAX_VALUE, var8, var6, "trash", (InventoryAddConsumer)null);
            this.setAmount(Math.min(this.getItemAmount(), this.getItemStackLimit(this.getItem())));
            if (var3.getItemAmount() <= 0) {
               var3.setItem((InventoryItem)null);
            }
         } else {
            int var9 = Math.min(this.getItemStackLimit(var3.getItem()), var8);
            this.setItem(var3.getItem().copy(var9));
            var3.setAmount(var3.getItemAmount() - var9);
            if (var3.getItemAmount() <= 0) {
               var3.setItem((InventoryItem)null);
            }
         }

         this.markDirty();
         return ItemCombineResult.success();
      }
   }
}
