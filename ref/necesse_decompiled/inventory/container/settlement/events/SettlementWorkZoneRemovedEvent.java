package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementWorkZoneRemovedEvent extends ContainerEvent {
   public final int uniqueID;

   public SettlementWorkZoneRemovedEvent(int var1) {
      this.uniqueID = var1;
   }

   public SettlementWorkZoneRemovedEvent(PacketReader var1) {
      super(var1);
      this.uniqueID = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
   }
}
