package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class UseItemActiveJob extends ActiveJob {
   AtomicReference<InventoryItem> item;

   public UseItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, AtomicReference<InventoryItem> var3) {
      super(var1, var2);
      this.item = var3;
   }

   public UseItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, InventoryItem var3) {
      this(var1, var2, new AtomicReference(var3));
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
      if (this.item == null) {
         return false;
      } else if (var1) {
         InventoryItem var2 = (InventoryItem)this.item.get();
         return var2 == null ? false : this.worker.getWorkInventory().stream().anyMatch((var2x) -> {
            return var2x.equals(this.getLevel(), var2, true, false, "equals") && var2x.getAmount() >= var2.getAmount();
         });
      } else {
         return true;
      }
   }

   public ActiveJobResult perform() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         InventoryItem var1 = (InventoryItem)this.item.get();
         if (var1 == null) {
            return ActiveJobResult.FAILED;
         } else {
            ListIterator var2 = this.worker.getWorkInventory().listIterator();

            InventoryItem var3;
            do {
               if (!var2.hasNext()) {
                  return ActiveJobResult.FAILED;
               }

               var3 = (InventoryItem)var2.next();
            } while(!var1.equals(this.getLevel(), var3, true, false, "equals") || var3.getAmount() < var1.getAmount() || !this.useItem(var3, var2));

            return ActiveJobResult.FINISHED;
         }
      }
   }

   public abstract boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2);
}
