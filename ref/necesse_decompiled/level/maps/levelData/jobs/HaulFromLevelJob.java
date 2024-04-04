package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import necesse.engine.GameTileRange;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.levelData.settlementData.LevelStorage;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HaulFromLevelJob extends LevelJob {
   public LevelStorage storage;
   public boolean onlyAcceptSpecificAmount = false;
   public InventoryItem item;
   public LinkedList<HaulPosition> dropOffPositions = new LinkedList();

   public HaulFromLevelJob(LevelStorage var1, InventoryItem var2) {
      super(var1.tileX, var1.tileY);
      this.storage = var1;
      this.item = var2;
   }

   public HaulFromLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isSameJob(LevelJob var1) {
      if (this.isSameTile(var1) && var1.getID() == this.getID()) {
         HaulFromLevelJob var2 = (HaulFromLevelJob)var1;
         return this.item.equals(this.getLevel(), var2.item, true, false, "equals");
      } else {
         return false;
      }
   }

   public boolean isValid() {
      if (this.item != null && this.item.getAmount() > 0 && !this.dropOffPositions.isEmpty()) {
         InventoryRange var1 = this.getInventoryRange();
         if (var1 != null) {
            int var2 = 0;

            for(int var3 = var1.startSlot; var3 <= var1.endSlot; ++var3) {
               if (!var1.inventory.isSlotClear(var3)) {
                  InventoryItem var4 = var1.inventory.getItem(var3);
                  if (this.item.equals(this.getLevel(), var4, true, false, "dropoff")) {
                     var2 += var4.getAmount();
                     if (this.onlyAcceptSpecificAmount) {
                        if (var2 >= this.item.getAmount()) {
                           return true;
                        }
                     } else if (var2 > 0) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public InventoryRange getInventoryRange() {
      return this.storage.getInventoryRange();
   }

   public static <T extends HaulFromLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1) {
      return ((HaulFromLevelJob)var1.job).getJobSequence(var0, var1.priority);
   }

   public JobSequence getJobSequence(EntityJobWorker var1, JobTypeHandler.TypePriority var2) {
      ZoneTester var3 = var1.getJobRestrictZone();
      GameTileRange var4 = (GameTileRange)var1.getJobTypeHandler().getJobHandler(HaulFromLevelJob.class).tileRange.apply(this.getLevel());
      InventoryRange var5 = this.getInventoryRange();
      if (var5 != null) {
         LinkedList var6 = new LinkedList();
         ListIterator var7 = this.dropOffPositions.listIterator();

         while(true) {
            while(true) {
               HaulPosition var8;
               InventoryRange var9;
               int var10;
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           if (!var6.isEmpty()) {
                              Comparator var19 = Comparator.comparingInt((var0) -> {
                                 return -((HaulPosition)var0.object).priority;
                              });
                              var19 = var19.thenComparingDouble(ComputedValue::get);
                              HaulPosition var20 = (HaulPosition)var6.stream().map((var1x) -> {
                                 return new ComputedObjectValue(var1x, () -> {
                                    return (new Point(this.tileX * 32 + 16, this.tileY * 32 + 16)).distance((double)(var1x.storage.tileX * 32 + 16), (double)(var1x.storage.tileY * 32 + 16));
                                 });
                              }).min(var19).map((var0) -> {
                                 return (HaulPosition)var0.object;
                              }).orElse((Object)null);
                              if (var20 != null) {
                                 var10 = Math.min(var20.storage.canAddFutureDropOff(this.item), Math.min(var20.amount, this.item.itemStackSize()));
                                 if (var10 > 0) {
                                    LinkedList var11 = this.storage.findUnreservedSlots(this.item.copy(), var10, var10);
                                    if (var11 != null && !var11.isEmpty()) {
                                       GameMessage var12 = this.storage.getInventoryName();
                                       GameMessage var13 = ((SettlementStoragePickupSlot)var11.getFirst()).item.getItemLocalization();
                                       LocalMessage var14 = new LocalMessage("activities", "hauling", new Object[]{"item", var13, "target", var12});
                                       LinkedListJobSequence var15 = new LinkedListJobSequence(var14);
                                       Iterator var16 = var11.iterator();

                                       while(var16.hasNext()) {
                                          SettlementStoragePickupSlot var17 = (SettlementStoragePickupSlot)var16.next();
                                          AtomicReference var18 = new AtomicReference();
                                          var15.addFirst(new PickupSettlementStorageActiveJob(var1, var2, this.tileX, this.tileY, var17, var18));
                                          var15.addLast(new DropOffSettlementStorageActiveJob(var1, var2, var20.storage, (GameObjectReservable)null, (Supplier)null, false, () -> {
                                             return var17.isRemoved() ? (InventoryItem)var18.get() : var17.item;
                                          }));
                                          this.item.setAmount(this.item.getAmount() - var10);
                                       }

                                       return var15;
                                    }
                                 }

                                 return null;
                              }
                           }

                           return null;
                        }

                        var8 = (HaulPosition)var7.next();
                     } while(!var3.containsTile(var8.storage.tileX, var8.storage.tileY));
                  } while(!var4.isWithinRange(var1.getJobSearchTile(), var8.storage.tileX, var8.storage.tileY));

                  var9 = var8.getInventoryRange();
               } while(var9 == null);

               var10 = var8.storage.getFilter().getAddAmount(this.getLevel(), this.item, var9, false);
               if (var10 > 0 && var9.inventory.canAddItem(this.getLevel(), (PlayerMob)null, this.item, var9.startSlot, var9.endSlot, "hauljob") > 0) {
                  var6.add(var8);
               } else {
                  var7.remove();
               }
            }
         }
      } else {
         return null;
      }
   }

   public static class HaulPosition {
      public final LevelStorage storage;
      public final int priority;
      public final int amount;

      public HaulPosition(LevelStorage var1, int var2, int var3) {
         this.storage = var1;
         this.priority = var2;
         this.amount = var3;
      }

      public InventoryRange getInventoryRange() {
         return this.storage.getInventoryRange();
      }
   }
}
