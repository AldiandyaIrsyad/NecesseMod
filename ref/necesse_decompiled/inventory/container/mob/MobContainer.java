package necesse.inventory.container.mob;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.Container;

public class MobContainer extends Container {
   private Mob mob;
   protected boolean ignoreInteractRange;

   public MobContainer(NetworkClient var1, int var2, Mob var3) {
      super(var1, var2);
      this.mob = var3;
   }

   public Mob getMob() {
      return this.mob;
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else if (this.mob.removed()) {
         return false;
      } else {
         return this.ignoreInteractRange || this.mob.inInteractRange(var1.playerMob);
      }
   }
}
