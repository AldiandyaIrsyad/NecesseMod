package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;

public class VillageHouse4Preset extends VillagePreset {
   public VillageHouse4Preset(GameRandom var1) {
      super(3, 3, false, var1);
      this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileIDs = [18, stonepathtile, 11, rockfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, -1, -1, -1, 11, 11, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, 18, 18, 18, -1, -1, -1],\n\tobjectIDs = [0, air, 290, ladderdown, 28, stonewall, 61, torch, 29, stonedoor],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28, 28, 28, 28, 28, 0, 0, 0, 28, 28, 0, 0, 0, 28, 28, 0, 0, 28, 61, 0, 290, 0, 61, 28, 0, 0, 28, 0, 0, 0, 0, 0, 28, 0, 0, 28, 0, 0, 0, 0, 0, 28, 0, 0, 28, 28, 0, 0, 0, 28, 28, 0, 0, 0, 28, 28, 29, 28, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.open(1, 2, 2);
      this.addHumanMob(4, 5, new String[]{"guardhuman"});
   }
}
