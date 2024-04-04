package necesse.engine.util;

import java.util.Iterator;
import java.util.function.Function;

public class MapIterator<T, R> implements Iterator<R> {
   public final Iterator<T> iterator;
   public final Function<T, R> mapper;

   public MapIterator(Iterator<T> var1, Function<T, R> var2) {
      this.iterator = var1;
      this.mapper = var2;
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public R next() {
      return this.mapper.apply(this.iterator.next());
   }
}
