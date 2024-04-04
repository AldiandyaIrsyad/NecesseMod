package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public interface AttackStageInterface<T extends Mob> {
   void onStarted(T var1, Blackboard<T> var2);

   void onEnded(T var1, Blackboard<T> var2);
}
