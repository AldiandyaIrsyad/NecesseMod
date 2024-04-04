package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import necesse.level.maps.CollisionFilter;

public abstract class FlyingAttackingFollowingMob extends AttackingFollowingMob {
   public FlyingAttackingFollowingMob(int var1) {
      super(var1);
   }

   public int getFlyingHeight() {
      return 50;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }
}
