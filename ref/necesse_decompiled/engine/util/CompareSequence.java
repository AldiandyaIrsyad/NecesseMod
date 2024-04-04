package necesse.engine.util;

import java.util.Comparator;

public class CompareSequence<T> {
   private CompareSequence<T> before;
   private CompareSequence<T> then;
   private final Comparator<T> comparator;

   public CompareSequence(Comparator<T> var1) {
      this.comparator = var1;
   }

   public CompareSequence<T> beforeBy(Comparator<T> var1) {
      CompareSequence var2 = new CompareSequence(var1);
      var2.then = this;
      if (this.before != null) {
         var2.before = this.before;
         this.before.then = var2;
      }

      this.before = var2;
      return var2;
   }

   public CompareSequence<T> thenBy(Comparator<T> var1) {
      CompareSequence var2 = new CompareSequence(var1);
      var2.before = this;
      if (this.then != null) {
         var2.then = this.then;
         this.then.before = var2;
      }

      this.then = var2;
      return var2;
   }

   public CompareSequence<T> firstBy(Comparator<T> var1) {
      return this.getFirst().beforeBy(var1);
   }

   public CompareSequence<T> lastBy(Comparator<T> var1) {
      return this.getLast().thenBy(var1);
   }

   private CompareSequence<T> getFirst() {
      return this.before != null ? this.before.getFirst() : this;
   }

   private CompareSequence<T> getLast() {
      return this.then != null ? this.then.getLast() : this;
   }

   public Comparator<T> getComparator() {
      CompareSequence var1 = this.getFirst();
      Comparator var2 = var1.comparator;
      if (var1.then != null) {
         var1 = var1.then;
         var2 = var2.thenComparing(var1.comparator);
      }

      return var2;
   }
}
