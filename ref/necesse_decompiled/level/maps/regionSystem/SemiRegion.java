package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.level.gameObject.DoorObject;

public class SemiRegion {
   public Region region;
   public HashSet<SemiRegion> adjacentRegions = new HashSet();
   private HashSet<Point> cells = new HashSet();
   private long totalCellX;
   private long totalCellY;
   private int regionID;
   private int roomID;
   private boolean mapEdge;
   private RegionType type;
   private boolean isInvalidated;
   private GameLinkedList<SemiRegionEventListener> listeners = new GameLinkedList();

   public SemiRegion(Region var1, RegionType var2) {
      this.region = var1;
      this.type = var2;
      this.regionID = -1;
      this.roomID = -1;
      this.mapEdge = false;
   }

   public SemiRegionEventListener addListener(final Runnable var1, final SemiRegionDoorChangedFunction var2) {
      if (this.isInvalidated) {
         throw new IllegalStateException("Cannot add listeners on invalidated SemiRegions");
      } else {
         final AtomicReference var3 = new AtomicReference();
         SemiRegionEventListener var4 = new SemiRegionEventListener() {
            public void onInvalidated() {
               if (var1 != null) {
                  var1.run();
               }

            }

            public void onDoorChanged(DoorObject var1x, DoorObject var2x, int var3x, int var4) {
               var2.handle(var1x, var2x, var3x, var4);
            }

            public void submitHandlerInvalidated() {
               GameLinkedList.Element var1x = (GameLinkedList.Element)var3.get();
               if (var1x != null && !var1x.isRemoved()) {
                  var1x.remove();
               }

            }
         };
         var3.set(this.listeners.addLast(var4));
         return var4;
      }
   }

   public boolean isInvalidated() {
      return this.isInvalidated;
   }

