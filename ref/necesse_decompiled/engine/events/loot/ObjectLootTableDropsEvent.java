package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.inventory.InventoryItem;
import necesse.level.maps.LevelObject;

public class ObjectLootTableDropsEvent extends GameEvent {
   public final LevelObject object;
   public Point dropPos;
   public ArrayList<InventoryItem> drops;

   public ObjectLootTableDropsEvent(LevelObject var1, Point var2, ArrayList<InventoryItem> var3) {
      this.object = var1;
      this.dropPos = var2;
      this.drops = var3;
   }
}
