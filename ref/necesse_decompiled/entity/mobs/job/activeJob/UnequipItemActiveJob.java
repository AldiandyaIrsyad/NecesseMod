package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public class UnequipItemActiveJob extends ActiveJob {
   public int inventorySlot;
   public HumanMob humanMob;

   public UnequipItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, int var3, HumanMob var4) {
      super(var1, var2);
      this.inventorySlot = var3;
      this.humanMob = var4;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return null;
   }

   public boolean isAt(JobMoveToTile var1) {
      return true;
   }

   public void tick(boolean var1, boolean var2) {
   }

   public boolean isValid(boolean var1) {
      if (var1) {
         return !this.humanMob.getInventory().isSlotClear(this.inventorySlot);
      } else {
         return true;
      }
   }

   public ActiveJobResult perform() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         InventoryItem var1 = this.humanMob.getInventory().getItem(this.inventorySlot);
         if (var1 == null) {
            return ActiveJobResult.FAILED;
         } else {
            this.worker.showPlaceAnimation(this.worker.getMobWorker().dir == 3 ? this.humanMob.getX() - 10 : this.humanMob.getX() + 10, this.humanMob.getY(), var1.item, 200);
            this.humanMob.getInventory().setItem(this.inventorySlot, (InventoryItem)null);
            this.worker.getWorkInventory().add(var1.copy());
            this.worker.getWorkInventory().markDirty();
            return ActiveJobResult.FINISHED;
         }
      }
   }
}
