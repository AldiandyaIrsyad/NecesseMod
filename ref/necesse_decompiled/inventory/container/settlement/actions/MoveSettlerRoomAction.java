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

public class MoveSettlerRoomAction extends ContainerCustomAction {
   public final SettlementContainer container;

   public MoveSettlerRoomAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2, int var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var1);
      var5.putNextInt(var2);
      var5.putNextInt(var3);
      this.runAndSendAction(var4);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      int var4 = var1.getNextInt();
      if (this.container.client.isServer()) {
         SettlementLevelData var5 = this.container.getLevelData();
         if (var5 != null) {
            ServerClient var6 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var6)) {
               (new SettlementBasicsEvent(var5)).applyAndSendToClient(var6);
               return;
            }

            if (var5.moveSettler(var4, var2, var3, var6)) {
               var5.sendEvent(SettlementSettlersChangedEvent.class);
            }
         }
      }

   }
}
