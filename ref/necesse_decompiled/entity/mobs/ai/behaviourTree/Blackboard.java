package necesse.entity.mobs.ai.behaviourTree;

import java.util.HashMap;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitCalculatedEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIBeforeHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEventHandler;
import necesse.entity.mobs.ai.behaviourTree.event.AIEventListener;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.CustomAIEventHandler;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;

public class Blackboard<T extends Mob> extends HashMap<String, Object> {
   protected CustomAIEventHandler<T> customEvents = new CustomAIEventHandler();
   protected AIEventHandler<AIEvent> globalTickEvents = new AIEventHandler();
   protected AIEventHandler<AIBeforeHitEvent> beforeHitEvents = new AIEventHandler();
   protected AIEventHandler<AIBeforeHitCalculatedEvent> beforeHitCalculatedEvents = new AIEventHandler();
   protected AIEventHandler<AIWasHitEvent> wasHitEvents = new AIEventHandler();
   protected AIEventHandler<AIEvent> removedEvents = new AIEventHandler();
   protected AIEventHandler<AIEvent> onUnloadingEvents = new AIEventHandler();
   public final AIMover mover;

   public Blackboard(AIMover var1) {
      this.mover = var1;
   }

   protected void clearLatestEvents() {
      this.globalTickEvents.clearLatestEvents();
      this.beforeHitEvents.clearLatestEvents();
      this.beforeHitCalculatedEvents.clearLatestEvents();
      this.wasHitEvents.clearLatestEvents();
      this.removedEvents.clearLatestEvents();
      this.customEvents.clearLatestEvents();
   }

   public <C> C getObject(Class<? extends C> var1, String var2) {
      return this.getObject(var1, var2, (Object)null);
   }

   public <C> C getObject(Class<? extends C> var1, String var2, C var3) {
      try {
         return var1.cast(this.getOrDefault(var2, var3));
      } catch (ClassCastException var5) {
         return var3;
      }
   }

   public <C> C getObjectNotNull(Class<? extends C> var1, String var2, C var3) {
      Object var4 = this.getObject(var1, var2, var3);
      return var4 == null ? var3 : var4;
   }

   public void onGlobalTick(AIEventListener<AIEvent> var1) {
      this.globalTickEvents.addListener(var1);
   }

   public void onBeforeHit(AIEventListener<AIBeforeHitEvent> var1) {
      this.beforeHitEvents.addListener(var1);
   }

   public void onBeforeHitCalculated(AIEventListener<AIBeforeHitCalculatedEvent> var1) {
      this.beforeHitCalculatedEvents.addListener(var1);
   }

   public void onWasHit(AIEventListener<AIWasHitEvent> var1) {
      this.wasHitEvents.addListener(var1);
   }

   public void onUnloading(AIEventListener<AIEvent> var1) {
      this.onUnloadingEvents.addListener(var1);
   }

   public void onRemoved(AIEventListener<AIEvent> var1) {
      this.removedEvents.addListener(var1);
   }

   public void onEvent(String var1, AIEventListener<AIEvent> var2) {
      this.customEvents.addListener(var1, var2);
   }

   public void submitEvent(String var1, AIEvent var2) {
      this.customEvents.submitEvent(var1, var2);
   }

   public Iterable<AIWasHitEvent> getLastHits() {
      return this.wasHitEvents.getLastEvents();
   }

   public Stream<AIWasHitEvent> streamLastHits() {
      return this.wasHitEvents.streamLastEvents();
   }

   public Iterable<AIEvent> getLastCustomEvents(String var1) {
      return this.customEvents.getLastEvents(var1);
   }

   public Stream<AIEvent> streamLastCustomEvents(String var1) {
      return this.customEvents.streamLastEvents(var1);
   }
}
