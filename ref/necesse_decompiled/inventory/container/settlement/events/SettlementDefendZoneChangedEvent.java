package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementDefendZoneChangedEvent extends ContainerEvent {
   public final ZoningChange change;

   public SettlementDefendZoneChangedEvent(ZoningChange var1) {
      this.change = var1;
   }

   public SettlementDefendZoneChangedEvent(PacketReader var1) {
      super(var1);
      this.change = ZoningChange.fromPacket(var1);
   }

   public void write(PacketWriter var1) {
      this.change.write(var1);
   }
}
