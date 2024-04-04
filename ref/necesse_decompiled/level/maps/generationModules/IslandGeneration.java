package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class IslandGeneration {
   private Level level;
   public boolean[] cellMap;
   public int islandSize;
   public GameRandom random;

   public IslandGeneration(Level var1, int var2) {
      this.level = var1;
      this.random = new GameRandom(var1.getSeed());

      for(int var3 = 0; var3 < var1.width; ++var3) {
         for(int var4 = 0; var4 < var1.height; ++var4) {
            var1.setTile(var3, var4, 0);
         }
      }

      var1.liquidManager.calculateShores();
      this.cellMap = new boolean[var1.width * var1.height];
      this.islandSize = var2;
   }

   public void generateSimpleIsland(int var1, int var2, int var3, int var4, int var5) {
      this.generateRandomCellIsland(this.islandSize, var1, var2);
      this.cellMap = GenerationTools.doCellularAutomaton(this.cellMap, this.level.width, this.level.height, 5, 4, false, 4);
      this.updateCellMap(var4, var3);
      GenerationTools.smoothTile(this.level, var4);
      if (var5 != -1) {
         this.createBeach(4, var5, var4);
      }

   }

   public void generateShapedIsland(int var1, int var2, int var3) {
      int var4 = this.islandSize / 5;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = this.random.getIntBetween(Math.max(this.islandSize / 10, 10), (int)((float)this.islandSize / 2.0F));
         float var7 = (float)this.random.getIntBetween(0, var6 * 2);
         float var8 = (float)this.random.getIntBetween(0, var6 * 2);
         int var9 = this.random.nextInt(360);
         Point2D.Float var10 = GameMath.getAngleDir((float)var9);
         int var11 = (int)((float)(this.level.width / 2) + var10.x * var7);
         int var12 = (int)((float)(this.level.height / 2) + var10.y * var8);
         var11 = GameMath.limit(var11, var6 + 20, this.level.width - var6 - 20);
         var12 = GameMath.limit(var12, var6 + 20, this.level.height - var6 - 20);
         this.generateRandomCellIsland(var6, var11, var12);
      }

      this.cellMap = GenerationTools.doCellularAutomaton(this.cellMap, this.level.width, this.level.height, 5, 4, false, 4);
      this.updateCellMap(var2, var1);
      GenerationTools.smoothTile(this.level, var2);
      this.clearSmallLakes(4, var2);
      if (var3 != -1) {
         this.createBeach(4, var3, var2);
      }

   }

   public void clearSmallLakes(int var1, int var2) {
      boolean[][] var3 = new boolean[this.level.width][this.level.height];
      BiPredicate var4 = (var1x, var2x) -> {
         return !this.level.isLiquidTile(var1x, var2x);
      };

      int var5;
      int var6;
      for(var5 = 0; var5 < this.level.width; ++var5) {
         for(var6 = 0; var6 < this.level.height; ++var6) {
            if (this.level.isLiquidTile(var5, var6) && this.findNearbyTile(var5, var6, var1, var4)) {
               var3[var5][var6] = true;
            }
         }
      }

      for(var5 = 0; var5 < this.level.width; ++var5) {
         for(var6 = 0; var6 < this.level.height; ++var6) {
            if (var3[var5][var6]) {
               this.level.setTile(var5, var6, var2);
            }
         }
      }

   }

   public void clearTinyIslands(int var1) {
      boolean[][] var2 = new boolean[this.level.width][this.level.height];
      BiPredicate var3 = (var1x, var2x) -> {
         return this.level.isLiquidTile(var1x, var2x);
      };

      int var4;
      int var5;
      for(var4 = 0; var4 < this.level.width; ++var4) {
         for(var5 = 0; var5 < this.level.height; ++var5) {
            if (!this.level.isLiquidTile(var4, var5) && this.countNearbyTiles(var4, var5, 2, var3) > 12) {
               var2[var4][var5] = true;
            }
         }
      }

      for(var4 = 0; var4 < this.level.width; ++var4) {
         for(var5 = 0; var5 < this.level.height; ++var5) {
            if (var2[var4][var5]) {
               this.level.setTile(var4, var5, var1);
            }
         }
      }

   }

   private boolean findNearbyTile(int var1, int var2, int var3, BiPredicate<Integer, Integer> var4) {
      int var5 = Math.min(var1 + var3 + 1, this.level.width);
      int var6 = Math.min(var2 + var3 + 1, this.level.height);

      for(int var7 = Math.max(var1 - var3, 0); var7 < var5; ++var7) {
         for(int var8 = Math.max(var2 - var3, 0); var8 < var6; ++var8) {
            if (var4.test(var7, var8)) {
               return true;
            }
         }
      }

      return false;
   }

   private int countNearbyTiles(int var1, int var2, int var3, BiPredicate<Integer, Integer> var4) {
      int var5 = Math.min(var1 + var3 + 1, this.level.width);
      int var6 = Math.min(var2 + var3 + 1, this.level.height);
      int var7 = 0;

      for(int var8 = Math.max(var1 - var3, 0); var8 < var5; ++var8) {
         for(int var9 = Math.max(var2 - var3, 0); var9 < var6; ++var9) {
            if (var4.test(var8, var9)) {
               ++var7;
            }
         }
      }

      return var7;
   }

   private boolean findNearbyTileDistanced(int var1, int var2, int var3, BiPredicate<Integer, Integer> var4) {
      int var5 = Math.min(var1 + var3 + 1, this.level.width);
      int var6 = Math.min(var2 + var3 + 1, this.level.height);

      for(int var7 = Math.max(var1 - var3, 0); var7 < var5; ++var7) {
         for(int var8 = Math.max(var2 - var3, 0); var8 < var6; ++var8) {
            if (GameMath.diagonalMoveDistance(var1, var2, var7, var8) <= (double)var3 && var4.test(var7, var8)) {
               return true;
            }
         }
      }

      return false;
   }

   public void createBeach(int var1, int var2, int var3) {
      BiPredicate var4 = (var1x, var2x) -> {
         return this.level.getTile(var1x, var2x).isLiquid;
      };

      for(int var5 = 0; var5 < this.level.width; ++var5) {
         for(int var6 = 0; var6 < this.level.height; ++var6) {
            if (this.level.getTileID(var5, var6) == var3 && this.findNearbyTileDistanced(var5, var6, var1, var4)) {
               this.level.setTile(var5, var6, var2);
            }
         }
      }

   }

   public void generateObjects(int var1, int var2, float var3) {
      this.generateObjects(var1, var2, var3, true);
   }

   public void generateObjects(int var1, int var2, float var3, boolean var4) {
      GenerationTools.fillMap(this.level, this.random, var2, -1, 0.0F, var1, -1, var3, false, var4);
   }

   public void generateCellMapObjects(float var1, int var2, int var3, float var4) {
      boolean[] var5 = GenerationTools.generateRandomCellMap(this.random, this.level.width, this.level.height, var1);
      var5 = GenerationTools.doCellularAutomaton(var5, this.level.width, this.level.height, 4, 3, false, 4);
      GameObject var6 = ObjectRegistry.getObject(var2);

      for(int var7 = 0; var7 < this.level.width; ++var7) {
         for(int var8 = 0; var8 < this.level.height; ++var8) {
            if (var5[var7 + var8 * this.level.width] && (var3 == -1 || this.level.getTileID(var7, var8) == var3) && this.random.getChance(var4) && var6.canPlace(this.level, var7, var8, 0) == null) {
               var6.placeObject(this.level, var7, var8, 0);
            }
         }
      }

   }

   public void generateRiver(int var1, int var2, int var3) {
      float var4 = 0.0F;
      float var5 = (float)this.random.getIntBetween(this.islandSize / 2, this.islandSize * 2 + 60);
      int var6 = this.random.nextInt(360);
      Point2D.Float var7 = GameMath.getAngleDir((float)var6);
      Point var8 = new Point(this.level.width / 2 + (int)(-var7.x * (float)(this.islandSize + 20)), this.level.height / 2 + (int)(-var7.y * (float)(this.islandSize + 20)));
      LinesGeneration var9 = new LinesGeneration(var8.x, var8.y);

      while(var4 < var5) {
         var6 = this.random.getIntOffset(var6, 25);
         float var10 = this.random.getFloatBetween(5.0F, 10.0F);
         float var11 = this.random.getFloatBetween(3.0F, 4.0F);
         var4 += var10;
         var9 = var9.addArm((float)var6, var10, var11);
         if (var9.x2 < 0 || var9.x2 > this.level.width || var9.y2 < 0 || var9.y2 > this.level.height) {
            break;
         }
      }

      var9.doCellularAutomaton(this.random, 3, 4, 4).placeTiles(this.level, var1);
      (new LinesGeneration(var9.x2, var9.y2, this.random.getFloatBetween(5.0F, 10.0F) + (float)this.islandSize / 10.0F)).doCellularAutomaton(this.random, 5, 4, 5).placeTiles(this.level, var1);
      if (var3 != -1) {
         this.createBeach(3, var3, var2);
      }

   }

   public void generateLakes(float var1, int var2, int var3, int var4) {
      GenerationTools.generateRandomPoints(this.level, this.random, var1, 25, (var4x) -> {
         this.generateLake(var4x.x, var4x.y, var2, var3, var4);
      });
   }

   public void generateLake(int var1, int var2, int var3) {
      Point var4 = new Point(this.random.getIntBetween(25, this.level.width - 25), this.random.getIntBetween(25, this.level.height - 25));
      this.generateLake(var4.x, var4.y, var1, var2, var3);
   }

   public void generateLake(int var1, int var2, int var3, int var4, int var5) {
      LinesGeneration var6 = new LinesGeneration(var1, var2);
      int var7 = this.random.getIntBetween(2, 6);

      for(int var8 = 0; var8 < var7; ++var8) {
         LinesGeneration var9 = var6;
         int var10 = this.random.nextInt(360);

         for(int var11 = 0; var11 < 20; ++var11) {
            float var12 = this.random.getFloatBetween(3.0F, 5.0F);
            int var13 = this.random.getIntBetween(4, 5);
            var9 = var9.addArm((float)var10, var12, (float)var13);
            if (var9.x2 < 0 || var9.x2 > this.level.width || var9.y2 < 0 || var9.y2 > this.level.height || this.random.nextBoolean()) {
               break;
            }

            var10 = this.random.getIntOffset(var10, 25);
         }
      }

      var6.doCellularAutomaton(this.random, 3, 4, 4).placeTiles(this.level, var3);
      if (var5 != -1) {
         this.createBeach(3, var5, var4);
      }

   }

   public void generateRandomCellIsland(int var1, int var2, int var3) {
      Point var4 = new Point(var2, var3);

      for(int var5 = var2 - var1; var5 < var2 + var1; ++var5) {
         for(int var6 = var3 - var1; var6 < var3 + var1; ++var6) {
            if (var5 >= 0 && var6 >= 0 && var5 < this.level.width && var6 < this.level.height) {
               double var7 = var4.distance((double)var5, (double)var6);
               if (var7 < (double)var1) {
                  if (this.random.nextFloat() < 0.8F) {
                     this.cellMap[var5 + var6 * this.level.width] = true;
                  }

                  if (var7 > (double)(var1 - 5) & this.random.nextFloat() < 0.4F) {
                     this.cellMap[var5 + var6 * this.level.width] = false;
                  }
               }
            }
         }
      }

   }

   public void updateCellMap(int var1, int var2) {
      for(int var3 = 0; var3 < this.level.width; ++var3) {
         for(int var4 = 0; var4 < this.level.height; ++var4) {
            if (this.cellMap[var3 + var4 * this.level.width]) {
               this.level.setTile(var3, var4, var1);
            } else {
               this.level.setTile(var3, var4, var2);
            }
         }
      }

   }

   public void spawnMobHerds(String var1, int var2, int var3, int var4, int var5, float var6) {
      GenerationTools.spawnMobHerds(this.level, this.random, var1, (int)((float)var2 * var6), var3, var4, var5);
   }

   protected int[] toTileIDs(String... var1) {
      int[] var2 = new int[var1.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = TileRegistry.getTileID(var1[var3]);
      }

      return var2;
   }

   public void generateFruitGrowerVeins(String var1, float var2, int var3, int var4, float var5, String... var6) {
      this.generateFruitGrowerVeins(var1, var2, var3, var4, var5, this.toTileIDs(var6));
   }

   public void generateFruitGrowerVeins(String var1, float var2, int var3, int var4, float var5, int... var6) {
      GameObject var7 = ObjectRegistry.getObject(var1);
      GenerationTools.generateRandomVeins(this.level, this.random, var2, var3, var4, (var4x, var5x, var6x) -> {
         if (this.random.getChance(var5)) {
            if (var6.length != 0) {
               int var7x = var4x.getTileID(var5x, var6x);
               if (Arrays.stream(var6).noneMatch((var1) -> {
                  return var7x == var1;
               })) {
                  return;
               }
            }

            if (var7.canPlace(var4x, var5x, var6x, 0) == null) {
               var7.placeObject(var4x, var5x, var6x, 0);
               ObjectEntity var8 = var4x.entityManager.getObjectEntity(var5x, var6x);
               if (var8 instanceof FruitGrowerObjectEntity) {
                  ((FruitGrowerObjectEntity)var8).setRandomStage(this.random);
               }
            }

         }
      });
   }

   public void generateFruitGrowerSingle(String var1, float var2, String... var3) {
      this.generateFruitGrowerSingle(var1, var2, this.toTileIDs(var3));
   }

   public void generateFruitGrowerSingle(String var1, float var2, int... var3) {
      this.generateRandomObjects(var1, var2, (var1x) -> {
         ObjectEntity var2 = this.level.entityManager.getObjectEntity(var1x.x, var1x.y);
         if (var2 instanceof FruitGrowerObjectEntity) {
            ((FruitGrowerObjectEntity)var2).setRandomStage(this.random);
         }

      }, var3);
   }

   public void generateRandomObjects(String var1, float var2, String... var3) {
      this.generateRandomObjects(var1, var2, this.toTileIDs(var3));
   }

   public void generateRandomObjects(String var1, float var2, Consumer<Point> var3, String... var4) {
      this.generateRandomObjects(var1, var2, var3, this.toTileIDs(var4));
   }

   public void generateRandomObjects(String var1, float var2, int... var3) {
      this.generateRandomObjects(var1, var2, (Consumer)null, (int[])var3);
   }

   public void generateRandomObjects(String var1, float var2, Consumer<Point> var3, int... var4) {
      GameObject var5 = ObjectRegistry.getObject(var1);
      GenerationTools.generateRandomPoints(this.level, this.random, var2, (var4x) -> {
         if (var4.length != 0) {
            int var5x = this.level.getTileID(var4x.x, var4x.y);
            if (Arrays.stream(var4).noneMatch((var1) -> {
               return var5x == var1;
            })) {
               return;
            }
         }

         if (var5.canPlace(this.level, var4x.x, var4x.y, 0) == null) {
            var5.placeObject(this.level, var4x.x, var4x.y, 0);
            if (var3 != null) {
               var3.accept(var4x);
            }
         }

      });
   }

   public void ensureGenerateObjects(String var1, int var2, String... var3) {
      this.ensureGenerateObjects(var1, var2, this.toTileIDs(var3));
   }

   public void ensureGenerateObjects(String var1, int var2, Consumer<Point> var3, String... var4) {
      this.ensureGenerateObjects(var1, var2, var3, this.toTileIDs(var4));
   }

   public void ensureGenerateObjects(String var1, int var2, int... var3) {
      this.ensureGenerateObjects(var1, var2, (Consumer)null, (int[])var3);
   }

   public void ensureGenerateObjects(String var1, int var2, Consumer<Point> var3, int... var4) {
      GameObject var5 = ObjectRegistry.getObject(var1);
      GenerationTools.generateOnValidTiles(this.level, this.random, var2, (var3x, var4x) -> {
         if (var5.canPlace(this.level, var3x, var4x, 0) != null) {
            return false;
         } else if (var4.length != 0) {
            int var5x = this.level.getTileID(var3x, var4x);
            return Arrays.stream(var4).anyMatch((var1) -> {
               return var5x == var1;
            });
         } else {
            return true;
         }
      }, (var3x, var4x) -> {
         if (var5.canPlace(this.level, var3x, var4x, 0) == null) {
            var5.placeObject(this.level, var3x, var4x, 0);
            if (var3 != null) {
               var3.accept(new Point(var3x, var4x));
            }

            return true;
         } else {
            return false;
         }
      });
   }
}
