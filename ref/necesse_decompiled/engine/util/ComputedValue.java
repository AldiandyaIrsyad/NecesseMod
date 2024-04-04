package necesse.engine.util;

import java.util.function.Supplier;

public class ComputedValue<V> {
   private Supplier<V> supplier;
   private V value;

   public ComputedValue(Supplier<V> var1) {
      this.supplier = var1;
   }

   public V get() {
      if (this.supplier == null) {
         return this.value;
      } else {
         this.value = this.supplier.get();
         this.supplier = null;
         return this.value;
      }
   }
}
