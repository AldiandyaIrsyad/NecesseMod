package necesse.level.maps.presets.set;

import java.util.ArrayList;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.presets.Preset;

public class TrialRoomSet implements PresetSet<TrialRoomSet> {
   public static TrialRoomSet stone;
   public static TrialRoomSet sandStone;
   public static TrialRoomSet snowStone;
   public static TrialRoomSet swampStone;
   public static TrialRoomSet deepStone;
   public static TrialRoomSet deepSnowStone;
   public static TrialRoomSet deepSwampStone;
   public static TrialRoomSet deepSandstone;
   public int floor;
   public int pressureplate;
   public int path;
   public int door;
   public WallSet wallSet;
   public ArrayList<Integer> traps;

   public TrialRoomSet(int var1, WallSet var2, String... var3) {
      this.floor = var1;
      this.wallSet = var2;
   }

   public TrialRoomSet(String var1, String var2, String var3, String var4, WallSet var5, String... var6) {
      this.floor = TileRegistry.getTileID(var1);
      this.pressureplate = ObjectRegistry.getObjectID(var2);
      this.path = TileRegistry.getTileID(var3);
      this.door = ObjectRegistry.getObjectID(var4);
      this.wallSet = var5;
      this.traps = new ArrayList();
      String[] var7 = var6;
      int var8 = var6.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         String var10 = var7[var9];
         this.traps.add(ObjectRegistry.getObjectID(var10));
      }

   }

   public <C extends Preset> C replacePreset(TrialRoomSet var1, C var2) {
      if (var1 == this) {
         return var2;
      } else {
         var2.replaceTile(var1.floor, this.floor);
         var2.replaceObject(var1.pressureplate, this.pressureplate);
         var2.replaceTile(var1.path, this.path);
         var2.replaceObject(var1.door, this.door);
         this.wallSet.replacePreset(var1.wallSet, var2);

         for(int var3 = 0; var3 < this.traps.size() && var3 < var1.traps.size(); ++var3) {
            var2.replaceObject((Integer)var1.traps.get(var3), (Integer)this.traps.get(var3));
         }

         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset replacePreset(Object var1, Preset var2) {
      return this.replacePreset((TrialRoomSet)var1, var2);
   }

   static {
      stone = new TrialRoomSet("stonefloor", "stonepressureplate", "stonepathtile", "stonedoorlocked", WallSet.stone, new String[]{"stonearrowtrap", "stoneflametrap", "stonesawtrap"});
      sandStone = new TrialRoomSet("sandstonefloor", "sandstonepressureplate", "sandstonepathtile", "sandstonedoorlocked", WallSet.sandstone, new String[]{"sandstonearrowtrap", "sandstoneflametrap", "sandstonesawtrap"});
      snowStone = new TrialRoomSet("snowstonefloor", "snowstonepressureplate", "snowstonepathtile", "snowstonedoorlocked", WallSet.snowStone, new String[]{"snowstonearrowtrap", "stoneflametrap", "snowstonesawtrap"});
      swampStone = new TrialRoomSet("swampstonefloor", "swampstonepressureplate", "swampstonepathtile", "swampstonedoorlocked", WallSet.swampStone, new String[]{"swampstonearrowtrap", "swampstoneflametrap", "swampstonesawtrap"});
      deepStone = new TrialRoomSet("deepstonefloor", "deepstonepressureplate", "stonepathtile", "deepstonedoorlocked", WallSet.deepStone, new String[]{"deepstonearrowtrap", "deepstoneflametrap", "deepstonesawtrap"});
      deepSnowStone = new TrialRoomSet("deepsnowstonefloor", "deepsnowstonepressureplate", "snowstonepathtile", "deepsnowstonedoorlocked", WallSet.deepSnowStone, new String[]{"deepsnowstonearrowtrap", "deepsnowstoneflametrap", "deepsnowstonesawtrap"});
      deepSwampStone = new TrialRoomSet("deepswampstonefloor", "deepswampstonepressureplate", "swampstonepathtile", "deepswampstonedoorlocked", WallSet.deepSwampStone, new String[]{"deepswampstonearrowtrap", "deepswampstoneflametrap", "deepswampstonesawtrap"});
      deepSandstone = new TrialRoomSet("woodfloor", "woodpressureplate", "woodpathtile", "deepsandstonedoorlocked", WallSet.deepSandstone, new String[]{"deepsandstonearrowtrap", "deepsandstoneflametrap", "deepsandstonesawtrap"});
   }
}
