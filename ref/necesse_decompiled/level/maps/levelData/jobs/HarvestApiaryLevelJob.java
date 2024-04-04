package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.entity.objectEntity.ApiaryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.GameObject;

public class HarvestApiaryLevelJob extends LevelJob {
   public HarvestApiaryLevelJob(int var1, int var2) {
      super(var1, var2);
   }

   public HarvestApiaryLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isValid() {
      ApiaryObjectEntity var1 = this.getObjectEntity();
      return var1 != null && var1.getHoneyAmount() > 0;
   }

   public ApiaryObjectEntity getObjectEntity() {
      ObjectEntity var1 = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
      return var1 instanceof ApiaryObjectEntity ? (ApiaryObjectEntity)var1 : null;
   }

   public boolean shouldSave() {
      return false;
   }

   public ArrayList<InventoryItem> harvest() {
      ApiaryObjectEntity var1 = this.getObjectEntity();
      if (var1 != null) {
         ArrayList var2 = var1.getHarvestItems();
         var1.resetHarvestItems();
         return var2;
      } else {
         this.remove();
         return new ArrayList();
      }
   }

   public static <T extends HarvestApiaryLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1) {
      GameObject var2 = ((HarvestApiaryLevelJob)var1.job).getLevel().getObject(((HarvestApiaryLevelJob)var1.job).tileX, ((HarvestApiaryLevelJob)var1.job).tileY);
      LocalMessage var3 = new LocalMessage("activities", "harvesting", "target", var2.getLocalization());
      return new SingleJobSequence(((HarvestApiaryLevelJob)var1.job).getActiveJob(var0, var1.priority), var3);
   }

   public ActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2) {
      return new TileActiveJob(var1, var2, this.tileX, this.tileY) {
         public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
            return new JobMoveToTile(this.tileX, this.tileY, true);
         }

         public void tick(boolean var1, boolean var2) {
            HarvestApiaryLevelJob.this.reservable.reserve(this.worker.getMobWorker());
         }

         public boolean isValid(boolean var1) {
            return !HarvestApiaryLevelJob.this.isRemoved() && HarvestApiaryLevelJob.this.reservable.isAvailable(this.worker.getMobWorker()) ? HarvestApiaryLevelJob.this.isValid() : false;
         }

         public ActiveJobResult perform() {
            if (this.worker.isInWorkAnimation()) {
               return ActiveJobResult.PERFORMING;
            } else {
               ArrayList var1 = HarvestApiaryLevelJob.this.harvest();
               if (var1.isEmpty()) {
                  this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, (Item)null, 250);
               } else {
                  this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, ((InventoryItem)var1.get(0)).item, 250);
               }

               Iterator var2 = var1.iterator();

               while(var2.hasNext()) {
                  InventoryItem var3 = (InventoryItem)var2.next();
                  this.worker.getWorkInventory().add(var3);
               }

               return ActiveJobResult.FINISHED;
            }
         }
      };
   }
}
