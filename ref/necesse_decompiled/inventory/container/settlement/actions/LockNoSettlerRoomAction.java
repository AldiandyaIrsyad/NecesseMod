package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class LockNoSettlerRoomAction extends ContainerCustomAction {
   public final SettlementContainer container;

   public LockNoSettlerRoomAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextInt(var2);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      if (this.container.client.isServer()) {
         SettlementLevelData var4 = this.container.getLevelData();
         if (var4 != null) {
            ServerClient var5 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var5)) {
               (new SettlementBasicsEvent(var4)).applyAndSendToClient(var5);
               return;
            }

            if (var4.lockNoSettler(var2, var3, var5)) {
               var4.sendEvent(SettlementSettlersChangedEvent.class);
            }
         }
      }

   }
}
