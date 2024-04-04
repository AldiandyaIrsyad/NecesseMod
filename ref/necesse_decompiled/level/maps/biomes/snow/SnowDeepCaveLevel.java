package necesse.level.maps.biomes.snow;

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
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class SnowDeepCaveLevel extends SnowCaveLevel {
   public SnowDeepCaveLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public SnowDeepCaveLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public void generateLevel() {
      CaveGeneration var1 = new CaveGeneration(this, "deepsnowrocktile", "deepsnowrock");
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var1), (var1x) -> {
         var1.generateLevel(0.44F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var1), (var2x) -> {
         GameTile var3 = TileRegistry.getTile("deepicetile");
         GenerationTools.generateRandomSmoothVeins(this, var1.random, 0.06F, 2, 7.0F, 20.0F, 3.0F, 8.0F, (var1x, var2, var3x) -> {
            var3.placeTile(var1x, var2, var3x);
            var1x.setObject(var2, var3x, 0);
         });
         this.liquidManager.calculateShores();
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsnowcaverock"), 0.005F);
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsnowcaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveOresEvent(this, var1), (var1x) -> {
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("copperoredeepsnowrock"));
         var1.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoredeepsnowrock"));
         var1.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoredeepsnowrock"));
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("tungstenoredeepsnowrock"));
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepsnowrock"));
         var1.generateOreVeins(0.17F, 3, 6, ObjectRegistry.getObjectID("glacialoredeepsnowrock"));
      });
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var1));
      PresetGeneration var2 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var1, var2), (var2x) -> {
         AtomicInteger var3 = new AtomicInteger();
         int var4 = var1.random.getIntBetween(13, 18);

         for(int var5 = 0; var5 < var4; ++var5) {
            RandomCaveChestRoom var6 = new RandomCaveChestRoom(var1.random, LootTablePresets.deepSnowCaveChest, var3, new ChestRoomSet[]{ChestRoomSet.deepStone, ChestRoomSet.deepSnowStone});
            var6.replaceTile(TileRegistry.deepStoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID)));
            var6.replaceTile(TileRegistry.deepSnowStoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.deepSnowStoneFloorID, TileRegistry.deepSnowStoneBrickFloorID)));
            var2.findRandomValidPositionAndApply(var1.random, 5, var6, 10, true, true);
         }

         AtomicInteger var12 = new AtomicInteger();
         int var13 = var1.random.getIntBetween(25, 35);

         for(int var7 = 0; var7 < var13; ++var7) {
            WallSet var8 = (WallSet)var1.random.getOneOf((Object[])(WallSet.deepStone, WallSet.deepSnowStone));
            FurnitureSet var9 = (FurnitureSet)var1.random.getOneOf((Object[])(FurnitureSet.pine, FurnitureSet.spruce));
            String var10 = (String)var1.random.getOneOf((Object[])("deepsnowstonefloor", "deepsnowstonebrickfloor"));
            Preset var11 = ((CaveRuins.CaveRuinGetter)var1.random.getOneOf((List)CaveRuins.caveRuinGetters)).get(var1.random, var8, var9, var10, LootTablePresets.snowDeepCaveRuinsChest, var12);
            var2.findRandomValidPositionAndApply(var1.random, 5, var11, 10, true, true);
         }

         var1.generateRandomCrates(0.03F, ObjectRegistry.getObjectID("snowcrate"));
      });
      GenerationTools.checkValid(this);
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.snowDeepCrate;
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
   }
}
