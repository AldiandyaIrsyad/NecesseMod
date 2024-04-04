package necesse.inventory.container.settlement.actions;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangePrivacyAction extends BooleanCustomAction {
   public final SettlementContainer container;

   public ChangePrivacyAction(SettlementContainer var1) {
      this.container = var1;
   }

   protected void run(boolean var1) {
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            ServerClient var3 = this.container.client.getServerClient();
            boolean var4 = this.container.basics.isOwner(var3);
            if (!var4) {
               (new SettlementBasicsEvent(var2)).applyAndSendToClient(var3);
               return;
            }

            SettlementLevelLayer var5 = this.container.getLevelLayer();
            var5.setPrivate(var1);
            if (var5.getOwnerAuth() != var3.authentication) {
               var5.setOwner(var3);
            }

            var2.sendEvent(SettlementBasicsEvent.class);
         }
      }

   }
}
