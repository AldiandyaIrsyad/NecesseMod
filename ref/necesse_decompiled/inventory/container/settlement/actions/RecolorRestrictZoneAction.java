package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RecolorRestrictZoneAction extends SettlementAccessRequiredContainerCustomAction {
   public RecolorRestrictZoneAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, int var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(var2, 360);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      int var5 = var1.getNextMaxValue(360);
      RestrictZone var6 = var2.getRestrictZone(var4);
      if (var6 != null) {
         var6.colorHue = var5;
         (new SettlementRestrictZoneRecolorEvent(var6)).applyAndSendToClientsAtExcept(var3);
      } else {
         (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClient(var3);
      }

   }
}
