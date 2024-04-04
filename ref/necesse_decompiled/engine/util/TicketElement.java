package necesse.engine.util;

public abstract class TicketElement {
   protected int tickets;
   protected int startTicket;
   protected int endTicket;

   public TicketElement(int var1, int var2, int var3) {
      this.tickets = var1;
      this.startTicket = var2;
      this.endTicket = var3;
   }

   public boolean matchTicket(int var1) {
      return this.startTicket <= var1 && var1 < this.endTicket;
   }
}
