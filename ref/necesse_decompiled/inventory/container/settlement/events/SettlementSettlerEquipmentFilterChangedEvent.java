package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;

public class SettlementSettlerEquipmentFilterChangedEvent extends ContainerEvent {
   public final int mobUniqueID;
   public final boolean preferArmorSets;
   public final ItemCategoriesFilterChange change;

   public SettlementSettlerEquipmentFilterChangedEvent(int var1, boolean var2, ItemCategoriesFilterChange var3) {
      this.mobUniqueID = var1;
      this.change = var3;
      this.preferArmorSets = var2;
   }

   public SettlementSettlerEquipmentFilterChangedEvent(PacketReader var1) {
      super(var1);
      this.mobUniqueID = var1.getNextInt();
      this.preferArmorSets = var1.getNextBoolean();
      if (var1.getNextBoolean()) {
         this.change = ItemCategoriesFilterChange.fromPacket(var1);
      } else {
         this.change = null;
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.mobUniqueID);
      var1.putNextBoolean(this.preferArmorSets);
      var1.putNextBoolean(this.change != null);
      if (this.change != null) {
         this.change.write(var1);
      }

   }
}
