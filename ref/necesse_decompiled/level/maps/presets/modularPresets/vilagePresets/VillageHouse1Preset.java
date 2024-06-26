package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;

public class VillageHouse1Preset extends VillagePreset {
   public VillageHouse1Preset(GameRandom var1) {
      super(4, 3, false, var1);
      this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 9,\n\ttileIDs = [16, stonepathtile, 10, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 16, 16, 16, -1, -1, -1],\n\tobjectIDs = [0, air, 145, sprucebathtub, 146, sprucebathtub2, 130, sprucedinnertable, 147, sprucetoilet, 131, sprucedinnertable2, 134, sprucechair, 137, sprucebookshelf, 43, stonewall, 316, sunflower, 44, stonedoor, 143, sprucecandelabra],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 0, 0, 43, 143, 0, 134, 134, 43, 147, 145, 146, 43, 0, 0, 43, 316, 0, 130, 131, 43, 0, 0, 0, 43, 0, 0, 43, 0, 0, 134, 134, 43, 43, 44, 43, 43, 0, 0, 43, 0, 0, 0, 0, 0, 0, 0, 143, 43, 0, 0, 43, 137, 137, 0, 0, 0, 0, 0, 0, 43, 0, 0, 43, 43, 43, 43, 43, 43, 44, 43, 43, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.applyRandomFlower(2, 3, var1);
      this.open(2, 2, 2);
      this.addHumanMob(5, 5, new String[]{"farmerhuman"});
      this.addHumanMob(7, 6, new String[]{"guardhuman"});
   }
}
