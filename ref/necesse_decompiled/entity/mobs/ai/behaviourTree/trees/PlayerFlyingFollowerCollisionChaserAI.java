package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;

public class PlayerFlyingFollowerCollisionChaserAI<T extends Mob> extends SelectorAINode<T> {
   public PlayerFlyingFollowerCollisionChaserAI(int var1, GameDamage var2, int var3, int var4, int var5, int var6) {
      SequenceAINode var7 = new SequenceAINode();
      var7.addChild(new FollowerBaseSetterAINode());
      var7.addChild(new FollowerFocusTargetSetterAINode());
      var7.addChild(new SummonTargetFinderAINode(var1));
      CollisionChaserAINode var8 = new CollisionChaserAINode(var2, var3);
      var8.hitCooldowns.hitCooldown = var4;
      var7.addChild(var8);
      this.addChild(var7);
      this.addChild(new PlayerFlyingFollowerAINode(var5, var6));
   }
}
