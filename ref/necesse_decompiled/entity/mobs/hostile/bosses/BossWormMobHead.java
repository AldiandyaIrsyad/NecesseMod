package necesse.entity.mobs.hostile.bosses;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.hostile.HostileWormMobHead;

public abstract class BossWormMobHead<T extends BossWormMobBody<B, T>, B extends BossWormMobHead<T, B>> extends HostileWormMobHead<T, B> {
   public BossWormMobHead(int var1, float var2, float var3, int var4, float var5, float var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.canDespawn = false;
      this.shouldSave = false;
      this.removeWhenTilesOutOfLevel = 200;
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      return false;
   }

   public int getRespawnTime() {
      return BossMob.getBossRespawnTime(this);
   }
}
