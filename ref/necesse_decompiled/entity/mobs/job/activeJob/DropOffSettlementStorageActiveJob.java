package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.Entity;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.StorageDropOff;

public class DropOffSettlementStorageActiveJob extends TileActiveJob {
   public final StorageDropOff dropOff;
   public final GameObjectReservable reservable;
   public Supplier<Boolean> isRemovedCheck;
   public boolean requireFullAmount;

   public DropOffSettlementStorageActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, LevelStorage var3, GameObjectReservable var4, Supplier<Boolean> var5, boolean var6, Supplier<InventoryItem> var7) {
      this(var1, var2, var4, var5, var6, var3.addFutureDropOff(var7));
   }

   public DropOffSettlementStorageActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, GameObjectReservable var3, Supplier<Boolean> var4, boolean var5, StorageDropOff var6) {
      super(var1, var2, var6.storage.tileX, var6.storage.tileY);
      this.reservable = var3;
      this.isRemovedCheck = var4;
      this.requireFullAmount = var5;
      this.dropOff = var6;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return new JobMoveToTile(this.tileX, this.tileY, true);
   }

   public void tick(boolean var1, boolean var2) {
      if (this.reservable != null) {
         this.reservable.reserve(this.worker.getMobWorker());
      }

      this.dropOff.reserve((Entity)this.worker.getMobWorker());
   }

   public boolean isValid(boolean var1) {
      if (this.isRemovedCheck != null && (Boolean)this.isRemovedCheck.get()) {
         return false;
      } else if (this.reservable != null && !this.reservable.isAvailable(this.worker.getMobWorker())) {
         return false;
      } else if (this.requireFullAmount) {
         return this.dropOff.canAddFullAmount();
      } else {
         return this.dropOff.canAddAmount() > 0;
      }
   }

   public void onCancelled(boolean var1, boolean var2, boolean var3) {
      super.onCancelled(var1, var2, var3);
      this.dropOff.remove();
   }

   public ActiveJobResult perform() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         InventoryItem var1 = this.dropOff.getItem();
         ListIterator var2 = this.worker.getWorkInventory().listIterator();

         while(var2.hasNext()) {
            InventoryItem var3 = (InventoryItem)var2.next();
            if (var3.equals(this.getLevel(), var1, true, false, "equals")) {
               int var4 = this.dropOff.addItem(var1);
               if (var4 > 0) {
                  var3.setAmount(var3.getAmount() - var4);
                  if (var3.getAmount() <= 0) {
                     var2.remove();
                  }

                  this.worker.getWorkInventory().markDirty();
                  this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var3.item, 250);
                  return ActiveJobResult.FINISHED;
               }
            }
         }

         return ActiveJobResult.FAILED;
      }
   }
}
