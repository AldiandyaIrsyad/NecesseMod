package necesse.level.maps.biomes.plains;

import necesse.engine.network.server.Server;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;

public class PlainsBiome extends Biome {
   public static MobSpawnTable caveCritters;
   public static MobSpawnTable deepCaveCritters;

   public PlainsBiome() {
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return new PlainsSurfaceLevel(var1, var2, var3, var5);
   }

   public Level getNewCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new ForestCaveLevel(var1, var2, var3, var5);
   }

   public Level getNewDeepCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new ForestDeepCaveLevel(var1, var2, var3, var5);
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      if (var1.isCave) {
         return var1.getIslandDimension() == -2 ? deepCaveCritters : caveCritters;
      } else {
         return super.getCritterSpawnTable(var1);
      }
   }

   public FishingLootTable getFishingLootTable(FishingSpot var1) {
      return !var1.tile.level.isCave ? ForestBiome.forestSurfaceFish : super.getFishingLootTable(var1);
   }

   public LootTable getExtraMobDrops(Mob var1) {
      if (var1.isHostile && !var1.isBoss() && !var1.isSummoned) {
         if (var1.getLevel().getIslandDimension() == -1) {
            return new LootTable(new LootItemInterface[]{ForestBiome.randomPortalDrop, super.getExtraMobDrops(var1)});
         }

         if (var1.getLevel().getIslandDimension() == -2) {
            return new LootTable(new LootItemInterface[]{ForestBiome.randomShadowGateDrop, super.getExtraMobDrops(var1)});
         }
      }

      return super.getExtraMobDrops(var1);
   }

   static {
      caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"stonecaveling");
      deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"deepstonecaveling");
   }
}
