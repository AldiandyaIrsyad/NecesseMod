package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.GameTileRange;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.WorkInventory;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.PickupItemEntityActiveJob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.ItemPickupReservedAmount;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class StorePickupItemLevelJob extends EntityLevelJob<ItemPickupEntity> {
   public StorePickupItemLevelJob(ItemPickupEntity var1) {
      super((Entity)var1);
   }

   public StorePickupItemLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean shouldSave() {
      return false;
   }

   public static <T extends StorePickupItemLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1) {
      return ((StorePickupItemLevelJob)var1.job).getJobSequence(var0, var1.priority);
   }

   public static JobTypeHandler.JobStreamSupplier<? extends StorePickupItemLevelJob> getJobStreamer() {
      return (var0, var1) -> {
         Mob var2 = var0.getMobWorker();
         ZoneTester var3 = var0.getJobRestrictZone();
         Point var4 = var0.getJobSearchTile();
         GameTileRange var5 = (GameTileRange)var1.tileRange.apply(var2.getLevel());
         return var2.getLevel().entityManager.pickups.streamInRegionsShape(var5.getRangeBounds(var4), 0).filter((var1x) -> {
            return !var1x.removed() && var2.isSamePlace(var1x) && var1x.getTimeSinceSpawned() > 10000L && var1x instanceof ItemPickupEntity;
         }).filter((var1x) -> {
            return var3.containsTile(var1x.getTileX(), var1x.getTileY());
         }).filter((var2x) -> {
            return var5.isWithinRange(var4, var2x.getTileX(), var2x.getTileY());
         }).map((var0x) -> {
            return (ItemPickupEntity)var0x;
         }).filter(ItemPickupEntity::canBePickedUpBySettlers).map(StorePickupItemLevelJob::new);
      };
   }

   public LinkedListJobSequence getJobSequence(EntityJobWorker var1, JobTypeHandler.TypePriority var2) {
      LocalMessage var3 = new LocalMessage("activities", "theground");
      GameMessage var4 = ((ItemPickupEntity)this.target).item.getItemLocalization();
      LocalMessage var5 = new LocalMessage("activities", "hauling", new Object[]{"item", var4, "target", var3});
      LinkedListJobSequence var6 = new LinkedListJobSequence(var5);
      return this.addToJobSequence(var1, var2, var6) ? var6 : null;
   }

   public boolean addToJobSequence(EntityJobWorker var1, JobTypeHandler.TypePriority var2, final LinkedListJobSequence var3) {
      int var4 = ((ItemPickupEntity)this.target).getAvailableAmount();
      if (var4 <= 0) {
         return false;
      } else {
         WorkInventory var5 = var1.getWorkInventory();
         var4 = Math.min(var5.getCanAddAmount(((ItemPickupEntity)this.target).item), var4);
         if (var4 <= 0) {
            return false;
         } else {
            ArrayList var6 = HasStorageLevelJob.findDropOffLocation(var1, ((ItemPickupEntity)this.target).item.copy(Math.min(var4, ((ItemPickupEntity)this.target).item.itemStackSize())), ((ItemPickupEntity)this.target).getPositionPoint());
            int var7 = 0;
            LinkedList var8 = new LinkedList();
            Iterator var9 = var6.iterator();

            while(var9.hasNext()) {
               HasStorageLevelJob.DropOffFind var10 = (HasStorageLevelJob.DropOffFind)var9.next();
               var7 += var10.item.getAmount();
               var8.add(var10.getActiveJob(var1, var2, false));
            }

            if (var7 > 0) {
               ItemPickupReservedAmount var12 = ((ItemPickupEntity)this.target).reservePickupAmount(var7);
               if (var12 != null) {
                  Iterator var13 = var8.iterator();

                  while(var13.hasNext()) {
                     DropOffSettlementStorageActiveJob var11 = (DropOffSettlementStorageActiveJob)var13.next();
                     var3.addLast(var11);
                  }

                  var3.addFirst(new PickupItemEntityActiveJob(var1, var2, var12, (GameTileRange)var2.type.tileRange.apply(var1.getLevel())) {
                     public ActiveJobResult performTarget() {
                        ActiveJobResult var1 = super.performTarget();
                        if (var1 == ActiveJobResult.FINISHED && !this.worker.getWorkInventory().isFull()) {
                           JobTypeHandler.SubHandler var2 = this.worker.getJobTypeHandler().getJobHandler(StorePickupItemLevelJob.class);
                           Comparator var3x = Comparator.comparingInt((var0) -> {
                              return -((StorePickupItemLevelJob)var0.job).getSameJobPriority();
                           });
                           var3x = var3x.thenComparingDouble(FoundJob::getDistanceFromWorker);
                           var2.streamFoundJobs(this.worker).filter(FoundJob::isWithinWorkRange).sorted(var3x).filter(FoundJob::canMoveTo).filter((var2x) -> {
                              return ((StorePickupItemLevelJob)var2x.job).addToJobSequence(this.worker, var2x.priority, var3);
                           }).findFirst();
                        }

                        return var1;
                     }
                  });
                  return true;
               }
            }

            return false;
         }
      }
   }
}
