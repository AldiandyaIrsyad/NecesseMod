package necesse.inventory.container.settlement.actions;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class BanishSettlerAction extends IntCustomAction {
   public final SettlementContainer container;

   public BanishSettlerAction(SettlementContainer var1) {
      this.container = var1;
   }

   protected void run(int var1) {
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            ServerClient var3 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var3)) {
               (new SettlementBasicsEvent(var2)).applyAndSendToClient(var3);
               return;
            }

            LevelSettler var4 = var2.getSettler(var1);
            if (var4 != null && var4.canBanish()) {
               if (var2.removeSettler(var1, var3)) {
                  var2.sendEvent(SettlementSettlersChangedEvent.class);
               }
            } else {
               (new SettlementSettlersChangedEvent(var2)).applyAndSendToClient(var3);
            }
         }
      }

   }
}
