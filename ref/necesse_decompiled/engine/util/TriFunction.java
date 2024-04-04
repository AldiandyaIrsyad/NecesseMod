package necesse.engine.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {
   R apply(A var1, B var2, C var3);

   default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> var1) {
      Objects.requireNonNull(var1);
      return (var2, var3, var4) -> {
         return var1.apply(this.apply(var2, var3, var4));
      };
   }
}
