package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Predicate;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;

public class SettlementStorageItemIDIndex extends SettlementStorageIndex {
   protected HashMap<Integer, SettlementStorageRecordsRegionData> regions = new HashMap();

   public SettlementStorageItemIDIndex(Level var1) {
      super(var1);
   }

   public void clear() {
      this.regions.clear();
   }

   protected SettlementStorageRecordsRegionData getRegionData(Item var1) {
      return (SettlementStorageRecordsRegionData)this.regions.compute(var1.getID(), (var2, var3) -> {
         return var3 == null ? new SettlementStorageRecordsRegionData(this, (var1x) -> {
            return var1.getID() == var1x.item.getID();
         }) : var3;
      });
   }

   public void add(InventoryItem var1, SettlementStorageRecord var2) {
      this.getRegionData(var1.item).add(var2);
   }

   public SettlementStorageRecordsRegionData getItem(int var1) {
      return (SettlementStorageRecordsRegionData)this.regions.get(var1);
   }

   public SettlementStorageRecordsRegionData getItem(Item var1) {
      return this.getItem(var1.getID());
   }

   public SettlementStorageRecordsRegionData getItem(String var1) {
      return this.getItem(ItemRegistry.getItemID(var1));
   }

   public LinkedList<SettlementStoragePickupSlot> findPickupSlots(int var1, EntityJobWorker var2, Predicate<InventoryItem> var3, int var4, int var5) {
      SettlementStorageRecordsRegionData var6 = this.getItem(var1);
      return var6 != null ? var6.startFinder(var2).findPickupSlots(var4, var5, var3) : null;
   }

   public LinkedList<SettlementStoragePickupSlot> findPickupSlots(Item var1, EntityJobWorker var2, Predicate<InventoryItem> var3, int var4, int var5) {
      return this.findPickupSlots(var1.getID(), var2, var3, var4, var5);
   }

   public LinkedList<SettlementStoragePickupSlot> findPickupSlots(String var1, EntityJobWorker var2, Predicate<InventoryItem> var3, int var4, int var5) {
      return this.findPickupSlots(ItemRegistry.getItemID(var1), var2, var3, var4, var5);
   }
}
