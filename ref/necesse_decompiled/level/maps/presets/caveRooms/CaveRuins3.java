package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins3 extends CaveRuins {
   public CaveRuins3(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 5,\n\theight = 6,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, 11, 11, 11, 11, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, 11, 11, 11, 11, -1],\n\tobjectIDs = [0, air, 98, oakchair, 102, oakcabinet, 26, woodwall, 93, oakchest, 94, oakdinnertable, 95, oakdinnertable2],\n\tobjects = [-1, 26, 26, 26, 26, -1, 26, 98, 95, 98, 26, 26, 98, 94, 98, 26, 93, 0, 0, 0, 26, 102, 0, 0, -1, 26, 26, 26, 26, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 1, 0, 3, 2, 1, 0, 0, 0, 2, 1, 1, 1, 0, 1, 1, 1, 1, 0]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 1, 3, new Object[]{var6});
   }
}
