package necesse.engine.util;

import java.util.HashSet;

public class HashMapSet<K, V> extends HashMapCollection<K, V, HashSet<V>> {
   public HashMapSet() {
      super(HashSet::new);
   }
}
