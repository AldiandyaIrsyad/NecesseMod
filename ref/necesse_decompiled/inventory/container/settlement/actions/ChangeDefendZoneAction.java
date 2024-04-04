package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeDefendZoneAction extends SettlementAccessRequiredContainerCustomAction {
   public ChangeDefendZoneAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(ZoningChange var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var1.write(var3);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      ZoningChange var4 = ZoningChange.fromPacket(var1);
      var2.applyDefendZoneChange(var4);
   }
}
