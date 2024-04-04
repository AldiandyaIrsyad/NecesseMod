package necesse.engine.modifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ModifierList implements Iterable<Modifier> {
   private final ArrayList<Modifier> modifiers = new ArrayList();
   private final HashMap<String, Integer> stringIDIndexes = new HashMap();

   public ModifierList() {
   }

   public int getModifierCount() {
      return this.modifiers.size();
   }

   int addModifier(Modifier<?> var1) {
      if (var1.stringID != null && this.stringIDIndexes.containsKey(var1.stringID)) {
         throw new IllegalArgumentException("Modifier with stringID " + var1.stringID + " already exists");
      } else {
         int var2 = this.modifiers.size();
         this.modifiers.add(var1);
         this.stringIDIndexes.put(var1.stringID, var2);
         return var2;
      }
   }

   public Modifier getModifier(int var1) {
      return (Modifier)this.modifiers.get(var1);
   }

   public Modifier getModifierByStringID(String var1) {
      int var2 = (Integer)this.stringIDIndexes.getOrDefault(var1, -1);
      return var2 == -1 ? null : this.getModifier(var2);
   }

   public Iterator<Modifier> iterator() {
      return this.modifiers.iterator();
   }

   public void forEach(Consumer<? super Modifier> var1) {
      this.modifiers.forEach(var1);
   }

   public Spliterator<Modifier> spliterator() {
      throw new UnsupportedOperationException("Spliterator not supported for modifier list");
   }

   private static class CombinedIterator<T> implements Iterator<T> {
      private final Iterator<T>[] is;
      private int current;

      @SafeVarargs
      public CombinedIterator(Iterator<T>... var1) {
         this.is = var1;
         this.current = 0;
      }

      private void peekNext() {
         while(this.current < this.is.length && !this.is[this.current].hasNext()) {
            ++this.current;
         }

      }

      public boolean hasNext() {
         this.peekNext();
         return this.current < this.is.length;
      }

      public T next() {
         this.peekNext();
         return this.is[this.current].next();
      }
   }
}
