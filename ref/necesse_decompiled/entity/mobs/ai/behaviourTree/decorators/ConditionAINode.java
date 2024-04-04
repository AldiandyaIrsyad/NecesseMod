package necesse.entity.mobs.ai.behaviourTree.decorators;

import java.util.function.Predicate;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;

public class ConditionAINode<T extends Mob> extends DecoratorAINode<T> {
   public Predicate<T> condition;
   public AINodeResult failResult;

   public ConditionAINode(AINode<T> var1, Predicate<T> var2, AINodeResult var3) {
      super(var1);
      this.condition = var2;
      this.failResult = var3;
   }

   public AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3) {
      if (var1.lastResult != AINodeResult.RUNNING && !this.condition.test(var2)) {
         return this.failResult;
      } else {
         var1.lastResult = var1.tick(var2, var3);
         return var1.lastResult;
      }
   }
}
