package necesse.entity.mobs.hostile.bosses;

import necesse.entity.mobs.hostile.HostileWormMobBody;

public class BossWormMobBody<T extends BossWormMobHead<B, T>, B extends BossWormMobBody<T, B>> extends HostileWormMobBody<T, B> {
   public BossWormMobBody(int var1) {
      super(var1);
      this.canDespawn = false;
      this.shouldSave = false;
   }

   public int getRespawnTime() {
      return BossMob.getBossRespawnTime(this);
   }
}
