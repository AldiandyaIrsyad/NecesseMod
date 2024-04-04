package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class MagicWeaponsLootTable extends LootTable {
   public static final LootTable magicWeapons = new LootTable(new LootItemInterface[]{new LootItem("woodstaff"), new LootItem("sparkler"), new LootItem("bloodbolt"), new LootItem("venomstaff"), new LootItem("froststaff"), new LootItem("bloodvolley"), new LootItem("voidstaff"), new LootItem("voidmissile"), new LootItem("swamptome"), new LootItem("boulderstaff"), new LootItem("quartzstaff"), new LootItem("dredgingstaff"), new LootItem("genielamp"), new LootItem("elderlywand"), new LootItem("shadowbolt"), new LootItem("iciclestaff"), new LootItem("shadowbeam"), new LootItem("cryoquake"), new LootItem("swampdwellerstaff"), new LootItem("venomshower"), new LootItem("ancientdredgingstaff"), new LootItem("dragonlance")});
   public static final MagicWeaponsLootTable instance = new MagicWeaponsLootTable();

   private MagicWeaponsLootTable() {
      super(magicWeapons);
   }
}
