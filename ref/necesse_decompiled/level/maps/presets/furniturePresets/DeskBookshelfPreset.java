package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class DeskBookshelfPreset extends Preset {
   public DeskBookshelfPreset(FurnitureSet var1, int var2, Preset.ApplyPredicateFunction var3) {
      super(2, 2 + var2);
      this.applyScript("PRESET = {\n\twidth = 2,\n\theight = 2,\n\tobjectIDs = [96, oakbookshelf, 0, air, 91, oakdesk, 93, oakchair],\n\tobjects = [91, 96, 93, 0],\n\trotations = [2, 2, 0, 0]\n}");
      var1.replacePreset((FurnitureSet)FurnitureSet.oak, this);
      if (var3 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, 1, -1, 2, var3);
      }

   }

   public DeskBookshelfPreset(FurnitureSet var1, int var2) {
      this(var1, var2, (var0, var1x, var2x, var3) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
