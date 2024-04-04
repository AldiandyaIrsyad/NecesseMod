package necesse.level.maps.biomes.pirate;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.AbstractMusicList;
import necesse.engine.GameEvents;
import necesse.engine.MusicList;
import necesse.engine.events.worldGeneration.GenerateIslandFeatureEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFeatureEvent;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.VillageModularGeneration;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageBossPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePathPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom1HPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom1VPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom2Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom3Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWalkway1HPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWalkway1VPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWall1Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWall2Preset;

public class PirateVillageBiome extends ForestBiome {
   public PirateVillageBiome() {
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return addVillage(super.getNewSurfaceLevel(var1, var2, Math.max(var3, 0.7F), var4, var5), var3);
   }

   public AbstractMusicList getLevelMusic(Level var1, PlayerMob var2) {
      return (AbstractMusicList)(!var1.isCave ? new MusicList(new GameMusic[]{MusicRegistry.PiratesHorizon}) : super.getLevelMusic(var1, var2));
   }

   private static Level addVillage(Level var0, float var1) {
      GameEvents.triggerEvent(new GenerateIslandFeatureEvent(var0, var1), (var2) -> {
         int var3 = (int)(var1 * 20.0F) + 60;
         AtomicInteger var4 = new AtomicInteger();
         AtomicInteger var5 = new AtomicInteger();
         VillageModularGeneration var6 = new VillageModularGeneration(var0, var3 / 3, var3 / 3, 3, 3, 1);
         int var7 = var0.width / 2 - var6.cellRes * var6.cellsWidth / 2;
         int var8 = var0.height / 2 - var6.cellRes * var6.cellsWidth / 2;
         var6.setStartPreset(new PirateVillageBossPreset(var6.random));
         var6.addPreset(new PirateVillagePathPreset(true, true, true, true), 6);
         var6.addPreset(new PirateVillagePathPreset(true, false, true, false), 3);
         var6.addPreset(new PirateVillagePathPreset(false, true, false, true), 3);
         var6.addPreset(new PirateVillageRoom1HPreset(var6.random, var4), 3);
         var6.addPreset(new PirateVillageRoom1VPreset(var6.random, var4), 3);
         var6.addPreset(new PirateVillageWalkway1HPreset(var6.random, var4), 2);
         var6.addPreset(new PirateVillageWalkway1VPreset(var6.random, var4), 2);
         var6.addPreset(new PirateVillageRoom2Preset(var6.random, var4), 4);
         var6.addPreset(new PirateVillageRoom3Preset(var6.random, var5), 10, 2);
         var6.initGeneration(var7, var8);
         var6.tickGeneration(var7, var8, var3 * 3);
         var6.addFillPreset(new PirateVillageWall1Preset(var6.random), 5);
         var6.addFillPreset(new PirateVillageWall2Preset(var6.random), 5);
         int var9 = var3 / 10;
         var6.tickFillGeneration(var7, var8, var9);
         String[] var10 = new String[]{"piraterecruit"};
         var6.addRandomMobs(var7, var8, var10, var3 * 4, var3 / 3);
         var6.endGeneration();
      });
      GameEvents.triggerEvent(new GeneratedIslandFeatureEvent(var0, var1));
      GenerationTools.checkValid(var0);
      return var0;
   }
}
