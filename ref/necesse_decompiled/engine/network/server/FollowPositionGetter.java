package necesse.engine.network.server;

import necesse.entity.mobs.Mob;

interface FollowPositionGetter {
   FollowerPosition getPosition(Mob var1, int var2, int var3);
}
