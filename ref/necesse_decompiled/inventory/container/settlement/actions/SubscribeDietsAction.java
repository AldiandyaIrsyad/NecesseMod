package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SubscribeDietsAction extends EventSubscribeEmptyCustomAction {
   public final SettlementContainer container;

   public SubscribeDietsAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementNewSettlerDietChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementSettlerDietChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            (new SettlementNewSettlerDietChangedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
         }
      }

   }
}
