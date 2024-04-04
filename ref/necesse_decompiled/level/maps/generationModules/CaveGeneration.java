package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.HashSet;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class CaveGeneration {
   private Level level;
   public GameRandom random;
   public final int rockTile;
   public final int rockObject;
   private boolean[] cellMap;
   private HashSet<Point> illegalCrateTiles = new HashSet();

   public CaveGeneration(Level var1, String var2, String var3) {
      this.level = var1;
      this.random = new GameRandom(var1.getSeed() + (long)var1.getIslandDimension());
      this.rockTile = TileRegistry.getTileID(var2);
      this.rockObject = ObjectRegistry.getObjectID(var3);
   }

   public boolean[] generateLevel() {
      return this.generateLevel(0.42F, 4, 3, 4);
   }

   public boolean[] generateLevel(float var1, int var2, int var3, int var4) {
      GameTile var5 = TileRegistry.getTile(this.rockTile);

      for(int var6 = 0; var6 < this.level.width; ++var6) {
         for(int var7 = 0; var7 < this.level.height; ++var7) {
            var5.placeTile(this.level, var6, var7);
         }
      }

      this.cellMap = GenerationTools.generateRandomCellMap(this.random, this.level.width, this.level.height, var1);
      this.doCellularAutomaton(var2, var3, var4);
      this.updateCellMap();
      return this.cellMap;
   }

   public void doCellularAutomaton(int var1, int var2, int var3) {
      this.cellMap = GenerationTools.doCellularAutomaton(this.cellMap, this.level.width, this.level.height, var1, var2, true, var3);
   }

   public void updateCellMap() {
      GameObject var1 = ObjectRegistry.getObject(this.rockObject);

      for(int var2 = 0; var2 < this.level.width; ++var2) {
         for(int var3 = 0; var3 < this.level.height; ++var3) {
            if (this.cellMap[var2 + var3 * this.level.width]) {
               var1.placeObject(this.level, var2, var3, 0);
            }
         }
      }

   }

   public void addIllegalCrateTile(int var1, int var2) {
      this.illegalCrateTiles.add(new Point(var1, var2));
   }

   public void generateRandomSingleRocks(int var1, float var2) {
      for(int var3 = 0; var3 < this.level.width; ++var3) {
         for(int var4 = 0; var4 < this.level.height; ++var4) {
            GameTile var5 = this.level.getTile(var3, var4);
            if ((var5.isLiquid || var5.getID() == this.rockTile) && this.random.getChance(var5.isLiquid ? var2 * 3.0F : var2)) {
               GameObject var6 = ObjectRegistry.getObject(var1);
               if (var6.canPlace(this.level, var3, var4, 0) == null) {
                  var6.placeObject(this.level, var3, var4, 0);
               }
            }
         }
      }

   }

   public void generateRandomCrates(float var1, int... var2) {
      for(int var3 = 0; var3 < this.level.width; ++var3) {
         for(int var4 = 0; var4 < this.level.height; ++var4) {
            if ((this.level.getObjectID(var3 - 1, var4) != 0 && !this.isCrate(this.level.getObjectID(var3 - 1, var4), var2) || this.level.getObjectID(var3 + 1, var4) != 0 && !this.isCrate(this.level.getObjectID(var3 + 1, var4), var2) || this.level.getObjectID(var3, var4 - 1) != 0 && !this.isCrate(this.level.getObjectID(var3, var4 - 1), var2) || this.level.getObjectID(var3, var4 + 1) != 0 && !this.isCrate(this.level.getObjectID(var3, var4 + 1), var2)) && this.random.getChance(var1) && !this.illegalCrateTiles.contains(new Point(var3, var4))) {
               int var5 = var2[this.random.nextInt(var2.length)];
               GameObject var6 = ObjectRegistry.getObject(var5);
               if (var6.canPlace(this.level, var3, var4, 0) == null) {
                  var6.placeObject(this.level, var3, var4, 0);
               }
            }
         }
      }

   }

   private boolean isCrate(int var1, int... var2) {
      int[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         if (var1 == var6) {
            return true;
         }
      }

      return false;
   }

   public int generateGuaranteedOreVeins(int var1, int var2, int var3, int var4) {
      GameObject var5 = ObjectRegistry.getObject(var4);
      return GenerationTools.generateGuaranteedRandomVeins(this.level, this.random, var1, var2, var3, (var1x, var2x, var3x) -> {
         return var1x.getObjectID(var2x, var3x) == this.rockObject;
      }, (var1x, var2x, var3x) -> {
         var5.placeObject(var1x, var2x, var3x, 0);
      });
   }

   public void generateOreVeins(float var1, int var2, int var3, int var4) {
      GenerationTools.generateRandomVeins(this.level, this.random, var1, var2, var3, -1, -1, 0.0F, var4, this.rockObject, 1.0F, true, false);
   }

   public void generateTileVeins(float var1, int var2, int var3, int var4, int var5) {
      GenerationTools.generateRandomVeins(this.level, this.random, var1, var2, var3, var4, -1, 1.0F, var5, -1, 1.0F, true, false);
   }

   public void generateSmoothOreVeins(float var1, int var2, int var3, int var4) {
      GameObject var5 = ObjectRegistry.getObject(var4);
      GenerationTools.generateRandomPoints(this.level, this.random, var1, (var4x) -> {
         (new LinesGeneration(var4x.x, var4x.y)).addRandomArms(this.random, 2, (float)var2 / 2.0F, (float)var3 / 2.0F, (float)var2 / 2.0F, (float)var3 / 2.0F).doCellularAutomaton(this.random).forEachTile(this.level, (var2x, var3x, var4) -> {
            if (var2x.getObjectID(var3x, var4) == this.rockObject) {
               var5.placeObject(var2x, var3x, var4, 0);
            }

         });
      });
   }
}
