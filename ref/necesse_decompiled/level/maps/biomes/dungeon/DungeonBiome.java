package necesse.level.maps.biomes.dungeon;

import necesse.engine.AbstractMusicList;
import necesse.engine.GameEvents;
import necesse.engine.MusicList;
import necesse.engine.events.worldGeneration.GenerateIslandFeatureEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFeatureEvent;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.presets.DungeonEntrancePreset;

public class DungeonBiome extends ForestBiome {
   public static MobSpawnTable defaultDungeonMobs = (new MobSpawnTable()).add(80, (String)"enchantedzombie").add(60, (String)"enchantedzombiearcher").add(15, (String)"enchantedcrawlingzombie").add(30, (String)"voidapprentice");

   public DungeonBiome() {
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return addDungeonEntrance(super.getNewSurfaceLevel(var1, var2, var3, var4, var5), var3);
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      if (var1.getIslandDimension() == -100) {
         return defaultDungeonMobs;
      } else {
         return var1.getIslandDimension() == -101 ? new MobSpawnTable() : super.getMobSpawnTable(var1);
      }
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      return var1.getIslandDimension() != -100 && var1.getIslandDimension() != -101 ? super.getCritterSpawnTable(var1) : defaultCaveCritters;
   }

   public float getSpawnRateMod(Level var1) {
      return var1.getIslandDimension() == -100 ? super.getSpawnRateMod(var1) * 0.9F : super.getSpawnRateMod(var1);
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return (AbstractMusicList)(!var1.isIslandPosition() || var1.getIslandDimension() != -100 && var1.getIslandDimension() != -101 ? super.getLevelMusic(var1, var2) : new MusicList(new GameMusic[]{MusicRegistry.VoidsEmbrace}));
   }

   private static Level addDungeonEntrance(Level var0, float var1) {
      GameEvents.triggerEvent(new GenerateIslandFeatureEvent(var0, var1), (var1x) -> {
         GameRandom var2 = new GameRandom(var0.getSeed());
         (new DungeonEntrancePreset(var2)).applyToLevelCentered(var0, var0.width / 2, var0.height / 2);
      });
      GameEvents.triggerEvent(new GeneratedIslandFeatureEvent(var0, var1));
      return var0;
   }
}
