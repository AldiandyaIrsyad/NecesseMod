package necesse.inventory.container.events;

import necesse.engine.util.GameLinkedList;

public abstract class ContainerEventHandler<T extends ContainerEvent> {
   private Class<T> eventClass;
   private GameLinkedList<ContainerEventHandler<?>>.Element element;
   private boolean isDisposed;

   public ContainerEventHandler() {
   }

   public void init(Class<T> var1, GameLinkedList<ContainerEventHandler<?>>.Element var2) {
      if (this.eventClass != null) {
         throw new IllegalStateException("Cannot initialize event handler twice");
      } else {
         this.eventClass = var1;
         this.element = var2;
      }
   }

   public abstract void handleEvent(T var1);

   public void dispose() {
      if (!this.isDisposed) {
         this.isDisposed = true;
         if (this.element != null) {
            this.element.remove();
         }

      }
   }

   public boolean isDisposed() {
      return this.isDisposed;
   }

   public final void handleEventUntyped(ContainerEvent var1) {
      if (this.eventClass == var1.getClass()) {
         this.handleEvent(var1);
      }

   }
}
