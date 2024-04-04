package necesse.engine.util;

import java.util.HashMap;

public class HashMapQueue<K, V> extends MapQueue<K, V> {
   public HashMapQueue() {
      super(new HashMap());
   }
}
