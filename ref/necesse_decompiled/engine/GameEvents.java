package necesse.engine;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import necesse.engine.events.GameEvent;
import necesse.engine.events.PreventableGameEvent;

public class GameEvents {
   private static final HashMap<Class<? extends GameEvent>, GameEventsHandler<GameEvent>> handlers = new HashMap();

   public GameEvents() {
   }

   public static <T extends GameEvent, R extends GameEventInterface<T>> R addListener(Class<T> var0, R var1) {
      GameEventsHandler var2 = (GameEventsHandler)handlers.compute(var0, (var0x, var1x) -> {
         if (var1x == null) {
            var1x = new GameEventsHandler(true);
         }

         return var1x;
      });
      var2.addListener(var1);
      return var1;
   }

   public static <T extends GameEvent> void triggerEvent(T var0) {
      handlers.computeIfPresent(var0.getClass(), (var1, var2) -> {
         var2.triggerEvent(var0);
         return var2;
      });
   }

   public static <T extends PreventableGameEvent> void triggerEvent(T var0, Consumer<T> var1) {
      handlers.computeIfPresent(var0.getClass(), (var1x, var2) -> {
         var2.triggerEvent(var0);
         return var2;
      });
      if (!var0.isPrevented()) {
         var1.accept(var0);
      }

   }

   public void cleanListeners() {
      handlers.values().forEach(GameEventsHandler::cleanListeners);
   }

   public <T extends GameEvent> void cleanListeners(Class<T> var1) {
      handlers.computeIfPresent(var1, (var0, var1x) -> {
         var1x.cleanListeners();
         return var1x;
      });
   }

   public static <T extends GameEvent> int getListenerCount(Class<T> var0) {
      AtomicInteger var1 = new AtomicInteger(0);
      handlers.computeIfPresent(var0, (var1x, var2) -> {
         var1.set(var2.getListenerCount());
         return var2;
      });
      return var1.get();
   }
}
