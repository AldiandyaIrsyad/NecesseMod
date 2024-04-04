package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.Performance;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.layers.LevelLayer;

public class Region {
   public static final int REGION_SIZE = 15;
   public final Level level;
   public final RegionManager manager;
   public final int regionX;
   public final int regionY;
   public final int regionWidth;
   public final int regionHeight;
   public final ArrayList<SemiRegion> semiRegions;
   public final HashMap<Point, SemiRegion> semiRegionTiles;

   public Region(Level var1, RegionManager var2, int var3, int var4, int var5, int var6) {
      this.level = var1;
      this.manager = var2;
      this.regionX = var3;
      this.regionY = var4;
      this.regionWidth = var5;
      this.regionHeight = var6;
      this.semiRegions = new ArrayList();
      this.semiRegionTiles = new HashMap();
   }

   public void calculateRegion() {
      boolean[][] var1 = new boolean[15][15];
      Iterator var2 = this.semiRegions.iterator();

      while(var2.hasNext()) {
         SemiRegion var3 = (SemiRegion)var2.next();
         Iterator var4 = var3.adjacentRegions.iterator();

         while(var4.hasNext()) {
            SemiRegion var5 = (SemiRegion)var4.next();
            var5.adjacentRegions.remove(var3);
         }

         this.manager.getRoomSizes().remove(var3.getRoomID());
         var3.invalidate();
      }

      this.semiRegions.clear();
      this.semiRegionTiles.clear();

      for(int var6 = 0; var6 < 15; ++var6) {
         for(int var7 = 0; var7 < 15; ++var7) {
            if (!var1[var6][var7]) {
               this.semiRegions.add(this.floodFillSemiRegion(var6, var7, var1));
            }
         }
      }

   }

   public void update() {
      if (this.manager.calculatedRegions()) {
         Performance.recordConstant(this.level.debugLoadingPerformance, "regions", () -> {
            this.calculateRegion();
            this.calcAdjacentRegions();
         });
         Performance.recordConstant(this.level.debugLoadingPerformance, "ids", () -> {
            Iterator var1 = this.semiRegions.iterator();

            while(var1.hasNext()) {
               SemiRegion var2 = (SemiRegion)var1.next();
               Performance.recordConstant(this.level.debugLoadingPerformance, "regionIDs", () -> {
                  if (var2.getRegionID() == -1) {
                     var2.setRegionID(this.manager.getRegionIDCounter());
                  }

               });
               Performance.recordConstant(this.level.debugLoadingPerformance, "roomIDs", () -> {
                  if (var2.getRoomID() == -1) {
                     var2.setRoomID(this.manager.getRoomIDCounter(), this.manager.getRoomSizes());
                  }

               });
            }

         });
      }
   }

   public void calcAdjacentRegions() {
      Iterator var1 = this.semiRegions.iterator();

      while(var1.hasNext()) {
         SemiRegion var2 = (SemiRegion)var1.next();
         var2.findAdjacentSemiRegions();
      }

   }

   public void updateLiquidManager() {
      int var1 = this.getLevelX(0);
      int var2 = this.getLevelY(0);
      this.level.liquidManager.updateLevel(var1 - 1, var2 - 1, var1 + this.regionWidth + 1, var2 + this.regionHeight + 1);
   }

   public void updateSplattingManager() {
      int var1 = this.getLevelX(0);
      int var2 = this.getLevelY(0);
      this.level.splattingManager.updateSplatting(var1 - 1, var2 - 1, var1 + this.regionWidth + 1, var2 + this.regionHeight + 1);
   }

   public void updateObjectEntities() {
      for(int var1 = 0; var1 < this.regionWidth; ++var1) {
         for(int var2 = 0; var2 < this.regionHeight; ++var2) {
            this.level.replaceObjectEntity(this.getLevelX(var1), this.getLevelY(var2));
         }
      }

   }

   public void updateLight() {
      int var1 = this.getLevelX(0);
      int var2 = this.getLevelY(0);
      this.level.lightManager.updateStaticLight(var1, var2, var1 + this.regionWidth, var2 + this.regionHeight, true);
   }

   public GameObject getObject(int var1, int var2) {
      return this.level.getObject(this.getLevelX(var1), this.getLevelY(var2));
   }

   public int getLevelX(int var1) {
      return this.regionX * 15 + var1;
   }

   public int getLevelY(int var1) {
      return this.regionY * 15 + var1;
   }

   public int getRegionX(int var1) {
      return var1 - this.regionX * 15;
   }

   public int getRegionY(int var1) {
      return var1 - this.regionY * 15;
   }

