package necesse.entity.mobs.ai.behaviourTree.composites;

import java.util.Iterator;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.CompositeAINode;

public class SequenceAINode<T extends Mob> extends CompositeAINode<T> {
   public SequenceAINode() {
   }

   protected AINodeResult tickChildren(AINode<T> var1, AINodeResult var2, Iterable<AINode<T>> var3, T var4, Blackboard<T> var5) {
      if (var2 == AINodeResult.FAILURE) {
         return AINodeResult.FAILURE;
      } else {
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            AINode var7 = (AINode)var6.next();
            AINodeResult var8 = var7.lastResult = var7.tick(var4, var5);
            switch (var8) {
               case RUNNING:
                  this.runningNode = var7;
                  return AINodeResult.RUNNING;
               case FAILURE:
                  return AINodeResult.FAILURE;
            }
         }

         return AINodeResult.SUCCESS;
      }
   }
}
