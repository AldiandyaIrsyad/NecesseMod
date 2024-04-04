package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins2 extends CaveRuins {
   public CaveRuins2(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 6,\n\theight = 5,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, 11, 11, 11, -1, -1, -1],\n\tobjectIDs = [0, air, 99, oakbench, 100, oakbench2, 23, alchemytable, 26, woodwall, 106, oakclock, 93, oakchest],\n\tobjects = [-1, 26, 26, 26, 26, 26, 26, 26, 23, 93, 106, 26, 26, 100, 0, 0, 0, 26, 26, 99, 0, 0, 0, -1, 26, 26, 0, -1, -1, -1],\n\trotations = [0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 0, 1, 0, 0, 1, 3, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 3, 1, new Object[]{var6});
   }
}
