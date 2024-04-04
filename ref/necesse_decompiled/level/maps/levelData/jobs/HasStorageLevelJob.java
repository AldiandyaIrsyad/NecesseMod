package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HasStorageLevelJob extends LevelJob {
   public SettlementInventory settlementInventory;
   private Supplier<Boolean> validCheck;

   public HasStorageLevelJob(SettlementInventory var1, Supplier<Boolean> var2) {
      super(var1.tileX, var1.tileY);
      this.settlementInventory = var1;
      this.validCheck = var2;
      this.reservable = new GameObjectReservable() {
         public boolean isAvailable(Object var1, WorldEntity var2) {
            return true;
         }
      };
   }

   public HasStorageLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isValid() {
      return this.getInventory() != null && (Boolean)this.validCheck.get();
   }

   public Inventory getInventory() {
      ObjectEntity var1 = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
      return var1 instanceof OEInventory ? ((OEInventory)var1).getInventory() : null;
   }

   public int getSameJobPriority() {
      return this.settlementInventory.priority;
   }

   public boolean prioritizeForSameJobAgain() {
      return true;
   }

   public static <T extends HasStorageLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1) {
      if (var0.getWorkInventory().isEmpty()) {
         return null;
      } else {
         Iterator var2 = var0.getWorkInventory().items().iterator();

         InventoryItem var3;
         int var4;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            var3 = (InventoryItem)var2.next();
            var4 = ((HasStorageLevelJob)var1.job).settlementInventory.canAddFutureDropOff(var3);
         } while(var4 <= 0);

         InventoryItem var5 = var3.copy();
         LocalMessage var6 = new LocalMessage("activities", "droppingoffinv");
         return new SingleJobSequence(new DropOffSettlementStorageActiveJob(var0, var1.priority, ((HasStorageLevelJob)var1.job).settlementInventory, (GameObjectReservable)null, (Supplier)null, false, () -> {
            return var5;
         }), var6);
      }
   }

   public static Stream<HasStorageLevelJob> streamStorageJobs(EntityJobWorker var0, JobTypeHandler.SubHandler<?> var1) {
      Mob var2 = var0.getMobWorker();
      JobsLevelData var3 = JobsLevelData.getJobsLevelData(var2.getLevel());
      ZoneTester var4 = var0.getJobRestrictZone();
      Point var5 = var0.getJobSearchTile();
      int var6 = LevelJobRegistry.hasStorageID;
      if (var1 == null) {
         var1 = var0.getJobTypeHandler().getJobHandler(var6);
      }

      GameTileRange var7 = (GameTileRange)var1.tileRange.apply(var2.getLevel());
      return var3.streamJobsInRegionsShape(var7.getRangeBounds(var5), 0).filter((var1x) -> {
         return ((LevelJob)var1x.object).getID() == var6;
      }).filter((var1x) -> {
         return var4.containsTile(((LevelJob)var1x.object).tileX, ((LevelJob)var1x.object).tileY);
      }).filter((var2x) -> {
         return var7.isWithinRange(var5, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
      }).map((var0x) -> {
         return (HasStorageLevelJob)var0x.object;
      });
   }

   public static GameAreaStream<HasStorageLevelJob> streamStorageJobsArea(EntityJobWorker var0, JobTypeHandler.SubHandler<?> var1) {
      Mob var2 = var0.getMobWorker();
      JobsLevelData var3 = JobsLevelData.getJobsLevelData(var2.getLevel());
      ZoneTester var4 = var0.getJobRestrictZone();
      Point var5 = var0.getJobSearchTile();
      int var6 = LevelJobRegistry.hasStorageID;
      if (var1 == null) {
         var1 = var0.getJobTypeHandler().getJobHandler(var6);
      }

      GameTileRange var7 = (GameTileRange)var1.tileRange.apply(var2.getLevel());
      return var3.streamAreaJobs(var5.x, var5.y, var7.maxRange).filter((var1x) -> {
         return ((LevelJob)var1x.object).getID() == var6;
      }).filter((var1x) -> {
         return var4.containsTile(((LevelJob)var1x.object).tileX, ((LevelJob)var1x.object).tileY);
      }).filter((var2x) -> {
         return var7.isWithinRange(var5, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
      }).map((var0x) -> {
         return (HasStorageLevelJob)var0x.object;
      });
   }

   public static Stream<SettlementStoragePickupFuture> findPickupItems(EntityJobWorker var0, JobTypeHandler.SubHandler<?> var1, Predicate<InventoryItem> var2) {
      return streamStorageJobs(var0, var1).filter((var1x) -> {
         return var0.estimateCanMoveTo(var1x.settlementInventory.tileX, var1x.settlementInventory.tileY, true);
      }).flatMap((var1x) -> {
         return var1x.settlementInventory.findFutureUnreservedSlots().filter((var1) -> {
            return var2.test(var1.item);
         });
      });
   }

   public static int getItemCount(EntityJobWorker var0, Predicate<InventoryItem> var1, int var2, boolean var3, boolean var4) {
      Mob var5 = var0.getMobWorker();
      JobsLevelData var6 = JobsLevelData.getJobsLevelData(var5.getLevel());
      ZoneTester var7 = var0.getJobRestrictZone();
      Point var8 = var0.getJobSearchTile();
      AtomicInteger var9 = new AtomicInteger();
      int var10 = LevelJobRegistry.hasStorageID;
      JobTypeHandler.SubHandler var11 = var0.getJobTypeHandler().getJobHandler(var10);
      GameTileRange var12 = (GameTileRange)var11.tileRange.apply(var5.getLevel());
      var9.addAndGet((Integer)((Stream)var6.streamJobsInRegionsShape(var12.getRangeBounds(var8), 0).filter((var1x) -> {
         return ((LevelJob)var1x.object).getID() == var10;
      }).filter((var1x) -> {
         return var7.containsTile(((LevelJob)var1x.object).tileX, ((LevelJob)var1x.object).tileY);
      }).filter((var2x) -> {
         return var12.isWithinRange(var8, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
      }).map((var0x) -> {
         return (HasStorageLevelJob)var0x.object;
      }).sequential()).reduce(0, (var4x, var5x) -> {
         int var6 = var2 - var4x - var9.get();
         if (var6 > 0) {
            var4x = var4x + var5x.settlementInventory.getItemCount(var1, var6, var3);
         }

         return var4x;
      }, Integer::sum));
      if (var9.get() < var2 && var4) {
         var10 = LevelJobRegistry.useWorkstationID;
         var11 = var0.getJobTypeHandler().getJobHandler(var10);
         var12 = (GameTileRange)var11.tileRange.apply(var5.getLevel());
         var9.addAndGet((Integer)((Stream)var6.streamJobsInRegionsShape(var12.getRangeBounds(var8), 0).filter((var1x) -> {
            return ((LevelJob)var1x.object).getID() == var10;
         }).filter((var1x) -> {
            return var7.containsTile(((LevelJob)var1x.object).tileX, ((LevelJob)var1x.object).tileY);
         }).filter((var2x) -> {
            return var12.isWithinRange(var8, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
         }).map((var0x) -> {
            return (UseWorkstationLevelJob)var0x.object;
         }).sequential()).reduce(0, (var3x, var4x) -> {
            int var5 = var2 - var3x - var9.get();
            if (var5 > 0) {
               SettlementWorkstationLevelObject var6 = var4x.workstation.getWorkstationObject();
               if (var6 != null) {
                  int var7 = 0;
                  Iterator var8 = var6.getCurrentAndFutureProcessingOutputs().iterator();

                  while(var8.hasNext()) {
                     InventoryItem var9x = (InventoryItem)var8.next();
                     if (var9x != null && var1.test(var9x)) {
                        var7 += var9x.getAmount();
                        if (var7 >= var5) {
                           return var5;
                        }
                     }
                  }

                  var3x = var3x + var7;
               }
            }

            return var3x;
         }, Integer::sum));
      }

      return Math.min(var9.get(), var2);
   }

   public static ArrayList<DropOffFind> findDropOffLocation(EntityJobWorker var0, InventoryItem var1) {
      return findDropOffLocation(var0, var1, (Point)null);
   }

   public static ArrayList<DropOffFind> findDropOffLocation(EntityJobWorker var0, InventoryItem var1, Point var2) {
      Mob var3 = var0.getMobWorker();
      int var4 = LevelJobRegistry.hasStorageID;
      JobTypeHandler.SubHandler var5 = var0.getJobTypeHandler().getJobHandler(var4);
      JobsLevelData var6 = JobsLevelData.getJobsLevelData(var3.getLevel());
      ZoneTester var7 = var0.getJobRestrictZone();
      Point var8 = var0.getJobSearchTile();
      GameTileRange var9 = (GameTileRange)var5.tileRange.apply(var3.getLevel());
      GameLinkedList var10 = (GameLinkedList)var6.streamJobsInRegionsShape(var9.getRangeBounds(var8), 0).filter((var1x) -> {
         return ((LevelJob)var1x.object).getID() == var4;
      }).filter((var1x) -> {
         return var7.containsTile(((LevelJob)var1x.object).tileX, ((LevelJob)var1x.object).tileY);
      }).filter((var2x) -> {
         return var9.isWithinRange(var8, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
      }).map((var0x) -> {
         return (HasStorageLevelJob)var0x.object;
      }).map((var1x) -> {
         return new ComputedObjectValue(var1x, () -> {
            int var2 = var1x.settlementInventory.canAddFutureDropOff(var1);
            return Math.max(var2, 0);
         });
      }).filter((var0x) -> {
         return (Integer)var0x.get() > 0;
      }).filter((var1x) -> {
         return var0.estimateCanMoveTo(((HasStorageLevelJob)var1x.object).tileX, ((HasStorageLevelJob)var1x.object).tileY, true);
      }).collect(GameLinkedList::new, GameLinkedList::add, GameLinkedList::addAll);
      ArrayList var11 = new ArrayList();
      HasStorageLevelJob var12 = null;
      int var13 = 0;

      while(var13 < var1.getAmount() && !var10.isEmpty()) {
         Point var14;
         if (var12 != null) {
            var14 = new Point(var12.tileX * 32 + 16, var12.tileY * 32 + 16);
         } else if (var2 != null) {
            var14 = var2;
         } else {
            var14 = new Point(var3.getX(), var3.getY());
         }

         Comparator var15 = (var0x, var1x) -> {
            return -Integer.compare(((HasStorageLevelJob)((ComputedObjectValue)var0x.object).object).getSameJobPriority(), ((HasStorageLevelJob)((ComputedObjectValue)var1x.object).object).getSameJobPriority());
         };
         var15 = var15.thenComparingDouble((var1x) -> {
            return var14.distance((double)(((HasStorageLevelJob)((ComputedObjectValue)var1x.object).object).tileX * 32 + 16), (double)(((HasStorageLevelJob)((ComputedObjectValue)var1x.object).object).tileY * 32 + 16));
         });
         GameLinkedList.Element var16 = (GameLinkedList.Element)var10.streamElements().min(var15).orElse((Object)null);
         if (var16 == null) {
            break;
         }

         int var17 = var1.getAmount() - var13;
         int var18 = Math.min((Integer)((ComputedObjectValue)var16.object).get(), var17);
         InventoryItem var19 = var1.copy(var18);
         var11.add(new DropOffFind(((HasStorageLevelJob)((ComputedObjectValue)var16.object).object).settlementInventory, var19));
         var13 += var18;
         if (var13 >= var1.getAmount()) {
            break;
         }

         var12 = (HasStorageLevelJob)((ComputedObjectValue)var16.object).object;
         var16.remove();
      }

      return var11;
   }

   public static class DropOffFind {
      public final SettlementInventory inventory;
      public final InventoryItem item;

      public DropOffFind(SettlementInventory var1, InventoryItem var2) {
         this.inventory = var1;
         this.item = var2;
      }

      public DropOffSettlementStorageActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, GameObjectReservable var3, Supplier<Boolean> var4, boolean var5) {
         return new DropOffSettlementStorageActiveJob(var1, var2, this.inventory, var3, var4, var5, () -> {
            return this.item;
         });
      }

      public DropOffSettlementStorageActiveJob getActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, boolean var3) {
         return new DropOffSettlementStorageActiveJob(var1, var2, this.inventory, (GameObjectReservable)null, (Supplier)null, var3, () -> {
            return this.item;
         });
      }
   }
}
