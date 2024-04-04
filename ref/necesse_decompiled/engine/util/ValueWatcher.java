package necesse.engine.util;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class ValueWatcher<T> {
   private final Supplier<T> getter;
   private T last;

   public ValueWatcher(Supplier<T> var1) {
      this.getter = var1;
      this.last = var1.get();
   }

   public void update() {
      Object var1 = this.getter.get();
      if (this.hasChanged(this.last, var1)) {
         this.last = var1;
         this.onChange(var1);
      }

   }

   protected boolean hasChanged(T var1, T var2) {
      return !Objects.equals(var1, var2);
   }

   public abstract void onChange(T var1);
}
