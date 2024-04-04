package necesse.level.maps.presets.furniturePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FurnitureSet;

public class SingleChestPreset extends Preset {
   public SingleChestPreset(FurnitureSet var1, int var2, GameRandom var3, LootTable var4, Preset.ApplyPredicateFunction var5, Object... var6) {
      super(3, 1 + var2);
      this.applyScript("PRESET = {\n\twidth = 3,\n\theight = 1,\n\tobjectIDs = [0, air, 88, oakchest],\n\tobjects = [0, 88, 0],\n\trotations = [2, 2, 2]\n}");
      var1.replacePreset((FurnitureSet)FurnitureSet.oak, this);
      if (var4 != null) {
         this.addInventory(var4, var3, 1, 0, var6);
      }

      if (var5 != null) {
         this.addCanApplyAreaEachPredicate(0, -1, 2, -1, 2, var5);
      }

   }

   public SingleChestPreset(FurnitureSet var1, int var2, GameRandom var3, LootTable var4, Object... var5) {
      this(var1, var2, var3, var4, (var0, var1x, var2x, var3x) -> {
         GameObject var4 = var0.getObject(var1x, var2x);
         return var4.isWall && !var4.isDoor;
      }, var5);
   }
}
