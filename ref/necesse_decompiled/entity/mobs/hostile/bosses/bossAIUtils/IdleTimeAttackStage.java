package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class IdleTimeAttackStage<T extends Mob> extends AINode<T> implements AttackStageInterface<T> {
   public Function<T, Integer> idleTimeMSGetter;
   public int timer;

   public IdleTimeAttackStage(Function<T, Integer> var1) {
      this.idleTimeMSGetter = var1;
   }

   public IdleTimeAttackStage(int var1) {
      this((var1x) -> {
         return var1;
      });
   }

   public IdleTimeAttackStage(int var1, int var2) {
      this((var2x) -> {
         int var3 = var2 - var1;
         float var4 = (float)var2x.getHealth() / (float)var2x.getMaxHealth();
         return var1 + (int)((float)var3 * var4);
      });
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public void onStarted(T var1, Blackboard<T> var2) {
      this.timer = 0;
   }

   public void onEnded(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      this.timer += 50;
      return this.timer >= (Integer)this.idleTimeMSGetter.apply(var1) ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
   }
}
