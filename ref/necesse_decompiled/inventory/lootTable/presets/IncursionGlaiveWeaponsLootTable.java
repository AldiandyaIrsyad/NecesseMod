package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionGlaiveWeaponsLootTable extends LootTable {
   public static final LootTable incursionGlaiveWeapons = new LootTable(new LootItemInterface[]{new LootItem("slimeglaive")});
   public static final IncursionGlaiveWeaponsLootTable instance = new IncursionGlaiveWeaponsLootTable();

   private IncursionGlaiveWeaponsLootTable() {
      super(incursionGlaiveWeapons);
   }
}
