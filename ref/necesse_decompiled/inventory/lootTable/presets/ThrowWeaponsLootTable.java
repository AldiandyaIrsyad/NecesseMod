package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class ThrowWeaponsLootTable extends LootTable {
   public static final LootTable throwWeapons = new LootTable(new LootItemInterface[]{new LootItem("woodboomerang"), new LootItem("spiderboomerang"), new LootItem("frostboomerang"), new LootItem("voidboomerang"), new LootItem("razorbladeboomerang"), new LootItem("tungstenboomerang"), new LootItem("boxingglovegun"), new LootItem("glacialboomerang"), new LootItem("carapacedagger"), new LootItem("dragonsrebound")});
   public static final ThrowWeaponsLootTable instance = new ThrowWeaponsLootTable();

   private ThrowWeaponsLootTable() {
      super(throwWeapons);
   }
}
