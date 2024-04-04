package necesse.level.maps.presets.set;

import necesse.engine.registries.TileRegistry;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageSet implements PresetSet<VillageSet> {
   public static VillageSet defaultSet;
   public static VillageSet oak;
   public static VillageSet spruce;
   public static VillageSet pine;
   public static VillageSet palm;
   public FurnitureSet furniture = new FurnitureSet();
   public WallSet woodWalls = new WallSet();
   public WallSet stoneWalls = new WallSet();
   public int terrainTile = -1;
   public int shoreTile = -1;
   public int pathTile = -1;
   public int rockTile = -1;
   public int woodTile = -1;

   public VillageSet() {
   }

   public static VillageSet load(FurnitureSet var0, WallSet var1, WallSet var2, String var3, String var4, String var5, String var6, String var7) {
      VillageSet var8 = new VillageSet();
      var8.furniture = var0;
      var8.woodWalls = var1;
      var8.stoneWalls = var2;
      var8.terrainTile = TileRegistry.getTileID(var3);
      var8.shoreTile = TileRegistry.getTileID(var4);
      var8.pathTile = TileRegistry.getTileID(var5);
      var8.rockTile = TileRegistry.getTileID(var6);
      var8.woodTile = TileRegistry.getTileID(var7);
      return var8;
   }

   public <C extends Preset> C replacePreset(VillageSet var1, C var2) {
      if (var1 == this) {
         return var2;
      } else {
         if (var2 instanceof VillagePreset) {
            ((VillagePreset)var2).shoreTileID = this.shoreTile;
         }

         var2.replaceTile(var1.terrainTile, this.terrainTile);
         var2.replaceTile(var1.pathTile, this.pathTile);
         var2.replaceTile(var1.rockTile, this.rockTile);
         var2.replaceTile(var1.woodTile, this.woodTile);
         this.furniture.replacePreset(var1.furniture, var2);
         this.woodWalls.replacePreset(var1.woodWalls, var2);
         this.stoneWalls.replacePreset(var1.stoneWalls, var2);
         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset replacePreset(Object var1, Preset var2) {
      return this.replacePreset((VillageSet)var1, var2);
   }

   static {
      defaultSet = load(FurnitureSet.spruce, WallSet.wood, WallSet.stone, "grasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor");
      oak = load(FurnitureSet.oak, WallSet.wood, WallSet.stone, "grasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor");
      spruce = load(FurnitureSet.spruce, WallSet.wood, WallSet.stone, "grasstile", "sandtile", "stonepathtile", "stonefloor", "woodfloor");
      pine = load(FurnitureSet.pine, WallSet.pine, WallSet.snowStone, "snowtile", "icetile", "snowstonepathtile", "snowstonefloor", "pinefloor");
      palm = load(FurnitureSet.palm, WallSet.palm, WallSet.sandstone, "sandtile", "sandtile", "sandstonepathtile", "sandstonefloor", "palmfloor");
   }
}