   private SemiRegion floodFillSemiRegion(int var1, int var2, boolean[][] var3) {
      SemiRegion.RegionType var4 = this.getObject(var1, var2).getRegionType();
      SemiRegion var5 = new SemiRegion(this, var4);
      boolean var6 = this.regionY == 0;
      boolean var7 = this.regionX == this.manager.getRegionsWidth() - 1;
      boolean var8 = this.regionY == this.manager.getRegionsHeight() - 1;
      boolean var9 = this.regionX == 0;
      boolean var10 = false;
      var3[var1][var2] = true;
      var5.addPoint(new Point(var1, var2));
      if (!var4.isDoor) {
         ArrayList var11 = new ArrayList();
         var11.add(new Point(var1, var2));

         while(var11.size() != 0) {
            Point var12 = (Point)var11.get(0);
            if (var6 && var12.y == 0) {
               var10 = true;
            } else if (var9 && var12.x == 0) {
               var10 = true;
            } else if (var7 && var12.x == 14) {
               var10 = true;
            } else if (var8 && var12.y == 14) {
               var10 = true;
            }

            for(int var13 = -1; var13 <= 1; ++var13) {
               for(int var14 = -1; var14 <= 1; ++var14) {
                  if ((var13 != 0 || var14 != 0) && (var13 == 0 || var14 == 0)) {
                     Point var15 = new Point(var12.x + var13, var12.y + var14);
                     if (var15.x >= 0 && var15.x < 15 && var15.y >= 0 && var15.y < 15 && !var3[var15.x][var15.y] && this.getObject(var15.x, var15.y).getRegionType() == var5.getType()) {
                        var5.addPoint(var15);
                        var3[var15.x][var15.y] = true;
                        var11.add(var15);
                     }
                  }
               }
            }

            var11.remove(0);
         }
      }

      if (var10) {
         var5.setMapEdge();
      }

      return var5;
   }

   public SemiRegion getSemiRegion(int var1, int var2) {
      return (SemiRegion)this.semiRegionTiles.get(new Point(var1, var2));
   }

   public Packet getRegionDataPacket(ServerClient var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      LevelLayer[] var4 = this.level.layers;
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         LevelLayer var7 = var4[var6];
         var7.writeRegionPacket(this, var3);
      }

      for(int var8 = 0; var8 < this.regionWidth; ++var8) {
         var5 = this.getLevelX(var8);

         for(var6 = 0; var6 < this.regionHeight; ++var6) {
            int var9 = this.getLevelY(var6);
            var3.putNextBoolean(var1.isTileKnown(var5, var9));
         }
      }

      return var2;
   }

   public boolean applyRegionDataPacket(Packet var1) {
      AtomicBoolean var2 = new AtomicBoolean(true);
      PacketReader var3 = new PacketReader(var1);
      Performance.recordConstant(this.level.debugLoadingPerformance, "applyTime", () -> {
         LevelLayer[] var3x = this.level.layers;
         int var4 = var3x.length;

         int var5;
         for(var5 = 0; var5 < var4; ++var5) {
            LevelLayer var6 = var3x[var5];
            if (!var6.readRegionPacket(this, var3)) {
               var2.set(false);
               System.err.println("Received invalid region data in " + var6.getStringID() + " layer");
               break;
            }
         }

         if (var2.get() && this.level.isClient()) {
            LevelMap var8 = this.level.getClient().levelManager.map();

            for(var4 = 0; var4 < this.regionWidth; ++var4) {
               var5 = this.getLevelX(var4);

               for(int var9 = 0; var9 < this.regionHeight; ++var9) {
                  int var7 = this.getLevelY(var9);
                  var8.setMapDiscovered(var5, var7, var3.getNextBoolean());
               }
            }
         }

      });
      Performance.recordConstant(this.level.debugLoadingPerformance, "updateTime", () -> {
         this.update();
      });
      Performance.recordConstant(this.level.debugLoadingPerformance, "liquidManager", () -> {
         this.updateLiquidManager();
      });
      Performance.recordConstant(this.level.debugLoadingPerformance, "splatting", () -> {
         this.updateSplattingManager();
      });
      Performance.recordConstant(this.level.debugLoadingPerformance, "objectEntities", () -> {
         this.updateObjectEntities();
      });
      Performance.recordConstant(this.level.debugLoadingPerformance, "lightTime", () -> {
         this.updateLight();
      });
      return var2.get();
   }

   public void unloadRegion() {
      LevelLayer[] var1 = this.level.layers;
      int var2 = var1.length;

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         LevelLayer var4 = var1[var3];
         var4.unloadRegion(this);
      }

      if (this.level.isClient()) {
         LevelMap var6 = this.level.getClient().levelManager.map();

         for(var2 = 0; var2 < this.regionWidth; ++var2) {
            var3 = this.getLevelX(var2);

            for(int var7 = 0; var7 < this.regionHeight; ++var7) {
               int var5 = this.getLevelY(var7);
               var6.setMapDiscovered(var3, var5, false);
            }
         }
      }

      this.update();
      this.updateLiquidManager();
      this.updateSplattingManager();
      this.updateObjectEntities();
      this.updateLight();
   }
}
