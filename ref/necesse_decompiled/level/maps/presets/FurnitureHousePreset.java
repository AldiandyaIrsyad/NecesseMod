package necesse.level.maps.presets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.presets.set.WallSet;

public class FurnitureHousePreset extends Preset {
   public FurnitureHousePreset(int var1, int var2, int var3, WallSet var4, GameRandom var5, TicketSystemList<Preset> var6, float var7) {
      super(var1, var2);
      this.fillTile(0, 0, var1, var2, var3);
      this.fillObject(0, 0, var1, var2, 0);
      this.boxObject(0, 0, var1, var2, var4.wall);
      int var8 = var1 / 2;
      this.setObject(var8, 0, var4.doorClosed, 0);
      if (var1 % 2 == 0) {
         this.setObject(var8 - 1, 0, var4.doorClosed, 0);
      }

      this.addCustomApplyAreaEach(1, 1, var1 - 2, var2 - 2, 2, (var3x, var4x, var5x, var6x) -> {
         if (var5.getChance(var7)) {
            GenerationTools.generateFurniture(var3x, var5, var4x, var5x, var6, (var0) -> {
               return var0.objectID() == 0;
            });
         }

         return null;
      });
   }
}
