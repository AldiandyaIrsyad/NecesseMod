package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;

public class SettlementStorageFoodQualityIndex extends SettlementStorageIndex {
   protected HashMap<FoodQuality, SettlementStorageRecordsRegionData> regions = new HashMap();

   public SettlementStorageFoodQualityIndex(Level var1) {
      super(var1);
   }

   public void clear() {
      this.regions.clear();
   }

   protected SettlementStorageRecordsRegionData getRegionData(FoodQuality var1) {
      return (SettlementStorageRecordsRegionData)this.regions.compute(var1, (var2, var3) -> {
         return var3 == null ? new SettlementStorageRecordsRegionData(this, (var1x) -> {
            return var1x.item.isFoodItem() && ((FoodConsumableItem)var1x.item).quality == var1;
         }) : var3;
      });
   }

   public void add(InventoryItem var1, SettlementStorageRecord var2) {
      if (var1.item.isFoodItem()) {
         FoodConsumableItem var3 = (FoodConsumableItem)var1.item;
         this.getRegionData(var3.quality).add(var2);
      }

   }

   public SettlementStorageRecordsRegionData getFoodQuality(FoodQuality var1) {
      return (SettlementStorageRecordsRegionData)this.regions.get(var1);
   }
}
