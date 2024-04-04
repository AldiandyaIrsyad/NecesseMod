package necesse.engine.util;

import java.util.Iterator;

public class ForIterator implements Iterator<Integer> {
   public final int startIndex;
   public final int endIndex;
   public final int delta;
   private int current;

   public ForIterator(int var1, int var2, int var3) {
      this.startIndex = var1;
      this.endIndex = var2;
      this.current = var1;
      this.delta = getDelta(var1, var2, var3);
   }

   public ForIterator(int var1, int var2) {
      this(var1, var2, 1);
   }

   private static int getDelta(int var0, int var1, int var2) {
      if (var2 == 0) {
         throw new IllegalArgumentException("Delta cannot be 0");
      } else {
         return var1 < var0 ? -Math.abs(var2) : Math.abs(var2);
      }
   }

   public boolean hasNext() {
      if (this.delta < 0) {
         return this.current >= this.endIndex;
      } else {
         return this.current <= this.endIndex;
      }
   }

   public Integer next() {
      int var1 = this.current;
      this.current += this.delta;
      return var1;
   }

   public Iterable<Integer> iterable() {
      return () -> {
         return this;
      };
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object next() {
      return this.next();
   }
}
