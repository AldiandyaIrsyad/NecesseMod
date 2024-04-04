package necesse.level.maps.presets.set;

import necesse.engine.registries.ObjectRegistry;
import necesse.level.maps.presets.Preset;

public class WallSet implements PresetSet<WallSet> {
   public static WallSet wood = loadByStringID("wood");
   public static WallSet pine = loadByStringID("pine");
   public static WallSet palm = loadByStringID("palm");
   public static WallSet stone = loadByStringID("stone");
   public static WallSet sandstone = loadByStringID("sandstone");
   public static WallSet swampStone = loadByStringID("swampstone");
   public static WallSet snowStone = loadByStringID("snowstone");
   public static WallSet ice = loadByStringID("ice");
   public static WallSet brick = loadByStringID("brick");
   public static WallSet dungeon = loadByStringID("dungeon");
   public static WallSet deepStone = loadByStringID("deepstone");
   public static WallSet obsidian = loadByStringID("obsidian");
   public static WallSet deepSnowStone = loadByStringID("deepsnowstone");
   public static WallSet deepSwampStone = loadByStringID("deepswampstone");
   public static WallSet deepSandstone = loadByStringID("deepsandstone");
   public static WallSet crypt = loadByStringID("crypt");
   public int wall = -1;
   public int doorClosed = -1;
   public int doorOpen = -1;

   public WallSet() {
   }

   public static WallSet loadByStringID(String var0) {
      WallSet var1 = new WallSet();
      var1.wall = ObjectRegistry.getObjectID(var0 + "wall");
      var1.doorClosed = ObjectRegistry.getObjectID(var0 + "door");
      var1.doorOpen = ObjectRegistry.getObjectID(var0 + "dooropen");
      return var1;
   }

   public <C extends Preset> C replacePreset(WallSet var1, C var2) {
      if (var1 == this) {
         return var2;
      } else {
         var2.replaceObject(var1.wall, this.wall);
         var2.replaceObject(var1.doorClosed, this.doorClosed);
         var2.replaceObject(var1.doorOpen, this.doorOpen);
         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset replacePreset(Object var1, Preset var2) {
      return this.replacePreset((WallSet)var1, var2);
   }
}
