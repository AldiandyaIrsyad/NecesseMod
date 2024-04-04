package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class CreateNewRestrictZoneAction extends SettlementAccessRequiredContainerCustomAction {
   public CreateNewRestrictZoneAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend() {
      this.runAndSendAction(new Packet());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      if (var2.getRestrictZones().size() < SettlementLevelData.MAX_RESTRICT_ZONES) {
         var2.addNewRestrictZone();
         (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClientsAt(var3);
      }

   }
}
