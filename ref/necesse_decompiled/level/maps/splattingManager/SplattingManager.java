package necesse.level.maps.splattingManager;

import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class SplattingManager {
   private final Level level;
   private SplattingOptions[][] splatTiles;

   public SplattingManager(Level var1) {
      this.level = var1;
      this.splatTiles = new SplattingOptions[var1.width][var1.height];
   }

   public SplattingOptions getSplatTiles(int var1, int var2) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         return this.splatTiles == null ? null : this.splatTiles[var1][var2];
      } else {
         return null;
      }
   }

   public void updateSplatting(int var1, int var2, int var3, int var4) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var3 > this.level.width - 1) {
         var3 = this.level.width - 1;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      if (var4 > this.level.height - 1) {
         var4 = this.level.height - 1;
      }

      for(int var5 = var1; var5 <= var3; ++var5) {
         for(int var6 = var2; var6 <= var4; ++var6) {
            this.updateSplatting(var5, var6);
         }
      }

   }

   public void updateSplatting(int var1, int var2) {
      if (this.splatTiles != null && var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         if (this.level.getTile(var1, var2).terrainSplatting) {
            GameObject var3 = this.level.getObject(var1, var2);
            if (var3.stopsTerrainSplatting()) {
               this.splatTiles[var1][var2] = null;
            } else {
               SplattingOptions var4 = new SplattingOptions(this.level, var1, var2);
               this.splatTiles[var1][var2] = var4.isNull() ? null : var4;
            }
         } else {
            this.splatTiles[var1][var2] = null;
         }

      }
   }

   public void onSplattingChange(int var1, int var2) {
      this.updateSplatting(var1 - 1, var2 - 1, var1 + 1, var2 + 1);
   }

   public void addDebugTooltips(int var1, int var2, StringTooltips var3) {
      GameTile var4 = this.level.getTile(var1, var2);
      if (var4.terrainSplatting) {
         var3.add("Splat priority: " + ((TerrainSplatterTile)var4).getTerrainPriority());
      }

      SplattingOptions var5 = this.getSplatTiles(var1, var2);
      if (var5 != null) {
         var5.addDebugTooltips(var3);
      }

   }
}
