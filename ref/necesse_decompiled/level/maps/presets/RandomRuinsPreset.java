package necesse.level.maps.presets;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.CollisionFilter;

public class RandomRuinsPreset extends Preset {
   private String[] walls = new String[]{"stonewall", "woodwall"};
   private String[] tiles = new String[]{"woodfloor", "stonefloor"};
   private String[] chests = new String[]{"storagebox", "barrel"};
   public GameRandom random;
   private int currentWall;
   private int currentTile;

   public RandomRuinsPreset(GameRandom var1) {
      super(9, 9);
      this.random = var1;
      this.currentWall = ObjectRegistry.getObjectID(this.walls[var1.nextInt(this.walls.length)]);
      this.currentTile = TileRegistry.getTileID(this.tiles[var1.nextInt(this.tiles.length)]);
      float var2 = 0.5F;
      float var3 = 0.65F;

      int var4;
      for(var4 = 2; var4 <= 6; ++var4) {
         this.setObject(var4, 0, this.currentWall, var1, var2);
         this.setObject(var4, 8, this.currentWall, var1, var2);
      }

      for(var4 = 2; var4 <= 6; ++var4) {
         this.setObject(0, var4, this.currentWall, var1, var2);
         this.setObject(8, var4, this.currentWall, var1, var2);
      }

      for(var4 = 0; var4 <= 1; ++var4) {
         this.setObject(var4 + 1, 1, this.currentWall, var1, var2);
         this.setObject(var4 + 6, 1, this.currentWall, var1, var2);
         this.setObject(var4 + 1, 7, this.currentWall, var1, var2);
         this.setObject(var4 + 6, 7, this.currentWall, var1, var2);
      }

      this.setObject(1, 2, this.currentWall, var1, var2);
      this.setObject(7, 2, this.currentWall, var1, var2);
      this.setObject(1, 6, this.currentWall, var1, var2);
      this.setObject(7, 6, this.currentWall, var1, var2);

      for(var4 = 1; var4 < 8; ++var4) {
         for(int var5 = 1; var5 < 8; ++var5) {
            if ((var5 != 1 && var5 != 7 || var4 > 2 && var4 < 6) && (var5 != 2 && var5 != 6 || var4 > 1 && var4 < 7)) {
               this.setTile(var4, var5, this.currentTile, var1, var3);
            }
         }
      }

      this.setObject(4, 4, ObjectRegistry.getObjectID(this.chests[var1.nextInt(this.chests.length)]), var1.nextInt(4));
      this.addInventory(LootTablePresets.surfaceRuinsChest, var1, 4, 4, new Object[0]);
      this.addCanApplyRectPredicate(-1, -1, this.width + 2, this.height + 2, 0, (var0, var1x, var2x, var3x, var4x, var5x) -> {
         Rectangle var6 = new Rectangle(var1x * 32, var2x * 32, (var3x - var1x) * 32, (var4x - var2x) * 32);
         return !var0.collides((Shape)var6, (CollisionFilter)(new CollisionFilter()).allLiquidTiles());
      });
   }

   public RandomRuinsPreset setWalls(String... var1) {
      this.walls = var1;
      int var2 = this.currentWall;
      this.currentWall = ObjectRegistry.getObjectID(var1[this.random.nextInt(var1.length)]);
      this.replaceObject(var2, this.currentWall);
      return this;
   }

   public RandomRuinsPreset setTiles(String... var1) {
      this.tiles = var1;
      int var2 = this.currentTile;
      this.currentTile = TileRegistry.getTileID(var1[this.random.nextInt(var1.length)]);
      this.replaceTile(var2, this.currentTile);
      return this;
   }

   public RandomRuinsPreset setChests(String... var1) {
      this.chests = var1;
      this.setObject(4, 4, ObjectRegistry.getObjectID(var1[this.random.nextInt(var1.length)]), this.random.nextInt(4));
      return this;
   }

   public void setObject(int var1, int var2, int var3, GameRandom var4, float var5) {
      if (var4.getChance(var5)) {
         this.setObject(var1, var2, var3);
      }

   }

   public void setTile(int var1, int var2, int var3, GameRandom var4, float var5) {
      if (var4.getChance(var5)) {
         this.setTile(var1, var2, var3);
         this.setObject(var1, var2, 0);
      }

   }
}
