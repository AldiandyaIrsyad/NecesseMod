package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins4 extends CaveRuins {
   public CaveRuins4(GameRandom var1, WallSet var2, FurnitureSet var3, String var4, LootTable var5, AtomicInteger var6) {
      super("PRESET = {\n\twidth = 4,\n\theight = 7,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, 11, 11, 11, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, 11, 11, 11, -1],\n\tobjectIDs = [0, air, 97, oakmodulartable, 98, oakchair, 26, woodwall, 106, oakclock, 93, oakchest],\n\tobjects = [-1, 26, 26, 26, -1, 26, 106, 93, 26, 26, 0, 0, 26, 97, 98, 0, 26, 97, 98, 0, 26, 97, 98, -1, 26, 26, 0, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 3, 3, 0, 2, 1, 0, 0]\n}", var1, var2, var3, var4);
      this.addInventory(var5, var1, 3, 1, new Object[]{var6});
   }
}
