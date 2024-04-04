package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.inventory.InventoryItem;
import necesse.level.maps.LevelTile;

public class TileLootTableDropsEvent extends GameEvent {
   public final LevelTile tile;
   public Point dropPos;
   public ArrayList<InventoryItem> drops;

   public TileLootTableDropsEvent(LevelTile var1, Point var2, ArrayList<InventoryItem> var3) {
      this.tile = var1;
      this.dropPos = var2;
      this.drops = var3;
   }
}
