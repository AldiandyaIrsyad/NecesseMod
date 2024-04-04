package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class BenchPreset extends Preset {
   public BenchPreset(FurnitureSet var1, int var2, Preset.ApplyPredicateFunction var3) {
      super(2, 1 + var2);
      this.applyScript("PRESET = {\n\twidth = 2,\n\theight = 1,\n\tobjectIDs = [99, oakbench, 100, oakbench2],\n\tobjects = [99, 100],\n\trotations = [1, 1]\n}");
      var1.replacePreset((FurnitureSet)FurnitureSet.oak, this);
      if (var3 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, 1, -1, 2, var3);
      }

   }

   public BenchPreset(FurnitureSet var1, int var2) {
      this(var1, var2, (var0, var1x, var2x, var3) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
