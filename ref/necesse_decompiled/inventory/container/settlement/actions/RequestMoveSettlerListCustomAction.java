package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RequestMoveSettlerListCustomAction extends SettlementAccessRequiredContainerCustomAction {
   public RequestMoveSettlerListCustomAction(SettlementDependantContainer var1) {
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
      (new SettlementOpenSettlementListEvent(var2, var3, var4)).applyAndSendToClient(var3);
   }
}
