package necesse.entity.mobs.ai.behaviourTree.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public class AIEventHandler<T extends AIEvent> {
   private ArrayList<AIEventListener<T>> listeners = new ArrayList();
   private ArrayList<T> lastEvents = new ArrayList();

   public AIEventHandler() {
   }

   public void addListener(AIEventListener<T> var1) {
      Objects.requireNonNull(var1);
      this.listeners.add(var1);
   }

   public void clearListeners() {
      this.listeners.clear();
   }

   public void cleanListeners() {
      this.listeners.removeIf(AIEventListener::disposed);
   }

   public void submitEvent(T var1) {
      this.lastEvents.add(var1);
      this.cleanListeners();
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         AIEventListener var3 = (AIEventListener)var2.next();
         var3.onEvent(var1);
      }

   }

   public void clearLatestEvents() {
      this.lastEvents.clear();
   }

   public Iterable<T> getLastEvents() {
      return this.lastEvents;
   }

   public Stream<T> streamLastEvents() {
      return this.lastEvents.stream();
   }
}
