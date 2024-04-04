package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionThrowWeaponsLootTable extends LootTable {
   public static final LootTable incursionThrowWeapons = new LootTable(new LootItemInterface[]{new LootItem("nightrazorboomerang")});
   public static final IncursionThrowWeaponsLootTable instance = new IncursionThrowWeaponsLootTable();

   private IncursionThrowWeaponsLootTable() {
      super(incursionThrowWeapons);
   }
}
