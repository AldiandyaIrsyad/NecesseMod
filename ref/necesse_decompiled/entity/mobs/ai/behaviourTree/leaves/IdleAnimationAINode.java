package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class IdleAnimationAINode<T extends Mob> extends AINode<T> {
   public int animIn;

   public IdleAnimationAINode() {
      this.resetIdleAnimationCooldown();
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (!var1.isAccelerating() && !var2.mover.isMoving()) {
         --this.animIn;
         if (this.animIn <= 0) {
            this.runIdleAnimation(var1);
            this.resetIdleAnimationCooldown();
         }
      }

      return AINodeResult.FAILURE;
   }

   public void resetIdleAnimationCooldown() {
      this.animIn = this.getIdleAnimationCooldown(GameRandom.globalRandom);
   }

   public abstract int getIdleAnimationCooldown(GameRandom var1);

   public abstract void runIdleAnimation(T var1);
}
