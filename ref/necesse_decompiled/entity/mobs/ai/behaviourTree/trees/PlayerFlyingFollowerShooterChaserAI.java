package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public abstract class PlayerFlyingFollowerShooterChaserAI<T extends Mob> extends SelectorAINode<T> {
   public PlayerFlyingFollowerShooterChaserAI(int var1, CooldownAttackTargetAINode.CooldownTimer var2, int var3, int var4, int var5, int var6) {
      SequenceAINode var7 = new SequenceAINode();
      var7.addChild(new FollowerBaseSetterAINode());
      var7.addChild(new FollowerFocusTargetSetterAINode());
      final SummonTargetFinderAINode var8 = new SummonTargetFinderAINode(var1);
      var7.addChild(var8);
      var7.addChild(new FlyingFollowerAINode<T>(-1, -1) {
         public Mob getFollowingMob(T var1) {
            return (Mob)this.getBlackboard().getObject(Mob.class, var8.currentTargetKey);
         }
      });
      var7.addChild(new CooldownAttackTargetAINode<T>(var2, var3, var4) {
         public boolean attackTarget(T var1, Mob var2) {
            return PlayerFlyingFollowerShooterChaserAI.this.shootAtTarget(var1, var2);
         }
      });
      this.addChild(var7);
      this.addChild(new PlayerFlyingFollowerAINode(var5, var6));
   }

   protected abstract boolean shootAtTarget(T var1, Mob var2);
}
