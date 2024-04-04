package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;

public class SettlementRestrictZoneRecolorEvent extends ContainerEvent {
   public final int restrictZoneUniqueID;
   public final int hue;

   public SettlementRestrictZoneRecolorEvent(RestrictZone var1) {
      this.restrictZoneUniqueID = var1.uniqueID;
      this.hue = var1.colorHue;
   }

   public SettlementRestrictZoneRecolorEvent(PacketReader var1) {
      super(var1);
      this.restrictZoneUniqueID = var1.getNextInt();
      this.hue = var1.getNextMaxValue(360);
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.restrictZoneUniqueID);
      var1.putNextMaxValue(this.hue, 360);
   }
}
