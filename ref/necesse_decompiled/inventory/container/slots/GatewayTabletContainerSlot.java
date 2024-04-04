package necesse.inventory.container.slots;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.Level;

public class GatewayTabletContainerSlot extends ContainerSlot {
   public GatewayTabletContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      String var2 = super.getItemInvalidError(var1);
      if (var2 != null) {
         return var2;
      } else {
         return var1 != null && !(var1.item instanceof GatewayTabletItem) ? "" : null;
      }
   }

   public ItemCombineResult combineSlots(Level var1, PlayerMob var2, ContainerSlot var3, int var4, boolean var5, boolean var6, String var7) {
      if (var3.isClear()) {
         return ItemCombineResult.failure();
      } else {
         InventoryItem var8 = this.getItem();
         if (var8 != null) {
            if (var2 != null) {
               if (this.getItemInvalidError(var3.getItem()) != null) {
                  return ItemCombineResult.failure(this.getItemInvalidError(var3.getItem()));
               } else {
                  var2.getInv().addItemsDropRemaining(var8, "addback", var2, false, false, true);
                  this.setItem(var3.getItem());
                  var3.setItem((InventoryItem)null);
                  this.markDirty();
                  var3.markDirty();
                  return ItemCombineResult.success();
               }
            } else {
               return super.combineSlots(var1, var2, var3, var4, var5, var6, var7);
            }
         } else {
            return super.combineSlots(var1, var2, var3, var4, var5, var6, var7);
         }
      }
   }
}
