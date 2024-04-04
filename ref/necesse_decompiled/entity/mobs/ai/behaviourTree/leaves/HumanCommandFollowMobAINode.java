package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanCommandFollowMobAINode<T extends HumanMob> extends FollowMobAINode<T> {
   public long nextApplyBuffTime;

   public HumanCommandFollowMobAINode() {
   }

   public AINodeResult tickFollowing(Mob var1, T var2, Blackboard<T> var3) {
      AINodeResult var4 = super.tickFollowing(var1, var2, var3);
      if (var1 != null) {
         double var5 = GameMath.diagonalMoveDistance(var2.getX(), var2.getY(), var1.getX(), var1.getY());
         if (var2.commandMoveToFollowPoint && var5 <= (double)((float)this.tileRadius * 1.5F * 32.0F)) {
            var2.commandMoveToFollowPoint = false;
            var3.submitEvent("resetTarget", new AIEvent());
         }

         if (var5 > (double)((float)this.tileRadius * 2.5F * 32.0F)) {
            if (this.nextApplyBuffTime <= var2.getTime()) {
               var2.buffManager.addBuff(new ActiveBuff(BuffRegistry.SETTLER_SPRINT, var2, 2.0F, (Attacker)null), true);
               this.nextApplyBuffTime = var2.getTime() + 1500L;
            }
         } else {
            this.nextApplyBuffTime = var2.getTime() + 500L;
         }
      } else {
         this.nextApplyBuffTime = 0L;
         var2.commandMoveToFollowPoint = false;
      }

      return var4;
   }

   public void onMovedToFollowTarget(Mob var1, T var2, Blackboard<T> var3, boolean var4) {
      super.onMovedToFollowTarget(var1, var2, var3, var4);
      if (var2.commandMoveToFollowPoint) {
         var2.commandMoveToFollowPoint = false;
         var3.submitEvent("resetTarget", new AIEvent());
      }

   }

   public Mob getFollowingMob(T var1) {
      if (var1.isHiding && var1.isSettlerOnCurrentLevel()) {
         return null;
      } else {
         if (var1.commandFollowMob != null && var1.commandFollowMob.removed()) {
            if (var1.isSettlerOnCurrentLevel()) {
               var1.commandFollowMob = null;
            } else {
               var1.commandGuard((ServerClient)null, var1.getX(), var1.getY());
            }
         }

         if (var1.commandFollowMob != null) {
            if (var1.objectUser != null) {
               var1.objectUser.stopUsing();
            }

            var1.setActivity("command", 15000, new LocalMessage("activities", "following", "target", var1.commandFollowMob.getLocalization()));
            return var1.commandFollowMob;
         } else {
            return null;
         }
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Mob getFollowingMob(Mob var1) {
      return this.getFollowingMob((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onMovedToFollowTarget(Mob var1, Mob var2, Blackboard var3, boolean var4) {
      this.onMovedToFollowTarget(var1, (HumanMob)var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickFollowing(Mob var1, Mob var2, Blackboard var3) {
      return this.tickFollowing(var1, (HumanMob)var2, var3);
   }
}
