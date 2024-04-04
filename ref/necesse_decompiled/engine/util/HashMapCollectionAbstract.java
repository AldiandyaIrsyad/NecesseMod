package necesse.engine.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class HashMapCollectionAbstract<K, V, L> {
   private HashMap<K, L> map = new HashMap();

   public HashMapCollectionAbstract() {
   }

   public void add(K var1, V var2) {
      this.map.compute(var1, (var2x, var3) -> {
         if (var3 == null) {
            var3 = this.createNewList();
         }

         this.addToList(var3, var2);
         return var3;
      });
   }

   public void addAll(K var1, Collection<V> var2) {
      this.map.compute(var1, (var2x, var3) -> {
         if (var3 == null) {
            var3 = this.createNewList();
         }

         this.addAllToList(var3, var2);
         return var3;
      });
   }

   public L clear(K var1) {
      return this.map.remove(var1);
   }

   public void clearAll() {
      this.map.clear();
   }

   public Stream<V> stream(K var1) {
      Object var2 = this.map.get(var1);
      return var2 == null ? Stream.empty() : this.streamValues(var2);
   }

   public L get(K var1) {
      return this.map.compute(var1, (var1x, var2) -> {
         if (var2 == null) {
            var2 = this.createNewList();
         }

         return var2;
      });
   }

   public boolean contains(K var1, V var2) {
      Object var3 = this.map.get(var1);
      return var3 != null && this.listContains(var3, var2);
   }

   public boolean remove(K var1, V var2) {
      Object var3 = this.map.get(var1);
      if (var3 != null && this.removeFromList(var3, var2)) {
         if (this.isListEmpty(var3)) {
            this.map.remove(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isEmpty(K var1) {
      Object var2 = this.map.get(var1);
      return var2 == null || this.isListEmpty(var2);
   }

   public int getSize(K var1) {
      Object var2 = this.map.get(var1);
      return var2 == null ? 0 : this.getListSize(var2);
   }

   public Set<K> keySet() {
      return this.map.keySet();
   }

   public Collection<L> values() {
      return this.map.values();
   }

   public Set<Map.Entry<K, L>> entrySet() {
      return this.map.entrySet();
   }

   protected abstract L createNewList();

   protected abstract void addToList(L var1, V var2);

   protected abstract void addAllToList(L var1, Collection<V> var2);

   protected abstract Stream<V> streamValues(L var1);

   protected abstract boolean listContains(L var1, V var2);

   protected abstract boolean isListEmpty(L var1);

   protected abstract int getListSize(L var1);

   protected abstract boolean removeFromList(L var1, V var2);
}
