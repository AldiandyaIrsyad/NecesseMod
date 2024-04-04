package necesse.engine.network.server;

import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class FollowerPosition {
   public final int x;
   public final int y;
   public final Function<Mob, MobMovement> movementGetter;

   public FollowerPosition(int var1, int var2, Function<Mob, MobMovement> var3) {
      this.x = var1;
      this.y = var2;
      this.movementGetter = var3;
   }

   public FollowerPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
      this.movementGetter = (var2x) -> {
         return new MobMovementRelative(var2x, (float)var1, (float)var2, true, false);
      };
   }
}
