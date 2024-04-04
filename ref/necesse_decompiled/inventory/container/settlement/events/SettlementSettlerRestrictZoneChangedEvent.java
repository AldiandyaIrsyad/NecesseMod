package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerRestrictZoneChangedEvent extends ContainerEvent {
   public final int mobUniqueID;
   public final int restrictZoneUniqueID;

   public SettlementSettlerRestrictZoneChangedEvent(LevelSettler var1) {
      this.mobUniqueID = var1.mobUniqueID;
      this.restrictZoneUniqueID = var1.getRestrictZoneUniqueID();
   }

   public SettlementSettlerRestrictZoneChangedEvent(PacketReader var1) {
      super(var1);
      this.mobUniqueID = var1.getNextInt();
      this.restrictZoneUniqueID = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.mobUniqueID);
      var1.putNextInt(this.restrictZoneUniqueID);
   }
}
