package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementRestrictZoneChangedEvent extends ContainerEvent {
   public final int restrictZoneUniqueID;
   public final ZoningChange change;

   public SettlementRestrictZoneChangedEvent(int var1, ZoningChange var2) {
      this.restrictZoneUniqueID = var1;
      this.change = var2;
   }

   public SettlementRestrictZoneChangedEvent(PacketReader var1) {
      super(var1);
      this.restrictZoneUniqueID = var1.getNextInt();
      this.change = ZoningChange.fromPacket(var1);
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.restrictZoneUniqueID);
      this.change.write(var1);
   }
}
