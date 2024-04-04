package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class SettlementHusbandryZoneUpdateEvent extends ContainerEvent {
   public final int uniqueID;
   public final int maxAnimalsBeforeSlaughter;

   public SettlementHusbandryZoneUpdateEvent(SettlementHusbandryZone var1) {
      this.uniqueID = var1.getUniqueID();
      this.maxAnimalsBeforeSlaughter = var1.getMaxAnimalsBeforeSlaughter();
   }

   public SettlementHusbandryZoneUpdateEvent(PacketReader var1) {
      super(var1);
      this.uniqueID = var1.getNextInt();
      this.maxAnimalsBeforeSlaughter = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
      var1.putNextInt(this.maxAnimalsBeforeSlaughter);
   }
}
