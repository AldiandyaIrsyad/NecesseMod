package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.engine.network.server.ServerClient;
import necesse.entity.TileDamageType;
import necesse.level.maps.Level;

public class DamageTileEvent extends PreventableGameEvent {
   public final Level level;
   public final int tileX;
   public final int tileY;
   public final int damage;
   public final TileDamageType type;
   public final int toolTier;
   public final ServerClient client;

   public DamageTileEvent(Level var1, int var2, int var3, int var4, TileDamageType var5, int var6, ServerClient var7) {
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
      this.damage = var4;
      this.type = var5;
      this.toolTier = var6;
      this.client = var7;
   }
}
