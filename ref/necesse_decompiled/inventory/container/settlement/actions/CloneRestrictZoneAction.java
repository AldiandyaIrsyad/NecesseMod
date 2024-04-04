package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class CloneRestrictZoneAction extends SettlementAccessRequiredContainerCustomAction {
   public CloneRestrictZoneAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      if (var2.getRestrictZones().size() < SettlementLevelData.MAX_RESTRICT_ZONES) {
         RestrictZone var5 = var2.getRestrictZone(var4);
         if (var5 != null) {
            RestrictZone var6 = var2.addNewRestrictZone();
            var6.copyZoneFrom(var5);
            (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClientsAt(var3);
         } else {
            (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClient(var3);
         }
      }

   }
}
