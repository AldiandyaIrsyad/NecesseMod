package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;

public class PlayerFlyingFollowerAINode<T extends Mob> extends FlyingFollowerAINode<T> {
   public PlayerFlyingFollowerAINode(int var1, int var2) {
      super(var1, var2);
   }

   public Mob getFollowingMob(T var1) {
      return var1.getFollowingServerPlayer();
   }
}
