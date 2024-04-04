package necesse.entity.mobs.ai.behaviourTree;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitCalculatedEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;

public class BehaviourTreeAI<T extends Mob> {
   public final T mob;
   public final AINode<T> tree;
   public final Blackboard<T> blackboard;

   public BehaviourTreeAI(T var1, AINode<T> var2) {
      this(var1, var2, new AIMover());
   }

   public BehaviourTreeAI(T var1, AINode<T> var2, AIMover var3) {
      this.mob = var1;
      this.tree = var2;
      this.blackboard = new Blackboard(var3);
      var2.makeRoot(var1, this.blackboard);
   }

   public void tick() {
      this.blackboard.globalTickEvents.submitEvent(new AIEvent());
      this.tree.init(this.mob, this.blackboard);
      this.tree.lastResult = this.tree.tick(this.mob, this.blackboard);
      this.blackboard.mover.tick(this.mob);
      this.blackboard.clearLatestEvents();
   }

   public void resetStuck() {
      this.blackboard.mover.resetStuck();
   }

   public void beforeHit(MobBeforeHitEvent var1) {
      this.blackboard.beforeHitEvents.submitEvent(new AIBeforeHitEvent(var1));
   }

   public void beforeHitCalculated(MobBeforeHitCalculatedEvent var1) {
      this.blackboard.beforeHitCalculatedEvents.submitEvent(new AIBeforeHitCalculatedEvent(var1));
   }

   public void wasHit(MobWasHitEvent var1) {
      this.blackboard.wasHitEvents.submitEvent(new AIWasHitEvent(var1));
   }

   public void onUnloading() {
      this.blackboard.onUnloadingEvents.submitEvent(new AIEvent());
   }

   public void isRemoved() {
      this.blackboard.removedEvents.submitEvent(new AIEvent());
   }
}
