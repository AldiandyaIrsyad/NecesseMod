package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerEquipmentFilterData extends SettlementSettlerData {
   public boolean preferArmorSets;
   public final ItemCategoriesFilter equipmentFilter;

   public SettlementSettlerEquipmentFilterData(LevelSettler var1) {
      super(var1);
      this.equipmentFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
      this.preferArmorSets = var1.preferArmorSets;
      this.equipmentFilter.loadFromCopy(var1.equipmentFilter);
   }

   public SettlementSettlerEquipmentFilterData(PacketReader var1) {
      super(var1);
      this.equipmentFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
      this.preferArmorSets = var1.getNextBoolean();
      this.equipmentFilter.readPacket(var1);
   }

   public void writeContentPacket(PacketWriter var1) {
      super.writeContentPacket(var1);
      var1.putNextBoolean(this.preferArmorSets);
      this.equipmentFilter.writePacket(var1);
   }
}
