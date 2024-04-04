package necesse.engine.util;

import java.util.ArrayList;

public class HashMapArrayList<K, V> extends HashMapCollection<K, V, ArrayList<V>> {
   public HashMapArrayList() {
      super(ArrayList::new);
   }

   public HashMapArrayList(int var1) {
      super(() -> {
         return new ArrayList(var1);
      });
   }
}
