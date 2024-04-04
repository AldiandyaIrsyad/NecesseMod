package necesse.entity.objectEntity;

import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;

public class ArmorStandObjectEntity extends InventoryObjectEntity {
   public ArmorStandObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 3);
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      if (var2 != null) {
         if (var1 == 0) {
            return var2.item.isArmorItem() && ((ArmorItem)var2.item).armorType == ArmorItem.ArmorType.HEAD;
         }

         if (var1 == 1) {
            return var2.item.isArmorItem() && ((ArmorItem)var2.item).armorType == ArmorItem.ArmorType.CHEST;
         }

         if (var1 == 2) {
            return var2.item.isArmorItem() && ((ArmorItem)var2.item).armorType == ArmorItem.ArmorType.FEET;
         }
      }

      return true;
   }

   public boolean isSettlementStorageItemDisabled(Item var1) {
      return !var1.isArmorItem();
   }

   public boolean canQuickStackInventory() {
      return false;
   }

   public boolean canRestockInventory() {
      return false;
   }

   public boolean canSortInventory() {
      return false;
   }

   public boolean canUseForNearbyCrafting() {
      return false;
   }
}
