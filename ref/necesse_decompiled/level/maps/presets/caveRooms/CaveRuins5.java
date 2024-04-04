package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins5 extends CaveRuins {
   public CaveRuins5(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 5,\n\theight = 5,\n\ttileIDs = [11, rockfloor],\n\ttiles = [11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, 11, 11, 11, 11],\n\tobjectIDs = [0, air, 17, workstation, 18, forge, 99, oakbench, 100, oakbench2, 26, woodwall, 93, oakchest],\n\tobjects = [26, 26, 26, 26, 26, 26, 18, 93, 17, 26, 26, 0, 0, 0, 0, 26, 26, 100, 99, 0, -1, 26, 26, 26, 26],\n\trotations = [0, 0, 1, 1, 2, 0, 2, 2, 2, 2, 1, 2, 0, 2, 2, 1, 1, 3, 3, 2, 0, 1, 1, 1, 1]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 2, 1, new Object[]{var6});
   }
}
