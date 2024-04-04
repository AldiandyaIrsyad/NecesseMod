package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GreatbowWeaponsLootTable extends LootTable {
   public static final LootTable greatbowWeapons = new LootTable(new LootItemInterface[]{new LootItem("goldgreatbow"), new LootItem("voidgreatbow"), new LootItem("ivygreatbow"), new LootItem("tungstengreatbow"), new LootItem("myceliumgreatbow"), new LootItem("druidsgreatbow")});
   public static final GreatbowWeaponsLootTable instance = new GreatbowWeaponsLootTable();

   private GreatbowWeaponsLootTable() {
      super(greatbowWeapons);
   }
}
