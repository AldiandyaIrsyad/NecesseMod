package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;

public class SettlementRestrictZoneRenameEvent extends ContainerEvent {
   public final int restrictZoneUniqueID;
   public final GameMessage name;

   public SettlementRestrictZoneRenameEvent(RestrictZone var1) {
      this.restrictZoneUniqueID = var1.uniqueID;
      this.name = var1.name;
   }

   public SettlementRestrictZoneRenameEvent(PacketReader var1) {
      super(var1);
      this.restrictZoneUniqueID = var1.getNextInt();
      this.name = GameMessage.fromPacket(var1);
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.restrictZoneUniqueID);
      this.name.writePacket(var1);
   }
}
