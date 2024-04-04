package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementNewSettlerDietChangedEvent extends ContainerEvent {
   public final ItemCategoriesFilterChange change;

   public SettlementNewSettlerDietChangedEvent(SettlementLevelData var1) {
      this.change = ItemCategoriesFilterChange.fullChange(var1.getNewSettlerDiet());
   }

   public SettlementNewSettlerDietChangedEvent(ItemCategoriesFilterChange var1) {
      this.change = var1;
   }

   public SettlementNewSettlerDietChangedEvent(PacketReader var1) {
      super(var1);
      this.change = ItemCategoriesFilterChange.fromPacket(var1);
   }

   public void write(PacketWriter var1) {
      this.change.write(var1);
   }
}
