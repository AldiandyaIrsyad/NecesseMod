package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementNewSettlerEquipmentFilterChangedEvent extends ContainerEvent {
   public final boolean selfManageEquipment;
   public final boolean preferArmorSets;
   public final ItemCategoriesFilterChange change;

   public SettlementNewSettlerEquipmentFilterChangedEvent(SettlementLevelData var1) {
      this.selfManageEquipment = var1.newSettlerSelfManageEquipment;
      this.preferArmorSets = var1.newSettlerEquipmentPreferArmorSets;
      this.change = ItemCategoriesFilterChange.fullChange(var1.getNewSettlerEquipmentFilter());
   }

   public SettlementNewSettlerEquipmentFilterChangedEvent(boolean var1, boolean var2, ItemCategoriesFilterChange var3) {
      this.selfManageEquipment = var1;
      this.preferArmorSets = var2;
      this.change = var3;
   }

   public SettlementNewSettlerEquipmentFilterChangedEvent(PacketReader var1) {
      super(var1);
      this.selfManageEquipment = var1.getNextBoolean();
      this.preferArmorSets = var1.getNextBoolean();
      if (var1.getNextBoolean()) {
         this.change = ItemCategoriesFilterChange.fromPacket(var1);
      } else {
         this.change = null;
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextBoolean(this.selfManageEquipment);
      var1.putNextBoolean(this.preferArmorSets);
      var1.putNextBoolean(this.change != null);
      if (this.change != null) {
         this.change.write(var1);
      }

   }
}
