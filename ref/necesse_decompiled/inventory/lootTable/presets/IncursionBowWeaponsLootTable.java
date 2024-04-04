package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class IncursionBowWeaponsLootTable extends LootTable {
   public static final LootTable incursionBowWeapons = new LootTable(new LootItemInterface[]{new LootItem("thecrimsonsky"), new LootItem("arachnidwebbow")});
   public static final IncursionBowWeaponsLootTable instance = new IncursionBowWeaponsLootTable();

   private IncursionBowWeaponsLootTable() {
      super(incursionBowWeapons);
   }
}
