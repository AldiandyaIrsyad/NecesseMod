package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins7 extends CaveRuins {
   public CaveRuins7(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 7,\n\theight = 7,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, 11, 11, 11, -1],\n\tobjectIDs = [0, air, 99, oakbench, 100, oakbench2, 101, oakbookshelf, 21, ironanvil, 103, oakbed, 104, oakbed2, 105, oakdresser, 26, woodwall, 93, oakchest],\n\tobjects = [-1, -1, 26, 26, 26, 26, 26, 26, 26, 26, 101, 93, 21, 26, 26, 103, 105, 0, 0, 0, 26, 26, 104, 0, 0, 0, 0, 0, 26, 101, 0, 0, 0, 0, 0, 26, 26, 26, 26, 100, 99, -1, -1, -1, -1, 26, 26, 26, -1],\n\trotations = [0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 1, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 3, 1, 1, 1, 3, 3, 0, 0, 0, 0, 1, 1, 1, 0]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 4, 1, new Object[]{var6});
   }
}
