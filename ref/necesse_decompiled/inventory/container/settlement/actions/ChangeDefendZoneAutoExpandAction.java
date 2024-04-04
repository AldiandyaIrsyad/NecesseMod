package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDefendZoneAutoExpandEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeDefendZoneAutoExpandAction extends SettlementAccessRequiredContainerCustomAction {
   public ChangeDefendZoneAutoExpandAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextBoolean(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      var2.autoExpandDefendZone = var1.getNextBoolean();
      var2.sendEvent(SettlementDefendZoneAutoExpandEvent.class);
   }
}
