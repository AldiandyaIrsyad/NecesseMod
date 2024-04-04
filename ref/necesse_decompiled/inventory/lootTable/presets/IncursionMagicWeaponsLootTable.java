package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionMagicWeaponsLootTable extends LootTable {
   public static final LootTable incursionMagicWeapons = new LootTable(new LootItemInterface[]{new LootItem("slimestaff"), new LootItem("phantompopper"), new LootItem("bloodgrimoire"), new LootItem("webweaver")});
   public static final IncursionMagicWeaponsLootTable instance = new IncursionMagicWeaponsLootTable();

   private IncursionMagicWeaponsLootTable() {
      super(incursionMagicWeapons);
   }
}
