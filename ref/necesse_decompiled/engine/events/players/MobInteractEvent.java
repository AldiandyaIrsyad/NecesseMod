package necesse.engine.events.players;

import necesse.engine.events.PreventableGameEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;

public class MobInteractEvent extends PreventableGameEvent {
   public final Mob mob;
   public final PlayerMob player;

   public MobInteractEvent(Mob var1, PlayerMob var2) {
      this.mob = var1;
      this.player = var2;
   }
}
