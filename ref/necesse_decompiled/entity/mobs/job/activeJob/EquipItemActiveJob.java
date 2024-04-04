package necesse.entity.mobs.job.activeJob;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.InventoryItem;

public class EquipItemActiveJob extends UseItemActiveJob {
   public int inventorySlot;
   public HumanMob humanMob;

   public EquipItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, AtomicReference<InventoryItem> var3, int var4, HumanMob var5) {
      super(var1, var2, var3);
      this.inventorySlot = var4;
      this.humanMob = var5;
   }

   public EquipItemActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, InventoryItem var3, int var4, HumanMob var5) {
      super(var1, var2, var3);
      this.inventorySlot = var4;
      this.humanMob = var5;
   }

   public boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2) {
      Mob var3 = this.worker.getMobWorker();
      this.worker.showPickupAnimation(this.worker.getMobWorker().dir == 3 ? var3.getX() - 10 : var3.getX() + 10, var3.getY(), var1.item, 200);
      InventoryItem var4 = this.humanMob.equipmentInventory.getItem(this.inventorySlot);
      this.humanMob.equipmentInventory.setItem(this.inventorySlot, var1.copy(1));
      var1.setAmount(var1.getAmount() - 1);
      if (var1.getAmount() <= 0) {
         var2.remove();
         this.worker.getWorkInventory().markDirty();
      }

      if (var4 != null) {
         var2.add(var4);
         this.worker.getWorkInventory().markDirty();
      }

      return true;
   }
}
