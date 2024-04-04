package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RequestSettlerPrioritiesAction extends SettlementAccessRequiredContainerCustomAction {
   public RequestSettlerPrioritiesAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend() {
      this.runAndSendAction(new Packet());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      (new SettlementSettlerPrioritiesEvent(var2)).applyAndSendToClient(var3);
   }
}
