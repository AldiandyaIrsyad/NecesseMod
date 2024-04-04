package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class SummonWeaponsLootTable extends LootTable {
   public static final LootTable summonWeapons = new LootTable(new LootItemInterface[]{new LootItem("brainonastick"), new LootItem("spiderstaff"), new LootItem("frostpiercer"), new LootItem("magicbranch"), new LootItem("slimecanister"), new LootItem("vulturestaff"), new LootItem("cryostaff"), new LootItem("reaperscall"), new LootItem("swampsgrasp"), new LootItem("skeletonstaff")});
   public static final SummonWeaponsLootTable instance = new SummonWeaponsLootTable();

   private SummonWeaponsLootTable() {
      super(summonWeapons);
   }
}
