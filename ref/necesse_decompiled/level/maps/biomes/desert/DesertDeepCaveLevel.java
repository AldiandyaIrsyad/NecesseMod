package necesse.level.maps.biomes.desert;

import java.awt.Point;
import java.awt.geom.Point2D;
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
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.furniturePresets.BedDresserPreset;
import necesse.level.maps.presets.furniturePresets.BenchPreset;
import necesse.level.maps.presets.furniturePresets.BookshelfClockPreset;
import necesse.level.maps.presets.furniturePresets.BookshelvesPreset;
import necesse.level.maps.presets.furniturePresets.CabinetsPreset;
import necesse.level.maps.presets.furniturePresets.DeskBookshelfPreset;
import necesse.level.maps.presets.furniturePresets.DinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.DisplayStandClockPreset;
import necesse.level.maps.presets.furniturePresets.ModularDinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.ModularTablesPreset;
import necesse.level.maps.presets.furniturePresets.SingleChestPreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class DesertDeepCaveLevel extends DesertCaveLevel {
   public DesertDeepCaveLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public DesertDeepCaveLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public void generateLevel() {
      CaveGeneration var1 = new CaveGeneration(this, "deepsandstonetile", "deepsandstonerock");
      GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, var1), (var1x) -> {
         var1.generateLevel(0.44F, 4, 3, 6);
      });
      GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, var1), (var2x) -> {
         GenerationTools.generateRandomSmoothTileVeins(this, var1.random, 0.05F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
         this.liquidManager.calculateShores();
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsandcaverock"), 0.005F);
         var1.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsandcaverocksmall"), 0.01F);
      });
      GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, var1));
      GameEvents.triggerEvent(new GenerateCaveOresEvent(this, var1), (var1x) -> {
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("copperoredeepsandstonerock"));
         var1.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoredeepsandstonerock"));
         var1.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoredeepsandstonerock"));
         var1.generateOreVeins(0.17F, 3, 6, ObjectRegistry.getObjectID("ancientfossiloredeepsnowrock"));
         var1.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepsandstonerock"));
      });
      GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, var1));
      PresetGeneration var2 = new PresetGeneration(this);
      GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, var1, var2), (var3) -> {
         AtomicInteger var4 = new AtomicInteger();
         int var5 = var1.random.getIntBetween(13, 18);

         for(int var6 = 0; var6 < var5; ++var6) {
            RandomCaveChestRoom var7 = new RandomCaveChestRoom(var1.random, LootTablePresets.deepDesertCaveChest, var4, new ChestRoomSet[]{ChestRoomSet.deepSandstone, ChestRoomSet.obsidian});
            var7.replaceTile(TileRegistry.deepStoneFloorID, (Integer)var1.random.getOneOf((Object[])(TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID)));
            var2.findRandomValidPositionAndApply(var1.random, 5, var7, 10, true, true);
         }

         AtomicInteger var29 = new AtomicInteger();
         int var30 = var1.random.getIntBetween(25, 35);

         int var8;
         for(var8 = 0; var8 < var30; ++var8) {
            WallSet var9 = (WallSet)var1.random.getOneOf((Object[])(WallSet.deepSandstone, WallSet.obsidian));
            FurnitureSet var10 = (FurnitureSet)var1.random.getOneOf((Object[])(FurnitureSet.palm, FurnitureSet.spruce));
            String var11 = (String)var1.random.getOneOf((Object[])("deepstonefloor", "deepstonebrickfloor"));
            Preset var12 = ((CaveRuins.CaveRuinGetter)var1.random.getOneOf((List)CaveRuins.caveRuinGetters)).get(var1.random, var9, var10, var11, LootTablePresets.desertDeepCaveRuinsChest, var29);
            var2.findRandomValidPositionAndApply(var1.random, 5, var12, 10, true, true);
         }

         var8 = var1.random.nextInt(360);
         Point2D.Float var31 = GameMath.getAngleDir((float)var8);
         float var32 = var1.random.getFloatBetween(0.0F, 0.4F);
         Point var33 = new Point(this.width / 2 + (int)(var31.x * (float)(this.width / 2) * var32), this.height / 2 + (int)(var31.y * (float)(this.height / 2) * var32));
         float var34 = 15.5F;
         LinesGeneration var13 = new LinesGeneration(var33.x, var33.y, var34);
         int var14 = var1.random.nextInt(360);
         byte var15 = 8;
         int var16 = 360 / var15;

         for(int var17 = 0; var17 < var15; ++var17) {
            var14 += var16;
            var13.addMultiArm(var1.random, var14, 15, var1.random.getIntBetween(this.width / 2, (int)((float)this.width / 1.5F)), 5.0F, 10.0F, 7.0F, 8.0F, (var1x) -> {
               return var1x.x2 < 10 || var1x.x2 > this.width - 10 || var1x.y2 < 10 || var1x.y2 > this.height - 10;
            });
         }

         CellAutomaton var35 = var13.doCellularAutomaton(var1.random);
         var35.cleanHardEdges();

         int var18;
         int var19;
         for(var18 = var33.x - (int)Math.floor((double)var34); (double)var18 <= (double)var33.x + Math.ceil((double)var34); ++var18) {
            for(var19 = var33.y - (int)Math.floor((double)var34); (double)var19 <= (double)var33.y + Math.ceil((double)var34); ++var19) {
               if (var33.distance((double)var18, (double)var19) <= (double)var34) {
                  var35.setAlive(var18, var19);
               }
            }
         }

         var18 = TileRegistry.getTileID("sandbrick");
         var19 = TileRegistry.getTileID("woodfloor");
         var35.forEachTile(this, (var3x, var4x, var5x) -> {
            if (var1.random.getChance(0.75F)) {
               var3x.setTile(var4x, var5x, var18);
            } else {
               var3x.setTile(var4x, var5x, var19);
            }

            var3x.setObject(var4x, var5x, 0);
         });
         var35.placeEdgeWalls(this, ObjectRegistry.getObjectID("deepsandstonewall"), true);
         var35.forEachTile(this, (var1x, var2x, var3x) -> {
            if (var1.random.getChance(0.02F)) {
               GameObject var4 = ObjectRegistry.getObject((String)var1.random.getOneOf((Object[])("crate", "vase")));
               if (var4.canPlace(var1x, var2x, var3x, 0) == null) {
                  var4.placeObject(var1x, var2x, var3x, 0);
               }
            }

         });

         int var21;
         for(int var20 = var33.x - (int)Math.floor((double)var34); (double)var20 <= (double)var33.x + Math.ceil((double)var34); ++var20) {
            for(var21 = var33.y - (int)Math.floor((double)var34); (double)var21 <= (double)var33.y + Math.ceil((double)var34); ++var21) {
               if (var33.distance((double)var20, (double)var21) <= (double)var34 && var1.random.getChance(0.05F)) {
                  GameObject var22 = ObjectRegistry.getObject((String)var1.random.getOneOf((Object[])("crate", "vase")));
                  if (var22.canPlace(this, var20, var21, 0) == null) {
                     var22.placeObject(this, var20, var21, 0);
                  }
               }
            }
         }

         LootTable var36 = new LootTable();

         for(var21 = 0; var21 < 5; ++var21) {
            var36.items.add(this.getCrateLootTable());
         }

         TicketSystemList var37 = new TicketSystemList();
         var37.addObject(100, new BedDresserPreset(FurnitureSet.palm, 2));
         var37.addObject(100, new BenchPreset(FurnitureSet.palm, 2));
         var37.addObject(100, new BookshelfClockPreset(FurnitureSet.palm, 2));
         var37.addObject(100, new BookshelvesPreset(FurnitureSet.palm, 2, 3));
         var37.addObject(100, new CabinetsPreset(FurnitureSet.palm, 2, 3));
         var37.addObject(100, new DeskBookshelfPreset(FurnitureSet.palm, 2));
         var37.addObject(100, new DinnerTablePreset(FurnitureSet.palm, 2));
         var37.addObject(100, new DisplayStandClockPreset(FurnitureSet.palm, 2, var1.random, (LootTable)null, new Object[0]));
         var37.addObject(100, new ModularDinnerTablePreset(FurnitureSet.palm, 2, 1));
         var37.addObject(100, new ModularTablesPreset(FurnitureSet.palm, 2, 2, true));
         var37.addObject(100, new SingleChestPreset(FurnitureSet.palm, 2, var1.random, var36, new Object[0]));
         var35.placeFurniturePresets(var37, 0.4F, this, var1.random);
         int var38 = var1.random.getIntBetween(6, 8);
         int var23 = 360 / var38;
         int var24 = var1.random.nextInt(360);
         float var25 = var34 - var34 / 3.0F;
         int var26 = ObjectRegistry.getObjectID("deepsandstonecolumn");

         for(int var27 = 0; var27 < var38; ++var27) {
            var24 += var1.random.getIntOffset(var23, var23 / 5);
            Point2D.Float var28 = GameMath.getAngleDir((float)var24);
            this.setObject(var33.x + (int)(var28.x * var25), var33.y + (int)(var28.y * var25), var26);
         }

         this.setObject(var33.x, var33.y, ObjectRegistry.getObjectID("templepedestal"));
         var1.generateRandomCrates(0.03F, ObjectRegistry.getObjectID("crate"), ObjectRegistry.getObjectID("vase"));
      });
      GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, var1, var2));
      GenerationTools.checkValid(this);
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.desertDeepCrate;
   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
   }
}
