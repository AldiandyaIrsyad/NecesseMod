package necesse.inventory.container.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class AdventurePartyChangedEvent extends ContainerEvent {
   public AdventurePartyChangedEvent() {
   }

   public AdventurePartyChangedEvent(PacketReader var1) {
      super(var1);
   }

   public void write(PacketWriter var1) {
   }
}
