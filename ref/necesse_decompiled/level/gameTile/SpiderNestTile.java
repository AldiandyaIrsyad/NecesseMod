package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.incursions.SpiderCastleBiome;
import necesse.level.maps.biomes.incursions.SwampDeepCaveBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.layers.SimulatePriorityList;

public class SpiderNestTile extends TerrainSplatterTile {
   public static MobSpawnTable nestSpawnTable = (new MobSpawnTable()).add(100, (MobSpawnTable.MobProducer)((var0, var1, var2) -> {
      if (var0.biome instanceof SpiderCastleBiome) {
         return null;
      } else {
         String var3 = "giantcavespider";
         if (var0.biome instanceof SnowBiome) {
            var3 = "blackcavespider";
         } else if (var0.biome instanceof SwampBiome) {
            if (var0.getIslandDimension() == -2) {
               var3 = "smallswampcavespider";
            } else {
               var3 = "swampcavespider";
            }
         } else if (var0.biome instanceof SwampDeepCaveBiome) {
            var3 = "smallswampcavespider";
         }

         return MobRegistry.getMob(var3, var0);
      }
   }));
   public static double webGrowChance = GameMath.getAverageSuccessRuns(2500.0);
   private final GameRandom drawRandom;

   public SpiderNestTile() {
      super(false, "spidernest", "splattingmaskwide");
      this.mapColor = new Color(31, 29, 25);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
      GrassTile.addSimulateGrow(var1, var2, var3, webGrowChance, var4, "cobweb", (var3x, var4x, var5, var6x, var7x) -> {
         return var3x.canPlace(var1, var2, var3, 0) == null && var1.getObjectID(var2, var3 - 1) != ObjectRegistry.getObjectID("royaleggobject");
      }, var6, var7);
   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         if (var1.getObjectID(var2, var3) == 0 && GameRandom.globalRandom.getChance(webGrowChance)) {
            GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cobweb"));
            if (var4.canPlace(var1, var2, var3, 0) == null && var1.getObjectID(var2, var3 - 1) != ObjectRegistry.getObjectID("royaleggobject")) {
               var4.placeObject(var1, var2, var3, 0);
               var1.sendObjectUpdatePacket(var2, var3);
            }
         }

      }
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public int getTerrainPriority() {
      return 200;
   }

   public MobSpawnTable getMobSpawnTable(TilePosition var1, MobSpawnTable var2) {
      if (var1.objectID() == ObjectRegistry.cobWebID) {
         return nestSpawnTable;
      } else {
         int var3 = 0;
         Integer[] var4 = var1.level.getAdjacentObjectsInt(var1.tileX, var1.tileY);
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var4[var6];
            if (var7 == ObjectRegistry.cobWebID) {
               ++var3;
            }
         }

         if (var3 >= 2) {
            return nestSpawnTable;
         } else {
            return super.getMobSpawnTable(var1, var2);
         }
      }
   }
}
