package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class ItemPlaceEvent extends PreventableGameEvent {
   public final Level level;
   public final int tileX;
   public final int tileY;
   public final InventoryItem item;
   public final PlayerMob player;

   public ItemPlaceEvent(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
      this.item = var5;
      this.player = var4;
   }
}
