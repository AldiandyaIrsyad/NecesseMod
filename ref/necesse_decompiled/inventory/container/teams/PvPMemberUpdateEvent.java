package necesse.inventory.container.teams;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPMemberUpdateEvent extends ContainerEvent {
   public long auth;
   public boolean added;
   public String name;

   public PvPMemberUpdateEvent(long var1, boolean var3, String var4) {
      this.auth = var1;
      this.added = var3;
      this.name = var4;
   }

   public PvPMemberUpdateEvent(PacketReader var1) {
      super(var1);
      this.auth = var1.getNextLong();
      this.added = var1.getNextBoolean();
      if (this.added) {
         this.name = var1.getNextString();
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextLong(this.auth);
      var1.putNextBoolean(this.added);
      if (this.added) {
         var1.putNextString(this.name);
      }

   }
}
