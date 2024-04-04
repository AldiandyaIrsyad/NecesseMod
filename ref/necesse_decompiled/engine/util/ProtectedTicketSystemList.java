package necesse.engine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class ProtectedTicketSystemList<T> {
   private List<ProtectedTicketSystemList<T>.TicketObject> list;
   private int ticketCounter;

   public ProtectedTicketSystemList() {
      this.list = new ArrayList();
      this.ticketCounter = 0;
   }

   public ProtectedTicketSystemList(ProtectedTicketSystemList<T> var1) {
      this();
      if (var1 != null) {
         this.addAll(var1);
      }

   }

   protected ProtectedTicketSystemList<T> addAll(ProtectedTicketSystemList<T> var1) {
      Iterator var2 = var1.list.iterator();

      while(var2.hasNext()) {
         TicketObject var3 = (TicketObject)var2.next();
         this.addObject(var3.tickets, var3.object);
      }

      return this;
   }

   protected ProtectedTicketSystemList<T> addAllReversed(ProtectedTicketSystemList<T> var1) {
      Iterator var2 = var1.list.iterator();

      while(var2.hasNext()) {
         TicketObject var3 = (TicketObject)var2.next();
         int var4 = var1.ticketCounter - var3.tickets;
         this.addObject(var4, var3.object);
      }

      return this;
   }

   protected ProtectedTicketSystemList<T> addObject(int var1, T var2) {
      int var3 = this.ticketCounter;
      int var4 = this.ticketCounter + var1;
      this.list.add(new TicketObject(var1, var3, var4, var2));
      this.ticketCounter = var4;
      return this;
   }

   protected boolean removeObject(T var1) {
      ListIterator var2 = this.list.listIterator();
      TicketObject var3 = null;

      while(var2.hasNext()) {
         TicketObject var4 = (TicketObject)var2.next();
         if (var3 != null) {
            var4.startTicket = this.ticketCounter;
            var4.endTicket = this.ticketCounter + var4.tickets;
            this.ticketCounter = var4.endTicket;
         } else if (Objects.equals(var4.object, var1)) {
            var2.remove();
            var3 = var4;
            this.ticketCounter = var4.startTicket;
         }
      }

      return var3 != null;
   }

   protected T getRandomObject(Random var1) {
      if (this.isEmpty()) {
         return null;
      } else {
         int var2 = var1.nextInt(this.ticketCounter);
         return this.list.stream().filter((var1x) -> {
            return var1x.matchTicket(var2);
         }).map((var0) -> {
            return var0.object;
         }).findFirst().orElse((Object)null);
      }
   }

   protected T getAndRemoveRandomObject(Random var1) {
      if (this.isEmpty()) {
         return null;
      } else {
         int var2 = var1.nextInt(this.ticketCounter);
         ListIterator var3 = this.list.listIterator();
         TicketObject var4 = null;

         while(var3.hasNext()) {
            TicketObject var5 = (TicketObject)var3.next();
            if (var4 != null) {
               var5.startTicket = this.ticketCounter;
               var5.endTicket = this.ticketCounter + var5.tickets;
               this.ticketCounter = var5.endTicket;
            } else if (var5.matchTicket(var2)) {
               var3.remove();
               var4 = var5;
               this.ticketCounter = var5.startTicket;
            }
         }

         if (var4 == null) {
            return null;
         } else {
            return var4.object;
         }
      }
   }

   protected void fixElements() {
      this.ticketCounter = 0;

      TicketObject var2;
      for(Iterator var1 = this.list.iterator(); var1.hasNext(); this.ticketCounter = var2.endTicket) {
         var2 = (TicketObject)var1.next();
         var2.startTicket = this.ticketCounter;
         var2.endTicket = this.ticketCounter + var2.tickets;
      }

   }

   public int getTotalTickets() {
      return this.ticketCounter;
   }

   public int getTotalElements() {
      return this.list.size();
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   protected Iterable<ProtectedTicketSystemList<T>.TicketObject> getTicketItems() {
      return this.list;
   }

   public void printChances() {
      this.printChances(Objects::toString);
   }

   public void printChances(Function<T, String> var1) {
      System.out.println("Ticket chances (" + this.ticketCounter + " total tickets):");
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         TicketObject var3 = (TicketObject)var2.next();
         float var4 = (float)var3.tickets / (float)this.ticketCounter;
         System.out.println("\t" + (String)var1.apply(var3.object) + " " + var4 * 100.0F + "% (" + var3.tickets + " tickets)");
      }

   }

   protected ProtectedTicketSystemList<T> reversed() {
      ProtectedTicketSystemList var1 = new ProtectedTicketSystemList();
      var1.addAllReversed(this);
      return var1;
   }

   protected Iterable<T> getAll() {
      return GameUtils.mapIterable(this.list.iterator(), (var0) -> {
         return var0.object;
      });
   }

   protected class TicketObject extends TicketElement {
      public final T object;

      public TicketObject(int var2, int var3, int var4, T var5) {
         super(var2, var3, var4);
         this.object = var5;
      }
   }
}
