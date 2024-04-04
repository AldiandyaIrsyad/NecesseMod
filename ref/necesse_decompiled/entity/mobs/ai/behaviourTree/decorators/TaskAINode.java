package necesse.entity.mobs.ai.behaviourTree.decorators;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.util.FutureAITask;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class TaskAINode<T extends Mob> extends AINode<T> {
   private int tickCounter;
   private FutureAITask<?> task;

   public TaskAINode() {
   }

   public final AINodeResult startTask(FutureAITask<?> var1) {
      this.tickCounter = 0;
      this.task = var1;
      if (!var1.isStarted()) {
         var1.runConcurrently();
      }

      return AINodeResult.RUNNING;
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.task != null) {
         ++this.tickCounter;
         if (this.task.isComplete()) {
            FutureAITask var3 = this.task;
            this.task = null;

            try {
               AINodeResult var4 = var3.runComplete();
               return var4 != null ? var4 : this.tickNode(var1, var2);
            } catch (Exception var5) {
               throw new RuntimeException("Path error from " + var1.getStringID() + " (" + var1.getUniqueID() + ")", var5);
            }
         } else {
            return this.tickWorking(var1, var2);
         }
      } else {
         return this.tickNode(var1, var2);
      }
   }

   public boolean hasTask() {
      return this.task != null;
   }

   public void clearTask() {
      this.task = null;
   }

   public abstract AINodeResult tickNode(T var1, Blackboard<T> var2);

   public AINodeResult tickWorking(T var1, Blackboard<T> var2) {
      return AINodeResult.RUNNING;
   }

   public void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      if (this.task != null) {
         var1.add("Task: " + this.task + " running for " + this.tickCounter + " ticks");
      } else {
         var1.add("No current task");
      }

   }
}
