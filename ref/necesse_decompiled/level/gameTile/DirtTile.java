package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.layers.SimulatePriorityList;

public class DirtTile extends TerrainSplatterTile {
   public static double snowChance = GameMath.getAverageSuccessRuns(1400.0);
   private final GameRandom drawRandom;
   public static Point[] spreadTiles = new Point[]{new Point(-1, 0), new Point(0, -1), new Point(1, 0), new Point(0, 1)};

   public DirtTile() {
      super(false, "dirt");
      this.mapColor = new Color(136, 120, 72);
      this.canBeMined = false;
      this.drawRandom = new GameRandom();
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      Point[] var8 = spreadTiles;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Point var11 = var8[var10];
         GameTile var12 = var1.getTile(var2 + var11.x, var3 + var11.y);
         double var13 = var12.spreadToDirtChance();
         if (var13 > 0.0 && var12.canPlace(var1, var2, var3) == null) {
            double var15 = Math.max(1.0, GameMath.getRunsForSuccess(var13, GameRandom.globalRandom.nextDouble()));
            long var17 = (long)((double)var4 - var15);
            if (var17 > 0L) {
               var6.add(var2, var3, var17, () -> {
                  var12.placeTile(var1, var2, var3);
                  if (var7) {
                     var1.sendTileUpdatePacket(var2, var3);
                  }

                  var12.addSimulateLogic(var1, var2, var3, var17, var6, var7);
                  Point[] var9 = spreadTiles;
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Point var12x = var9[var11];
                     Point var13 = new Point(var2 + var12x.x, var3 + var12x.y);
                     int var14 = var1.getTileID(var13.x, var13.y);
                     if (var14 == this.getID()) {
                        this.addSimulateLogic(var1, var13.x, var13.y, var17, var6, var7);
                     }
                  }

               });
            }
         }
      }

   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         if (var1.biome instanceof SnowBiome && var1.rainingLayer.isRaining() && GameRandom.globalRandom.getChance(snowChance)) {
            var1.setTile(var2, var3, TileRegistry.snowID);
            var1.sendTileUpdatePacket(var2, var3);
         }

         Performance.record(var1.tickManager(), "grassTick", (Runnable)(() -> {
            Point[] var3x = spreadTiles;
            int var4 = var3x.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Point var6 = var3x[var5];
               GameTile var7 = var1.getTile(var2 + var6.x, var3 + var6.y);
               double var8 = var7.spreadToDirtChance();
               if (var8 != 0.0 && GameRandom.globalRandom.getChance(var8) && var7.canPlace(var1, var2, var3) == null) {
                  var7.placeTile(var1, var2, var3);
                  var1.sendTileUpdatePacket(var2, var3);
                  break;
               }
            }

         }));
      }
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public int getTerrainPriority() {
      return 0;
   }
}
