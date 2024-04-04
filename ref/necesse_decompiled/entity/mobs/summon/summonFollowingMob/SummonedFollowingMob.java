package necesse.entity.mobs.summon.summonFollowingMob;

import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.SummonedMob;

public class SummonedFollowingMob extends SummonedMob {
   public SummonedFollowingMob(int var1) {
      super(var1);
      this.shouldSave = false;
      this.canDespawn = false;
      this.isStatic = true;
   }

   public void onUnloading() {
      super.onUnloading();
      if (this.isServer()) {
         PlayerMob var1 = this.getFollowingServerPlayer();
         if (var1 == null) {
            this.followingSlot = -1;
         } else if (!var1.isSamePlace(this)) {
            this.onFollowingAnotherLevel(var1);
         }
      }

   }

   public boolean canPushMob(Mob var1) {
      return var1.isFollowing() && !var1.isMounted();
   }

   public void serverTick() {
      super.serverTick();
      if (!this.isFollowing()) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public Mob getFirstAttackOwner() {
      if (this.isFollowing()) {
         if (this.isServer()) {
            return this.getFollowingServerPlayer();
         }

         if (this.isClient()) {
            return this.getFollowingClientPlayer();
         }
      }

      return super.getFirstAttackOwner();
   }
}
