package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;

public class ConsumeItemActiveJob extends UseItemActiveJob {
   public HungerMob hungerMob;

   public ConsumeItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, AtomicReference<InventoryItem> var3, HungerMob var4) {
      super(var1, var2, var3);
      this.hungerMob = var4;
   }

   public ConsumeItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, InventoryItem var3, HungerMob var4) {
      super(var1, var2, var3);
      this.hungerMob = var4;
   }

   public boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2) {
      if (var1.item.isFoodItem()) {
         FoodConsumableItem var3 = (FoodConsumableItem)var1.item;
         Mob var4 = this.worker.getMobWorker();
         this.worker.showPickupAnimation(this.worker.getMobWorker().dir == 3 ? var4.getX() - 10 : var4.getX() + 10, var4.getY(), var3, 200);
         this.hungerMob.useFoodItem(var3, true);
         if (var3.singleUse) {
            var1.setAmount(var1.getAmount() - 1);
            if (var1.getAmount() <= 0) {
               var2.remove();
               this.worker.getWorkInventory().markDirty();
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
