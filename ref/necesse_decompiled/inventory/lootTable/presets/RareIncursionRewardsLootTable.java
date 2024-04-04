package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class RareIncursionRewardsLootTable extends LootTable {
   public static final LootTable rareIncursionRewards = new LootTable(new LootItemInterface[]{new LootItem("ravenlordsheaddress"), new LootItem("ravenwinggreatsword"), new LootItem("theravensnest")});
   public static final RareIncursionRewardsLootTable instance = new RareIncursionRewardsLootTable();

   private RareIncursionRewardsLootTable() {
      super(rareIncursionRewards);
   }
}
