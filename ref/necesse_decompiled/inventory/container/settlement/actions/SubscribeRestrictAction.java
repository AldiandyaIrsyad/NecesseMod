package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDefendZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerRestrictZoneChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SubscribeRestrictAction extends EventSubscribeEmptyCustomAction {
   public final SettlementContainer container;

   public SubscribeRestrictAction(SettlementContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementRestrictZonesFullEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementNewSettlerRestrictZoneChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementSettlerRestrictZoneChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementRestrictZoneChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementRestrictZoneRenameEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementRestrictZoneRecolorEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementDefendZoneChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      if (this.container.client.isServer()) {
         SettlementLevelData var2 = this.container.getLevelData();
         if (var2 != null) {
            (new SettlementRestrictZonesFullEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            (new SettlementNewSettlerRestrictZoneChangedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            (new SettlementDefendZoneChangedEvent(ZoningChange.full(var2.getDefendZone()))).applyAndSendToClient(this.container.client.getServerClient());
         }
      }

   }
}
