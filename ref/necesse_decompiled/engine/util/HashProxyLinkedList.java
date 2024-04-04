package necesse.engine.util;

import java.util.HashMap;
import java.util.function.Function;

public class HashProxyLinkedList<T, P> extends GameLinkedList<T> {
   private final HashMap<P, GameLinkedList<T>.Element> set = new HashMap();
   private final Function<T, P> proxyMapper;

   public HashProxyLinkedList(Function<T, P> var1) {
      this.proxyMapper = var1;
   }

   public void onAdded(GameLinkedList<T>.Element var1) {
      this.set.put(this.proxyMapper.apply(var1.object), var1);
   }

   public void onRemoved(GameLinkedList<T>.Element var1) {
      this.set.remove(this.proxyMapper.apply(var1.object));
   }

   public boolean contains(Object var1) {
      synchronized(this.lock) {
         return this.set.containsKey(var1);
      }
   }

   public boolean remove(Object var1) {
      synchronized(this.lock) {
         GameLinkedList.Element var3 = (GameLinkedList.Element)this.set.get(var1);
         if (var3 != null) {
            var3.remove();
            return true;
         } else {
            return false;
         }
      }
   }

   public GameLinkedList<T>.Element getElement(Object var1) {
      return (GameLinkedList.Element)this.set.get(var1);
   }

   public T getObject(Object var1) {
      GameLinkedList.Element var2 = this.getElement(var1);
      return var2 != null ? var2.object : null;
   }
}
