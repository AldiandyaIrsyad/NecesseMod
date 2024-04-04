package necesse.level.maps.levelData.jobs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Predicate;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
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
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class PlantSaplingLevelJob extends PlaceObjectLevelJob {
   protected boolean shouldSave;

   public PlantSaplingLevelJob(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3);
      this.shouldSave = var4;
   }

   public PlantSaplingLevelJob(LoadData var1) {
      super(var1);
      this.shouldSave = true;
   }

   public boolean shouldSave() {
      return this.shouldSave;
   }

   public int getSaplingItemID() {
      return this.getObject().getObjectItem().getID();
   }

   public Item plant(WorkInventory var1) {
      Object var2 = null;
      if (var1 != null) {
         ObjectItem var3 = this.getObject().getObjectItem();
         ListIterator var4 = var1.listIterator();

         while(var4.hasNext()) {
            InventoryItem var5 = (InventoryItem)var4.next();
            if (var5.getAmount() > 0 && var5.item.getID() == var3.getID()) {
               var2 = var5.item;
               var5.setAmount(var5.getAmount() - 1);
               if (var5.getAmount() <= 0) {
                  var4.remove();
               }

               var1.markDirty();
               break;
            }
         }
      } else {
         var2 = this.getObject().getObjectItem();
      }

      if (var2 != null) {
         GameObject var6 = this.getObject();
         var6.placeObject(this.getLevel(), this.tileX, this.tileY, this.objectRotation);
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithTile(new PacketPlaceObject(this.getLevel(), (ServerClient)null, this.objectID, this.objectRotation, this.tileX, this.tileY), this.getLevel(), this.tileX, this.tileY);
         }

         this.getLevel().getTile(this.tileX, this.tileY).checkAround(this.getLevel(), this.tileX, this.tileY);
         this.getLevel().getObject(this.tileX, this.tileY).checkAround(this.getLevel(), this.tileX, this.tileY);
      }

      return (Item)var2;
   }

   public static <T extends PlantSaplingLevelJob> JobSequence getJobSequence(EntityJobWorker var0, boolean var1, FoundJob<T> var2) {
      GameObject var3 = ((PlantSaplingLevelJob)var2.job).getObject();
      LocalMessage var4 = new LocalMessage("activities", "planting", "item", var3.getLocalization());
      LinkedListJobSequence var5 = new LinkedListJobSequence(var4);
      if (var1) {
         int var6 = ((PlantSaplingLevelJob)var2.job).getSaplingItemID();
         if (var0.getWorkInventory().stream().noneMatch((var1x) -> {
            return var1x.item.getID() == var6;
         })) {
            SettlementStorageRecords var7 = PickupSettlementStorageActiveJob.getStorageRecords(var0);
            if (var7 == null) {
               return null;
            }

            LinkedList var8 = ((SettlementStorageItemIDIndex)var7.getIndex(SettlementStorageItemIDIndex.class)).findPickupSlots(var6, var0, (Predicate)null, 1, 10);
            if (var8 == null) {
               return null;
            }

            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               SettlementStoragePickupSlot var10 = (SettlementStoragePickupSlot)var9.next();
               var5.add(var10.toPickupJob(var0, var2.priority));
            }
         }
      }

      var5.add(((PlantSaplingLevelJob)var2.job).getActiveJob(var0, var2.priority, var1));
      return var5;
   }

   public ActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, final boolean var3) {
      return new TileActiveJob(var1, var2, this.tileX, this.tileY) {
         public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
            return new JobMoveToTile(this.tileX, this.tileY, true);
         }

         public void tick(boolean var1, boolean var2) {
            PlantSaplingLevelJob.this.reservable.reserve(this.worker.getMobWorker());
         }

         public boolean isValid(boolean var1) {
            if (!PlantSaplingLevelJob.this.isRemoved() && PlantSaplingLevelJob.this.reservable.isAvailable(this.worker.getMobWorker())) {
               if (var1 && var3) {
                  int var2 = PlantSaplingLevelJob.this.getSaplingItemID();
                  Iterator var3x = this.worker.getWorkInventory().items().iterator();

                  InventoryItem var4;
                  do {
                     if (!var3x.hasNext()) {
                        return false;
                     }

                     var4 = (InventoryItem)var3x.next();
                  } while(var4.item.getID() != var2);

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
               Item var1 = PlantSaplingLevelJob.this.plant(var3 ? this.worker.getWorkInventory() : null);
               if (var1 != null) {
                  this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var1, 250);
                  PlantSaplingLevelJob.this.remove();
                  return ActiveJobResult.FINISHED;
               } else {
                  return ActiveJobResult.FAILED;
               }
            }
         }
      };
   }
}
