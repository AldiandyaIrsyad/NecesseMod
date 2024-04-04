package necesse.level.maps.presets.furniturePresets;

import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class ModularTablesPreset extends Preset {
   public ModularTablesPreset(FurnitureSet var1, int var2, int var3, boolean var4, Preset.ApplyPredicateFunction var5) {
      super(var3, (var4 ? 2 : 1) + var2);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.setObject(var6, 0, var1.modularTable, 2);
         if (var4) {
            this.setObject(var6, 1, var1.chair, 0);
         }
      }

      if (var5 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, var3 - 1, -1, 2, var5);
      }

   }

   public ModularTablesPreset(FurnitureSet var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, (var0, var1x, var2x, var3x) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      });
   }
}
