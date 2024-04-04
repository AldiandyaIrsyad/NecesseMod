package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

public class SettlementRoom {
   public final SettlementLevelData data;
   private HashMap<Point, SettlementRoom> roomsMap;
   public final int tileX;
   public final int tileY;
   private boolean calculatedStats = false;
   private boolean invalidated;
   private HashMap<String, Integer> roomProperties = new HashMap();
   private HashMap<String, Integer> furnitureTypes = new HashMap();
   private ArrayList<SettlementBed> beds = new ArrayList();
   private boolean expandedDefendZone = false;

   public SettlementRoom(SettlementLevelData var1, HashMap<Point, SettlementRoom> var2, int var3, int var4) {
      this.data = var1;
      this.roomsMap = var2;
      this.tileX = var3;
      this.tileY = var4;
   }

   protected void calculateStats() {
      try {
         if (this.calculatedStats) {
            return;
         }

         this.roomProperties.clear();
         this.furnitureTypes.clear();
         this.beds.clear();
         if (this.getLevel().isOutside(this.tileX, this.tileY)) {
            return;
         }

         SemiRegion var1 = this.getLevel().regionManager.getSemiRegion(this.tileX, this.tileY);
         if (var1 != null) {
            if (var1.getType().roomInt != SemiRegion.RegionType.OPEN.roomInt) {
               return;
            }

            int var2 = this.getLevel().regionManager.getRoomSize(this.tileX, this.tileY);
            this.addRoomProperty("size", var2);
            HashSet var3 = var1.getAllConnected((var0) -> {
               return var0.getType().roomInt == SemiRegion.RegionType.OPEN.roomInt;
            });
            HashSet var4 = new HashSet();
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               SemiRegion var6 = (SemiRegion)var5.next();
               Iterator var7 = var6.adjacentRegions.iterator();

               while(var7.hasNext()) {
                  SemiRegion var8 = (SemiRegion)var7.next();
                  if (var8.getType() == SemiRegion.RegionType.DOOR && !var4.contains(var8) && var8.adjacentRegions.stream().anyMatch((var1x) -> {
                     return var1x.getType().roomInt == SemiRegion.RegionType.OPEN.roomInt && !var3.contains(var1x);
                  })) {
                     var4.add(var8);
                  }
               }

               var7 = var6.getLevelTiles().iterator();

               while(var7.hasNext()) {
                  Point var15 = (Point)var7.next();
                  GameObject var9 = this.getLevel().getObject(var15.x, var15.y);
                  var9.roomProperties.forEach((var1x) -> {
                     this.addRoomProperty(var1x, 1);
                  });
                  if (var9 instanceof RoomFurniture) {
                     String var10 = ((RoomFurniture)var9).getFurnitureType();
                     if (var10 != null) {
                        this.addFurnitureType(var10, 1);
                     }
                  }

                  GameTile var16 = this.getLevel().getTile(var15.x, var15.y);
                  var16.roomProperties.forEach((var1x) -> {
                     this.addRoomProperty(var1x, 1);
                  });
                  if (!var16.isFloor) {
                     this.addRoomProperty("outsidefloor", 1);
                  }

                  SettlementBed var11 = this.data.addOrValidateBed(var15.x, var15.y);
                  if (var11 != null) {
                     this.beds.add(var11);
                  }

                  if (this.roomsMap != null) {
                     this.roomsMap.put(var15, this);
                  }
               }
            }

            this.addRoomProperty("doors", var4.size());
            return;
         }
      } finally {
         this.calculatedStats = true;
      }

   }

   public void invalidate() {
      this.invalidated = true;
   }

   public boolean isInvalidated() {
      return this.invalidated;
   }

   public void recalculateStats() {
      this.calculatedStats = false;
   }

   public SettlementLevelData getData() {
      return this.data;
   }

   public Level getLevel() {
      return this.data.getLevel();
   }

   private void addRoomProperty(String var1, int var2) {
      this.roomProperties.compute(var1, (var1x, var2x) -> {
         return var2x == null ? var2 : var2x + var2;
      });
   }

   private void addFurnitureType(String var1, int var2) {
      this.furnitureTypes.compute(var1, (var1x, var2x) -> {
         return var2x == null ? var2 : var2x + var2;
      });
   }

   public int getRoomProperty(String var1) {
      if (!this.calculatedStats) {
         this.calculateStats();
      }

      return (Integer)this.roomProperties.getOrDefault(var1, 0);
   }

   public int getFurnitureTypes(String var1) {
      if (!this.calculatedStats) {
         this.calculateStats();
      }

      return (Integer)this.furnitureTypes.getOrDefault(var1, 0);
   }

   public int getFurnitureScore() {
      if (!this.calculatedStats) {
         this.calculateStats();
      }

      return this.furnitureTypes.values().stream().mapToInt((var0) -> {
         return (int)Math.pow((double)var0, 0.44);
      }).sum();
   }

   public int getRoomSize() {
      if (!this.calculatedStats) {
         this.calculateStats();
      }

      return (Integer)this.roomProperties.getOrDefault("size", 0);
   }

   public int getOccupiedBeds() {
      if (!this.calculatedStats) {
         this.calculateStats();
      }

      return (int)this.beds.stream().filter((var0) -> {
         return var0.getSettler() != null;
      }).count();
   }

   public Collection<SemiRegion> getSemiRegions() {
      if (this.getLevel().isOutside(this.tileX, this.tileY)) {
         return new HashSet();
      } else {
         SemiRegion var1 = this.data.getLevel().regionManager.getSemiRegion(this.tileX, this.tileY);
         return var1 == null ? new HashSet() : var1.getAllConnected((var0) -> {
            return var0.getType().roomInt == SemiRegion.RegionType.OPEN.roomInt;
         });
      }
   }

   public void expandDefendZone() {
      if (!this.expandedDefendZone) {
         Collection var1 = this.getSemiRegions();
         this.data.expandDefendZone(var1, SettlementLevelData.AUTO_EXPAND_DEFEND_ZONE_RANGE);
         this.expandedDefendZone = true;
      }
   }

   public List<String> getDebugTooltips() {
      if (!this.calculatedStats) {
         this.calculateStats();
      }

      LinkedList var1 = new LinkedList();
      Iterator var2;
      Map.Entry var3;
      if (!this.roomProperties.isEmpty()) {
         var1.add("Room properties:");
         var2 = this.roomProperties.entrySet().iterator();

         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            var1.add("  " + (String)var3.getKey() + ": " + var3.getValue());
         }
      }

      if (!this.furnitureTypes.isEmpty()) {
         var1.add("Furniture score: " + this.getFurnitureScore());
         var2 = this.furnitureTypes.entrySet().iterator();

         while(var2.hasNext()) {
            var3 = (Map.Entry)var2.next();
            var1.add("  " + (String)var3.getKey() + ": " + var3.getValue());
         }
      }

      return var1;
   }
}
