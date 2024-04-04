package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.network.NetworkClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class CommandAttackTargetterAINode<T extends Mob> extends AINode<T> {
   public String currentTargetKey = "currentTarget";
   public Mob lastTarget;

   public CommandAttackTargetterAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = this.getTarget(var1);

      AINodeResult var4;
      try {
         if (this.lastTarget != null && this.lastTarget != var3) {
            var2.put(this.currentTargetKey, (Object)null);
         }

         if (var3 == null) {
            return AINodeResult.FAILURE;
         }

         if (!var3.removed() && var1.isSamePlace(var3)) {
            if (var1.estimateCanMoveTo(var3.getTileX(), var3.getTileY(), var3.canBeTargetedFromAdjacentTiles())) {
               this.tickTargetSet(var1, var3);
               if (var3.canBeTargeted(var1, (NetworkClient)null)) {
                  var2.put(this.currentTargetKey, var3);
                  var4 = AINodeResult.SUCCESS;
                  return var4;
               }

               this.resetTarget(var1);
            } else if (var2.getObject(Mob.class, this.currentTargetKey) == var3) {
               var2.put(this.currentTargetKey, (Object)null);
               return AINodeResult.FAILURE;
            }

            return AINodeResult.FAILURE;
         }

         this.resetTarget(var1);
         if (var2.getObject(Mob.class, this.currentTargetKey) == var3) {
            var2.put(this.currentTargetKey, (Object)null);
         }

         var4 = AINodeResult.FAILURE;
      } finally {
         this.lastTarget = var3;
      }

      return var4;
   }

   public abstract Mob getTarget(T var1);

   public abstract void resetTarget(T var1);

   public abstract void tickTargetSet(T var1, Mob var2);
}
