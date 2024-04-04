package necesse.level.maps.biomes.desert;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
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
import necesse.level.maps.biomes.MobSpawnTable;

public class DesertBiome extends Biome {
   public static MobSpawnTable surfaceMobs;
   public static MobSpawnTable caveMobs;
   public static MobSpawnTable deepDesertCaveMobs;
   public static MobSpawnTable surfaceCritters;
   public static MobSpawnTable caveCritters;
   public static MobSpawnTable deepCaveCritters;
   public static LootItemInterface randomAncientStatueDrop;
   public static LootItemInterface dragonSoulsDrop;

   public DesertBiome() {
   }

   public boolean canRain(Level var1) {
      return false;
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return new DesertSurfaceLevel(var1, var2, var3, var5);
   }

   public Level getNewCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new DesertCaveLevel(var1, var2, var3, var5);
   }

   public Level getNewDeepCaveLevel(int var1, int var2, int var3, Server var4, WorldEntity var5) {
      return new DesertDeepCaveLevel(var1, var2, var3, var5);
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      if (!var1.isCave) {
         return surfaceMobs;
      } else {
         return var1.getIslandDimension() == -2 ? deepDesertCaveMobs : caveMobs;
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
            return new LootTable(new LootItemInterface[]{randomAncientStatueDrop, super.getExtraMobDrops(var1)});
         }

         if (var1.getLevel().getIslandDimension() == -2) {
            return new LootTable(new LootItemInterface[]{dragonSoulsDrop, super.getExtraMobDrops(var1)});
         }
      }

      return super.getExtraMobDrops(var1);
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      if (var1.isCave) {
         return var1.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.SandCatacombs}) : new MusicList(new GameMusic[]{MusicRegistry.DustyHollows});
      } else {
         return var1.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.NightInTheDunes}) : new MusicList(new GameMusic[]{MusicRegistry.OasisSerenade});
      }
   }

   static {
      surfaceMobs = (new MobSpawnTable()).include(defaultSurfaceMobs).add(35, (String)"mummy");
      caveMobs = (new MobSpawnTable()).add(80, (String)"mummy").add(40, (String)"mummymage").add(40, (String)"sandspirit").add(80, (String)"jackal");
      deepDesertCaveMobs = (new MobSpawnTable()).add(100, (String)"ancientskeleton").add(40, (String)"ancientskeletonthrower").add(50, (String)"desertcrawler").addLimited(6, "sandworm", 1, Mob.MOB_SPAWN_AREA.maxSpawnDistance * 2);
      surfaceCritters = (new MobSpawnTable()).add(100, (String)"crab").add(60, (String)"scorpion").add(60, (String)"canarybird").add(20, (String)"bird").add(60, (String)"turtle").add(10, (String)"duck");
      caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"sandstonecaveling");
      deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, (String)"deepsandstonecaveling");
      randomAncientStatueDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.005F, "ancientstatue")});
      dragonSoulsDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "dragonsouls")});
   }
}
