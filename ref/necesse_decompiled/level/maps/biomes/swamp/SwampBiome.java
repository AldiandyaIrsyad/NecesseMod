package necesse.level.maps.biomes.swamp;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public class SwampBiome extends Biome {
   public static FishingLootTable swampSurfaceFish;
   public static MobSpawnTable surfaceMobs;
   public static MobSpawnTable caveMobs;
   public static MobSpawnTable deepSwampCaveMobs;
   public static MobSpawnTable surfaceCritters;
   public static MobSpawnTable caveCritters;
   public static MobSpawnTable deepCaveCritters;
   public static LootItemInterface randomSpikedFossilDrop;
   public static LootItemInterface randomDecayingLeafDrop;

   public SwampBiome() {
   }

   public int getRainTimeInSeconds(Level var1, GameRandom var2) {
      return var2.getIntBetween(300, 420);
   }

   public int getDryTimeInSeconds(Level var1, GameRandom var2) {
      return var2.getIntBetween(180, 240);
   }

   public float getAverageRainingPercent(Level var1) {
      float var2 = 360.0F;
      float var3 = 210.0F;
      return var2 / (var2 + var3);
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return new SwampSurfaceLevel(var1, var2, var3, var5);
   }

   public Level getNewCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new SwampCaveLevel(var1, var2, var3, var5);
   }

   public Level getNewDeepCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new SwampDeepCaveLevel(var1, var2, var3, var5);
   }

   public FishingLootTable getFishingLootTable(FishingSpot var1) {
      return !var1.tile.level.isCave ? swampSurfaceFish : super.getFishingLootTable(var1);
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      if (!var1.isCave) {
         return surfaceMobs;
      } else {
         return var1.getIslandDimension() == -2 ? deepSwampCaveMobs : caveMobs;
      }
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      if (var1.isCave) {
         return var1.getIslandDimension() == -2 ? deepCaveCritters : caveCritters;
      } else {
         return surfaceCritters;
      }
   }

   public LootTable getExtraMobDrops(Mob var1) {
      if (var1.isHostile && !var1.isBoss() && !var1.isSummoned) {
         if (var1.getLevel().getIslandDimension() == -1) {
            return new LootTable(new LootItemInterface[]{randomSpikedFossilDrop, super.getExtraMobDrops(var1)});
         }

         if (var1.getLevel().getIslandDimension() == -2) {
            return new LootTable(new LootItemInterface[]{randomDecayingLeafDrop, super.getExtraMobDrops(var1)});
         }
      }

      return super.getExtraMobDrops(var1);
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      if (var1.isCave) {
         return var1.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.SwampCavern}) : new MusicList(new GameMusic[]{MusicRegistry.MurkyMire});
      } else {
         return var1.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.GatorsLullaby}) : new MusicList(new GameMusic[]{MusicRegistry.WatersideSerenade});
      }
   }

   static {
      swampSurfaceFish = (new FishingLootTable(defaultSurfaceFish)).addWater(120, (String)"swampfish");
      surfaceMobs = (new MobSpawnTable()).include(defaultSurfaceMobs).add(50, (String)"swampzombie").add(50, (String)"swampslime");
      caveMobs = (new MobSpawnTable()).include(defaultCaveMobs).add(100, (String)"swampzombie").add(100, (String)"swampslime").add(60, (String)"swampshooter");
      deepSwampCaveMobs = (new MobSpawnTable()).add(70, (String)"ancientskeleton").add(25, (String)"ancientskeletonthrower").add(30, (String)"swampskeleton").add(40, (String)"swampdweller").add(70, (String)"giantswampslime");
      surfaceCritters = (new MobSpawnTable()).add(100, (String)"swampslug").add(80, (String)"frog").add(40, (String)"bird").add(40, (String)"cardinalbird").add(40, (String)"duck");
      caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"swampstonecaveling").add(100, (String)"frog");
      deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"deepswampstonecaveling").add(100, (String)"frog");
      randomSpikedFossilDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.005F, "spikedfossil")});
      randomDecayingLeafDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "decayingleaf")});
   }
}
