package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class BowWeaponsLootTable extends LootTable {
   public static final LootTable bowWeapons = new LootTable(new LootItemInterface[]{new LootItem("woodbow"), new LootItem("copperbow"), new LootItem("ironbow"), new LootItem("goldbow"), new LootItem("frostbow"), new LootItem("demonicbow"), new LootItem("ivybow"), new LootItem("vulturesburst"), new LootItem("tungstenbow"), new LootItem("glacialbow"), new LootItem("bowofdualism"), new LootItem("antiquebow")});
   public static final BowWeaponsLootTable instance = new BowWeaponsLootTable();

   private BowWeaponsLootTable() {
      super(bowWeapons);
   }
}
