package necesse.entity.objectEntity;

import necesse.level.maps.Level;

public class DisplayStandObjectEntity extends InventoryObjectEntity {
   public DisplayStandObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 1);
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
