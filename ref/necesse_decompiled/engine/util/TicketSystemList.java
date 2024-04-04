package necesse.engine.util;

import java.util.Random;

public class TicketSystemList<T> extends ProtectedTicketSystemList<T> {
   public TicketSystemList() {
   }

   public TicketSystemList(TicketSystemList<T> var1) {
      super(var1);
   }

   public TicketSystemList<T> addAll(ProtectedTicketSystemList<T> var1) {
      return (TicketSystemList)super.addAll(var1);
   }

   public TicketSystemList<T> addObject(int var1, T var2) {
      return (TicketSystemList)super.addObject(var1, var2);
   }

   protected boolean removeObject(T var1) {
      return super.removeObject(var1);
   }

   public T getRandomObject(Random var1) {
      return super.getRandomObject(var1);
   }

   public T getAndRemoveRandomObject(Random var1) {
      return super.getAndRemoveRandomObject(var1);
   }

   public TicketSystemList<T> reversed() {
      return (TicketSystemList)(new TicketSystemList()).addAllReversed(this);
   }

   public Iterable<T> getAll() {
      return super.getAll();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ProtectedTicketSystemList reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ProtectedTicketSystemList addObject(int var1, Object var2) {
      return this.addObject(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ProtectedTicketSystemList addAll(ProtectedTicketSystemList var1) {
      return this.addAll(var1);
   }
}
