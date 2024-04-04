package necesse.inventory.container.teams;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPOwnerUpdateEvent extends ContainerEvent {
   public long ownerAuth;

   public PvPOwnerUpdateEvent(long var1) {
      this.ownerAuth = var1;
   }

   public PvPOwnerUpdateEvent(PacketReader var1) {
      super(var1);
      this.ownerAuth = var1.getNextLong();
   }

   public void write(PacketWriter var1) {
      var1.putNextLong(this.ownerAuth);
   }
}
