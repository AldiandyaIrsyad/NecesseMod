package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RenameRestrictZoneAction extends SettlementAccessRequiredContainerCustomAction {
   public RenameRestrictZoneAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, String var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextString(var2);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      String var5 = var1.getNextString();
      if (!var5.isEmpty()) {
         RestrictZone var6 = var2.getRestrictZone(var4);
         if (var6 != null) {
            if (var5.length() > 30) {
               var5 = var5.substring(0, 30);
            }

            var6.name = new StaticMessage(var5);
            (new SettlementRestrictZoneRenameEvent(var6)).applyAndSendToClientsAtExcept(var3);
         } else {
            (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClient(var3);
         }

      }
   }
}
