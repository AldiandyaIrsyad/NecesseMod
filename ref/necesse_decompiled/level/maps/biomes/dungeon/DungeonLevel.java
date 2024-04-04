package necesse.level.maps.biomes.dungeon;

import java.awt.Point;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridor1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridor2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridorDouble1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridorDouble2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonEnd1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonEnd2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonIntersectionDouble1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonIntersectionDouble2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonIntersectionPreset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom3Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom4Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonStartPreset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonTSection1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonTSection2Preset;

public class DungeonLevel extends Level {
   public static final int DUNGEON_SIZE = 200;
   public static final int ROOM_SIZE = 15;

   public DungeonLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public DungeonLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 200, 200, var4);
      this.isCave = true;
      this.generateLevel();
   }

   public GameMessage getSetSpawnError(int var1, int var2, ServerClient var3) {
      return new LocalMessage("misc", "spawndungeon");
   }

   public void generateLevel() {
      ModularGeneration var1 = new ModularGeneration(this, 15, 3, 2);
      AtomicInteger var2 = new AtomicInteger();
      var1.setStartPreset(new DungeonStartPreset(var1.random));
      var1.addPreset(new DungeonCorridor1Preset(var1.random), 1);
      var1.addPreset(new DungeonCorridor2Preset(var1.random), 1);
      var1.addPreset(new DungeonCorridorDouble1Preset(var1.random), 1);
      var1.addPreset(new DungeonCorridorDouble2Preset(var1.random), 1);
      var1.addPreset(new DungeonEnd1Preset(var1.random, var2), 1);
      var1.addPreset(new DungeonEnd2Preset(var1.random, var2), 1);
      var1.addPreset(new DungeonIntersectionPreset(var1.random), 1);
      var1.addPreset(new DungeonRoom1Preset(var1.random, var2), 2);
      var1.addPreset(new DungeonRoom2Preset(var1.random), 1);
      var1.addPreset(new DungeonRoom3Preset(var1.random), 1);
      var1.addPreset(new DungeonRoom4Preset(var1.random, var2), 2);
      var1.addPreset(new DungeonTSection1Preset(var1.random), 1);
      var1.addPreset(new DungeonTSection2Preset(var1.random), 1);
      var1.addPreset(new DungeonIntersectionDouble1Preset(var1.random), 1);
      var1.addPreset(new DungeonIntersectionDouble2Preset(var1.random), 1);
      int var3 = ObjectRegistry.getObjectID("dungeonwall");
      int var4 = TileRegistry.getTileID("dungeonfloor");

      for(int var5 = 0; var5 < this.width; ++var5) {
         for(int var6 = 0; var6 < this.height; ++var6) {
            this.setTile(var5, var6, var4);
            this.setObject(var5, var6, var3);
         }
      }

      this.liquidManager.calculateShores();
      var1.generateModularGeneration();
      var1.fixHeuristic();
      PlacedPreset var9 = null;
      byte var10 = 6;
      Iterator var7 = var1.getPlacedPresets().iterator();

      while(var7.hasNext()) {
         PlacedPreset var8 = (PlacedPreset)var7.next();
         if (var8.heuristic != 0 && var8.preset.sectionWidth == 1 && var8.preset.sectionHeight == 1 && (var9 == null || var8.heuristic > var9.heuristic && var8.heuristic <= var10 || var8.heuristic < var9.heuristic && var8.heuristic >= var10)) {
            var9 = var8;
            if (var8.heuristic == var10) {
               break;
            }
         }
      }

      if (var9 != null) {
         var1.replacePreset(var9.cell, new DungeonStartPreset(var1.random), false, false, 0, 0);
         int var11 = var1.getCellRealX(var9.cell.x) + 7;
         int var12 = var1.getCellRealY(var9.cell.y) + 7;
         this.setObject(var11, var12, ObjectRegistry.getObjectID("dungeonentrance"));
      } else {
         System.out.println("Could not find position for dungeon boss preset");
      }

   }

   public static Point getSpawnLadderPos(Level var0) {
      Point var1 = ModularGeneration.getStartCell(var0, ModularGeneration.getCellsSize(200, 15) - 1, ModularGeneration.getCellsSize(200, 15) - 1);
      int var2 = ModularGeneration.getCellRealPos(var1.x, 15) + 7;
      int var3 = ModularGeneration.getCellRealPos(var1.y, 15) + 7;
      return new Point(var2, var3);
   }
}
