package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class FeetArmorLootTable extends LootTable {
   public static final LootTable feetArmor = new LootTable(new LootItemInterface[]{new LootItem("leatherboots"), new LootItem("clothboots"), new LootItem("copperboots"), new LootItem("ironboots"), new LootItem("goldboots"), new LootItem("spiderboots"), new LootItem("frostboots"), new LootItem("demonicboots"), new LootItem("voidboots"), new LootItem("bloodplateboots"), new LootItem("ivyboots"), new LootItem("quartzboots"), new LootItem("tungstenboots"), new LootItem("shadowboots"), new LootItem("ninjashoes"), new LootItem("glacialboots"), new LootItem("myceliumboots"), new LootItem("widowboots"), new LootItem("ancientfossilboots")});
   public static final FeetArmorLootTable instance = new FeetArmorLootTable();

   private FeetArmorLootTable() {
      super(feetArmor);
   }
}
