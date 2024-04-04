package necesse.entity.mobs;

import necesse.level.maps.CollisionFilter;

public class FlyingTargetMob extends Mob {
   public FlyingTargetMob(int var1) {
      super(var1);
   }

   public int getFlyingHeight() {
      return 50;
   }

   public boolean canHitThroughCollision() {
      return true;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }
}
