package necesse.inventory.container.settlement;

import necesse.engine.network.NetworkClient;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public abstract class SettlementDependantContainer extends Container {
   public SettlementDependantContainer(NetworkClient var1, int var2) {
      super(var1, var2);
      this.subscribeEvent(SettlementBasicsEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
   }

   public SettlementLevelData getLevelData() {
      if (!this.client.isServer()) {
         throw new IllegalStateException("Cannot get level data client side");
      } else {
         return SettlementLevelData.getSettlementData(this.getLevel());
      }
   }

   public SettlementLevelLayer getLevelLayer() {
      return this.getLevel().settlementLayer;
   }

   protected abstract Level getLevel();
}
