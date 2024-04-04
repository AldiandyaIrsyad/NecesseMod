package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;

public class VillageHouse6Preset extends VillagePreset {
   public VillageHouse6Preset(GameRandom var1) {
      super(4, 3, false, var1);
      this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 9,\n\ttileIDs = [16, stonepathtile, 10, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, 16, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, 16, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, 16, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 129, sprucechest, 34, woodwall, 130, sprucedinnertable, 131, sprucedinnertable2, 35, wooddoor, 134, sprucechair, 137, sprucebookshelf, 138, sprucecabinet, 331, mushroomflower, 139, sprucebed, 140, sprucebed2, 141, sprucedresser, 142, spruceclock, 143, sprucecandelabra, 209, leathercarpet],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 0, 0, 34, 143, 331, 137, 134, 129, 134, 130, 134, 34, 0, 0, 34, 0, 0, 209, 209, 0, 134, 131, 134, 34, 0, 0, 35, 0, 0, 209, 209, 0, 0, 0, 0, 34, 0, 0, 34, 0, 0, 0, 0, 0, 0, 0, 141, 34, 0, 0, 34, 0, 142, 138, 134, 0, 143, 140, 139, 34, 0, 0, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 0, 3, 0, 1, 3, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.addInventory(LootTablePresets.hunterChest, var1, 6, 2, new Object[0]);
      this.applyRandomFlower(3, 2, var1);
      this.open(0, 1, 3);
      this.addHumanMob(4, 4, new String[]{"human"});
      this.addHumanMob(7, 4, new String[]{"hunterhuman", "animalkeeperhuman"});
   }
}
