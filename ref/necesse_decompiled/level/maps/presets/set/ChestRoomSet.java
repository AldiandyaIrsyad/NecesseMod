package necesse.level.maps.presets.set;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.presets.Preset;

public class ChestRoomSet implements PresetSet<ChestRoomSet> {
   public static ChestRoomSet wood;
   public static ChestRoomSet stone;
   public static ChestRoomSet sandstone;
   public static ChestRoomSet snowStone;
   public static ChestRoomSet ice;
   public static ChestRoomSet swampStone;
   public static ChestRoomSet deepStone;
   public static ChestRoomSet obsidian;
   public static ChestRoomSet deepSnowStone;
   public static ChestRoomSet deepSwampStone;
   public static ChestRoomSet deepSandstone;
   public int floor;
   public int pressureplate;
   public WallSet wallSet;
   public int inventoryObject;
   public ArrayList<Integer> traps;

   public ChestRoomSet(int var1, int var2, WallSet var3, int var4, Integer... var5) {
      this.floor = var1;
      this.pressureplate = var2;
      this.wallSet = var3;
      this.inventoryObject = var4;
      this.traps = new ArrayList(Arrays.asList(var5));
   }

   public ChestRoomSet(String var1, String var2, WallSet var3, String var4, String... var5) {
      this.floor = TileRegistry.getTileID(var1);
      this.pressureplate = ObjectRegistry.getObjectID(var2);
      this.wallSet = var3;
      this.inventoryObject = ObjectRegistry.getObjectID(var4);
      this.traps = new ArrayList();
      String[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         this.traps.add(ObjectRegistry.getObjectID(var9));
      }

   }

   public <C extends Preset> C replacePreset(ChestRoomSet var1, C var2) {
      if (var1 == this) {
         return var2;
      } else {
         var2.replaceTile(var1.floor, this.floor);
         var2.replaceObject(var1.pressureplate, this.pressureplate);
         this.wallSet.replacePreset(var1.wallSet, var2);
         var2.replaceObject(var1.inventoryObject, this.inventoryObject);

         for(int var3 = 0; var3 < this.traps.size() && var3 < var1.traps.size(); ++var3) {
            var2.replaceObject((Integer)var1.traps.get(var3), (Integer)this.traps.get(var3));
         }

         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset replacePreset(Object var1, Preset var2) {
      return this.replacePreset((ChestRoomSet)var1, var2);
   }

   static {
      wood = new ChestRoomSet("stonefloor", "stonepressureplate", WallSet.wood, "storagebox", new String[]{"woodarrowtrap"});
      stone = new ChestRoomSet("stonefloor", "stonepressureplate", WallSet.stone, "storagebox", new String[]{"stoneflametrap", "stonearrowtrap"});
      sandstone = new ChestRoomSet("sandstonefloor", "sandstonepressureplate", WallSet.sandstone, "storagebox", new String[]{"sandstoneflametrap", "sandstonearrowtrap"});
      snowStone = new ChestRoomSet("snowstonefloor", "snowstonepressureplate", WallSet.snowStone, "storagebox", new String[]{"snowstonearrowtrap"});
      ice = new ChestRoomSet("snowstonefloor", "snowstonepressureplate", WallSet.ice, "storagebox", new String[]{"icearrowtrap"});
      swampStone = new ChestRoomSet("swampstonefloor", "swampstonepressureplate", WallSet.swampStone, "storagebox", new String[]{"swampstoneflametrap", "swampstonearrowtrap"});
      deepStone = new ChestRoomSet("deepstonefloor", "deepstonepressureplate", WallSet.deepStone, "storagebox", new String[]{"deepstoneflametrap", "deepstonearrowtrap"});
      obsidian = new ChestRoomSet("deepstonefloor", "deepstonepressureplate", WallSet.obsidian, "storagebox", new String[]{"obsidianflametrap", "obsidianarrowtrap"});
      deepSnowStone = new ChestRoomSet("deepsnowstonefloor", "deepsnowstonepressureplate", WallSet.deepSnowStone, "storagebox", new String[]{"deepsnowstoneflametrap", "deepsnowstonearrowtrap"});
      deepSwampStone = new ChestRoomSet("deepswampstonefloor", "deepswampstonepressureplate", WallSet.deepSwampStone, "storagebox", new String[]{"deepswampstoneflametrap", "deepswampstonearrowtrap"});
      deepSandstone = new ChestRoomSet("woodfloor", "woodpressureplate", WallSet.deepSandstone, "storagebox", new String[]{"deepsandstoneflametrap", "deepsandstonearrowtrap"});
   }
}
