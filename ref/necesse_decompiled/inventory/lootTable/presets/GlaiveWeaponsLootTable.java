package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GlaiveWeaponsLootTable extends LootTable {
   public static final LootTable glaiveWeapons = new LootTable(new LootItemInterface[]{new LootItem("goldglaive"), new LootItem("frostglaive"), new LootItem("quartzglaive"), new LootItem("cryoglaive")});
   public static final GlaiveWeaponsLootTable instance = new GlaiveWeaponsLootTable();

   private GlaiveWeaponsLootTable() {
      super(glaiveWeapons);
   }
}
