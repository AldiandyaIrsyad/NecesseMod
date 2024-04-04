package necesse.inventory.container.events;

public abstract class ContainerEventSubscription<T extends ContainerEvent> {
   public final Class<T> eventClass;

   public ContainerEventSubscription(Class<T> var1) {
      this.eventClass = var1;
   }

   public abstract boolean shouldReceiveEvent(T var1);

   public final boolean testUntypedEvent(ContainerEvent var1) {
      return this.eventClass == var1.getClass() && this.shouldReceiveEvent(var1);
   }

   public abstract boolean isActive();
}
