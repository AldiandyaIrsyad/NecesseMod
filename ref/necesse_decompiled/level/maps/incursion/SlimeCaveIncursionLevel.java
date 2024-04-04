package necesse.level.maps.incursion;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;

public class SlimeCaveIncursionLevel extends IncursionLevel {
   public SlimeCaveIncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SlimeCaveIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, WorldEntity var3) {
      super(var1, 150, 150, var2, var3);
      this.biome = BiomeRegistry.SLIME_CAVE;
      this.isCave = true;
      this.generateLevel(var2);
   }

   public void generateLevel(BiomeMissionIncursionData var1) {
      CaveGeneration var2 = new CaveGeneration(this, "slimerocktile", "slimerock");
      var2.random.setSeed((long)var1.getUniqueID());
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var2), (var1x) -> {
         var2.generateLevel(0.38F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var2));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var2), (var2x) -> {
         GenerationTools.generateRandomSmoothObjectVeins(this, var2.random, 0.5F, 2, 4.0F, 8.0F, 2.0F, 3.0F, ObjectRegistry.getObjectID("groundslime"), 1.0F, true);
         GenerationTools.generateRandomSmoothTileVeins(this, var2.random, 0.12F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("liquidslimetile"), 1.0F, true);
         this.liquidManager.calculateShores();
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("slimecaverock"), 0.005F);
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("slimecaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var2));
      PresetGeneration var3 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var2, var3), (var1x) -> {
         var2.generateRandomCrates(0.03F, ObjectRegistry.getObjectID("swampcrate"));
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var2, var3));
      IncursionBiome.generateEntrance(this, var2.random, 30, var2.rockTile, "deepswampstonebrickfloor", "deepswampstonefloor", "deepswampstonecolumn");
      GenerationTools.checkValid(this);
      if (var1 instanceof BiomeExtractionIncursionData) {
         var2.generateGuaranteedOreVeins(100, 8, 16, ObjectRegistry.getObjectID("slimeumslimerock"));
      }

      var2.generateGuaranteedOreVeins(100, 6, 12, ObjectRegistry.getObjectID("upgradeshardslimerock"));
      var2.generateGuaranteedOreVeins(100, 6, 12, ObjectRegistry.getObjectID("alchemyshardslimerock"));
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var2));
   }
}
