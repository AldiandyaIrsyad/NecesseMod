package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerDietsData extends SettlementSettlerData {
   public final ItemCategoriesFilter dietFilter;

   public SettlementSettlerDietsData(LevelSettler var1) {
      super(var1);
      this.dietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
      this.dietFilter.loadFromCopy(var1.dietFilter);
   }

   public SettlementSettlerDietsData(PacketReader var1) {
      super(var1);
      this.dietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
      this.dietFilter.readPacket(var1);
   }

   public void writeContentPacket(PacketWriter var1) {
      super.writeContentPacket(var1);
      this.dietFilter.writePacket(var1);
   }
}
