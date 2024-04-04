package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;

public class SettlementSettlerDietChangedEvent extends ContainerEvent {
   public final int mobUniqueID;
   public final ItemCategoriesFilterChange change;

   public SettlementSettlerDietChangedEvent(int var1, ItemCategoriesFilterChange var2) {
      this.mobUniqueID = var1;
      this.change = var2;
   }

   public SettlementSettlerDietChangedEvent(PacketReader var1) {
      super(var1);
      this.mobUniqueID = var1.getNextInt();
      this.change = ItemCategoriesFilterChange.fromPacket(var1);
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.mobUniqueID);
      this.change.write(var1);
   }
}
