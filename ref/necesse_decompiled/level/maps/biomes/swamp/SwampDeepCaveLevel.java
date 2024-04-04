package necesse.level.maps.biomes.swamp;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.RandomLootAreaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class SwampDeepCaveLevel extends SwampCaveLevel {
   public SwampDeepCaveLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SwampDeepCaveLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public void generateLevel() {
      CaveGeneration var1 = new CaveGeneration(this, "deepswamprocktile", "deepswamprock");
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var1), (var1x) -> {
         var1.generateLevel();
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var1));
      int var2 = ObjectRegistry.getObjectID("swampcrate");
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var1), (var2x) -> {
         GenerationTools.generateRandomSmoothVeinsC(this, var1.random, 0.02F, 4, 15.0F, 25.0F, 3.0F, 5.0F, (var2) -> {
            var2.forEachTile(this, (var1x, var2x, var3) -> {
               var1x.setTile(var2x, var3, TileRegistry.spiderNestID);
               if (var1.random.getChance(0.95F)) {
                  var1x.setObject(var2x, var3, ObjectRegistry.cobWebID);
               } else {
                  var1x.setObject(var2x, var3, 0);
               }

            });
            var2.spawnMobs(this, var1.random, "smallswampcavespider", 4, 8, 1, 8);
         });
         GameTile var3 = TileRegistry.getTile(TileRegistry.deepSwampRockID);
         GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswamptallgrass"));
         GenerationTools.generateRandomSmoothVeinsC(this, var1.random, 0.03F, 5, 4.0F, 10.0F, 3.0F, 5.0F, (var4x) -> {
            var4x.forEachTile(this, (var4xx, var5, var6) -> {
               var3.placeTile(var4xx, var5, var6);
               this.setObject(var5, var6, 0);
               if (var1.random.getChance(0.85F) && var4.canPlace(var4xx, var5, var6, 0) == null) {
                  var4.placeObject(var4xx, var5, var6, 0);
               }

            });
         });
         GenerationTools.generateRandomSmoothTileVeins(this, var1.random, 0.04F, 2, 2.0F, 10.0F, 2.0F, 4.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
         this.liquidManager.calculateShores();
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverock"), 0.005F);
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepswampcaverocksmall"), 0.01F);
         GameObject var5 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepswampgrass"));
         GenerationTools.iterateLevel(this, (var2, var3x) -> {
            return this.getTileID(var2, var3x) == TileRegistry.deepSwampRockID && this.getObjectID(var2, var3x) == 0 && var1.random.getChance(0.6F);
         }, (var2, var3x) -> {
            var5.placeObject(this, var2, var3x, 0);
         });
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveOresEvent(this, var1), (var1x) -> {
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("copperoredeepswamprock"));
         var1.generateOreVeins(0.1F, 3, 6, ObjectRegistry.getObjectID("ironoredeepswamprock"));
         var1.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoredeepswamprock"));
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("tungstenoredeepswamprock"));
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepswamprock"));
         var1.generateOreVeins(0.17F, 3, 6, ObjectRegistry.getObjectID("myceliumoredeepswamprock"));
      });
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var1));
      PresetGeneration var3 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var1, var3), (var3x) -> {
         AtomicInteger var4 = new AtomicInteger();
         int var5 = var1.random.getIntBetween(13, 18);

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            RandomCaveChestRoom var7 = new RandomCaveChestRoom(var1.random, LootTablePresets.deepSwampCaveChest, var4, new ChestRoomSet[]{ChestRoomSet.deepSwampStone, ChestRoomSet.deepStone});
            var7.replaceTile(TileRegistry.deepStoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID)));
            var7.replaceTile(TileRegistry.deepSwampStoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.deepSwampStoneFloorID, TileRegistry.deepSwampStoneBrickFloorID)));
            var3.findRandomValidPositionAndApply(var1.random, 5, var7, 10, true, true);
         }

         var6 = var1.random.getIntBetween(5, 10);

         for(int var14 = 0; var14 < var6; ++var14) {
            RandomLootAreaPreset var8 = new RandomLootAreaPreset(var1.random, 15, "deepswampstonecolumn", new String[]{"swampdweller"});
            var3.findRandomValidPositionAndApply(var1.random, 5, var8, 10, true, true);
         }

         AtomicInteger var15 = new AtomicInteger();
         int var16 = var1.random.getIntBetween(25, 35);

         for(int var9 = 0; var9 < var16; ++var9) {
            WallSet var10 = (WallSet)var1.random.getOneOf((Object[])(WallSet.deepSwampStone, WallSet.deepStone));
            FurnitureSet var11 = (FurnitureSet)var1.random.getOneOf((Object[])(FurnitureSet.oak, FurnitureSet.spruce));
            String var12 = (String)var1.random.getOneOf((Object[])("deepswampstonefloor", "deepswampstonebrickfloor"));
            Preset var13 = ((CaveRuins.CaveRuinGetter)var1.random.getOneOf((List)CaveRuins.caveRuinGetters)).get(var1.random, var10, var11, var12, LootTablePresets.swampDeepCaveRuinsChest, var15);
            var3.findRandomValidPositionAndApply(var1.random, 5, var13, 10, true, true);
         }

         var1.generateRandomCrates(0.03F, var2);
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var1, var3));
      GenerationTools.checkValid(this);
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.swampDeepCrate;
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
   }
}
