package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class CabinetsPreset extends Preset {
   public CabinetsPreset(FurnitureSet var1, int var2, int var3, Preset.ApplyPredicateFunction var4) {
      super(var3, 1 + var2);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.setObject(var5, 0, var1.cabinet, 2);
      }

      if (var4 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, var3 - 1, -1, 2, var4);
      }

   }

   public CabinetsPreset(FurnitureSet var1, int var2, int var3) {
      this(var1, var2, var3, (var0, var1x, var2x, var3x) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
