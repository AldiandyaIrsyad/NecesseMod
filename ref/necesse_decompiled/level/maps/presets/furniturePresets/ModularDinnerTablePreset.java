package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class ModularDinnerTablePreset extends Preset {
   public ModularDinnerTablePreset(FurnitureSet var1, int var2, int var3, Preset.ApplyPredicateFunction var4) {
      super(3, var3 + var2);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.setObject(1, var5, var1.modularTable, 2);
         this.setObject(0, var5, var1.chair, 1);
         this.setObject(2, var5, var1.chair, 3);
      }

      if (var4 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, var4);
      }

   }

   public ModularDinnerTablePreset(FurnitureSet var1, int var2, int var3) {
      this(var1, var2, var3, (var0, var1x, var2x, var3x) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
