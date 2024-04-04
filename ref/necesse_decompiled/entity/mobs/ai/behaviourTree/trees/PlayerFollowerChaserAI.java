package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public abstract class PlayerFollowerChaserAI<T extends Mob> extends SelectorAINode<T> {
   public PlayerFollowerChaserAI(int var1, int var2, boolean var3, boolean var4, int var5, int var6) {
      SequenceAINode var7 = new SequenceAINode();
      var7.addChild(new FollowerBaseSetterAINode());
      var7.addChild(new FollowerFocusTargetSetterAINode());
      var7.addChild(new SummonTargetFinderAINode(var1));
      var7.addChild(new ChaserAINode<T>(var2, var3, var4) {
         public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
            return PlayerFollowerChaserAI.this.canHitTarget(var1, var2, var3, var4);
         }

         public boolean attackTarget(T var1, Mob var2) {
            return PlayerFollowerChaserAI.this.attackTarget(var1, var2);
         }
      });
      this.addChild(var7);
      this.addChild(new PlayerFollowerAINode(var5, var6));
   }

   public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
      return ChaserAINode.hasLineOfSightToTarget(var1, var2, var3, var4);
   }

   public abstract boolean attackTarget(T var1, Mob var2);
}
