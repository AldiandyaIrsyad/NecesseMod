package necesse.engine.util;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HashMapCollection<K, V, L extends Collection<V>> extends HashMapCollectionAbstract<K, V, L> {
   private Supplier<L> listCreator;

   public HashMapCollection(Supplier<L> var1) {
      this.listCreator = var1;
   }

   protected L createNewList() {
      return (Collection)this.listCreator.get();
   }

   protected void addToList(L var1, V var2) {
      var1.add(var2);
   }

   protected void addAllToList(L var1, Collection<V> var2) {
      var1.addAll(var2);
   }

   protected Stream<V> streamValues(L var1) {
      return var1.stream();
   }

   protected boolean listContains(L var1, V var2) {
      return var1.contains(var2);
   }

   protected boolean isListEmpty(L var1) {
      return var1.isEmpty();
   }

   protected int getListSize(L var1) {
      return var1.size();
   }

   protected boolean removeFromList(L var1, V var2) {
      return var1.remove(var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean removeFromList(Object var1, Object var2) {
      return this.removeFromList((Collection)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected int getListSize(Object var1) {
      return this.getListSize((Collection)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean isListEmpty(Object var1) {
      return this.isListEmpty((Collection)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean listContains(Object var1, Object var2) {
      return this.listContains((Collection)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Stream streamValues(Object var1) {
      return this.streamValues((Collection)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void addAllToList(Object var1, Collection var2) {
      this.addAllToList((Collection)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void addToList(Object var1, Object var2) {
      this.addToList((Collection)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object createNewList() {
      return this.createNewList();
   }
}
