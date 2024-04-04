package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;

public class TrinketsLootTable extends LootTable {
   public static final LootTable trinkets = new LootTable();
   public static final TrinketsLootTable instance = new TrinketsLootTable();

   private TrinketsLootTable() {
      super(trinkets);
   }
}
