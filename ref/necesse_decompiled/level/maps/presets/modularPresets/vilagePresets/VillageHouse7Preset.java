package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;

public class VillageHouse7Preset extends VillagePreset {
   public VillageHouse7Preset(GameRandom var1) {
      super(5, 5, false, var1);
      this.applyScript("PRESET = {\n\twidth = 15,\n\theight = 15,\n\ttileIDs = [16, stonepathtile, 10, woodfloor, 13, stonefloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, 16, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, 13, 13, 13, 13, 13, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 129, sprucechest, 130, sprucedinnertable, 2, oaksapling, 131, sprucedinnertable2, 134, sprucechair, 137, sprucebookshelf, 138, sprucecabinet, 331, mushroomflower, 139, sprucebed, 140, sprucebed2, 141, sprucedresser, 142, spruceclock, 143, sprucecandelabra, 208, woolcarpet, 144, sprucedisplay, 209, leathercarpet, 25, workstation, 26, forge, 27, carpentersbench, 28, carpentersbench2, 29, ironanvil, 104, armorstand, 43, stonewall, 44, stonedoor],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 43, 0, 0, 43, 143, 27, 28, 29, 26, 25, 143, 0, 0, 0, 0, 43, 0, 0, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 134, 134, 43, 0, 0, 44, 0, 0, 0, 209, 209, 0, 0, 0, 0, 130, 131, 43, 0, 0, 43, 0, 0, 0, 209, 209, 0, 0, 0, 0, 134, 134, 43, 0, 0, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 0, 0, 43, 104, 129, 0, 0, 0, 138, 137, 331, 0, 0, 143, 43, 0, 0, 43, 43, 43, 43, 0, 43, 43, 43, 43, 43, 43, 43, 43, 0, 0, 43, 143, 134, 137, 0, 141, 139, 43, 0, 0, 0, 0, 0, 0, 0, 43, 0, 208, 208, 0, 0, 140, 43, 0, 2, 0, 2, 0, 0, 0, 43, 140, 208, 208, 0, 0, 0, 43, 0, 0, 0, 0, 0, 0, 0, 43, 139, 141, 0, 142, 144, 143, 43, 0, 2, 0, 2, 0, 0, 0, 43, 43, 43, 43, 43, 43, 43, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 2, 2, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
      int var2 = ObjectRegistry.getObjectID(var1.nextBoolean() ? "oaksapling" : "sprucesapling");
      this.setObject(10, 10, var2);
      this.setObject(12, 10, var2);
      this.setObject(10, 12, var2);
      this.setObject(12, 12, var2);
      this.applyRandomFlower(9, 7, var1);
      this.addInventory(LootTablePresets.blacksmithChest, var1, 3, 7, new Object[0]);
      this.open(0, 1, 3);
      this.addHumanMob(4, 4, new String[]{"blacksmithhuman"});
      this.addHumanMob(8, 4, new String[]{"gunsmithhuman"});
   }
}
