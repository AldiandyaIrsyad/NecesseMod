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
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;

public class SwampDeepCaveIncursionLevel extends IncursionLevel {
   public SwampDeepCaveIncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SwampDeepCaveIncursionLevel(LevelIdentifier var1, BiomeMissionIncursionData var2, WorldEntity var3) {
      super(var1, 150, 150, var2, var3);
      this.biome = BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION;
      this.isCave = true;
      this.generateLevel(var2);
   }

   public void generateLevel(BiomeMissionIncursionData var1) {
      CaveGeneration var2 = new CaveGeneration(this, "deepswamprocktile", "deepswamprock");
      var2.random.setSeed((long)var1.getUniqueID());
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var2), (var1x) -> {
         var2.generateLevel(0.38F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var2));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var2), (var2x) -> {
         GenerationTools.generateRandomSmoothVeinsC(this, var2.random, 0.02F, 4, 15.0F, 25.0F, 3.0F, 5.0F, (var2xx) -> {
            var2xx.forEachTile(this, (var1, var2x, var3) -> {
               var1.setTile(var2x, var3, TileRegistry.spiderNestID);
               if (var2.random.getChance(0.95F)) {
                  var1.setObject(var2x, var3, ObjectRegistry.cobWebID);
               } else {
                  var1.setObject(var2x, var3, 0);
               }

            });
         });
         GameTile var3 = TileRegistry.getTile(TileRegistry.deepSwampRockID);
         GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswamptallgrass"));
         GenerationTools.generateRandomSmoothVeinsC(this, var2.random, 0.03F, 5, 4.0F, 10.0F, 3.0F, 5.0F, (var4x) -> {
            var4x.forEachTile(this, (var4xx, var5, var6) -> {
               var3.placeTile(var4xx, var5, var6);
               this.setObject(var5, var6, 0);
               if (var2.random.getChance(0.85F) && var4.canPlace(var4xx, var5, var6, 0) == null) {
                  var4.placeObject(var4xx, var5, var6, 0);
               }

            });
         });
         GenerationTools.generateRandomSmoothTileVeins(this, var2.random, 0.04F, 2, 2.0F, 10.0F, 2.0F, 4.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
         this.liquidManager.calculateShores();
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverock"), 0.005F);
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverocksmall"), 0.01F);
         GameObject var5 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswampgrass"));
         GenerationTools.iterateLevel(this, (var2xx, var3x) -> {
            return this.getTileID(var2xx, var3x) == TileRegistry.deepSwampRockID && this.getObjectID(var2xx, var3x) == 0 && var2.random.getChance(0.6F);
         }, (var2xx, var3x) -> {
            var5.placeObject(this, var2xx, var3x, 0);
         });
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
         var2.generateGuaranteedOreVeins(100, 8, 16, ObjectRegistry.getObjectID("myceliumoredeepswamprock"));
      }

      var2.generateGuaranteedOreVeins(100, 6, 12, ObjectRegistry.getObjectID("upgradesharddeepswamprock"));
      var2.generateGuaranteedOreVeins(100, 6, 12, ObjectRegistry.getObjectID("alchemysharddeepswamprock"));
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var2));
   }
}
