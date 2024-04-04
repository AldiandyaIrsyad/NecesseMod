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
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class FillApiaryFrameLevelJob extends MineObjectLevelJob {
   public FillApiaryFrameLevelJob(int var1, int var2) {
      super(var1, var2);
   }

   public FillApiaryFrameLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isValidObject(LevelObject var1) {
      AbstractBeeHiveObjectEntity var2 = (AbstractBeeHiveObjectEntity)var1.getCurrentObjectEntity(AbstractBeeHiveObjectEntity.class);
      return var2 != null ? var2.canAddFrame() : false;
   }

   public boolean shouldSave() {
      return false;
   }

   public static Object getPreSequenceCompute(EntityJobWorker var0, JobTypeHandler.SubHandler<FillApiaryFrameLevelJob> var1) {
      return new FoundApiaryFrame(var0, var1);
   }

   public static <T extends FillApiaryFrameLevelJob> JobSequence getJobSequence(EntityJobWorker var0, boolean var1, FoundJob<T> var2) {
      LocalMessage var3 = new LocalMessage("activities", "fillingapiary");
      LinkedListJobSequence var4 = new LinkedListJobSequence(var3);
      if (var1) {
         FoundApiaryFrame var5 = (FoundApiaryFrame)var2.preSequenceCompute.get();
         if (var5.hasNoneInWorkInventory) {
            if (var5.items == null) {
               return null;
            }

            var4.addAll(var5.items);
         }
      }

      var4.add(((FillApiaryFrameLevelJob)var2.job).getActiveJob(var0, var2.priority, var1));
      return var4;
   }

   public ActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, final boolean var3) {
      return new TileActiveJob(var1, var2, this.tileX, this.tileY) {
         public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
            return new JobMoveToTile(this.tileX, this.tileY, true);
         }

         public void tick(boolean var1, boolean var2) {
            FillApiaryFrameLevelJob.this.reservable.reserve(this.worker.getMobWorker());
         }

         public boolean isValid(boolean var1) {
            if (!FillApiaryFrameLevelJob.this.isRemoved() && FillApiaryFrameLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
               if (var1 && var3) {
                  ItemFilter var2 = new ItemFilter(ItemRegistry.getItemID("apiaryframe"));
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
               AbstractBeeHiveObjectEntity var1 = (AbstractBeeHiveObjectEntity)this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY, AbstractBeeHiveObjectEntity.class);
               if (var1 != null) {
                  if (!var1.canAddFrame()) {
                     return ActiveJobResult.FAILED;
                  } else {
                     Item var2 = ItemRegistry.getItem("apiaryframe");
                     if (var3) {
                        boolean var3x = false;
                        WorkInventory var4 = this.worker.getWorkInventory();
                        ListIterator var5 = var4.listIterator();

                        while(var5.hasNext()) {
                           InventoryItem var6 = (InventoryItem)var5.next();
                           if (var6.getAmount() > 0 && var6.item.getID() == var2.getID()) {
                              var6.setAmount(var6.getAmount() - 1);
                              if (var6.getAmount() <= 0) {
                                 var5.remove();
                              }

                              var4.markDirty();
                              var3x = true;
                           }
                        }

                        if (!var3x) {
                           return ActiveJobResult.FAILED;
                        }
                     }

                     this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var2, 250);
                     var1.addFrame();
                     if (!var1.canAddFrame()) {
                        FillApiaryFrameLevelJob.this.remove();
                     }

                     return ActiveJobResult.FINISHED;
                  }
               } else {
                  return ActiveJobResult.FAILED;
               }
            }
         }
      };
   }

   private static class FoundApiaryFrame {
      public boolean hasNoneInWorkInventory;
      public ArrayList<PickupSettlementStorageActiveJob> items;

      public FoundApiaryFrame(EntityJobWorker var1, JobTypeHandler.SubHandler<FillApiaryFrameLevelJob> var2) {
         TickManager var3 = var1.getMobWorker().getLevel().tickManager();
         int var4 = ItemRegistry.getItemID("apiaryframe");
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
