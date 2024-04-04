package necesse.entity.mobs.job.activeJob;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameLinkedList;
import necesse.entity.Entity;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.ItemPickupReservedAmount;
import necesse.inventory.InventoryItem;

public class PickupItemEntityActiveJob extends EntityActiveJob<ItemPickupEntity> {
   protected LinkedList<ItemPickupReservedAmount> pickups = new LinkedList();
   protected GameLinkedList<ActiveJob>.Element followCombinedSequence;

   public PickupItemEntityActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, ItemPickupReservedAmount var3, GameTileRange var4) {
      super(var1, var2, var3.entity, var4);
      this.addPickup(var3);
      this.acceptAdjacentTiles = true;
   }

   public void addPickup(ItemPickupReservedAmount var1) {
      this.pickups.add(var1);
      var1.onItemCombined((var1x) -> {
         if (this.followCombinedSequence != null && var1x.next.canBePickedUpBySettlers() && var1x.next.item.equals(this.getLevel(), ((ItemPickupEntity)this.target).item, true, false, "equals")) {
            ItemPickupReservedAmount var2 = var1x.next.reservePickupAmount(var1x.combinedAmount);
            if (var2 != null) {
               Iterator var3 = this.followCombinedSequence.getList().iterator();

               while(var3.hasNext()) {
                  ActiveJob var4 = (ActiveJob)var3.next();
                  if (var4 instanceof PickupItemEntityActiveJob) {
                     PickupItemEntityActiveJob var5 = (PickupItemEntityActiveJob)var4;
                     if (var5.target == var1x.next) {
                        var5.addPickup(var2);
                        return;
                     }
                  }
               }

               PickupItemEntityActiveJob var6 = new PickupItemEntityActiveJob(this.worker, this.priority, var2, this.maxRange);
               if (this.followCombinedSequence.isRemoved()) {
                  var6.setFollowCombinedSequence(this.followCombinedSequence.getList().addLast(var6));
               } else {
                  var6.setFollowCombinedSequence(this.followCombinedSequence.insertAfter(var6));
               }
            }
         }

      });
   }

   public void setFollowCombinedSequence(GameLinkedList<ActiveJob>.Element var1) {
      this.followCombinedSequence = var1;
   }

   public int getDirectMoveToDistance() {
      return super.getDirectMoveToDistance();
   }

   public MobMovement getDirectMoveTo(ItemPickupEntity var1) {
      return new MobMovementLevelPos(var1.x, var1.y);
   }

   public void tick(boolean var1, boolean var2) {
      Iterator var3 = this.pickups.iterator();

      while(var3.hasNext()) {
         ItemPickupReservedAmount var4 = (ItemPickupReservedAmount)var3.next();
         var4.reserve((Entity)this.worker.getMobWorker());
      }

   }

   public boolean isAtTarget() {
      return this.getDistanceToTarget() <= (double)this.getCompleteRange();
   }

   public boolean shouldClearSequence() {
      return false;
   }

   public boolean isJobValid(boolean var1) {
      if (!var1) {
         return true;
      } else if (!((ItemPickupEntity)this.target).canBePickedUpBySettlers()) {
         return false;
      } else {
         this.pickups.removeIf((var0) -> {
            return !var0.isValid();
         });
         return !this.pickups.isEmpty();
      }
   }

   public void onMadeCurrent() {
      super.onMadeCurrent();
   }

   public ActiveJobResult performTarget() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         boolean var1 = true;

         for(Iterator var2 = this.pickups.iterator(); var2.hasNext(); var1 = false) {
            ItemPickupReservedAmount var3 = (ItemPickupReservedAmount)var2.next();
            InventoryItem var4 = var3.pickupItem();
            if (var1) {
               this.worker.showPickupAnimation(((ItemPickupEntity)this.target).getX(), ((ItemPickupEntity)this.target).getY(), var4.item, 250);
            }

            this.worker.getWorkInventory().add(var4);
         }

         return ActiveJobResult.FINISHED;
      }
   }

   public static void addItemPickupJobs(EntityJobWorker var0, JobTypeHandler.TypePriority var1, List<ItemPickupEntity> var2, GameLinkedListJobSequence var3) {
      addItemPickupJobs(var0, var1, (GameTileRange)null, var2, var3);
   }

   public static void addItemPickupJobs(EntityJobWorker var0, JobTypeHandler.TypePriority var1, GameTileRange var2, List<ItemPickupEntity> var3, GameLinkedListJobSequence var4) {
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         ItemPickupEntity var6 = (ItemPickupEntity)var5.next();
         if (var6.canBePickedUpBySettlers()) {
            ItemPickupReservedAmount var7 = var6.reservePickupAmount(Math.min(var6.item.getAmount(), var6.item.itemStackSize()));
            if (var7 != null) {
               PickupItemEntityActiveJob var8 = new PickupItemEntityActiveJob(var0, var1, var7, var2);
               var8.setFollowCombinedSequence(var4.addLast(var8));
            }
         }
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public MobMovement getDirectMoveTo(Entity var1) {
      return this.getDirectMoveTo((ItemPickupEntity)var1);
   }
}
