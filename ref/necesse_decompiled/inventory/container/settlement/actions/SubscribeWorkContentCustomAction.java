package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZonesEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationsEvent;

public class SubscribeWorkContentCustomAction extends EventSubscribeEmptyCustomAction {
   public final SettlementDependantContainer container;

   public SubscribeWorkContentCustomAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void onSubscribed(BooleanSupplier var1) {
      this.container.subscribeEvent(SettlementStorageEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementSingleStorageEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementWorkstationsEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementSingleWorkstationsEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementWorkZoneRemovedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementWorkZoneChangedEvent.class, (var0) -> {
         return true;
      }, var1);
      this.container.subscribeEvent(SettlementWorkZoneNameEvent.class, (var0) -> {
         return true;
      }, var1);
      if (this.container.client.isServer()) {
         (new SettlementStorageEvent(this.container.getLevelData())).applyAndSendToClient(this.container.client.getServerClient());
         (new SettlementWorkstationsEvent(this.container.getLevelData())).applyAndSendToClient(this.container.client.getServerClient());
         (new SettlementWorkZonesEvent(this.container.getLevelData())).applyAndSendToClient(this.container.client.getServerClient());
      }

   }
}
