package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionFeetArmorLootTable extends LootTable {
   public static final LootTable incursionFeetArmor = new LootTable(new LootItemInterface[]{new LootItem("slimeboots"), new LootItem("nightsteelboots"), new LootItem("spideritegreaves")});
   public static final IncursionFeetArmorLootTable instance = new IncursionFeetArmorLootTable();

   private IncursionFeetArmorLootTable() {
      super(incursionFeetArmor);
   }
}
