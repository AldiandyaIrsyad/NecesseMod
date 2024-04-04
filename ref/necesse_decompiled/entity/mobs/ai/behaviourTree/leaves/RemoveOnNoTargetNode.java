package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class RemoveOnNoTargetNode<T extends Mob> extends AINode<T> {
   public String targetKey = "currentTarget";
   public int counter;
   public int removeAfterTicks;

   public RemoveOnNoTargetNode(int var1) {
      this.removeAfterTicks = var1;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent("refreshBossDespawn", (var1x) -> {
         this.counter = 0;
      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (var3 == null) {
         ++this.counter;
         if (this.counter > this.removeAfterTicks) {
            var1.remove();
         }
      } else {
         this.counter = 0;
      }

      return AINodeResult.SUCCESS;
   }
}
