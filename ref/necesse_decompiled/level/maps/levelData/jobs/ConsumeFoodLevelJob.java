package necesse.level.maps.levelData.jobs;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.ConsumeItemActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageFoodQualityIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class ConsumeFoodLevelJob extends LevelJob {
   public JobTypeHandler.SubHandler<?> handler;
   public ItemCategoriesFilter dietFilter;

   public ConsumeFoodLevelJob(int var1, int var2, JobTypeHandler.SubHandler<?> var3, ItemCategoriesFilter var4) {
      super(var1, var2);
      this.handler = var3;
      this.dietFilter = var4;
   }

   public ConsumeFoodLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isWithinRestrictZone(ZoneTester var1) {
      return true;
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isSameJob(LevelJob var1) {
      return var1.getID() == this.getID();
   }

   public boolean isValid() {
      return true;
   }

   public int getFirstPriority() {
      return Integer.MAX_VALUE;
   }

   public static <T extends ConsumeFoodLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1, HungerMob var2, Set<Integer> var3) {
      Predicate var4 = (var2x) -> {
         if (((ConsumeFoodLevelJob)var1.job).dietFilter != null && !((ConsumeFoodLevelJob)var1.job).dietFilter.isItemAllowed(var2x.item)) {
            return false;
         } else if (var3 != null && var3.contains(var2x.item.getID())) {
            return false;
         } else if (!var2x.item.isFoodItem()) {
            return false;
         } else {
            FoodConsumableItem var3x = (FoodConsumableItem)var2x.item;
            return var3x.nutrition > 0 && var3x.quality != null;
         }
      };
      InventoryItem var5 = (InventoryItem)var0.getWorkInventory().stream().filter(var4).max(Comparator.comparingInt((var0x) -> {
         return ((FoodConsumableItem)var0x.item).quality.happinessIncrease;
      })).orElse((Object)null);
      FoodConsumableItem var6 = var5 == null ? null : (FoodConsumableItem)var5.item;
      SettlementStorageRecords var7 = PickupSettlementStorageActiveJob.getStorageRecords(var0);
      if (var7 != null) {
         Iterator var8 = Settler.foodQualities.descendingSet().iterator();

         while(var8.hasNext()) {
            FoodQuality var9 = (FoodQuality)var8.next();
            if (var6 != null && var6.quality.happinessIncrease >= var9.happinessIncrease) {
               break;
            }

            SettlementStorageRecordsRegionData var10 = ((SettlementStorageFoodQualityIndex)var7.getIndex(SettlementStorageFoodQualityIndex.class)).getFoodQuality(var9);
            if (var10 != null) {
               SettlementStoragePickupSlot var11 = var10.startFinder(var0).findFirstItemPickup(var4);
               if (var11 != null) {
                  LocalMessage var12 = new LocalMessage("activities", "consuming", "item", var11.item.getItemLocalization());
                  LinkedListJobSequence var13 = new LinkedListJobSequence(var12);
                  AtomicReference var14 = new AtomicReference();
                  var13.add(var11.toPickupJob(var0, var1.priority, var14));
                  var13.add(new ConsumeItemActiveJob(var0, var1.priority, var14, var2));
                  return var13;
               }
            }
         }
      }

      if (var5 != null) {
         LocalMessage var15 = new LocalMessage("activities", "consuming", "item", var5.copy(1).getItemLocalization());
         return new SingleJobSequence(new ConsumeItemActiveJob(var0, var1.priority, var5.copy(1), var2), var15);
      } else {
         return var3 != null && !var3.isEmpty() ? getJobSequence(var0, var1, var2, (Set)null) : null;
      }
   }

   public static JobTypeHandler.JobStreamSupplier<? extends ConsumeFoodLevelJob> getJobStreamer(Supplier<ItemCategoriesFilter> var0) {
      return (var1, var2) -> {
         ItemCategoriesFilter var3 = (ItemCategoriesFilter)var0.get();
         if (var3 != null && var3.master.isAnyAllowed()) {
            Mob var4 = var1.getMobWorker();
            return Stream.of(new ConsumeFoodLevelJob(var4.getX() / 32, var4.getY() / 32, var2, var3));
         } else {
            return Stream.empty();
         }
      };
   }
}
