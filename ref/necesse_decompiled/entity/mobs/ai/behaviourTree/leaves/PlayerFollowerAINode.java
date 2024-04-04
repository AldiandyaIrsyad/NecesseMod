package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;

public class PlayerFollowerAINode<T extends Mob> extends FollowerAINode<T> {
   public PlayerFollowerAINode(int var1, int var2) {
      super(var1, var2);
   }

   public Mob getFollowingMob(T var1) {
      return var1.getFollowingServerPlayer();
   }
}
