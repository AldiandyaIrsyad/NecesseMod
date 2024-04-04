package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;

public class SubscribePrioritiesAction extends EventSubscribeEmptyCustomAction {
   public final SettlementContainer container;

   public SubscribePrioritiesAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementSettlerPrioritiesChangedEvent.class, (var0) -> {
         return true;
      }, var1);
   }
}
