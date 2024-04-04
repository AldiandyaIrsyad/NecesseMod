package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;

public class PlayerFlyingFollowerCollisionAI<T extends Mob> extends SelectorAINode<T> {
   public PlayerFlyingFollowerCollisionAI(int var1, GameDamage var2, int var3, int var4, int var5, int var6) {
      CollisionChaserAINode var7 = new CollisionChaserAINode(var2, var3);
      var7.hitCooldowns.hitCooldown = var4;
      this.addChild(new PlayerFlyingFollowerAINode(var5, var6));
   }
}
