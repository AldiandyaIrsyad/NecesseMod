package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeNameAction extends ContainerCustomAction {
   public final SettlementContainer container;

   public ChangeNameAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void runAndSend(GameMessage var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextContentPacket(var1.getContentPacket());
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      GameMessage var2 = GameMessage.fromContentPacket(var1.getNextContentPacket());
      if (this.container.client.isServer()) {
         SettlementLevelData var3 = this.container.getLevelData();
         if (var3 != null) {
            ServerClient var4 = this.container.client.getServerClient();
            boolean var5 = this.container.basics.isOwner(var4);
            if (!var5) {
               (new SettlementBasicsEvent(var3)).applyAndSendToClient(var4);
               return;
            }

            this.container.getLevelLayer().setName(var2);
            var3.sendEvent(SettlementBasicsEvent.class);
         }
      }

   }
}
