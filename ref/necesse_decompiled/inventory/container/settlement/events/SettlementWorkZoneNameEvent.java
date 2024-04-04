package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementWorkZoneNameEvent extends ContainerEvent {
   public final int uniqueID;
   public final GameMessage name;

   public SettlementWorkZoneNameEvent(SettlementWorkZone var1) {
      this.uniqueID = var1.getUniqueID();
      this.name = var1.getName();
   }

   public SettlementWorkZoneNameEvent(PacketReader var1) {
      super(var1);
      this.uniqueID = var1.getNextInt();
      this.name = GameMessage.fromPacket(var1);
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
      this.name.writePacket(var1);
   }
}
