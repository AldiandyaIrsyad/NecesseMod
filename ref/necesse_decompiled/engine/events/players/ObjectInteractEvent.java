package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class ObjectInteractEvent extends PreventableGameEvent {
   public final Level level;
   public final int tileX;
   public final int tileY;
   public final PlayerMob player;

   public ObjectInteractEvent(Level var1, int var2, int var3, PlayerMob var4) {
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
      this.player = var4;
   }
}
