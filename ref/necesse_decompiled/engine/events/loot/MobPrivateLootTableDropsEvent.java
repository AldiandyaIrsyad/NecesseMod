package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;

public class MobPrivateLootTableDropsEvent extends GameEvent {
   public final Mob mob;
   public final ServerClient client;
   public Point dropPos;
   public ArrayList<InventoryItem> drops;

   public MobPrivateLootTableDropsEvent(Mob var1, ServerClient var2, Point var3, ArrayList<InventoryItem> var4) {
      this.mob = var1;
      this.client = var2;
      this.dropPos = var3;
      this.drops = var4;
   }
}
