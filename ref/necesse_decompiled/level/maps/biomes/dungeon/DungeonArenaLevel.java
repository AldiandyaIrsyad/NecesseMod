package necesse.level.maps.biomes.dungeon;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.generationModules.GenerationTools;
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
import necesse.level.maps.presets.set.FurnitureSet;

public class DungeonArenaLevel extends DungeonLevel {
   public static final int DUNGEON_BOSS_SIZE = 40;
   public static final int DUNGEON_BOSS_EDGE = 40;
   private static final int TOTAL_SIZE = 120;

   public DungeonArenaLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public DungeonArenaLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 120, 120, var4);
      this.isCave = true;
      this.isProtected = true;
      this.generateLevel();
   }

   public void generateLevel() {
      GameRandom var1 = new GameRandom(this.getSeed());
      int var2 = ObjectRegistry.getObjectID("dungeonwall");
      int var3 = TileRegistry.getTileID("dungeonfloor");
      int var4 = ObjectRegistry.getObjectID("dungeoncandelabra");
      int var5 = this.width / 2;
      int var6 = this.height / 2;

      int var7;
      int var8;
      for(var7 = 0; var7 < this.width; ++var7) {
         for(var8 = 0; var8 < this.height; ++var8) {
            double var9 = (new Point2D.Float((float)var5, (float)var6)).distance((double)var7, (double)var8);
            if (var9 <= 20.5) {
               this.setObject(var7, var8, 0);
            } else {
               this.setObject(var7, var8, var2);
            }
         }
      }

      for(var7 = 0; var7 < this.width; ++var7) {
         for(var8 = 0; var8 < this.height; ++var8) {
            this.setTile(var7, var8, var3);
         }
      }

      TicketSystemList var11 = new TicketSystemList();
      var11.addObject(100, new BedDresserPreset(FurnitureSet.dungeon, 2));
      var11.addObject(100, new BenchPreset(FurnitureSet.dungeon, 2));
      var11.addObject(100, new BookshelfClockPreset(FurnitureSet.dungeon, 2));
      var11.addObject(100, new BookshelvesPreset(FurnitureSet.dungeon, 2, 3));
      var11.addObject(100, new CabinetsPreset(FurnitureSet.dungeon, 2, 3));
      var11.addObject(100, new DeskBookshelfPreset(FurnitureSet.dungeon, 2));
      var11.addObject(100, new DinnerTablePreset(FurnitureSet.dungeon, 2));
      var11.addObject(100, new DisplayStandClockPreset(FurnitureSet.dungeon, 2, (GameRandom)null, (LootTable)null, new Object[0]));
      var11.addObject(100, new ModularDinnerTablePreset(FurnitureSet.dungeon, 2, 1));
      var11.addObject(50, new ModularTablesPreset(FurnitureSet.dungeon, 2, 2, true));

      int var12;
      for(var8 = 0; var8 < this.width; ++var8) {
         for(var12 = 0; var12 < this.height; ++var12) {
            if (this.getObjectID(var8, var12) == 0 && var1.getChance(0.8F)) {
               GenerationTools.generateFurniture(this, var1, var8, var12, var11, (var0) -> {
                  return var0.objectID() == 0;
               });
            }
         }
      }

      Point var13 = getLadderPosition();
      this.setObject(var13.x - 5, var13.y, var4);
      this.setObject(var13.x + 5, var13.y, var4);

      for(var12 = 0; var12 < 5; ++var12) {
         int var10 = 6 + var12 * 6;
         this.setObject(var13.x - 4, 75 - var10, var4);
         this.setObject(var13.x + 4, 75 - var10, var4);
         this.setObject(var13.x - 11, 75 - var10 + 2, var4);
         this.setObject(var13.x + 11, 75 - var10 + 2, var4);
      }

      Mob var15 = MobRegistry.getMob("voidwizard", this);
      Point2D.Float var14 = getBossPosition();
      this.entityManager.addMob(var15, var14.x, var14.y);
   }

   public static Point getLadderPosition() {
      return new Point(60, 75);
   }

   public static Point2D.Float getBossPosition() {
      return new Point2D.Float(1936.0F, 1456.0F);
   }
}
