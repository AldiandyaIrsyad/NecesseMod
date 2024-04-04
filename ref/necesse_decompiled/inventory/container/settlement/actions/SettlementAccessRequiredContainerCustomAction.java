package necesse.inventory.container.settlement.actions;

import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public abstract class SettlementAccessRequiredContainerCustomAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public SettlementAccessRequiredContainerCustomAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public final void executePacket(PacketReader var1) {
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            ServerClient var3 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var3)) {
               (new SettlementBasicsEvent(var2)).applyAndSendToClient(var3);
               return;
            }

            this.executePacket(var1, var2, var3);
         }
      }

   }

   public abstract void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3);
}
