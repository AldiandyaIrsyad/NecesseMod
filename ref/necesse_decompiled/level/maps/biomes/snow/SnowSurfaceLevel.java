package necesse.level.maps.biomes.snow;

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
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.presets.RandomRuinsPreset;

public class SnowSurfaceLevel extends Level {
   public SnowSurfaceLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SnowSurfaceLevel(int var1, int var2, float var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, 0), 300, 300, var4);
      this.generateLevel(var3);
   }

   public void generateLevel(float var1) {
      int var2 = (int)(var1 * 100.0F) + 20;
      IslandGeneration var3 = new IslandGeneration(this, var2);
      int var4 = TileRegistry.waterID;
      int var5 = TileRegistry.snowID;
      int var6 = TileRegistry.iceID;
      GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, var1, var3), (var5x) -> {
         if (var3.random.getChance(0.05F)) {
            var3.generateSimpleIsland(this.width / 2, this.height / 2, var4, var5, var6);
         } else {
            var3.generateShapedIsland(var4, var5, var6);
         }

         int var6x = var3.random.getIntBetween(1, 5);

         for(int var7 = 0; var7 < var6x && (var7 <= 0 || !var3.random.getChance(0.4F)); ++var7) {
            var3.generateRiver(var4, var5, var6);
         }

         var3.generateLakes(0.02F, var4, var5, var6);
         var3.clearTinyIslands(var4);
         this.liquidManager.calculateHeights();
      });
      GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, var1, var3), (var3x) -> {
         int var4 = ObjectRegistry.getObjectID("pinetree");
         var3.generateCellMapObjects(0.4F, var4, var5, 0.08F);
         var3.generateObjects(ObjectRegistry.getObjectID("snowpile0"), var5, 0.05F);
         var3.generateObjects(ObjectRegistry.getObjectID("snowpile1"), var5, 0.05F);
         var3.generateObjects(ObjectRegistry.getObjectID("snowpile2"), var5, 0.05F);
         var3.generateObjects(ObjectRegistry.getObjectID("snowpile3"), var5, 0.05F);
         var3.generateObjects(ObjectRegistry.getObjectID("snowsurfacerock"), -1, 0.001F, false);
         var3.generateObjects(ObjectRegistry.getObjectID("snowsurfacerocksmall"), -1, 0.002F, false);
         var3.generateFruitGrowerVeins("blackberrybush", 0.04F, 8, 10, 0.1F, (int[])(var5));
         GenerationTools.generateRandomObjectVeinsOnTile(this, var3.random, 0.03F, 6, 12, var5, ObjectRegistry.getObjectID("wildiceblossom"), 0.2F, false);
      });
      GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, var1, var3), (var2x) -> {
         GenerationTools.spawnRandomPreset(this, (new RandomRuinsPreset(var3.random)).setTiles("woodfloor", "snowstonefloor").setWalls("woodwall", "snowstonewall"), false, false, var3.random, false, 40, 1);
      });
      GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, var1, var3), (var3x) -> {
         var3.spawnMobHerds("sheep", var3.random.getIntBetween(20, 40), var5, 2, 6, var1);
         var3.spawnMobHerds("penguin", var3.random.getIntBetween(20, 40), var5, 2, 6, var1);
         var3.spawnMobHerds("polarbear", var3.random.getIntBetween(5, 10), var5, 1, 1, var1);
      });
      GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, var1, var3));
      GenerationTools.checkValid(this);
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
   }
}
