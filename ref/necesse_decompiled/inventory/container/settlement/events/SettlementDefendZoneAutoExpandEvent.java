package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementDefendZoneAutoExpandEvent extends ContainerEvent {
   public final boolean active;

   public SettlementDefendZoneAutoExpandEvent(SettlementLevelData var1) {
      this.active = var1.autoExpandDefendZone;
   }

   public SettlementDefendZoneAutoExpandEvent(PacketReader var1) {
      super(var1);
      this.active = var1.getNextBoolean();
   }

   public void write(PacketWriter var1) {
      var1.putNextBoolean(this.active);
   }
}
