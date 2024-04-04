package necesse.level.maps.biomes.desert;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateIslandFeatureEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFeatureEvent;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.VillageGeneration;
import necesse.level.maps.presets.set.VillageSet;

public class DesertVillageBiome extends DesertBiome {
   public DesertVillageBiome() {
   }

   public boolean hasVillage() {
      return true;
   }

   public Level getNewSurfaceLevel(int var1, int var2, float var3, Server var4, WorldEntity var5) {
      return addVillage(super.getNewSurfaceLevel(var1, var2, Math.max(var3, 0.6F), var4, var5), var3);
   }

   private static Level addVillage(Level var0, float var1) {
      GameEvents.triggerEvent(new GenerateIslandFeatureEvent(var0, var1), (var2) -> {
         GameRandom var3 = new GameRandom(var0.getSeed());
         VillageSet var4 = (VillageSet)var3.getOneOf((Object[])(VillageSet.palm));
         (new VillageGeneration(var0, var1, var4, var3)).addStandardPresets().generate();
      });
      GameEvents.triggerEvent(new GeneratedIslandFeatureEvent(var0, var1));
      GenerationTools.checkValid(var0);
      return var0;
   }
}
