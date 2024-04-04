package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeRestrictZoneAction extends SettlementAccessRequiredContainerCustomAction {
   public ChangeRestrictZoneAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, ZoningChange var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var2.write(var4);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      ZoningChange var5 = ZoningChange.fromPacket(var1);
      RestrictZone var6 = var2.getRestrictZone(var4);
      if (var6 != null) {
         if (var6.applyChange(var5)) {
            (new SettlementRestrictZoneChangedEvent(var4, var5)).applyAndSendToClientsAtExcept(var3);
         }
      } else {
         (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClient(var3);
      }

   }
}
