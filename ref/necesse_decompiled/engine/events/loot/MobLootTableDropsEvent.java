package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;

public class MobLootTableDropsEvent extends GameEvent {
   public final Mob mob;
   public Point dropPos;
   public ArrayList<InventoryItem> drops;

   public MobLootTableDropsEvent(Mob var1, Point var2, ArrayList<InventoryItem> var3) {
      this.mob = var1;
      this.dropPos = var2;
      this.drops = var3;
   }
}
