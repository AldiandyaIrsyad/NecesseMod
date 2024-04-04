package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import java.awt.Point;
import java.util.Iterator;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.presets.modularPresets.ModularPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineHallwayPreset;

public class SpiderCastlePreset extends ModularPreset {
   public final int wall;
   public final int floor;
   public GameRandom random;

   public SpiderCastlePreset(int var1, int var2, int var3, int var4, GameRandom var5) {
      super(var1, var2, 7, var3, var4);
      this.random = var5;
      this.overlap = true;
      this.wall = ObjectRegistry.getObjectID("deepstonewall");
      this.floor = TileRegistry.deepStoneFloorID;
   }

   public SpiderCastlePreset(int var1, int var2, GameRandom var3) {
      this(var1, var2, 5, 1, var3);
   }

   public SpiderCastlePreset(int var1, int var2) {
      this(var1, var2, (GameRandom)null);
   }

   protected SpiderCastlePreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return new SpiderCastlePreset(var1, var2, var4, var5, this.random);
   }

   public static void generateSpiderCasteOnLevel(Level var0, GameRandom var1) {
      ModularGeneration var2 = new ModularGeneration(var0, var1.getIntBetween(10, 12), var1.getIntBetween(8, 12), 7, 5, 1) {
         public Point getStartCell() {
            return new Point((this.cellsWidth - this.startPreset.sectionWidth) / 2, (this.cellsHeight - this.startPreset.sectionHeight) / 2);
         }
      };
      int var3 = var0.width / 2 - var2.cellRes * var2.cellsWidth / 2;
      int var4 = var0.height / 2 - var2.cellRes * var2.cellsHeight / 2;
      SpiderCastleThroneRoomPreset var5 = new SpiderCastleThroneRoomPreset(var2.random);
      var2.setStartPreset(var5);
      Point var6 = var2.initGeneration(var3, var4);
      int var7 = ModularGeneration.getCellRealPos(var6.x, var2.cellRes) + var2.cellRes * var5.sectionWidth / 2;
      int var8 = ModularGeneration.getCellRealPos(var6.y, var2.cellRes) + var2.cellRes * var5.sectionHeight / 2;
      var2.addPreset(new SpiderCastleHallwayCornerPreset(var2.random), 75);
      var2.addPreset(new SpiderCastleBigSpiderRoomPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleCrossRoomPreset(var2.random), 25);
      var2.addPreset(new SpiderCastleSmallCrossRoomPreset(var2.random), 35);
      var2.addPreset(new SpiderCastleLibraryPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleDoubleDoorHallwayPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleHallwayUTurnPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleLRoomPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleSecretTunnelRoomPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleSecretTunnelHallwayPreset(var2.random), 75);
      var2.addPreset(new SpiderCastleLongHallwayPreset(var2.random), 50);
      var2.addPreset(new SpiderCastleHallwayStraight1Preset(var2.random), 100);
      var2.addPreset(new SpiderCastleHallwayStraight2Preset(var2.random), 100);
      var2.addPreset(new SpiderCastleHallwayStraight3Preset(var2.random), 100);
      var2.addPreset(new SpiderCastleHallwayStraight4Preset(var2.random), 100);
      var2.addPreset(new SpiderCastleTRoom1Preset(var2.random), 75);
      var2.addPreset(new SpiderCastleTRoom2Preset(var2.random), 75);
      var2.addPreset(new SpiderCastleTRoom3Preset(var2.random), 75);
      var2.addPreset(new SpiderCastleDeadEnd1Preset(var2.random), 25);
      var2.addPreset(new SpiderCastleDeadEnd2Preset(var2.random), 25);
      var2.addPreset(new SpiderCastleDeadEnd3Preset(var2.random), 25);
      var2.addPreset(new SpiderCastleDeadEnd4Preset(var2.random), 25);
      var2.tickGeneration(var3, var4, Integer.MAX_VALUE);
      Iterator var9 = var2.getPlacedPresets().iterator();

      while(var9.hasNext()) {
         PlacedPreset var10 = (PlacedPreset)var9.next();
         if (var10.preset instanceof AbandonedMineHallwayPreset) {
            fixHallway(var0, var2, var3, var4, var10);
         }
      }

      var2.endGeneration();
      IncursionBiome.addReturnPortal(var0, (float)((var7 + var3) * 32) - 0.5F, (float)((var8 + var4) * 32) + 0.5F);
   }

   private static void fixHallway(Level var0, ModularGeneration var1, int var2, int var3, PlacedPreset var4) {
      for(int var5 = 0; var5 < 4; ++var5) {
         Point var6 = var1.getNextCell(var4.cell, var5);
         PlacedPreset var7 = var1.getPlacedPreset(var6);
         if (var7 == null) {
            var4.preset.closeLevel(var0, 0, 0, var1.getCellRealX(var4.cell.x) + var2, var1.getCellRealY(var4.cell.y) + var3, var5, var1.cellRes);
         }
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   protected ModularPreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return this.newModularObject(var1, var2, var3, var4, var5);
   }
}
