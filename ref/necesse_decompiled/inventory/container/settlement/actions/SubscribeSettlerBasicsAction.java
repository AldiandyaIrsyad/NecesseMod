package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;

public class SubscribeSettlerBasicsAction extends EventSubscribeEmptyCustomAction {
   public final SettlementContainer container;

   public SubscribeSettlerBasicsAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementSettlerBasicsEvent.class, (var0) -> {
         return true;
      }, var1);
   }
}
