package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementNewSettlerRestrictZoneChangedEvent extends ContainerEvent {
   public final int restrictZoneUniqueID;

   public SettlementNewSettlerRestrictZoneChangedEvent(SettlementLevelData var1) {
      this.restrictZoneUniqueID = var1.getNewSettlerRestrictZoneUniqueID();
   }

   public SettlementNewSettlerRestrictZoneChangedEvent(PacketReader var1) {
      super(var1);
      this.restrictZoneUniqueID = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.restrictZoneUniqueID);
   }
}
