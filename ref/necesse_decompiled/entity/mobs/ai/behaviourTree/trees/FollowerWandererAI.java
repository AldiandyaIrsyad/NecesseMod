package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;

public abstract class FollowerWandererAI<T extends Mob> extends SelectorAINode<T> {
   public FollowerWandererAI(int var1, int var2, int var3) {
      this.addChild(new FollowerAINode<T>(var1, var2) {
         public Mob getFollowingMob(T var1) {
            return FollowerWandererAI.this.getFollowingMob(var1);
         }
      });
      this.addChild(new WandererAINode(var3));
   }

   protected abstract Mob getFollowingMob(T var1);
}
