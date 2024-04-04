package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;

public class VillageHouse5Preset extends VillagePreset {
   public VillageHouse5Preset(GameRandom var1) {
      super(4, 4, false, var1);
      this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [16, stonepathtile, 13, stonefloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 129, sprucechest, 34, woodwall, 130, sprucedinnertable, 35, wooddoor, 131, sprucedinnertable2, 132, sprucedesk, 133, sprucemodulartable, 102, sign, 134, sprucechair, 135, sprucebench, 136, sprucebench2, 137, sprucebookshelf, 138, sprucecabinet, 139, sprucebed, 140, sprucebed2, 141, sprucedresser, 142, spruceclock, 143, sprucecandelabra, 145, sprucebathtub, 146, sprucebathtub2, 147, sprucetoilet],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 0, 0, 34, 129, 102, 143, 145, 146, 0, 0, 143, 34, 0, 0, 34, 0, 0, 0, 0, 0, 0, 134, 134, 34, 0, 0, 35, 0, 0, 0, 0, 0, 0, 130, 131, 34, 0, 0, 34, 0, 0, 0, 137, 137, 0, 134, 134, 34, 0, 0, 34, 142, 0, 0, 138, 138, 0, 0, 0, 34, 0, 0, 34, 147, 0, 0, 0, 0, 0, 136, 133, 34, 0, 0, 34, 0, 0, 0, 0, 0, 0, 135, 133, 34, 0, 0, 34, 139, 140, 143, 0, 141, 143, 134, 132, 34, 0, 0, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 2, 0, 2, 2, 0, 0, 0, 0, 1, 1, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.addInventory(LootTablePresets.carpenterChest, var1, 2, 2, new Object[0]);
      this.open(0, 1, 3);
      this.addHumanMob(4, 4, new String[]{"stylisthuman"});
      this.addHumanMob(4, 7, new String[]{"human"});
   }
}
