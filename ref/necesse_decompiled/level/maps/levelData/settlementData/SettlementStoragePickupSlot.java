package necesse.level.maps.levelData.settlementData;

import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.util.GameLinkedList;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class SettlementStoragePickupSlot {
   private GameLinkedList<SettlementStoragePickupSlot>.Element element;
   public final LevelStorage storage;
   public final int slot;
   public final InventoryItem item;
   protected int prevAmountReserved;
   protected long reserveTick;

   protected SettlementStoragePickupSlot(LevelStorage var1, int var2, InventoryItem var3, int var4) {
      this.storage = var1;
      this.slot = var2;
      this.prevAmountReserved = var4;
      this.item = var3;
   }

   protected void init(GameLinkedList<SettlementStoragePickupSlot>.Element var1, WorldEntity var2) {
      if (this.element != null) {
         throw new IllegalStateException("Storage slot already initialized");
      } else {
         this.element = var1;
         this.reserve(var2);
      }
   }

   public void reserve(WorldEntity var1) {
      this.reserveTick = var1.getGameTicks();
   }

   public void reserve(Entity var1) {
      this.reserve(var1.getWorldEntity());
   }

   public boolean isReserved(WorldEntity var1) {
      return this.reserveTick >= var1.getGameTicks() - 2L;
   }

   protected void removeUnsafe() {
      this.element.remove();
   }

   public void remove() {
      SettlementStoragePickupSlot var10000;
      for(GameLinkedList.Element var1 = this.element; var1.hasPrev(); var10000.prevAmountReserved -= this.item.getAmount()) {
         var1 = var1.prev();
         var10000 = (SettlementStoragePickupSlot)var1.object;
      }

      this.element.remove();
   }

   public boolean isValid(Inventory var1) {
      if (this.element.isRemoved()) {
         return false;
      } else {
         InventoryItem var2 = var1 == null ? null : var1.getItem(this.slot);
         if (var2 != null && var2.equals(this.storage.level, this.item, true, false, "pickups") && var2.getAmount() >= this.item.getAmount()) {
            return true;
         } else {
            this.remove();
            return false;
         }
      }
   }

   public InventoryItem pickupItem(Inventory var1) {
      if (this.element.isRemoved()) {
         return null;
      } else {
         InventoryItem var2 = var1 == null ? null : var1.getItem(this.slot);
         if (var2 != null && var2.equals(this.storage.level, this.item, true, false, "pickups") && var2.getAmount() >= this.item.getAmount()) {
            InventoryItem var3 = var2.copy(this.item.getAmount());
            var1.setAmount(this.slot, var2.getAmount() - var3.getAmount());
            this.remove();
            return var3;
         } else {
            this.remove();
            return null;
         }
      }
   }

   public boolean isRemoved() {
      return this.element.isRemoved();
   }

   public PickupSettlementStorageActiveJob toPickupJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, AtomicReference<InventoryItem> var3) {
      return new PickupSettlementStorageActiveJob(var1, var2, this.storage.tileX, this.storage.tileY, this, var3);
   }

   public PickupSettlementStorageActiveJob toPickupJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2) {
      return this.toPickupJob(var1, var2, new AtomicReference());
   }
}
