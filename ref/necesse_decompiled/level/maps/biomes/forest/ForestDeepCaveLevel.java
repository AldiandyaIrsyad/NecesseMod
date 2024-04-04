package necesse.level.maps.biomes.forest;

import java.awt.Rectangle;
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
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class ForestDeepCaveLevel extends ForestCaveLevel {
   public ForestDeepCaveLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public ForestDeepCaveLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public void generateLevel() {
      int var1 = TileRegistry.getTileID("deeprocktile");
      CaveGeneration var2 = new CaveGeneration(this, "deeprocktile", "deeprock");
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var2), (var1x) -> {
         var2.generateLevel(0.44F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var2));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var2), (var3x) -> {
         GenerationTools.generateRandomObjectVeinsOnTile(this, var2.random, 0.2F, 4, 8, var1, ObjectRegistry.getObjectID("wildcaveglow"), 0.2F, false);
         GenerationTools.generateRandomSmoothTileVeins(this, var2.random, 0.07F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
         this.liquidManager.calculateShores();
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverock"), 0.005F);
         var2.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepcaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var2));
      GameEvents.triggerEvent(new GenerateCaveOresEvent(this, var2), (var1x) -> {
         var2.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("copperoredeeprock"));
         var2.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoredeeprock"));
         var2.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoredeeprock"));
         var2.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("obsidianrock"));
         var2.generateOreVeins(0.2F, 3, 6, ObjectRegistry.getObjectID("tungstenoredeeprock"));
         var2.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("lifequartzdeeprock"));
      });
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var2));
      PresetGeneration var3 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var2, var3), (var3x) -> {
         int var4 = var2.random.getIntBetween(2, 3);

         for(int var5 = 0; var5 < var4; ++var5) {
            Rectangle var6 = AbandonedMinePreset.generateAbandonedMineOnLevel(this, var2.random, var3.getOccupiedSpace());
            if (var6 != null) {
               var3.addOccupiedSpace(var6);
            }
         }

         AtomicInteger var14 = new AtomicInteger();
         int var15 = var2.random.getIntBetween(13, 18);

         for(int var7 = 0; var7 < var15; ++var7) {
            RandomCaveChestRoom var8 = new RandomCaveChestRoom(var2.random, LootTablePresets.deepCaveChest, var14, new ChestRoomSet[]{ChestRoomSet.deepStone, ChestRoomSet.obsidian});
            var8.replaceTile(TileRegistry.deepStoneFloorID, (Integer)var2.random.getOneOf((Object[])(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID)));
            var3.findRandomValidPositionAndApply(var2.random, 5, var8, 10, true, true);
         }

         AtomicInteger var16 = new AtomicInteger();
         int var17 = var2.random.getIntBetween(25, 35);

         for(int var9 = 0; var9 < var17; ++var9) {
            WallSet var10 = (WallSet)var2.random.getOneOf((Object[])(WallSet.deepStone, WallSet.obsidian));
            FurnitureSet var11 = (FurnitureSet)var2.random.getOneOf((Object[])(FurnitureSet.oak, FurnitureSet.spruce));
            String var12 = (String)var2.random.getOneOf((Object[])("deepstonefloor", "deepstonebrickfloor"));
            Preset var13 = ((CaveRuins.CaveRuinGetter)var2.random.getOneOf((List)CaveRuins.caveRuinGetters)).get(var2.random, var10, var11, var12, LootTablePresets.basicDeepCaveRuinsChest, var16);
            var3.findRandomValidPositionAndApply(var2.random, 5, var13, 10, true, true);
         }

         var2.generateRandomCrates(0.03F, ObjectRegistry.getObjectID("crate"));
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var2, var3));
      GenerationTools.checkValid(this);
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.basicDeepCrate;
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
   }
}
