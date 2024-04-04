package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionCloseRangeWeaponsLootTable extends LootTable {
   public static final LootTable incursionCloseRangeWeapons = new LootTable(new LootItemInterface[]{new LootItem("bloodclaw"), new LootItem("causticexecutioner")});
   public static final IncursionCloseRangeWeaponsLootTable instance = new IncursionCloseRangeWeaponsLootTable();

   private IncursionCloseRangeWeaponsLootTable() {
      super(incursionCloseRangeWeapons);
   }
}
