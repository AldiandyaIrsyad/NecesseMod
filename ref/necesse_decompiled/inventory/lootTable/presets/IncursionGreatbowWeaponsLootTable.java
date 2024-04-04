package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionGreatbowWeaponsLootTable extends LootTable {
   public static final LootTable incursionGreatbowWeapons = new LootTable(new LootItemInterface[]{new LootItem("slimegreatbow"), new LootItem("nightpiercer")});
   public static final IncursionGreatbowWeaponsLootTable instance = new IncursionGreatbowWeaponsLootTable();

   private IncursionGreatbowWeaponsLootTable() {
      super(incursionGreatbowWeapons);
   }
}
