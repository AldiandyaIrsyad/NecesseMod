package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class InverterAINode<T extends Mob> extends DecoratorAINode<T> {
   public InverterAINode(AINode<T> var1) {
      super(var1);
   }

   public AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3) {
      AINodeResult var4 = var1.lastResult = var1.tick(var2, var3);
      switch (var4) {
         case RUNNING:
            return AINodeResult.RUNNING;
         case FAILURE:
            return AINodeResult.SUCCESS;
         case SUCCESS:
            return AINodeResult.FAILURE;
         default:
            return null;
      }
   }
}
