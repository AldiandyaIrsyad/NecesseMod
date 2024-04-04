package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class RunningAINode<T extends Mob> extends AINode<T> {
   public RunningAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public final AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.lastResult != AINodeResult.RUNNING) {
         this.start(var1, var2);
      }

      AINodeResult var3 = this.tickRunning(var1, var2);
      if (var3 != AINodeResult.RUNNING) {
         this.end(var1, var2);
      }

      return var3;
   }

   public abstract void start(T var1, Blackboard<T> var2);

   public abstract AINodeResult tickRunning(T var1, Blackboard<T> var2);

   public abstract void end(T var1, Blackboard<T> var2);
}
