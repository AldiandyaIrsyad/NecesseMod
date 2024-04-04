package necesse.entity.mobs.summon;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.CollisionFilter;

public class SummonedMob extends AttackAnimMob {
   public SummonedMob(int var1) {
      super(var1);
      this.isHostile = false;
   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.SUMMONED_MOB_OPTIONS : null;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return !this.isMounted() ? super.getLevelCollisionFilter().addFilter((var0) -> {
         return !var0.object().object.isDoor;
      }).summonedMobCollision() : super.getLevelCollisionFilter();
   }

   protected GameMessage getSummonLocalization() {
      return super.getLocalization();
   }

   public GameMessage getLocalization() {
      if (this.getLevel() == null) {
         return super.getLocalization();
      } else {
         PlayerMob var1 = null;
         if (this.isServer()) {
            var1 = this.getFollowingServerPlayer();
         } else if (this.isClient()) {
            var1 = this.getFollowingClientPlayer();
         }

         return (GameMessage)(var1 != null ? new LocalMessage("mob", "spawnedname", new Object[]{"player", var1.getDisplayName(), "mob", this.getSummonLocalization()}) : super.getLocalization());
      }
   }
}
