package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class LooseTargetTimerAINode<T extends Mob> extends AINode<T> {
   public String currentTargetKey;
   public int looseTargetTimer;
   public int maxTargetLooseTicks;

   public LooseTargetTimerAINode(String var1) {
      this.maxTargetLooseTicks = 120;
      this.currentTargetKey = var1;
      this.startTimer();
   }

   public LooseTargetTimerAINode() {
      this("currentTarget");
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.currentTargetKey);
      if (var3 != null) {
         --this.looseTargetTimer;
         if (this.looseTargetTimer <= 0) {
            var2.put(this.currentTargetKey, (Object)null);
            this.startTimer();
            return AINodeResult.SUCCESS;
         }
      }

      return AINodeResult.FAILURE;
   }

   public void startTimer() {
      this.looseTargetTimer = GameRandom.globalRandom.getIntBetween(this.maxTargetLooseTicks / 2, this.maxTargetLooseTicks);
   }
}
