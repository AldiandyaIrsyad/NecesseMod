package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;

public class VillageFarm1Preset extends VillagePreset {
   public VillageFarm1Preset(GameRandom var1) {
      super(3, 4, false, var1);
      this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 12,\n\ttileIDs = [18, stonepathtile, 9, farmland],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, 9, 18, 9, 18, 9, -1, -1, -1, -1, -1, 18, -1, 18, -1, -1, -1, -1, -1, -1, 18, 18, 18, -1, -1, -1],\n\tobjectIDs = [0, air, 67, sign, 228, wheatseed, 55, woodfence, 200, sunflowerseed, 207, firemoneseed],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 55, 55, 55, 55, 55, 55, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 207, 0, 200, 0, 228, 55, 0, 0, 55, 55, 0, 67, 0, 55, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0]\n}");
      this.applyRandomSeedArea(2, 2, 1, 8, var1);
      this.applyRandomSeedArea(4, 2, 1, 8, var1);
      this.applyRandomSeedArea(6, 2, 1, 8, var1);
      int var2 = ObjectRegistry.getObjectID("sign");
      this.addSign("Community farm", 4, 10, var2);
      this.open(1, 3, 2);
   }
}
