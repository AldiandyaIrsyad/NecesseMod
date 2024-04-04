package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;

public class SettlementStorageGlobalIngredientIDIndex extends SettlementStorageIndex {
   protected HashMap<Integer, SettlementStorageRecordsRegionData> regions = new HashMap();

   public SettlementStorageGlobalIngredientIDIndex(Level var1) {
      super(var1);
   }

   public void clear() {
      this.regions.clear();
   }

   protected SettlementStorageRecordsRegionData getRegionData(int var1) {
      return (SettlementStorageRecordsRegionData)this.regions.compute(var1, (var2, var3) -> {
         return var3 == null ? new SettlementStorageRecordsRegionData(this, (var1x) -> {
            return var1x.item.isGlobalIngredient(var1);
         }) : var3;
      });
   }

   public void add(InventoryItem var1, SettlementStorageRecord var2) {
      Iterator var3 = var1.item.getGlobalIngredients().iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         this.getRegionData(var4).add(var2);
      }

   }

   public SettlementStorageRecordsRegionData getGlobalIngredient(int var1) {
      return (SettlementStorageRecordsRegionData)this.regions.get(var1);
   }

   public LinkedList<SettlementStoragePickupSlot> findPickupSlots(int var1, EntityJobWorker var2, Predicate<InventoryItem> var3, int var4, int var5) {
      SettlementStorageRecordsRegionData var6 = this.getGlobalIngredient(var1);
      return var6 != null ? var6.startFinder(var2).findPickupSlots(var4, var5, var3) : null;
   }
}
