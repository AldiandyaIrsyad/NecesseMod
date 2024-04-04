package necesse.engine.util;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventVariable<T> {
   public final boolean autoClean;
   private T var;
   private LinkedList<EventVariable<T>.Listener> changedEvents;

   public EventVariable(T var1, boolean var2) {
      this.changedEvents = new LinkedList();
      this.var = var1;
      this.autoClean = var2;
   }

   public EventVariable(T var1) {
      this(var1, true);
   }

   public EventVariable<T>.Listener addChangeListener(Consumer<T> var1, Supplier<Boolean> var2) {
      if (this.autoClean) {
         this.cleanListeners();
      }

      Listener var3;
      this.changedEvents.add(var3 = new Listener(var1, var2));
      return var3;
   }

   public void cleanListeners() {
      this.changedEvents.removeIf((var0) -> {
         return (Boolean)var0.isDisposed.get();
      });
   }

   public int getTotalChangeListeners() {
      if (this.autoClean) {
         this.cleanListeners();
      }

      return this.changedEvents.size();
   }

   public void set(T var1) {
      if (!Objects.equals(this.var, var1)) {
         this.var = var1;
         if (this.autoClean) {
            this.cleanListeners();
         }

         this.changedEvents.forEach((var1x) -> {
            var1x.onChange.accept(var1);
         });
      }

   }

   public T get() {
      return this.var;
   }

   public class Listener {
      private final Consumer<T> onChange;
      private final Supplier<Boolean> isDisposed;

      private Listener(Consumer<T> var2, Supplier<Boolean> var3) {
         this.onChange = var2;
         this.isDisposed = var3;
      }

      public void dispose() {
         EventVariable.this.changedEvents.remove(this);
      }

      // $FF: synthetic method
      Listener(Consumer var2, Supplier var3, Object var4) {
         this(var2, var3);
      }
   }
}
