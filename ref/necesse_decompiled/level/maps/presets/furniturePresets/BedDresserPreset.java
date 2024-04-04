package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class BedDresserPreset extends Preset {
   public BedDresserPreset(FurnitureSet var1, int var2, Preset.ApplyPredicateFunction var3) {
      super(3, 1 + var2);
      this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 1,\n\tobjectIDs = [98, oakbed, 99, oakbed2, 100, oakdresser],\n\tobjects = [99, 98, 100],\n\trotations = [3, 3, 2]\n}");
      var1.replacePreset((FurnitureSet)FurnitureSet.oak, this);
      if (var3 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, var3);
      }

   }

   public BedDresserPreset(FurnitureSet var1, int var2) {
      this(var1, var2, (var0, var1x, var2x, var3) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
