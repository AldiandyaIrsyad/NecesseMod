package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class BodyArmorLootTable extends LootTable {
   public static final LootTable bodyArmor = new LootTable(new LootItemInterface[]{new LootItem("leathershirt"), new LootItem("clothrobe"), new LootItem("copperchestplate"), new LootItem("ironchestplate"), new LootItem("goldchestplate"), new LootItem("spiderchestplate"), new LootItem("frostchestplate"), new LootItem("demonicchestplate"), new LootItem("voidrobe"), new LootItem("bloodplatechestplate"), new LootItem("ivychestplate"), new LootItem("quartzchestplate"), new LootItem("tungstenchestplate"), new LootItem("shadowmantle"), new LootItem("ninjarobe"), new LootItem("glacialchestplate"), new LootItem("myceliumchestplate"), new LootItem("widowchestplate"), new LootItem("ancientfossilchestplate")});
   public static final BodyArmorLootTable instance = new BodyArmorLootTable();

   private BodyArmorLootTable() {
      super(bodyArmor);
   }
}
