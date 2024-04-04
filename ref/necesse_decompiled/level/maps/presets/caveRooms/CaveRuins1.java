package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins1 extends CaveRuins {
   public CaveRuins1(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 5,\n\theight = 5,\n\ttileIDs = [11, rockfloor],\n\ttiles = [11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, 11, 11, 11, 11, -1],\n\tobjectIDs = [0, air, 96, oakdesk, 98, oakchair, 103, oakbed, 23, alchemytable, 104, oakbed2, 26, woodwall, 93, oakchest],\n\tobjects = [26, 26, 26, 26, 26, 26, 103, 93, 23, 26, 26, 104, 0, 0, 0, 26, 96, 98, 0, -1, 26, 26, 26, 0, -1],\n\trotations = [1, 3, 3, 3, 2, 1, 2, 2, 2, 1, 3, 2, 0, 0, 1, 3, 1, 3, 0, 0, 3, 3, 0, 1, 0]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 2, 1, new Object[]{var6});
   }
}
