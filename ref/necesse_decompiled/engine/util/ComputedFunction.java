package necesse.engine.util;

import java.util.function.Function;

public class ComputedFunction<T, R> {
   private Function<T, R> function;
   private R result;

   public ComputedFunction(Function<T, R> var1) {
      this.function = var1;
   }

   public boolean isComputed() {
      return this.function == null;
   }

   public R get(T var1) {
      if (this.function == null) {
         return this.result;
      } else {
         this.result = this.function.apply(var1);
         this.function = null;
         return this.result;
      }
   }
}
