package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class CloseRangeWeaponsLootTable extends LootTable {
   public static final LootTable closeRangeWeapons = new LootTable(new LootItemInterface[]{new LootItem("woodsword"), new LootItem("coppersword"), new LootItem("ironsword"), new LootItem("goldsword"), new LootItem("nunchucks"), new LootItem("heavyhammer"), new LootItem("frostsword"), new LootItem("lightninghammer"), new LootItem("demonicsword"), new LootItem("spiderclaw"), new LootItem("ivysword"), new LootItem("cutlass"), new LootItem("tungstensword"), new LootItem("reaperscythe"), new LootItem("sandknife"), new LootItem("venomslasher"), new LootItem("antiquesword")});
   public static final CloseRangeWeaponsLootTable instance = new CloseRangeWeaponsLootTable();

   private CloseRangeWeaponsLootTable() {
      super(closeRangeWeapons);
   }
}
