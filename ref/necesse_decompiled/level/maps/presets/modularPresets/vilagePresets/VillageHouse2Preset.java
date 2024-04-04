package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;

public class VillageHouse2Preset extends VillagePreset {
   public VillageHouse2Preset(GameRandom var1) {
      super(5, 5, false, var1);
      this.applyScript("PRESET = {\n\twidth = 15,\n\theight = 15,\n\ttileIDs = [16, stonepathtile, 9, farmland, 10, woodfloor, 13, stonefloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, -1, -1, 16, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, -1, -1, 16, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, -1, -1, 16, 10, 10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 13, 13, 13, 13, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 13, 13, 13, 13, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 13, 13, 13, 13, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 2, oaksapling, 133, sprucemodulartable, 134, sprucechair, 135, sprucebench, 136, sprucebench2, 73, woodfence, 137, sprucebookshelf, 330, iceblossom, 43, stonewall, 139, sprucebed, 44, stonedoor, 140, sprucebed2, 141, sprucedresser, 143, sprucecandelabra, 87, storagebox, 25, workstation, 26, forge, 90, torch, 254, tomatoseed, 31, alchemytable],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 43, 43, 73, 73, 73, 73, 73, 0, 0, 43, 143, 26, 25, 87, 31, 0, 43, 254, 254, 254, 254, 73, 0, 0, 43, 0, 0, 0, 0, 0, 0, 43, 254, 254, 254, 254, 73, 0, 0, 44, 0, 0, 0, 0, 0, 330, 43, 254, 254, 254, 254, 73, 0, 0, 43, 0, 0, 0, 0, 0, 133, 43, 254, 254, 254, 254, 73, 0, 0, 43, 143, 0, 0, 0, 0, 133, 43, 0, 0, 0, 0, 73, 0, 0, 43, 43, 43, 0, 0, 0, 0, 44, 0, 0, 0, 0, 73, 0, 0, 0, 0, 43, 0, 0, 0, 0, 43, 0, 136, 135, 90, 73, 0, 0, 2, 0, 43, 137, 0, 0, 0, 43, 43, 43, 43, 43, 43, 0, 0, 0, 0, 43, 137, 0, 0, 0, 0, 133, 133, 133, 133, 43, 0, 0, 2, 0, 43, 140, 0, 0, 0, 0, 134, 134, 134, 134, 43, 0, 0, 0, 0, 43, 139, 141, 143, 0, 0, 0, 0, 0, 0, 43, 0, 0, 2, 0, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      this.addInventory(LootTablePresets.alchemistChest, var1, 5, 2, new Object[0]);
      int var2 = ObjectRegistry.getObjectID(var1.nextBoolean() ? "oaksapling" : "sprucesapling");
      this.setObject(1, 9, var2);
      this.setObject(1, 11, var2);
      this.setObject(1, 13, var2);
      this.applyRandomSeedArea(9, 2, 4, 4, var1);
      this.applyRandomFlower(7, 4, var1);
      this.open(0, 1, 3);
      this.addHumanMob(5, 4, new String[]{"alchemisthuman"});
      this.addHumanMob(6, 10, new String[]{"human"});
      this.addHumanMob(3, 4, new String[]{"guardhuman"});
   }
}
