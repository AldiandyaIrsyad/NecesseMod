package necesse.inventory.container.settlement.actions;

import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RenameSettlerNameAction extends ContainerCustomAction {
   public final SettlementContainer container;

   public RenameSettlerNameAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, String var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextString(var2);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      String var3 = var1.getNextString();
      if (this.container.client.isServer()) {
         SettlementLevelData var4 = this.container.getLevelData();
         if (var4 != null) {
            ServerClient var5 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var5)) {
               (new SettlementBasicsEvent(var4)).applyAndSendToClient(var5);
               return;
            }

            if (!var3.isEmpty() && var3.length() < 50) {
               var4.renameSettler(var2, var3);
            } else {
               GameLog.warn.println("Kicking player " + var5.getName() + " because they attempted to rename settler with invalid name");
               var5.getServer().disconnectClient(var5, PacketDisconnect.Code.STATE_DESYNC);
            }
         }
      }

   }
}
