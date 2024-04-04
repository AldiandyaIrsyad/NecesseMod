package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionSummonWeaponsLootTable extends LootTable {
   public static final LootTable incursionSummonWeapons = new LootTable(new LootItemInterface[]{new LootItem("orbofslimes"), new LootItem("phantomcaller"), new LootItem("empresscommand")});
   public static final IncursionSummonWeaponsLootTable instance = new IncursionSummonWeaponsLootTable();

   private IncursionSummonWeaponsLootTable() {
      super(incursionSummonWeapons);
   }
}