   public void invalidate() {
      if (!this.isInvalidated) {
         this.isInvalidated = true;
         SemiRegionEventListener[] var1 = (SemiRegionEventListener[])this.listeners.toArray(new SemiRegionEventListener[0]);
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            SemiRegionEventListener var4 = var1[var3];
            var4.onInvalidated();
         }

         this.listeners.clear();
      }
   }

   public void submitDoorChanged(DoorObject var1, DoorObject var2, int var3, int var4) {
      SemiRegionEventListener[] var5 = (SemiRegionEventListener[])this.listeners.toArray(new SemiRegionEventListener[0]);
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         SemiRegionEventListener var8 = var5[var7];
         var8.onDoorChanged(var1, var2, var3, var4);
      }

   }

   public int getListenersSize() {
      return this.listeners.size();
   }

   protected void changeDoorType(RegionType var1, DoorObject var2, DoorObject var3, int var4, int var5) {
      if (this.type.isDoor && var1.isDoor) {
         this.type = var1;
         this.submitDoorChanged(var2, var3, var4, var5);
      } else {
         throw new IllegalStateException("Cannot change SemiRegion type on non doors");
      }
   }

   public void findAdjacentSemiRegions() {
      Iterator var1 = this.adjacentRegions.iterator();

      while(var1.hasNext()) {
         SemiRegion var2 = (SemiRegion)var1.next();
         var2.adjacentRegions.remove(this);
      }

      this.adjacentRegions.clear();
      var1 = this.cells.iterator();

      while(var1.hasNext()) {
         Point var7 = (Point)var1.next();

         for(int var3 = -1; var3 <= 1; ++var3) {
            for(int var4 = -1; var4 <= 1; ++var4) {
               if ((var3 != 0 || var4 != 0) && (var3 == 0 || var4 == 0)) {
                  Point var5 = new Point(var7.x + var3, var7.y + var4);
                  if (!this.cells.contains(var5)) {
                     SemiRegion var6 = this.region.manager.getSemiRegion(this.region.regionX * 15 + var5.x, this.region.regionY * 15 + var5.y);
                     if (var6 != null) {
                        var6.addAdjacentRegion(this);
                        this.addAdjacentRegion(var6);
                     }
                  }
               }
            }
         }
      }

   }

   public void setRegionID(RegionManager.IDCounter var1) {
      HashSet var2 = new HashSet();
      var2.add(this);
      int var3 = var1.getAndIncrease();
      this.addAllConnected(var2, (var1x) -> {
         return var1x.getType() == this.getType();
      });

      SemiRegion var5;
      for(Iterator var4 = var2.iterator(); var4.hasNext(); var5.regionID = var3) {
         var5 = (SemiRegion)var4.next();
      }

   }

   public void setRoomID(RegionManager.IDCounter var1, HashMap<Integer, Integer> var2) {
      this.setRoomID(var1, var2, true);
   }

   private void setRoomID(RegionManager.IDCounter var1, HashMap<Integer, Integer> var2, boolean var3) {
      if (!this.mapEdge) {
         HashSet var4 = new HashSet();
         var4.add(this);
         int var5;
         if (this.addToRoom(var4)) {
            var5 = 0;
         } else {
            var5 = var1.getAndIncrease();
         }

         int var6 = 0;

         Iterator var7;
         SemiRegion var8;
         for(var7 = var4.iterator(); var7.hasNext(); var6 += var8.size()) {
            var8 = (SemiRegion)var7.next();
            if (var8.roomID != var5) {
               var8.roomID = var5;
            }

            var2.remove(var8.roomID);
         }

         if (var5 != 0) {
            var2.put(var5, var6);
         }

         if (var3) {
            var7 = this.adjacentRegions.iterator();

            while(var7.hasNext()) {
               var8 = (SemiRegion)var7.next();
               if (!var4.contains(var8) && var8.region != this.region) {
                  var8.setRoomID(var1, var2, false);
               }
            }
         }

      }
   }

   private boolean addToRoom(HashSet<SemiRegion> var1) {
      boolean var2 = false;
      Iterator var3 = this.adjacentRegions.iterator();

      while(var3.hasNext()) {
         SemiRegion var4 = (SemiRegion)var3.next();
         if (var4.getType().roomInt == this.getType().roomInt && !var1.contains(var4)) {
            if (var4.mapEdge) {
               var2 = true;
            } else {
               var1.add(var4);
               if (var4.addToRoom(var1)) {
                  var2 = true;
               }
            }
         }
      }

      return var2;
   }

   private int addAdjacentRoom(HashSet<SemiRegion> var1) {
      int var2 = -1;
      Iterator var3 = this.adjacentRegions.iterator();

      while(true) {
         int var5;
         do {
            do {
               SemiRegion var4;
               do {
                  do {
                     if (!var3.hasNext()) {
                        return var2;
                     }

                     var4 = (SemiRegion)var3.next();
                  } while(var4.getType().roomInt != this.getType().roomInt);
               } while(var1.contains(var4));

               if (var2 == -1) {
                  if (var4.mapEdge) {
                     var2 = 0;
                  } else if (var4.roomID != -1 && var4.roomID != 0) {
                     var2 = var4.roomID;
                  }
               }

               var1.add(var4);
               var5 = var4.addAdjacentRoom(var1);
            } while(var5 == -1);
         } while(var5 != 0 && var2 != -1);

         var2 = var5;
      }
   }

   public int getRegionID() {
      return this.regionID;
   }

   public int getRoomID() {
      return this.roomID;
   }

   public boolean isConnectedToOutside() {
      if (this.mapEdge) {
         return true;
      } else {
         Iterator var1 = this.adjacentRegions.iterator();

         SemiRegion var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (SemiRegion)var1.next();
         } while(!var2.mapEdge);

         return true;
      }
   }

   public void setMapEdge() {
      if (!this.mapEdge) {
         this.mapEdge = true;
         this.setRoomIDOutside();
      }
   }

   private void setRoomIDOutside() {
      this.roomID = 0;
      Iterator var1 = this.adjacentRegions.iterator();

      while(var1.hasNext()) {
         SemiRegion var2 = (SemiRegion)var1.next();
         if (var2.roomID != 0 && var2.getType().roomInt == this.getType().roomInt) {
            var2.setRoomIDOutside();
         }
      }

   }

   public boolean isOutside() {
      return this.roomID == 0;
   }

   public void addAdjacentRegion(SemiRegion var1) {
      this.adjacentRegions.add(var1);
   }

   public HashSet<SemiRegion> getAllConnected(Function<SemiRegion, Boolean> var1) {
      HashSet var2 = new HashSet();
      this.addAllConnected(var2, var1);
      return var2;
   }

   public boolean addAllConnected(HashSet<SemiRegion> var1, Function<SemiRegion, Boolean> var2) {
      boolean var3 = false;
      Iterator var4 = this.adjacentRegions.iterator();

      while(var4.hasNext()) {
         SemiRegion var5 = (SemiRegion)var4.next();
         if (!var1.contains(var5) && (Boolean)var2.apply(var5)) {
            var1.add(var5);
            var3 = true;
            var5.addAllConnected(var1, var2);
         }
      }

      return var3;
   }

   public void getAllConnectedRoom(ArrayList<SemiRegion> var1, RegionType var2) {
      if (var2 == null) {
         this.getAllConnected(var1, (var0) -> {
            return true;
         });
      } else {
         this.getAllConnected(var1, (var1x) -> {
            return var1x.getType().roomInt == var2.roomInt;
         });
      }

   }

   public void getAllConnected(ArrayList<SemiRegion> var1, Function<SemiRegion, Boolean> var2) {
      if (!var1.contains(this)) {
         var1.add(this);
      }

      this.adjacentRegions.stream().filter((var2x) -> {
         return (Boolean)var2.apply(var2x) && !var1.contains(var2x);
      }).forEach((var2x) -> {
         var2x.getAllConnected(var1, var2);
      });
   }

   public void addPoint(Point var1) {
      this.totalCellX += (long)var1.x;
      this.totalCellY += (long)var1.y;
      this.cells.add(var1);
      this.region.semiRegionTiles.put(var1, this);
   }

   public int getAverageCellX() {
      return (int)(this.totalCellX / (long)this.cells.size());
   }

   public int getAverageCellY() {
      return (int)(this.totalCellY / (long)this.cells.size());
   }

   public Point getAverageCell() {
      return new Point(this.getAverageCellX(), this.getAverageCellY());
   }

   public HashSet<Point> getCells() {
      return this.cells;
   }

   public Iterable<Point> getLevelTiles() {
      return GameUtils.mapIterable(this.cells.iterator(), this::getLevelTile);
   }

   public Stream<Point> streamLevelTiles() {
      return this.cells.stream();
   }

   public boolean hasPoint(int var1, int var2) {
      return this.cells.contains(new Point(var1, var2));
   }

   public boolean hasTile(int var1, int var2) {
      return this.hasPoint(this.region.getRegionX(var1), this.region.getRegionY(var2));
   }

   public int size() {
      return this.cells.size();
   }

   public Point getLevelTile(Point var1) {
      return new Point(this.region.getLevelX(var1.x), this.region.getLevelY(var1.y));
   }

   public RegionType getType() {
      return this.type;
   }

   public static enum RegionType {
      OPEN(0, false, false),
      FENCE(0, false, true),
      FENCE_GATE(0, true, true),
      SOLID(0, false, true),
      DOOR(1, true, true),
      WALL(2, false, true),
      SUMMON_IGNORED(0, false, true);

      public final int roomInt;
      public final boolean isDoor;
      public final boolean isSolid;

      private RegionType(int var3, boolean var4, boolean var5) {
         this.roomInt = var3;
         this.isDoor = var4;
         this.isSolid = var5;
      }

      // $FF: synthetic method
      private static RegionType[] $values() {
         return new RegionType[]{OPEN, FENCE, FENCE_GATE, SOLID, DOOR, WALL, SUMMON_IGNORED};
      }
   }
}
