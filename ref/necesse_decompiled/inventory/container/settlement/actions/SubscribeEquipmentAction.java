package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SubscribeEquipmentAction extends EventSubscribeEmptyCustomAction {
   public final SettlementContainer container;

   public SubscribeEquipmentAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementNewSettlerEquipmentFilterChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementSettlerEquipmentFilterChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            (new SettlementNewSettlerEquipmentFilterChangedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
         }
      }

   }
}
