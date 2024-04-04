package necesse.level.maps.levelData.settlementData.storage;

import java.awt.Point;
import java.awt.Shape;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapGameLinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.regionSystem.LevelRegionsSpliterator;
import necesse.level.maps.regionSystem.RegionPosition;

public class SettlementStorageRecordsRegionData {
   protected SettlementStorageIndex index;
   protected HashMapGameLinkedList<Point, SettlementStorageRecord>[][] records;
   protected Predicate<InventoryItem> indexFilter;

   public SettlementStorageRecordsRegionData(SettlementStorageIndex var1, Predicate<InventoryItem> var2) {
      this.index = var1;
      this.records = new HashMapGameLinkedList[var1.level.regionManager.getRegionsWidth()][var1.level.regionManager.getRegionsHeight()];
      this.indexFilter = var2;
   }

   public HashMapGameLinkedList<Point, SettlementStorageRecord> getRecordsInRegion(int var1, int var2) {
      HashMapGameLinkedList var3 = this.records[var1][var2];
      if (var3 == null) {
         var3 = new HashMapGameLinkedList();
         this.records[var1][var2] = var3;
      }

      return var3;
   }

   public HashMapGameLinkedList<Point, SettlementStorageRecord> getRecordsInRegion(RegionPosition var1) {
      return this.getRecordsInRegion(var1.regionX, var1.regionY);
   }

   public Stream<SettlementStorageRecord> streamRecordsInTile(int var1, int var2) {
      RegionPosition var3 = this.index.level.regionManager.getRegionPosByTile(var1, var2);
      return this.getRecordsInRegion(var3).stream(new Point(var1, var2));
   }

   public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamRecordsInRegionsShape(Shape var1, int var2) {
      return (new LevelRegionsSpliterator(this.index.level, var1, var2)).stream().map(this::getRecordsInRegion);
   }

   public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamInRegionsInRange(float var1, float var2, int var3) {
      return this.streamRecordsInRegionsShape(GameUtils.rangeBounds(var1, var2, var3), 0);
   }

   public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamInRegionsInTileRange(int var1, int var2, int var3) {
      return this.streamRecordsInRegionsShape(GameUtils.rangeTileBounds(var1, var2, var3), 0);
   }

   public Stream<HashMapGameLinkedList<Point, SettlementStorageRecord>> streamAllRecords() {
      Stream.Builder var1 = Stream.builder();
      HashMapGameLinkedList[][] var2 = this.records;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         HashMapGameLinkedList[] var5 = var2[var4];
         if (var5 != null) {
            HashMapGameLinkedList[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               HashMapGameLinkedList var9 = var6[var8];
               if (var9 != null) {
                  var1.add(var9);
               }
            }
         }
      }

      return var1.build();
   }

   public void add(SettlementStorageRecord var1) {
      RegionPosition var2 = this.index.level.regionManager.getRegionPosByTile(var1.storage.tileX, var1.storage.tileY);
      HashMapGameLinkedList var3 = this.getRecordsInRegion(var2.regionX, var2.regionY);
      var3.add(new Point(var1.storage.tileX, var1.storage.tileY), var1);
   }

   public InventoryItem validateItem(SettlementStorageRecord var1, Runnable var2) {
      if (var2 == null) {
         var2 = () -> {
         };
      }

      int var3 = var1.inventorySlot;
      InventoryRange var4 = var1.storage.getInventoryRange();
      if (var4 == null) {
         var2.run();
         return null;
      } else if (var3 >= var4.startSlot && var3 <= var4.endSlot) {
         InventoryItem var5 = var4.inventory.getItem(var3);
         if (var5 != null && this.indexFilter.test(var5)) {
            var1.itemAmount = var5.getAmount();
            return var5;
         } else {
            var2.run();
            return null;
         }
      } else {
         var2.run();
         return null;
      }
   }

   public InventoryItem validateItem(GameLinkedList<SettlementStorageRecord>.Element var1) {
      SettlementStorageRecord var10001 = (SettlementStorageRecord)var1.object;
      Objects.requireNonNull(var1);
      return this.validateItem(var10001, var1::remove);
   }

   public GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> getRecords(EntityJobWorker var1) {
      Point var2 = var1.getJobSearchTile();
      GameTileRange var3 = (GameTileRange)var1.getJobTypeHandler().getJobHandler(LevelJobRegistry.hasStorageID).tileRange.apply(this.index.level);
      ZoneTester var4 = var1.getJobRestrictZone();
      return this.getRecords(var2, var3, (var2x) -> {
         return var4.containsTile(var2x.x, var2x.y) && var1.estimateCanMoveTo(var2x.x, var2x.y, true);
      });
   }

   public GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> getRecords(Point var1, GameTileRange var2, Predicate<Point> var3) {
      return (GameLinkedList)this.streamRecordsInRegionsShape(var2.getRangeBounds(var1), 0).flatMap((var0) -> {
         return var0.entrySet().stream();
      }).filter((var2x) -> {
         return var2.isWithinRange(var1, (Point)var2x.getKey());
      }).filter((var1x) -> {
         return var3.test((Point)var1x.getKey());
      }).collect(GameLinkedList::new, GameLinkedList::add, GameLinkedList::addAll);
   }

   public ItemFinder startFinder(EntityJobWorker var1) {
      Mob var2 = var1.getMobWorker();
      Point var3 = new Point(var2.getTileX(), var2.getTileY());
      Point var4 = var1.getJobSearchTile();
      GameTileRange var5 = (GameTileRange)var1.getJobTypeHandler().getJobHandler(LevelJobRegistry.hasStorageID).tileRange.apply(this.index.level);
      ZoneTester var6 = var1.getJobRestrictZone();
      return this.startFinder(var4, var3, var5, (var2x) -> {
         return var6.containsTile(var2x.x, var2x.y) && var1.estimateCanMoveTo(var2x.x, var2x.y, true);
      });
   }

   public ItemFinder startFinder(Point var1, Point var2, GameTileRange var3, Predicate<Point> var4) {
      return (ItemFinder)Performance.record(this.index.level.tickManager(), "findItems", (Supplier)(() -> {
         return new ItemFinder(this, var2, this.getRecords(var1, var3, var4), this.indexFilter);
      }));
   }

   public int countItems(Point var1, GameTileRange var2, Predicate<Point> var3, Predicate<InventoryItem> var4) {
      Level var5 = this.index.level;
      return (Integer)Performance.record(var5.tickManager(), "countItems", (Supplier)(() -> {
         return (Integer)this.streamRecordsInRegionsShape(var2.getRangeBounds(var1), 0).flatMap((var0) -> {
            return var0.entrySet().stream();
         }).filter((var2x) -> {
            return var2.isWithinRange(var1, (Point)var2x.getKey());
         }).filter((var1x) -> {
            return var3.test((Point)var1x.getKey());
         }).reduce(0, (var1x, var2x) -> {
            GameLinkedList var3 = (GameLinkedList)var2x.getValue();
            int var4x = var1x;
            Iterator var5 = var3.iterator();

            while(true) {
               InventoryItem var8;
               do {
                  do {
                     SettlementStorageRecord var6;
                     InventoryRange var7;
                     do {
                        if (!var5.hasNext()) {
                           return var4x;
                        }

                        var6 = (SettlementStorageRecord)var5.next();
                        var7 = var6.storage.getInventoryRange();
                     } while(var7 == null);

                     var8 = var7.inventory.getItem(var6.inventorySlot);
                  } while(var8 == null);
               } while(var4 != null && !var4.test(var8));

               var4x += var8.getAmount();
            }
         }, Integer::sum);
      }));
   }
}
