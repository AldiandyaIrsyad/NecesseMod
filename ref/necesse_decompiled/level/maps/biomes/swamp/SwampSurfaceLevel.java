package necesse.level.maps.biomes.swamp;

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
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.presets.RandomRuinsPreset;

public class SwampSurfaceLevel extends Level {
   public SwampSurfaceLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SwampSurfaceLevel(int var1, int var2, float var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, 0), 300, 300, var4);
      this.generateLevel(var3);
   }

   public void generateLevel(float var1) {
      int var2 = (int)(var1 * 100.0F) + 20;
      IslandGeneration var3 = new IslandGeneration(this, var2);
      int var4 = TileRegistry.waterID;
      int var5 = TileRegistry.swampGrassID;
      GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, var1, var3), (var4x) -> {
         for(int var5x = 0; var5x < 25; ++var5x) {
            var3.generateRandomCellIsland(var3.random.getIntBetween(10, 40), var3.random.getIntBetween(50, this.width - 50), var3.random.getIntBetween(50, this.height - 50));
         }

         var3.cellMap = GenerationTools.doCellularAutomaton(var3.cellMap, this.width, this.height, 5, 4, false, 4);
         var3.updateCellMap(var5, var4);
         GenerationTools.smoothTile(this, var5);
         this.liquidManager.calculateHeights();
      });
      GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, var1, var3), (var3x) -> {
         GameTile var4 = TileRegistry.getTile(TileRegistry.mudID);
         GameObject var5x = ObjectRegistry.getObject(ObjectRegistry.getObjectID("wildmushroom"));
         GenerationTools.generateRandomSmoothVeins(this, var3.random, 0.1F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (var5xx, var6x, var7x) -> {
            if (var5xx.getTileID(var6x, var7x) == var5) {
               if (var3.random.getChance(0.7F)) {
                  var4.placeTile(var5xx, var6x, var7x);
               }

               if (this.getObjectID(var6x, var7x) == 0 && var3.random.getChance(0.1F) && var5x.canPlace(var5xx, var6x, var7x, 0) == null) {
                  var5x.placeObject(var5xx, var6x, var7x, 0);
               }
            }

         });
         int var6 = ObjectRegistry.getObjectID("willowtree");
         int var7 = ObjectRegistry.getObjectID("swampgrass");
         var3.generateCellMapObjects(0.35F, var6, var5, 0.08F);
         var3.generateObjects(var7, var5, 0.4F);
         var3.generateObjects(ObjectRegistry.getObjectID("swampsurfacerock"), -1, 0.001F, false);
         var3.generateObjects(ObjectRegistry.getObjectID("swampsurfacerocksmall"), -1, 0.002F, false);
         GameObject var8 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cattail"));
         GenerationTools.generateRandomVeins(this, var3.random, 0.2F, 12, 20, (var2, var3xx, var4x) -> {
            if (var3.random.getChance(0.3F) && var8.canPlace(var2, var3xx, var4x, 0) == null) {
               var8.placeObject(var2, var3xx, var4x, 0);
            }

         });
      });
      GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, var1, var3), (var2x) -> {
         GenerationTools.spawnRandomPreset(this, (new RandomRuinsPreset(var3.random)).setTiles("swampstonefloor").setWalls("swampstonewall"), false, false, var3.random, false, 40, 1);
      });
      GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, var1, var3));
      GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, var1, var3), (var3x) -> {
         var3.spawnMobHerds("sheep", var3.random.getIntBetween(15, 30), var5, 2, 6, var1);
         var3.spawnMobHerds("cow", var3.random.getIntBetween(10, 25), var5, 2, 6, var1);
      });
      GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, var1, var3));
      GenerationTools.checkValid(this);
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
   }
}
