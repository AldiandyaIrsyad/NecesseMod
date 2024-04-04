package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.GameTileRange;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class PickupSettlementStorageActiveJob extends TileActiveJob {
   public SettlementStoragePickupSlot slot;
   public AtomicReference<InventoryItem> pickedUpItemRef;

   public PickupSettlementStorageActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, int var3, int var4, SettlementStoragePickupSlot var5, AtomicReference<InventoryItem> var6) {
      super(var1, var2, var3, var4);
      this.slot = var5;
      this.pickedUpItemRef = var6;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return new JobMoveToTile(this.tileX, this.tileY, true);
   }

   public void tick(boolean var1, boolean var2) {
      this.slot.reserve((Entity)this.worker.getMobWorker());
   }

   public boolean isValid(boolean var1) {
      return this.slot.isValid(this.getTileInventory());
   }

   public void onCancelled(boolean var1, boolean var2, boolean var3) {
      super.onCancelled(var1, var2, var3);
      if (!this.slot.isRemoved()) {
         this.slot.remove();
      }

   }

   public ActiveJobResult perform() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         InventoryItem var1 = this.slot.pickupItem(this.getTileInventory());
         if (var1 != null) {
            this.pickedUpItemRef.set(var1.copy());
            this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var1.item, 250);
            this.worker.getWorkInventory().add(var1);
            return ActiveJobResult.FINISHED;
         } else {
            return ActiveJobResult.FAILED;
         }
      }
   }

   public static SettlementStorageRecords getStorageRecords(EntityJobWorker var0) {
      Level var1 = var0.getMobWorker().getLevel();
      SettlementLevelData var2 = SettlementLevelData.getSettlementData(var1);
      return var2 != null ? var2.storageRecords : null;
   }

   /** @deprecated */
   @Deprecated
   public static ArrayList<PickupSettlementStorageActiveJob> findItems(EntityJobWorker var0, JobTypeHandler.TypePriority var1, ItemFilter var2, int var3, int var4) {
      Objects.requireNonNull(var2);
      return findItems(var0, var1, var2::matchesItem, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public static ArrayList<PickupSettlementStorageActiveJob> findItems(EntityJobWorker var0, JobTypeHandler.TypePriority var1, Predicate<InventoryItem> var2, int var3, int var4) {
      Mob var5 = var0.getMobWorker();
      return (ArrayList)Performance.record(var5.getLevel().tickManager(), "findItems", (Supplier)(() -> {
         ZoneTester var6 = var0.getJobRestrictZone();
         Point var7 = var0.getJobSearchTile();
         JobsLevelData var8 = JobsLevelData.getJobsLevelData(var5.getLevel());
         GameTileRange var9 = (GameTileRange)var0.getJobTypeHandler().getJobHandler(LevelJobRegistry.hasStorageID).tileRange.apply(var5.getLevel());
         GameLinkedList var10 = (GameLinkedList)var8.streamJobsInRegionsShape(var9.getRangeBounds(var7), 0).filter((var0x) -> {
            return ((LevelJob)var0x.object).getID() == LevelJobRegistry.hasStorageID;
         }).filter((var1x) -> {
            return var6.containsTile(((LevelJob)var1x.object).tileX, ((LevelJob)var1x.object).tileY);
         }).filter((var2x) -> {
            return var9.isWithinRange(var7, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
         }).map((var0x) -> {
            return (HasStorageLevelJob)var0x.object;
         }).map((var2x) -> {
            return new ComputedObjectValue(var2x, () -> {
               int var3 = 0;
               InventoryRange var4x = var2x.settlementInventory.getInventoryRange();
               if (var4x != null) {
                  for(int var5 = var4x.startSlot; var5 <= var4x.endSlot; ++var5) {
                     InventoryItem var6 = var4x.inventory.getItem(var5);
                     if (var6 != null && var2.test(var6)) {
                        var3 += var6.getAmount();
                        if (var3 >= var4) {
                           break;
                        }
                     }
                  }
               }

               return var3;
            });
         }).filter((var0x) -> {
            return var0x.get() != null;
         }).filter((var1x) -> {
            return var0.estimateCanMoveTo(((HasStorageLevelJob)var1x.object).tileX, ((HasStorageLevelJob)var1x.object).tileY, true);
         }).collect(GameLinkedList::new, GameLinkedList::add, GameLinkedList::addAll);
         ArrayList var11 = new ArrayList();
         HasStorageLevelJob var12 = null;
         int var13 = 0;

         while(var13 < var4 && !var10.isEmpty()) {
            Point var14 = var12 == null ? new Point(var5.getX(), var5.getY()) : new Point(var12.tileX * 32 + 16, var12.tileY * 32 + 16);
            GameLinkedList.Element var15 = (GameLinkedList.Element)var10.streamElements().min(Comparator.comparingDouble((var1x) -> {
               return var14.distance((double)(((HasStorageLevelJob)((ComputedObjectValue)var1x.object).object).tileX * 32 + 16), (double)(((HasStorageLevelJob)((ComputedObjectValue)var1x.object).object).tileY * 32 + 16));
            })).orElse((Object)null);
            if (var15 == null) {
               break;
            }

            LinkedList var16 = ((HasStorageLevelJob)((ComputedObjectValue)var15.object).object).settlementInventory.findUnreservedSlots(var2, 1, var4 - var13);
            if (var16 != null) {
               Iterator var17 = var16.iterator();

               while(var17.hasNext()) {
                  SettlementStoragePickupSlot var18 = (SettlementStoragePickupSlot)var17.next();
                  var18.reserve((Entity)var0.getMobWorker());
                  var13 += var18.item.getAmount();
                  var11.add(new PickupSettlementStorageActiveJob(var0, var1, ((HasStorageLevelJob)((ComputedObjectValue)var15.object).object).tileX, ((HasStorageLevelJob)((ComputedObjectValue)var15.object).object).tileY, var18, new AtomicReference()));
               }
            }

            if (var13 >= var4) {
               break;
            }

            var12 = (HasStorageLevelJob)((ComputedObjectValue)var15.object).object;
            var15.remove();
         }

         return var13 >= var3 ? var11 : null;
      }));
   }
}
