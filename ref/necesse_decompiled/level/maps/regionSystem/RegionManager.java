package necesse.level.maps.regionSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.BasicPathDoorOption;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.path.SemiRegionPathResult;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class RegionManager {
   public static int INSIDE_MAX_SIZE = 500;
   private Level level;
   private Region[] regions;
   private IDCounter regionIDCounter;
   private IDCounter roomIDCounter;
   private HashMap<Integer, Integer> roomSizes;
   private int regionsWidth;
   private int regionsHeight;
   private boolean calculatedRegions;
   public PathDoorOption BASIC_DOOR_OPTIONS;
   public PathDoorOption CAN_OPEN_DOORS_OPTIONS;
   public PathDoorOption CANNOT_OPEN_CAN_CLOSE_DOORS_OPTIONS;
   public PathDoorOption CANNOT_PASS_DOORS_OPTIONS;
   public PathDoorOption SUMMONED_MOB_OPTIONS;
   public PathDoorOption CAN_BREAK_OBJECTS_OPTIONS;

   public RegionManager(Level var1) {
      this.level = var1;
      this.BASIC_DOOR_OPTIONS = new BasicPathDoorOption("BASIC", var1, false, false);
      this.CAN_OPEN_DOORS_OPTIONS = new BasicPathDoorOption("CAN_OPEN_DOORS", var1, true, true);
      this.CANNOT_OPEN_CAN_CLOSE_DOORS_OPTIONS = new BasicPathDoorOption("CANNOT_OPEN_CAN_CLOSE_DOORS", var1, false, true);
      this.CANNOT_PASS_DOORS_OPTIONS = new BasicPathDoorOption("CANNOT_PASS_DOORS", var1, false, false) {
         public boolean canPathThroughCheckTile(SemiRegion var1, int var2, int var3) {
            return false;
         }

         public boolean canPass(int var1, int var2) {
            return super.canPass(var1, var2) && !this.level.getObject(var1, var2).isDoor;
         }

         public boolean canPassDoor(DoorObject var1, int var2, int var3) {
            return false;
         }
      };
      this.SUMMONED_MOB_OPTIONS = new PathDoorOption("CAN_PASS_DOORS", var1) {
         public SemiRegionPathResult canPathThrough(SemiRegion var1) {
            if (var1.getType().isDoor) {
               return SemiRegionPathResult.VALID;
            } else if (var1.getType() == SemiRegion.RegionType.SUMMON_IGNORED) {
               return SemiRegionPathResult.VALID;
            } else {
               return var1.getType().isSolid ? SemiRegionPathResult.INVALID : SemiRegionPathResult.VALID;
            }
         }

         public boolean canPassDoor(DoorObject var1, int var2, int var3) {
            return true;
         }

         public boolean canBreakDown(int var1, int var2) {
            return false;
         }

         public boolean canOpen(int var1, int var2) {
            return false;
         }

         public boolean canClose(int var1, int var2) {
            return false;
         }

         public boolean doorChangeInvalidatesCache(DoorObject var1, DoorObject var2, int var3, int var4) {
            return false;
         }
      };
      this.CAN_BREAK_OBJECTS_OPTIONS = new BasicPathDoorOption("CAN_BREAK_OBJECTS", var1, true, false, false);
      this.initRegionManager();
   }

   public int getRegionsWidth() {
      return this.regionsWidth;
   }

   public int getRegionsHeight() {
      return this.regionsHeight;
   }

   public IDCounter getRegionIDCounter() {
      return this.regionIDCounter;
   }

   public IDCounter getRoomIDCounter() {
      return this.roomIDCounter;
   }

   public HashMap<Integer, Integer> getRoomSizes() {
      return this.roomSizes;
   }

   public int getRegionID(int var1, int var2) {
      SemiRegion var3 = this.getSemiRegion(var1, var2);
      return var3 != null ? var3.getRegionID() : -1;
   }

   public int getRoomID(int var1, int var2) {
      SemiRegion var3 = this.getSemiRegion(var1, var2);
      return var3 != null ? var3.getRoomID() : -1;
   }

   public boolean isOutsideRoomID(int var1, int var2) {
      return this.getRoomID(var1, var2) == 0;
   }

   public boolean isOutside(int var1, int var2) {
      SemiRegion var3 = this.getSemiRegion(var1, var2);
      if (var3 != null) {
         int var4 = var3.getRoomID();
         if (var4 == 0) {
            return true;
         } else if (var3.getType() != SemiRegion.RegionType.WALL && var3.getType() != SemiRegion.RegionType.DOOR) {
            return this.getRoomSize(var4) >= INSIDE_MAX_SIZE;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public int getRoomSize(int var1) {
      return (Integer)this.roomSizes.getOrDefault(var1, -1);
   }

   public int getRoomSize(int var1, int var2) {
      return this.getRoomSize(this.getRoomID(var1, var2));
   }

   public int getRegionXByTile(int var1) {
      return GameMath.limit(var1 / 15, 0, this.regionsWidth - 1);
   }

   public int getRegionYByTile(int var1) {
      return GameMath.limit(var1 / 15, 0, this.regionsHeight - 1);
   }

   public RegionPosition getRegionPosByTile(int var1, int var2) {
      return this.getRegionPos(this.getRegionXByTile(var1), this.getRegionYByTile(var2));
   }

   public RegionPosition getRegionPos(int var1, int var2) {
      return new RegionPosition(this.level, var1, var2);
   }

   public Region getRegion(int var1, int var2) {
      return var1 >= 0 && var2 >= 0 && var1 < this.regionsWidth && var2 < this.regionsHeight ? this.regions[var1 + var2 * this.regionsWidth] : null;
   }

   public Region getRegionByTile(int var1, int var2) {
      return this.getRegion(var1 / 15, var2 / 15);
   }

   public SemiRegion getSemiRegion(int var1, int var2) {
      Region var3 = this.getRegionByTile(var1, var2);
      return var3 != null ? var3.getSemiRegion(var1 % 15, var2 % 15) : null;
   }

   public SemiRegion.RegionType getRegionType(int var1, int var2) {
      return this.level.getObject(var1, var2).getRegionType();
   }

   public ArrayList<SemiRegion> getSameConnected(int var1, int var2) {
      ArrayList var3 = new ArrayList();
      SemiRegion var4 = this.getSemiRegion(var1, var2);
      if (var4 != null) {
         var4.getAllConnected(var3, (var1x) -> {
            return var1x.getType() == var4.getType();
         });
      }

      return var3;
   }

   public ArrayList<SemiRegion> getRoom(int var1, int var2) {
      ArrayList var3 = new ArrayList();
      SemiRegion var4 = this.getSemiRegion(var1, var2);
      if (var4 != null) {
         var4.getAllConnectedRoom(var3, var4.getType());
      }

      return var3;
   }

   public ArrayList<SemiRegion> getHouse(int var1, int var2) {
      ArrayList var3 = new ArrayList();
      SemiRegion var4 = this.getSemiRegion(var1, var2);
      if (var4 != null && !var4.isOutside() && var4.getType() != SemiRegion.RegionType.WALL) {
         var4.getAllConnected(var3, (var0) -> {
            return !var0.isOutside();
         });
      }

      return var3;
   }

   public boolean calculatedRegions() {
      return this.calculatedRegions;
   }

   public void initRegionManager() {
      this.calculatedRegions = false;
      this.roomSizes = new HashMap();
      int var1 = this.level.width % 15;
      int var2 = this.level.height % 15;
      this.regionsWidth = this.level.width / 15 + (var1 > 0 ? 1 : 0);
      this.regionsHeight = this.level.height / 15 + (var2 > 0 ? 1 : 0);
      this.regions = new Region[this.regionsWidth * this.regionsHeight];

      for(int var3 = 0; var3 < this.regionsWidth; ++var3) {
         for(int var4 = 0; var4 < this.regionsHeight; ++var4) {
            int var5 = 15;
            int var6 = 15;
            if (var3 == this.regionsWidth - 1 && var1 > 0) {
               var5 = var1;
            }

            if (var4 == this.regionsHeight - 1 && var2 > 0) {
               var6 = var2;
            }

            this.regions[var3 + var4 * this.regionsWidth] = new Region(this.level, this, var3, var4, var5, var6);
         }
      }

   }

   public void calculateRegions() {
      Region[] var1 = this.regions;
      int var2 = var1.length;

      int var3;
      Region var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         var4.calculateRegion();
      }

      var1 = this.regions;
      var2 = var1.length;

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         var4.calcAdjacentRegions();
      }

      this.calculateConnected();
      this.calculatedRegions = true;
   }

   public void calculateConnected() {
      this.regionIDCounter = new IDCounter();
      this.roomIDCounter = new IDCounter();
      this.roomSizes = new HashMap();
      Region[] var1 = this.regions;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Region var4 = var1[var3];
         Iterator var5 = var4.semiRegions.iterator();

         while(var5.hasNext()) {
            SemiRegion var6 = (SemiRegion)var5.next();
            if (var6.getRegionID() == -1) {
               var6.setRegionID(this.regionIDCounter);
            }

            if (var6.getRoomID() == -1) {
               var6.setRoomID(this.roomIDCounter, this.roomSizes);
            }
         }
      }

   }

   public void updateRegionByTile(int var1, int var2) {
      this.getRegionByTile(var1, var2).update();
   }

   public void objectUpdated(int var1, int var2, GameObject var3, GameObject var4) {
      SemiRegion.RegionType var5 = var3.getRegionType();
      SemiRegion.RegionType var6 = var4.getRegionType();
      if (var5.isDoor && var6.isDoor) {
         SemiRegion var7 = this.getSemiRegion(var1, var2);
         if (var7 != null) {
            var7.changeDoorType(var6, (DoorObject)var3, (DoorObject)var4, var1, var2);
         }
      } else if (var5 != var6) {
         this.updateRegionByTile(var1, var2);
      }

   }

   public static class IDCounter {
      private int currentID = 1;

      public IDCounter() {
      }

      public int getCurrentID() {
         return this.currentID;
      }

      public int getAndIncrease() {
         int var1 = this.currentID++;
         if (this.currentID == -1 || this.currentID == 0) {
            this.currentID = 1;
         }

         return var1;
      }
   }
}
