package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins6 extends CaveRuins {
   public CaveRuins6(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 4,\n\theight = 6,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, 11, 11, -1, 11, 11, 11, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, 11, 11],\n\tobjectIDs = [0, air, 98, oakchair, 21, ironanvil, 26, woodwall, 108, oakdisplay, 93, oakchest],\n\tobjects = [-1, 26, 26, -1, 26, 26, 98, -1, 26, 93, 0, 0, 26, 21, 0, 0, 26, 26, 26, 108, -1, -1, 26, 26],\n\trotations = [0, 3, 3, 0, 3, 3, 2, 0, 2, 1, 0, 0, 2, 1, 0, 0, 2, 2, 2, 0, 0, 0, 2, 1]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 1, 2, new Object[]{var6});
   }
}
