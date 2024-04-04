package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementOpenWorkZoneConfigEvent extends ContainerEvent {
   public final int uniqueID;
   public final Packet configPacket;

   public SettlementOpenWorkZoneConfigEvent(SettlementWorkZone var1) {
      this.uniqueID = var1.getUniqueID();
      this.configPacket = new Packet();
      var1.writeSettingsForm(new PacketWriter(this.configPacket));
   }

   public SettlementOpenWorkZoneConfigEvent(PacketReader var1) {
      super(var1);
      this.uniqueID = var1.getNextInt();
      this.configPacket = var1.getNextContentPacket();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
      var1.putNextContentPacket(this.configPacket);
   }
}
