package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionGreatswordWeaponsLootTable extends LootTable {
   public static final LootTable incursionGreatswordWeapons = new LootTable(new LootItemInterface[]{new LootItem("slimegreatsword")});
   public static final IncursionGreatswordWeaponsLootTable instance = new IncursionGreatswordWeaponsLootTable();

   private IncursionGreatswordWeaponsLootTable() {
      super(incursionGreatswordWeapons);
   }
}
