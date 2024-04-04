package necesse.engine.util;

import java.util.function.Supplier;

public class ComputedObjectValue<T, V> extends ComputedValue<V> {
   public final T object;

   public ComputedObjectValue(T var1, Supplier<V> var2) {
      super(var2);
      this.object = var1;
   }
}
