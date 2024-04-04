package necesse.level.maps.generationModules;

import java.awt.Point;
import necesse.level.maps.Level;
import necesse.level.maps.presets.modularPresets.ModularPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageModularGeneration extends ModularGeneration {
   public VillageModularGeneration(Level var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public boolean canApplyPreset(ModularPreset var1, Point var2, boolean var3, boolean var4, Point var5) {
      if (var5 == null) {
         return true;
      } else {
         if (var1 instanceof VillagePreset && ((VillagePreset)var1).isPath()) {
            for(int var6 = 0; var6 < 4; ++var6) {
               Point var7 = this.getNextCell(var2, var6);
               if (!var7.equals(var5)) {
                  PlacedPreset var8 = this.getPlacedPreset(var7);
                  ModularPreset var9 = var8 == null ? null : var8.preset;
                  if (var9 instanceof VillagePreset && ((VillagePreset)var1).isPath()) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   public Point getNextCellPlus(Point var1, int var2) {
      var2 %= 8;
      if (var2 == 0) {
         return new Point(var1.x - 1, var1.y - 1);
      } else if (var2 == 1) {
         return new Point(var1.x, var1.y - 1);
      } else if (var2 == 2) {
         return new Point(var1.x + 1, var1.y - 1);
      } else if (var2 == 3) {
         return new Point(var1.x - 1, var1.y);
      } else if (var2 == 4) {
         return new Point(var1.x + 1, var1.y);
      } else if (var2 == 5) {
         return new Point(var1.x - 1, var1.y + 1);
      } else if (var2 == 6) {
         return new Point(var1.x, var1.y + 1);
      } else {
         return var2 == 7 ? new Point(var1.x + 1, var1.y + 1) : null;
      }
   }
}
