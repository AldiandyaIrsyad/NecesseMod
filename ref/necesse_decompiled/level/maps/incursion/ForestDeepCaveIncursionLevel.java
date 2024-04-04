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

public class ForestDeepCaveIncursionLevel extends IncursionLevel {
   public ForestDeepCaveIncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public ForestDeepCaveIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, WorldEntity var3) {
      super(var1, 150, 150, var2, var3);
      this.biome = BiomeRegistry.FOREST_DEEP_CAVE_INCURSION;
      this.isCave = true;
      this.generateLevel(var2);
   }

   public void generateLevel(BiomeMissionIncursionData var1) {
      CaveGeneration var2 = new CaveGeneration(this, "deeprocktile", "deeprock");
      var2.random.setSeed((long)var1.getUniqueID());
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var2), (var1x) -> {
         var2.generateLevel(0.38F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var2));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var2), (var2x) -> {
         GenerationTools.generateRandomSmoothTileVeins(this, var2.random, 0.09F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
         this.liquidManager.calculateShores();
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverock"), 0.005F);
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var2));
      PresetGeneration var3 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var2, var3), (var1x) -> {
         var2.generateRandomCrates(0.03F, ObjectRegistry.getObjectID("crate"));
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var2, var3));
      IncursionBiome.generateEntrance(this, var2.random, 30, var2.rockTile, "deepstonebrickfloor", "deepstonefloor", "deepstonecolumn");
      GenerationTools.checkValid(this);
      if (var1 instanceof BiomeExtractionIncursionData) {
         var2.generateGuaranteedOreVeins(100, 8, 16, ObjectRegistry.getObjectID("tungstenoredeeprock"));
      }

      var2.generateGuaranteedOreVeins(100, 6, 12, ObjectRegistry.getObjectID("upgradesharddeeprock"));
      var2.generateGuaranteedOreVeins(100, 6, 12, ObjectRegistry.getObjectID("alchemysharddeeprock"));
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var2));
   }
}
