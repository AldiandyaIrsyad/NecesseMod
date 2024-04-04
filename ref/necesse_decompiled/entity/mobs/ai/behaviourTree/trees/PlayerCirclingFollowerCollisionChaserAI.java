package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerCirclingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public class PlayerCirclingFollowerCollisionChaserAI<T extends Mob> extends SelectorAINode<T> {
   public PlayerCirclingFollowerCollisionChaserAI(int var1, GameDamage var2, int var3, int var4, int var5) {
      SequenceAINode var6 = new SequenceAINode();
      var6.addChild(new FollowerBaseSetterAINode());
      var6.addChild(new FollowerFocusTargetSetterAINode());
      var6.addChild(new SummonTargetFinderAINode(var1));
      var6.addChild(new CollisionChaserAINode(var2, var3));
      this.addChild(var6);
      this.addChild(new PlayerCirclingFollowerAINode(var4, var5));
   }
}
