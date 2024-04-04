package necesse.engine.util;

public class ComparableSequence<T extends Comparable<? super T>> implements Comparable<ComparableSequence<T>> {
   private ComparableSequence<T> before;
   private ComparableSequence<T> then;
   private final T compareObject;

   public ComparableSequence(T var1) {
      this.compareObject = var1;
   }

   public ComparableSequence<T> beforeBy(ComparableSequence<T> var1) {
      var1.then = this;
      if (this.before != null) {
         var1.before = this.before;
         this.before.then = var1;
      }

      this.before = var1;
      return var1;
   }

   public ComparableSequence<T> beforeBy(T var1) {
      return this.beforeBy(new ComparableSequence(var1));
   }

   public ComparableSequence<T> thenBy(ComparableSequence<T> var1) {
      var1.before = this;
      if (this.then != null) {
         var1.then = this.then;
         this.then.before = var1;
      }

      this.then = var1;
      return var1;
   }

   public ComparableSequence<T> thenBy(T var1) {
      return this.thenBy(new ComparableSequence(var1));
   }

   public ComparableSequence<T> firstBy(ComparableSequence<T> var1) {
      return this.getFirst().beforeBy(var1);
   }

   public ComparableSequence<T> firstBy(T var1) {
      return this.getFirst().beforeBy(var1);
   }

   public ComparableSequence<T> lastBy(ComparableSequence<T> var1) {
      return this.getLast().thenBy(var1);
   }

   public ComparableSequence<T> lastBy(T var1) {
      return this.getLast().thenBy(var1);
   }

   private ComparableSequence<T> getFirst() {
      return this.before != null ? this.before.getFirst() : this;
   }

   private ComparableSequence<T> getLast() {
      return this.then != null ? this.then.getLast() : this;
   }

   public int compareTo(ComparableSequence<T> var1) {
      ComparableSequence var2 = this.getFirst();
      ComparableSequence var3 = var1.getFirst();

      int var4;
      for(var4 = var2.compareObject.compareTo(var3.compareObject); var4 == 0; var4 = var2.compareObject.compareTo(var3.compareObject)) {
         if (var2.then == null && var3.then == null) {
            return var4;
         }

         var2 = var2.then;
         var3 = var3.then;
         if (var2 == null) {
            return 1;
         }

         if (var3 == null) {
            return -1;
         }
      }

      return var4;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((ComparableSequence)var1);
   }
}
