package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RequestFullRestrictAction extends SettlementAccessRequiredContainerCustomAction {
   public RequestFullRestrictAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend() {
      this.runAndSendAction(new Packet());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClient(var3);
   }
}
