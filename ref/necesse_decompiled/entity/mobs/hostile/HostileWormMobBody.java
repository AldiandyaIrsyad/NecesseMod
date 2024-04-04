package necesse.entity.mobs.hostile;

import necesse.entity.mobs.WormMobBody;

public class HostileWormMobBody<T extends HostileWormMobHead<B, T>, B extends HostileWormMobBody<T, B>> extends WormMobBody<T, B> {
   public HostileWormMobBody(int var1) {
      super(var1);
      this.isHostile = true;
      this.setTeam(-2);
   }
}
