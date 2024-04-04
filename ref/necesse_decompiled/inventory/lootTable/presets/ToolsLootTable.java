package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class ToolsLootTable extends LootTable {
   public static final LootTable tools = new LootTable(new LootItemInterface[]{new LootItem("woodpickaxe"), new LootItem("copperpickaxe"), new LootItem("ironpickaxe"), new LootItem("goldpickaxe"), new LootItem("frostpickaxe"), new LootItem("demonicpickaxe"), new LootItem("ivypickaxe"), new LootItem("tungstenpickaxe"), new LootItem("glacialpickaxe"), new LootItem("icepickaxe"), new LootItem("myceliumpickaxe"), new LootItem("ancientfossilpickaxe"), new LootItem("woodaxe"), new LootItem("copperaxe"), new LootItem("ironaxe"), new LootItem("goldaxe"), new LootItem("frostaxe"), new LootItem("demonicaxe"), new LootItem("ivyaxe"), new LootItem("tungstenaxe"), new LootItem("glacialaxe"), new LootItem("myceliumaxe"), new LootItem("ancientfossilaxe"), new LootItem("woodshovel"), new LootItem("coppershovel"), new LootItem("ironshovel"), new LootItem("goldshovel"), new LootItem("frostshovel"), new LootItem("demonicshovel"), new LootItem("ivyshovel"), new LootItem("tungstenshovel"), new LootItem("glacialshovel"), new LootItem("myceliumshovel"), new LootItem("ancientfossilshovel")});
   public static final ToolsLootTable instance = new ToolsLootTable();

   private ToolsLootTable() {
      super(tools);
   }
}
