package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionBodyArmorLootTable extends LootTable {
   public static final LootTable incursionBodyArmor = new LootTable(new LootItemInterface[]{new LootItem("slimechestplate"), new LootItem("nightsteelchestplate"), new LootItem("spideritechestplate")});
   public static final IncursionBodyArmorLootTable instance = new IncursionBodyArmorLootTable();

   private IncursionBodyArmorLootTable() {
      super(incursionBodyArmor);
   }
}
