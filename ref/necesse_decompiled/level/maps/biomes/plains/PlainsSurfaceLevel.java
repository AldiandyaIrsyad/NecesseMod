package necesse.level.maps.biomes.plains;

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

public class PlainsSurfaceLevel extends Level {
   public PlainsSurfaceLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public PlainsSurfaceLevel(int var1, int var2, float var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, 0), 300, 300, var4);
      this.generateLevel(var3);
   }

   public void generateLevel(float var1) {
      int var2 = (int)(var1 * 90.0F) + 40;
      IslandGeneration var3 = new IslandGeneration(this, var2);
      int var4 = TileRegistry.getTileID("watertile");
      int var5 = TileRegistry.getTileID("sandtile");
      int var6 = TileRegistry.grassID;
      GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, var1, var3), (var5x) -> {
         if (var3.random.getChance(0.05F)) {
            var3.generateSimpleIsland(this.width / 2, this.height / 2, var4, var6, var5);
         } else {
            var3.generateShapedIsland(var4, var6, var5);
         }

         int var6x = var3.random.getIntBetween(1, 5);

         for(int var7 = 0; var7 < var6x && (var7 <= 0 || !var3.random.getChance(0.4F)); ++var7) {
            var3.generateRiver(var4, var6, var5);
         }

         var3.generateLakes(0.02F, var4, var6, var5);
         var3.clearTinyIslands(var4);
         this.liquidManager.calculateHeights();
      });
      GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, var1, var3), (var3x) -> {
         int var4 = ObjectRegistry.getObjectID("oaktree");
         int var5 = ObjectRegistry.getObjectID("sprucetree");
         int var6x = ObjectRegistry.getObjectID("grass");
         var3.generateCellMapObjects(0.32F, var4, var6, 0.03F);
         var3.generateCellMapObjects(0.32F, var5, var6, 0.05F);
         var3.generateObjects(var6x, var6, 0.2F);
         var3.generateObjects(ObjectRegistry.getObjectID("surfacerock"), -1, 0.001F, false);
         var3.generateObjects(ObjectRegistry.getObjectID("surfacerocksmall"), -1, 0.002F, false);
         var3.ensureGenerateObjects("beehive", 2, (int[])(var6));
         var3.generateFruitGrowerSingle("appletree", 0.01F, var6);
         var3.generateFruitGrowerVeins("blueberrybush", 0.04F, 8, 10, 0.1F, (int[])(var6));
         GenerationTools.generateRandomObjectVeinsOnTile(this, var3.random, 0.1F, 5, 10, var6, ObjectRegistry.getObjectID("wildsunflower"), 0.15F, false);
         GameObject var7 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("watergrass"));
         GenerationTools.generateRandomVeins(this, var3.random, 0.15F, 12, 20, (var2, var3xx, var4x) -> {
            if (var3.random.getChance(0.3F) && var7.canPlace(var2, var3xx, var4x, 0) == null && var2.liquidManager.isFreshWater(var3xx, var4x)) {
               var7.placeObject(var2, var3xx, var4x, 0);
            }

         });
      });
      GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, var1, var3), (var2x) -> {
         GenerationTools.spawnRandomPreset(this, new RandomRuinsPreset(var3.random), false, false, var3.random, false, 40, 2);
      });
      GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, var1, var3), (var3x) -> {
         var3.spawnMobHerds("sheep", var3.random.getIntBetween(25, 50), var6, 2, 6, var1);
         var3.spawnMobHerds("cow", var3.random.getIntBetween(15, 40), var6, 2, 6, var1);
      });
      GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, var1, var3));
      GenerationTools.checkValid(this);
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
   }
}
