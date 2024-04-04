package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class SpearWeaponsLootTable extends LootTable {
   public static final LootTable spearWeapons = new LootTable(new LootItemInterface[]{new LootItem("woodspear"), new LootItem("copperpitchfork"), new LootItem("copperspear"), new LootItem("ironspear"), new LootItem("goldspear"), new LootItem("frostspear"), new LootItem("demonicspear"), new LootItem("voidspear"), new LootItem("ivyspear"), new LootItem("vulturestalon"), new LootItem("tungstenspear"), new LootItem("cryospear")});
   public static final SpearWeaponsLootTable instance = new SpearWeaponsLootTable();

   private SpearWeaponsLootTable() {
      super(spearWeapons);
   }
}
