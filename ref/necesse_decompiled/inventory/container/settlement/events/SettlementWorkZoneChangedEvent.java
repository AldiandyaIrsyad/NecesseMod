package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;

public class SettlementWorkZoneChangedEvent extends ContainerEvent {
   public final SettlementWorkZone zone;

   public SettlementWorkZoneChangedEvent(SettlementWorkZone var1) {
      this.zone = var1;
   }

   public SettlementWorkZoneChangedEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      int var3 = var1.getNextInt();
      this.zone = SettlementWorkZoneRegistry.getNewZone(var2);
      this.zone.setUniqueID(var3);
      this.zone.applyPacket(var1);
   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.zone.getID());
      var1.putNextInt(this.zone.getUniqueID());
      this.zone.writePacket(var1);
   }
}
