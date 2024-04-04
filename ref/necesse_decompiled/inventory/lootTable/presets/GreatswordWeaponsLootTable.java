package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GreatswordWeaponsLootTable extends LootTable {
   public static final LootTable greatswordWeapons = new LootTable(new LootItemInterface[]{new LootItem("frostgreatsword"), new LootItem("quartzgreatsword"), new LootItem("irongreatsword"), new LootItem("ivygreatsword"), new LootItem("glacialgreatsword")});
   public static final GreatswordWeaponsLootTable instance = new GreatswordWeaponsLootTable();

   private GreatswordWeaponsLootTable() {
      super(greatswordWeapons);
   }
}
