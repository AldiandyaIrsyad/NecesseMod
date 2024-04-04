package necesse.engine.util;

import java.util.Map;
import java.util.function.Supplier;

public class MapQueue<K, V> {
   private final Map<K, GameLinkedList<MapQueue<K, V>.QueuedElement>.Element> map;
   private final GameLinkedList<MapQueue<K, V>.QueuedElement> queue = new GameLinkedList();

   public MapQueue(Map<K, GameLinkedList<MapQueue<K, V>.QueuedElement>.Element> var1) {
      this.map = var1;
   }

   public synchronized void addLast(K var1, V var2) {
      this.map.compute(var1, (var3, var4) -> {
         if (var4 != null && !var4.isRemoved()) {
            var4.remove();
         }

         return this.queue.addLast(new QueuedElement(var1, var2));
      });
   }

   public synchronized void addFirst(K var1, V var2) {
      this.map.compute(var1, (var3, var4) -> {
         if (var4 != null && !var4.isRemoved()) {
            var4.remove();
         }

         return this.queue.addFirst(new QueuedElement(var1, var2));
      });
   }

   public synchronized V getOrDefault(K var1, Supplier<V> var2) {
      GameLinkedList.Element var3 = (GameLinkedList.Element)this.map.get(var1);
      return var3 == null ? var2.get() : ((QueuedElement)var3.object).value;
   }

   public synchronized V getOrDefault(K var1, V var2) {
      return this.getOrDefault(var1, () -> {
         return var2;
      });
   }

   public synchronized V get(K var1) {
      return this.getOrDefault(var1, (Object)null);
   }

   public synchronized boolean containsKey(K var1) {
      return this.map.containsKey(var1);
   }

   public synchronized V getFirst() {
      return this.queue.isEmpty() ? null : ((QueuedElement)this.queue.getFirst()).value;
   }

   public synchronized V removeFirst() {
      QueuedElement var1 = (QueuedElement)this.queue.removeFirst();
      if (var1 != null) {
         this.map.remove(var1.key);
         return var1.value;
      } else {
         return null;
      }
   }

   public synchronized V getLast() {
      return this.queue.isEmpty() ? null : ((QueuedElement)this.queue.getLast()).value;
   }

   public synchronized V removeLast() {
      QueuedElement var1 = (QueuedElement)this.queue.removeLast();
      if (var1 != null) {
         this.map.remove(var1.key);
         return var1.value;
      } else {
         return null;
      }
   }

   public synchronized void remove(K var1) {
      GameLinkedList.Element var2 = (GameLinkedList.Element)this.map.remove(var1);
      if (var2 != null && !var2.isRemoved()) {
         var2.remove();
      }

   }

   public synchronized boolean isEmpty() {
      return this.queue.isEmpty();
   }

   public synchronized int size() {
      return this.queue.size();
   }

   private class QueuedElement {
      public final K key;
      public final V value;

      public QueuedElement(K var2, V var3) {
         this.key = var2;
         this.value = var3;
      }
   }
}
