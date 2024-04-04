package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDefendZoneAutoExpandEvent;
import necesse.inventory.container.settlement.events.SettlementDefendZoneChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SubscribeDefendZoneAction extends EventSubscribeEmptyCustomAction {
   public final SettlementContainer container;

   public SubscribeDefendZoneAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementDefendZoneChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            (new SettlementDefendZoneAutoExpandEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            (new SettlementDefendZoneChangedEvent(ZoningChange.full(var2.getDefendZone()))).applyAndSendToClient(this.container.client.getServerClient());
         }
      }

   }
}
