package necesse.entity.mobs.buffs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.HashMapLinkedList;
import necesse.engine.util.HashMapSet;
import necesse.entity.mobs.MobGenericEvent;

public class BuffEventSubscriptions {
   private final HashMapSet<Integer, Class<? extends MobGenericEvent>> buffIDEventSubscriptions = new HashMapSet();
   private final HashMapLinkedList<Class<? extends MobGenericEvent>, BuffSubscription<?>> eventClassBuffSubscriptions = new HashMapLinkedList();

   public BuffEventSubscriptions() {
   }

   public void removeSubscriptions(int var1) {
      HashSet var2 = (HashSet)this.buffIDEventSubscriptions.clear(var1);
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Class var4 = (Class)var3.next();
            LinkedList var5 = (LinkedList)this.eventClassBuffSubscriptions.get(var4);
            var5.removeIf((var1x) -> {
               return var1x.buffID == var1;
            });
         }

      }
   }

   public <T extends MobGenericEvent> void addSubscription(int var1, Class<T> var2, Consumer<T> var3) {
      this.buffIDEventSubscriptions.add(var1, var2);
      this.eventClassBuffSubscriptions.add(var2, new BuffSubscription(var1, var3));
   }

   public void submitEvent(MobGenericEvent var1) {
      Class var2 = var1.getClass();
      Iterator var3 = ((LinkedList)this.eventClassBuffSubscriptions.get(var2)).iterator();

      while(var3.hasNext()) {
         BuffSubscription var4 = (BuffSubscription)var3.next();
         var4.eventListener.accept(var1);
      }

   }

   public void clear() {
      this.buffIDEventSubscriptions.clearAll();
      this.eventClassBuffSubscriptions.clearAll();
   }

   private class BuffSubscription<T extends MobGenericEvent> {
      private int buffID;
      private Consumer<T> eventListener;

      public BuffSubscription(int var2, Consumer<T> var3) {
         this.buffID = var2;
         this.eventListener = var3;
      }
   }
}
