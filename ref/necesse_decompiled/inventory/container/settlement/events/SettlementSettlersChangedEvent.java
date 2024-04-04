package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementSettlersChangedEvent extends ContainerEvent {
   public SettlementSettlersChangedEvent(SettlementLevelData var1) {
   }

   public SettlementSettlersChangedEvent(PacketReader var1) {
      super(var1);
   }

   public void write(PacketWriter var1) {
   }
}
