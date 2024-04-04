package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.MobBeforeHitEvent;

public class AIBeforeHitEvent extends AIEvent {
   public final MobBeforeHitEvent event;

   public AIBeforeHitEvent(MobBeforeHitEvent var1) {
      this.event = var1;
   }
}
