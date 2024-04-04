package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class IsolateRunningAINode<T extends Mob> extends DecoratorAINode<T> {
   public AINodeResult runningResult;

   public IsolateRunningAINode(AINode<T> var1, AINodeResult var2) {
      super(var1);
      this.runningResult = var2;
   }

   public IsolateRunningAINode(AINode<T> var1) {
      this(var1, AINodeResult.SUCCESS);
   }

   public AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3) {
      AINodeResult var4 = var1.lastResult = var1.tick(var2, var3);
      return var4 == AINodeResult.RUNNING ? this.runningResult : var4;
   }
}
