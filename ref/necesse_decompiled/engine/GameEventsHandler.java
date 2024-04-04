package necesse.engine;

import java.util.Iterator;
import necesse.engine.util.GameLinkedList;

public class GameEventsHandler<T> {
   private GameLinkedList<GameEventInterface<T>> listeners = new GameLinkedList();
   private boolean autoClean;

   public GameEventsHandler(boolean var1) {
      this.autoClean = var1;
   }

   public <R extends GameEventInterface<T>> R addListener(R var1) {
      if (this.autoClean) {
         this.cleanListeners();
      }

      GameLinkedList.Element var2 = this.listeners.addLast(var1);
      var1.init(() -> {
         if (!var2.isRemoved()) {
            var2.remove();
         }

      });
      return var1;
   }

   public void triggerEvent(T var1) {
      Iterator var2 = this.listeners.elements().iterator();

      while(var2.hasNext()) {
         GameLinkedList.Element var3 = (GameLinkedList.Element)var2.next();
         if (((GameEventInterface)var3.object).isDisposed()) {
            var3.remove();
         } else {
            ((GameEventInterface)var3.object).onEvent(var1);
         }
      }

   }

   public void cleanListeners() {
      Iterator var1 = this.listeners.elements().iterator();

      while(var1.hasNext()) {
         GameLinkedList.Element var2 = (GameLinkedList.Element)var1.next();
         if (((GameEventInterface)var2.object).isDisposed()) {
            var2.remove();
         }
      }

   }

   public int getListenerCount() {
      return this.listeners.size();
   }
}
