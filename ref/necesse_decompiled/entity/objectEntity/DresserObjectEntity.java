package necesse.entity.objectEntity;

import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;

public class DresserObjectEntity extends InventoryObjectEntity {
   public static final int SETS = 10;

   public DresserObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 30);
   }

   public static ArmorItem.ArmorType getArmorType(int var0) {
      int var1 = var0 / 10;
      if (var1 == 0) {
         return ArmorItem.ArmorType.HEAD;
      } else if (var1 == 1) {
         return ArmorItem.ArmorType.CHEST;
      } else {
         return var1 == 2 ? ArmorItem.ArmorType.FEET : null;
      }
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      if (var2 != null) {
         ArmorItem.ArmorType var3 = getArmorType(var1);
         if (var3 != null) {
            return var2.item.isArmorItem() && ((ArmorItem)var2.item).armorType == var3;
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
