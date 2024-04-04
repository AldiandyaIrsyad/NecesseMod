package necesse.entity.mobs.ai.behaviourTree.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.util.HashMapArrayList;
import necesse.entity.mobs.Mob;

public class CustomAIEventHandler<T extends Mob> {
   private HashMapArrayList<String, AIEventListener<AIEvent>> listeners = new HashMapArrayList();
   private HashMapArrayList<String, AIEvent> lastEvents = new HashMapArrayList();

   public CustomAIEventHandler() {
   }

   public void addListener(String var1, AIEventListener<AIEvent> var2) {
      Objects.requireNonNull(var2);
      this.listeners.add(var1, var2);
   }

   public void clearListeners() {
      this.listeners.clearAll();
   }

   public void cleanListeners() {
      Iterator var1 = this.listeners.values().iterator();

      while(var1.hasNext()) {
         ArrayList var2 = (ArrayList)var1.next();
         var2.removeIf(AIEventListener::disposed);
      }

   }

   public void submitEvent(String var1, AIEvent var2) {
      this.lastEvents.add(var1, var2);
      this.cleanListeners();
      this.listeners.stream(var1).forEach((var1x) -> {
         var1x.onEvent(var2);
      });
   }

   public void clearLatestEvents() {
      this.lastEvents.clearAll();
   }

   public Iterable<AIEvent> getLastEvents(String var1) {
      return (Iterable)this.lastEvents.get(var1);
   }

   public Stream<AIEvent> streamLastEvents(String var1) {
      return this.lastEvents.stream(var1);
   }
}
