package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class BlacksmithChestLootTable extends LootTable {
   public static final OneOfLootItems ores = new OneOfLootItems(new LootItemList(new LootItemInterface[]{ChanceLootItem.offset(0.5F, "ironbar", 8, 2), LootItem.offset("ironore", 20, 5)}), new LootItemInterface[]{new LootItemList(new LootItemInterface[]{ChanceLootItem.offset(0.5F, "copperbar", 10, 2), LootItem.offset("copperore", 25, 5)}), new LootItemList(new LootItemInterface[]{ChanceLootItem.offset(0.5F, "goldbar", 5, 2), LootItem.offset("goldore", 15, 5)})});
   public static final BlacksmithChestLootTable instance = new BlacksmithChestLootTable();

   private BlacksmithChestLootTable() {
      super(ores, ChanceLootItem.offset(0.5F, "torch", 15, 5), ChanceLootItem.offset(0.5F, "stone", 45, 5));
   }
}
