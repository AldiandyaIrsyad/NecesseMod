package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SeedObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.zones.SettlementFertilizeZone;

public class FertilizeLevelJob extends MineObjectLevelJob {
   public SettlementFertilizeZone zone;

   public FertilizeLevelJob(int var1, int var2, SettlementFertilizeZone var3, GameObjectReservable var4) {
      super(var1, var2);
      this.zone = var3;
      if (var4 != null) {
         this.reservable = var4;
      }

   }

   public FertilizeLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isValidObject(LevelObject var1) {
      if (!this.zone.isRemoved() && this.zone.containsTile(this.tileX, this.tileY)) {
         ObjectEntity var2 = var1.getObjectEntity();
         if (var2 instanceof SeedObjectEntity) {
            return !((SeedObjectEntity)var2).isFertilized();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean shouldSave() {
      return false;
   }

   public static Object getPreSequenceCompute(EntityJobWorker var0, JobTypeHandler.SubHandler<FertilizeLevelJob> var1) {
      return new FoundFertilizer(var0, var1);
   }

   public static <T extends FertilizeLevelJob> JobSequence getJobSequence(EntityJobWorker var0, boolean var1, FoundJob<T> var2) {
      GameObject var3 = ((FertilizeLevelJob)var2.job).getLevel().getObject(((FertilizeLevelJob)var2.job).tileX, ((FertilizeLevelJob)var2.job).tileY);
      LocalMessage var4 = new LocalMessage("activities", "fertilizing", "target", var3.getLocalization());
      LinkedListJobSequence var5 = new LinkedListJobSequence(var4);
      if (var1) {
         FoundFertilizer var6 = (FoundFertilizer)var2.preSequenceCompute.get();
         if (var6.hasNoneInWorkInventory) {
            if (var6.items == null) {
               return null;
            }

            var5.addAll(var6.items);
         }
      }

      var5.add(((FertilizeLevelJob)var2.job).getActiveJob(var0, var2.priority, var1));
      return var5;
   }

   public ActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, final boolean var3) {
      return new TileActiveJob(var1, var2, this.tileX, this.tileY) {
         public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
            return new JobMoveToTile(this.tileX, this.tileY, true);
         }

         public void tick(boolean var1, boolean var2) {
            FertilizeLevelJob.this.reservable.reserve(this.worker.getMobWorker());
         }

         public boolean isValid(boolean var1) {
            if (!FertilizeLevelJob.this.isRemoved() && FertilizeLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
               if (var1 && var3) {
                  ItemFilter var2 = new ItemFilter(ItemRegistry.getItemID("fertilizer"));
                  Iterator var3x = this.worker.getWorkInventory().items().iterator();

                  InventoryItem var4;
                  do {
                     if (!var3x.hasNext()) {
                        return false;
                     }

                     var4 = (InventoryItem)var3x.next();
                  } while(!var2.matchesItem(var4));

                  return true;
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }

         public ActiveJobResult perform() {
            if (this.worker.isInWorkAnimation()) {
               return ActiveJobResult.PERFORMING;
            } else {
               ObjectEntity var1 = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
               if (!(var1 instanceof SeedObjectEntity)) {
                  return ActiveJobResult.FAILED;
               } else {
                  SeedObjectEntity var2 = (SeedObjectEntity)var1;
                  Item var3x = ItemRegistry.getItem("fertilizer");
                  if (var3) {
                     boolean var4 = false;
                     WorkInventory var5 = this.worker.getWorkInventory();
                     ListIterator var6 = var5.listIterator();

                     while(var6.hasNext()) {
                        InventoryItem var7 = (InventoryItem)var6.next();
                        if (var7.getAmount() > 0 && var7.item.getID() == var3x.getID()) {
                           var7.setAmount(var7.getAmount() - 1);
                           if (var7.getAmount() <= 0) {
                              var6.remove();
                           }

                           var5.markDirty();
                           var4 = true;
                        }
                     }

                     if (!var4) {
                        return ActiveJobResult.FAILED;
                     }
                  }

                  this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var3x, 250);
                  var2.fertilize();
                  FertilizeLevelJob.this.remove();
                  return ActiveJobResult.FINISHED;
               }
            }
         }
      };
   }

   private static class FoundFertilizer {
      public boolean hasNoneInWorkInventory;
      public ArrayList<PickupSettlementStorageActiveJob> items;

      public FoundFertilizer(EntityJobWorker var1, JobTypeHandler.SubHandler<FertilizeLevelJob> var2) {
         TickManager var3 = var1.getMobWorker().getLevel().tickManager();
         int var4 = ItemRegistry.getItemID("fertilizer");
         this.hasNoneInWorkInventory = (Boolean)Performance.record(var3, "lookInventory", (Supplier)(() -> {
            return var1.getWorkInventory().stream().noneMatch((var1x) -> {
               return var1x.item.getID() == var4;
            });
         }));
         if (this.hasNoneInWorkInventory) {
            SettlementStorageRecords var5 = PickupSettlementStorageActiveJob.getStorageRecords(var1);
            if (var5 != null) {
               LinkedList var6 = ((SettlementStorageItemIDIndex)var5.getIndex(SettlementStorageItemIDIndex.class)).findPickupSlots(var4, var1, (Predicate)null, 1, 10);
               if (var6 != null) {
                  this.items = (ArrayList)var6.stream().map((var2x) -> {
                     return var2x.toPickupJob(var1, var2.priority);
                  }).collect(Collectors.toCollection(ArrayList::new));
               }
            }
         }

      }
   }
}
