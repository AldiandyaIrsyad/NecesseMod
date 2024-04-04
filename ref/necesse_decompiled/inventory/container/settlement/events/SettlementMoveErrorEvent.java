package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementMoveErrorEvent extends ContainerEvent {
   public final GameMessage error;

   public SettlementMoveErrorEvent(GameMessage var1) {
      this.error = var1;
   }

   public SettlementMoveErrorEvent(PacketReader var1) {
      super(var1);
      this.error = GameMessage.fromContentPacket(var1.getNextContentPacket());
   }

   public void write(PacketWriter var1) {
      var1.putNextContentPacket(this.error.getContentPacket());
   }
}
