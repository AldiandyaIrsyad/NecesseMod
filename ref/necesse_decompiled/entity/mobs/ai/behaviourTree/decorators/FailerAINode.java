package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class FailerAINode<T extends Mob> extends InverterAINode<T> {
   public FailerAINode(AINode<T> var1) {
      super(var1);
   }

   public AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3) {
      AINodeResult var4 = var1.lastResult = var1.tick(var2, var3);
      return var4 == AINodeResult.RUNNING ? AINodeResult.RUNNING : AINodeResult.FAILURE;
   }
}
