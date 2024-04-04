package necesse.level.maps.levelData.settlementData.storage;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameLinkedList;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;

public class ItemFinder {
   private SettlementStorageRecordsRegionData regionData;
   private Point currentTile;
   private GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> records;

   public ItemFinder(SettlementStorageRecordsRegionData var1, Point var2, GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> var3, Predicate<InventoryItem> var4) {
      this.regionData = var1;
      this.currentTile = var2;
      this.records = var3;
   }

   public LinkedList<SettlementStoragePickupSlot> findPickupSlots(int var1, int var2, Predicate<InventoryItem> var3) {
      return (LinkedList)Performance.record(this.regionData.index.level.tickManager(), "findItems", (Supplier)(() -> {
         int var4 = 0;
         Point var5 = this.currentTile;
         HashSet var6 = new HashSet();
         LinkedList var7 = new LinkedList();

         while(var4 < var2 && !this.records.isEmpty()) {
            GameLinkedList.Element var8 = (GameLinkedList.Element)this.records.streamElements().filter((var1x) -> {
               return !var6.contains(((Map.Entry)var1x.object).getKey());
            }).min(Comparator.comparingDouble((var1x) -> {
               return this.currentTile.distance((Point2D)((Map.Entry)var1x.object).getKey());
            })).orElse((Object)null);
            if (var8 == null) {
               break;
            }

            var6.add((Point)((Map.Entry)var8.object).getKey());
            Point var9 = (Point)((Map.Entry)var8.object).getKey();
            GameLinkedList var10 = (GameLinkedList)((Map.Entry)var8.object).getValue();
            GameLinkedList.Element var11 = var10.getLastElement();
            boolean var12 = false;

            while(var11 != null) {
               GameLinkedList.Element var13 = var11.prev();

               try {
                  InventoryItem var14 = this.regionData.validateItem(var11);
                  if (var14 != null) {
                     SettlementStorageRecord var15 = (SettlementStorageRecord)var11.object;
                     var12 = true;
                     if (var3 == null || var3.test(var14)) {
                        int var16 = Math.min(var15.itemAmount, var2 - var4);
                        SettlementStoragePickupFuture var18 = var15.storage.getFutureReserve(var15.inventorySlot, var14, var16, (var2x) -> {
                           var15.itemAmount -= var2x.item.getAmount();
                           if (var15.itemAmount <= 0 && !var11.isRemoved()) {
                              var11.remove();
                           }

                        });
                        if (var18 != null) {
                           var7.add(var18);
                           var4 += var18.item.getAmount();
                           this.currentTile = var9;
                           if (var4 >= var2) {
                              break;
                           }
                        }
                     }
                  }
               } finally {
                  var11 = var13;
               }
            }

            if (!var12) {
               var8.remove();
            }
         }

         if (var4 < var1) {
            this.currentTile = var5;
            return null;
         } else {
            LinkedList var22 = new LinkedList();
            Iterator var23 = var7.iterator();

            while(var23.hasNext()) {
               SettlementStoragePickupFuture var24 = (SettlementStoragePickupFuture)var23.next();
               var22.add(var24.accept(var24.item.getAmount()));
            }

            return var22;
         }
      }));
   }

   public SettlementStoragePickupSlot findFirstItemPickup(Predicate<InventoryItem> var1) {
      return (SettlementStoragePickupSlot)Performance.record(this.regionData.index.level.tickManager(), "findItems", (Supplier)(() -> {
         HashSet var2 = new HashSet();

         label118:
         while(true) {
            if (!this.records.isEmpty()) {
               GameLinkedList.Element var3 = (GameLinkedList.Element)this.records.streamElements().filter((var1x) -> {
                  return !var2.contains(((Map.Entry)var1x.object).getKey());
               }).min(Comparator.comparingDouble((var1x) -> {
                  return this.currentTile.distance((Point2D)((Map.Entry)var1x.object).getKey());
               })).orElse((Object)null);
               if (var3 != null) {
                  var2.add((Point)((Map.Entry)var3.object).getKey());
                  Point var4 = (Point)((Map.Entry)var3.object).getKey();
                  GameLinkedList var5 = (GameLinkedList)((Map.Entry)var3.object).getValue();
                  GameLinkedList.Element var6 = var5.getLastElement();
                  boolean var7 = false;

                  while(true) {
                     if (var6 != null) {
                        GameLinkedList.Element var8 = var6.prev();

                        SettlementStoragePickupSlot var12;
                        try {
                           InventoryItem var9 = this.regionData.validateItem(var6);
                           if (var9 == null) {
                              continue;
                           }

                           SettlementStorageRecord var10 = (SettlementStorageRecord)var6.object;
                           if (var1 != null && !var1.test(var9)) {
                              continue;
                           }

                           var7 = true;
                           SettlementStoragePickupSlot var11 = var10.storage.reserve(var10.inventorySlot, var9, 1);
                           if (var11 == null) {
                              continue;
                           }

                           this.currentTile = var4;
                           --var10.itemAmount;
                           if (var10.itemAmount <= 0 && !var6.isRemoved()) {
                              var6.remove();
                           }

                           var12 = var11;
                        } finally {
                           var6 = var8;
                        }

                        return var12;
                     }

                     if (!var7) {
                        var3.remove();
                     }
                     continue label118;
                  }
               }
            }

            return null;
         }
      }));
   }

   public SettlementStoragePickupSlot findBestItem(Predicate<InventoryItem> var1, Comparator<SettlementStoragePickupFuture> var2) {
      return (SettlementStoragePickupSlot)Performance.record(this.regionData.index.level.tickManager(), "findItems", (Supplier)(() -> {
         HashSet var3 = new HashSet();
         SettlementStoragePickupFuture var4 = null;

         while(!this.records.isEmpty()) {
            GameLinkedList.Element var5 = (GameLinkedList.Element)this.records.streamElements().filter((var1x) -> {
               return !var3.contains(((Map.Entry)var1x.object).getKey());
            }).min(Comparator.comparingDouble((var1x) -> {
               return this.currentTile.distance((Point2D)((Map.Entry)var1x.object).getKey());
            })).orElse((Object)null);
            if (var5 == null) {
               break;
            }

            var3.add((Point)((Map.Entry)var5.object).getKey());
            GameLinkedList var6 = (GameLinkedList)((Map.Entry)var5.object).getValue();
            GameLinkedList.Element var7 = var6.getLastElement();

            while(var7 != null) {
               GameLinkedList.Element var8 = var7.prev();

               try {
                  InventoryItem var9 = this.regionData.validateItem(var7);
                  if (var9 != null) {
                     SettlementStorageRecord var10 = (SettlementStorageRecord)var7.object;
                     if (var1 == null || var1.test(var9)) {
                        SettlementStoragePickupFuture var12 = var10.storage.getFutureReserve(var10.inventorySlot, var9, 1, (var2x) -> {
                           var10.itemAmount -= var2x.item.getAmount();
                           if (var10.itemAmount <= 0 && !var7.isRemoved()) {
                              var7.remove();
                           }

                        });
                        if (var4 == null || var2.compare(var4, var12) < 0) {
                           var4 = var12;
                        }
                     }
                  }
               } finally {
                  var7 = var8;
               }
            }
         }

         if (var4 != null) {
            this.currentTile = new Point(var4.storage.tileX, var4.storage.tileY);
            return var4.accept(1);
         } else {
            return null;
         }
      }));
   }
}
