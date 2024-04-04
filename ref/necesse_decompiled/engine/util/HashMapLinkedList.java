package necesse.engine.util;

import java.util.LinkedList;

public class HashMapLinkedList<K, V> extends HashMapCollection<K, V, LinkedList<V>> {
   public HashMapLinkedList() {
      super(LinkedList::new);
   }
}
