package necesse.level.maps.biomes.desert;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateIslandAnimalsEvent;
import necesse.engine.events.worldGeneration.GenerateIslandFloraEvent;
import necesse.engine.events.worldGeneration.GenerateIslandLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateIslandStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandAnimalsEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandFloraEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedIslandStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.presets.RandomRuinsPreset;

public class DesertSurfaceLevel extends Level {
   public DesertSurfaceLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public DesertSurfaceLevel(int var1, int var2, float var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, 0), 300, 300, var4);
      this.generateLevel(var3);
   }

   public void generateLevel(float var1) {
      int var2 = (int)(var1 * 90.0F) + 40;
      IslandGeneration var3 = new IslandGeneration(this, var2);
      int var4 = TileRegistry.getTileID("watertile");
      int var5 = TileRegistry.getTileID("sandtile");
      GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, var1, var3), (var4x) -> {
         if (var3.random.getChance(0.05F)) {
            var3.generateSimpleIsland(this.width / 2, this.height / 2, var4, var5, -1);
         } else {
            var3.generateShapedIsland(var4, var5, -1);
         }

         int var5x = var3.random.getIntBetween(1, 3);

         for(int var6 = 0; var6 < var5x && (var6 <= 0 || !var3.random.getChance(0.4F)); ++var6) {
            var3.generateRiver(var4, var5, -1);
         }

         var3.generateLakes(0.01F, var4, var5, -1);
         var3.clearTinyIslands(var4);
         this.liquidManager.calculateHeights();
      });
      GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, var1, var3), (var3x) -> {
         var3.generateObjects(ObjectRegistry.getObjectID("sandsurfacerock"), -1, 0.001F, false);
         var3.generateObjects(ObjectRegistry.getObjectID("sandsurfacerocksmall"), -1, 0.002F, false);
         int var4 = ObjectRegistry.getObjectID("cactus");
         var3.generateObjects(var4, var5, 0.01F);
         int var5x = ObjectRegistry.getObjectID("palmtree");
         var3.generateObjects(var5x, var5, 0.002F);
         var3.generateFruitGrowerSingle("coconuttree", 0.03F, var5);
         GameObject var6 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("watergrass"));
         GenerationTools.generateRandomVeins(this, var3.random, 0.15F, 12, 20, (var2, var3xx, var4x) -> {
            if (var3.random.getChance(0.3F) && var6.canPlace(var2, var3xx, var4x, 0) == null && var2.liquidManager.isFreshWater(var3xx, var4x)) {
               var6.placeObject(var2, var3xx, var4x, 0);
            }

         });
      });
      GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, var1, var3), (var2x) -> {
         GenerationTools.spawnRandomPreset(this, (new RandomRuinsPreset(var3.random)).setTiles("woodfloor", "sandstonefloor").setWalls("woodwall", "sandstonewall"), false, false, var3.random, false, 40, 1);
      });
      GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, var1, var3), (var3x) -> {
         GenerationTools.spawnMobHerds(this, var3.random, "wildostrich", 1, var5, 1, 1);
      });
      GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, var1, var3));
      GenerationTools.checkValid(this);
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
   }
}
