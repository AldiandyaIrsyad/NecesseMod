package necesse.engine.util.gameAreaSearch;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface GameAreaStream<T> {
   GameAreaStream<T> filter(Predicate<? super T> var1);

   <R> GameAreaStream<R> map(Function<? super T, ? extends R> var1);

   <R> GameAreaStream<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> var1);

   long count();

   default <R> GameAreaStream<R> flatStream(Function<? super T, Stream<? extends R>> var1) {
      return this.flatMap((var1x) -> {
         Iterator var2 = ((Stream)var1.apply(var1x)).iterator();
         return () -> {
            return var2;
         };
      });
   }

   void forEach(Consumer<? super T> var1);

   boolean anyMatch(Predicate<? super T> var1);

   boolean allMatch(Predicate<? super T> var1);

   boolean noneMatch(Predicate<? super T> var1);

   Optional<T> findFirst();

   default <R, A> R collect(Collector<? super T, A, R> var1) {
      return this.findExtraDistance(Integer.MAX_VALUE, var1);
   }

   <R, A> R findExtraDistance(int var1, Collector<? super T, A, R> var2);

   TreeSet<T> findExtraDistanceSorted(int var1, Comparator<? super T> var2);

   Optional<T> findBestDistance(int var1, Comparator<? super T> var2);

   <R, A> R findExtraItems(int var1, Collector<? super T, A, R> var2);

   TreeSet<T> findExtraItemsSorted(int var1, Comparator<? super T> var2);

   Optional<T> findBestItems(int var1, Comparator<? super T> var2);

   static <T> GameAreaStream<T> empty() {
      return new GameAreaPipeline(new EmptyGameAreaSearch());
   }
}
