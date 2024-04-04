package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class DinnerTablePreset extends Preset {
   public DinnerTablePreset(FurnitureSet var1, int var2, Preset.ApplyPredicateFunction var3) {
      super(3, 2 + var2);
      this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 2,\n\tobjectIDs = [89, oakdinnertable, 90, oakdinnertable2, 93, oakchair],\n\tobjects = [93, 89, 93, 93, 90, 93],\n\trotations = [1, 2, 3, 1, 2, 3]\n}");
      var1.replacePreset((FurnitureSet)FurnitureSet.oak, this);
      if (var3 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, var3);
      }

   }

   public DinnerTablePreset(FurnitureSet var1, int var2) {
      this(var1, var2, (var0, var1x, var2x, var3) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
