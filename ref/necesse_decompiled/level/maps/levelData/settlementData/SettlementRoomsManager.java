package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.regionSystem.SemiRegion;

public class SettlementRoomsManager {
   public final SettlementLevelData data;
   private HashMap<Point, SettlementRoom> rooms = new HashMap();

   public SettlementRoomsManager(SettlementLevelData var1) {
      this.data = var1;
   }

   public void addSaveData(SaveData var1) {
      SaveData var2 = new SaveData("ROOMS");
      HashSet var3 = new HashSet();
      Iterator var4 = this.rooms.values().iterator();

      while(var4.hasNext()) {
         SettlementRoom var5 = (SettlementRoom)var4.next();
         Point var6 = new Point(var5.tileX, var5.tileY);
         if (var3.add(var6)) {
            SaveData var7 = new SaveData("ROOM");
            var7.addPoint("tile", var6);
            var2.addSaveData(var7);
         }
      }

      var1.addSaveData(var2);
   }

   public void loadSaveData(LoadData var1) {
      Stream var2 = Stream.empty();
      var2 = Stream.concat(var2, var1.getLoadDataByName("ROOM").stream().filter(LoadData::isArray));
      LoadData var3 = var1.getFirstLoadDataByName("ROOMS");
      if (var3 != null) {
         var2 = Stream.concat(var2, var3.getLoadDataByName("ROOM").stream().filter(LoadData::isArray));
      }

      var2.forEach((var1x) -> {
         try {
            Point var2 = var1x.getPoint("tile", (Point)null, false);
            if (var2 == null) {
               int var3 = var1x.getInt("x", Integer.MIN_VALUE, false);
               int var4 = var1x.getInt("y", Integer.MIN_VALUE, false);
               if (var3 != Integer.MIN_VALUE && var4 != Integer.MIN_VALUE) {
                  var2 = new Point(var3, var4);
               }
            }

            if (var2 != null) {
               SettlementRoom var14 = this.getRoom(var2.x, var2.y);
               if (var14 != null) {
                  var14.calculateStats();
               }
            }

            if (var1x.hasLoadDataByName("SETTLER")) {
               try {
                  LevelSettler var15 = new LevelSettler(this.data, var1x.getFirstLoadDataByName("SETTLER"));
                  this.data.settlers.add(var15);
                  if (var2 != null && !this.data.getLevel().isOutside(var2.x, var2.y)) {
                     SemiRegion var16 = this.data.getLevel().regionManager.getSemiRegion(var2.x, var2.y);
                     if (var16 != null) {
                        boolean var5 = false;
                        HashSet var6 = var16.getAllConnected((var0) -> {
                           return var0.getType().roomInt == SemiRegion.RegionType.OPEN.roomInt;
                        });
                        Iterator var7 = var6.iterator();

                        while(var7.hasNext()) {
                           SemiRegion var8 = (SemiRegion)var7.next();
                           Iterator var9 = var8.getLevelTiles().iterator();

                           while(var9.hasNext()) {
                              Point var10 = (Point)var9.next();
                              SettlementBed var11 = this.data.addOrValidateBed(var10.x, var10.y);
                              if (var11 != null && !var11.isLocked && var11.getSettler() == null) {
                                 var15.bed = var11;
                                 var11.settler = var15;
                                 var5 = true;
                                 break;
                              }
                           }

                           if (var5) {
                              break;
                           }
                        }
                     }
                  }
               } catch (Exception var12) {
                  System.err.println("Could not load settlement settler at level " + this.data.getLevel().getIdentifier());
                  var12.printStackTrace();
               }
            }
         } catch (Exception var13) {
            System.err.println("Could not load settlement room at level " + this.data.getLevel().getIdentifier());
         }

      });
   }

   public void refreshRooms(Iterable<Point> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         SettlementRoom var4 = (SettlementRoom)this.rooms.remove(var3);
         if (var4 != null) {
            var4.invalidate();
         }
      }

   }

   public void recalculateStats(Iterable<Point> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         SettlementRoom var4 = (SettlementRoom)this.rooms.get(var3);
         if (var4 != null) {
            var4.recalculateStats();
         }
      }

   }

   public void findAndCalculateRoom(int var1, int var2) {
      SettlementRoom var3 = this.getRoom(var1, var2);
      if (var3 != null) {
         var3.calculateStats();
      }

   }

   public SettlementRoom getRoom(int var1, int var2) {
      return this.data.getLevel().isOutside(var1, var2) ? null : (SettlementRoom)this.rooms.compute(new Point(var1, var2), (var3, var4) -> {
         return var4 != null && !var4.isInvalidated() ? var4 : new SettlementRoom(this.data, this.rooms, var1, var2);
      });
   }
}
