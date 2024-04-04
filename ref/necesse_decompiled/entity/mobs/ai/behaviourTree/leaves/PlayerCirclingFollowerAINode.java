package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;

public class PlayerCirclingFollowerAINode<T extends Mob> extends CirclingFollowerAINode<T> {
   public PlayerCirclingFollowerAINode(int var1, int var2) {
      super(var1, var2);
   }

   public Mob getFollowingMob(T var1) {
      return var1.getFollowingServerPlayer();
   }
}
