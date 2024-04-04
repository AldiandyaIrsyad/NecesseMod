package necesse.entity.mobs.ai.behaviourTree.event;

@FunctionalInterface
public interface AIEventListener<T extends AIEvent> {
   void onEvent(T var1);

   default boolean disposed() {
      return false;
   }
}
