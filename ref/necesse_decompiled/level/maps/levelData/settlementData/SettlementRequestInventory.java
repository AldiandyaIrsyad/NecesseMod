package necesse.level.maps.levelData.settlementData;

import java.util.Iterator;
import java.util.Map;
import necesse.engine.util.GameLinkedList;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public abstract class SettlementRequestInventory extends SettlementOEInventory {
   protected SettlementRequestOptions requestOptions;
   protected ItemCategoriesFilter filter;

   public SettlementRequestInventory(Level var1, int var2, int var3, SettlementRequestOptions var4) {
      super(var1, var2, var3, true);
      this.requestOptions = var4;
      this.filter = new ItemCategoriesFilter(var4.minAmount, var4.maxAmount);
      this.refreshOEInventory();
   }

   public ItemCategoriesFilter getFilter() {
      return this.filter;
   }

   public void addHaulJobs(JobsLevelData var1, SettlementStorageRecords var2, int var3) {
      InventoryRange var4 = this.getInventoryRange();
      if (var4 != null) {
         int var5 = 0;

         for(int var6 = var4.startSlot; var6 <= var4.endSlot; ++var6) {
            var5 += var4.inventory.getAmount(var6);
            if (var5 >= this.requestOptions.minAmount) {
               return;
            }
         }

         SettlementStorageRecordsRegionData var15 = this.requestOptions.getRequestStorageData(var2);
         if (var15 != null) {
            Iterator var7 = var15.streamAllRecords().flatMap((var0) -> {
               return var0.entrySet().stream();
            }).iterator();

            while(var7.hasNext()) {
               Map.Entry var8 = (Map.Entry)var7.next();
               int var9 = this.requestOptions.maxAmount - var5;
               Iterator var10 = ((GameLinkedList)var8.getValue()).iterator();

               while(var10.hasNext()) {
                  SettlementStorageRecord var11 = (SettlementStorageRecord)var10.next();
                  int var12 = Math.min(var9, var11.itemAmount);
                  HaulFromLevelJob var13 = (HaulFromLevelJob)var11.storage.haulFromLevelJobs.stream().filter((var2x) -> {
                     return var2x.item.equals(this.level, var11.getItem(), true, false, "pickups");
                  }).findFirst().orElse((Object)null);
                  if (var13 != null) {
                     if (var13.dropOffPositions.stream().noneMatch((var1x) -> {
                        return var1x.storage == this;
                     })) {
                        var13.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(this, var3, var12));
                     }
                  } else {
                     HaulFromLevelJob var14 = new HaulFromLevelJob(var11.storage, var11.getItem().copy());
                     var14.dropOffPositions.add(new HaulFromLevelJob.HaulPosition(this, var3, var12));
                     var11.storage.haulFromLevelJobs.add(var14);
                     var1.addJob(var14, true);
                  }

                  var9 -= var12;
                  if (var9 <= 0) {
                     break;
                  }
               }
            }
         }
      }

   }
}
