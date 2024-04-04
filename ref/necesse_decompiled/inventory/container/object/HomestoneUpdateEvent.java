package necesse.inventory.container.object;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HomestoneUpdateEvent extends ContainerEvent {
   public final Packet content;

   public HomestoneUpdateEvent(SettlementLevelData var1) {
      this.content = HomestoneContainer.getContainerContent(var1.getLevel());
   }

   public HomestoneUpdateEvent(PacketReader var1) {
      super(var1);
      this.content = var1.getNextContentPacket();
   }

   public void write(PacketWriter var1) {
      var1.putNextContentPacket(this.content);
   }
}
