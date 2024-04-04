package necesse.level.maps.biomes.snow;

import java.awt.Color;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public class SnowBiome extends Biome {
   public static FishingLootTable snowSurfaceFish;
   public static MobSpawnTable surfaceMobs;
   public static MobSpawnTable caveMobs;
   public static MobSpawnTable deepSnowCaveMobs;
   public static MobSpawnTable surfaceCritters;
   public static MobSpawnTable caveCritters;
   public static MobSpawnTable deepCaveCritters;
   public static LootItemInterface randomRoyalEggDrop;
   public static LootItemInterface randomIceCrownDrop;

   public SnowBiome() {
   }

   protected void loadRainTexture() {
      this.rainTexture = GameTexture.fromFile("snowfall");
   }

   public Color getRainColor(Level var1) {
      return new Color(255, 255, 255, 200);
   }

   public void tickRainEffect(GameCamera var1, Level var2, int var3, int var4, float var5) {
   }

   public GameSound getRainSound(Level var1) {
      return null;
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return new SnowSurfaceLevel(var1, var2, var3, var5);
   }

   public Level getNewCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new SnowCaveLevel(var1, var2, var3, var5);
   }

   public Level getNewDeepCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new SnowDeepCaveLevel(var1, var2, var3, var5);
   }

   public FishingLootTable getFishingLootTable(FishingSpot var1) {
      return !var1.tile.level.isCave ? snowSurfaceFish : super.getFishingLootTable(var1);
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      if (!var1.isCave) {
         return surfaceMobs;
      } else {
         return var1.getIslandDimension() == -2 ? deepSnowCaveMobs : caveMobs;
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
            return new LootTable(new LootItemInterface[]{randomRoyalEggDrop, super.getExtraMobDrops(var1)});
         }

         if (var1.getLevel().getIslandDimension() == -2) {
            return new LootTable(new LootItemInterface[]{randomIceCrownDrop, super.getExtraMobDrops(var1)});
         }
      }

      return super.getExtraMobDrops(var1);
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      if (var1.isCave) {
         return var1.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.SubzeroSanctum}) : new MusicList(new GameMusic[]{MusicRegistry.GlaciersEmbrace});
      } else {
         return var1.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.PolarNight}) : new MusicList(new GameMusic[]{MusicRegistry.AuroraTundra});
      }
   }

   static {
      snowSurfaceFish = (new FishingLootTable(defaultSurfaceFish)).addWater(120, (String)"icefish");
      surfaceMobs = (new MobSpawnTable()).include(defaultSurfaceMobs).add(50, (String)"trapperzombie");
      caveMobs = (new MobSpawnTable()).add(60, (String)"zombie").add(20, (String)"zombiearcher").add(30, (String)"trapperzombie").add(30, (String)"crawlingzombie").add(40, (String)"frozendwarf").add(15, (String)"frostsentry").add(15, (String)"vampire").add(3, (String)"cavemole");
      deepSnowCaveMobs = (new MobSpawnTable()).add(120, (String)"snowwolf").add(70, (String)"skeleton").add(25, (String)"skeletonthrower").add(50, (String)"cryoflake").add(15, (String)"ninja");
      surfaceCritters = (new MobSpawnTable()).add(100, (String)"snowhare").add(60, (String)"bluebird").add(20, (String)"bird").add(60, (String)"duck");
      caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"snowstonecaveling");
      deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"deepsnowstonecaveling");
      randomRoyalEggDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.005F, "royalegg")});
      randomIceCrownDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "icecrown")});
   }
}
