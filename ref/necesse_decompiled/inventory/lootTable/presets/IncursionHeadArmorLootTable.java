package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionHeadArmorLootTable extends LootTable {
   public static final LootTable incursionHeadArmor = new LootTable(new LootItemInterface[]{new LootItem("slimehat"), new LootItem("slimehelmet"), new LootItem("nightsteelhelmet"), new LootItem("nightsteelmask"), new LootItem("nightsteelveil"), new LootItem("nightsteelcirclet"), new LootItem("spideritehelmet"), new LootItem("spideritehat"), new LootItem("spideritehood"), new LootItem("spideritecrown")});
   public static final IncursionHeadArmorLootTable instance = new IncursionHeadArmorLootTable();

   private IncursionHeadArmorLootTable() {
      super(incursionHeadArmor);
   }
}
