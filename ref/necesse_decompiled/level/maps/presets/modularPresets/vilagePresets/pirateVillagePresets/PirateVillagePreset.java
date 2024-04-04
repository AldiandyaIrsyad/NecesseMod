package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class PirateVillagePreset extends VillagePreset {
   public PirateVillagePreset(int var1, int var2, boolean var3, GameRandom var4) {
      super(var1, var2, var3, TileRegistry.gravelID, TileRegistry.grassID, var4);
   }

   public PirateVillagePreset(int var1, int var2, boolean var3) {
      super(var1, var2, var3, TileRegistry.gravelID, TileRegistry.grassID, (GameRandom)null);
   }

   protected void applyRandomCoinStack(int var1, int var2, GameRandom var3, float var4) {
      this.setObject(var1, var2, 0);
      this.addCustomApply(var1, var2, 0, (var2x, var3x, var4x, var5) -> {
         if (var3.getChance(var4)) {
            var2x.setObject(var3x, var4x, ObjectRegistry.getObjectID("coinstack"));
         }

         return null;
      });
   }

   protected void applyRandomCoinStack(int var1, int var2, GameRandom var3) {
      this.applyRandomCoinStack(var1, var2, var3, 0.6F);
   }
}
