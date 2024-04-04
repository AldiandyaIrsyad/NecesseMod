package necesse.engine.util;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapQueue<K, V> extends MapQueue<K, V> {
   public ConcurrentHashMapQueue() {
      super(new ConcurrentHashMap());
   }
}
