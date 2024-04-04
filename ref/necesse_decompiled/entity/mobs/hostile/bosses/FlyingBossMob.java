package necesse.entity.mobs.hostile.bosses;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.hostile.FlyingHostileMob;

public class FlyingBossMob extends FlyingHostileMob {
   public FlyingBossMob(int var1) {
      super(var1);
      this.canDespawn = false;
      this.shouldSave = false;
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      return false;
   }

   public int getRespawnTime() {
      return BossMob.getBossRespawnTime(this);
   }
}
