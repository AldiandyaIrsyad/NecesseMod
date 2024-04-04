package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class PirateChestLootTable extends LootTable {
   public static final RotationLootItem mainItems = RotationLootItem.presetRotation(new LootItem("lifeline"), new LootItem("inducingamulet"), new LootItem("cutlass"), new LootItem("genielamp"));
   public static final OneOfLootItems goldItems = new OneOfLootItems(LootItem.offset("goldbar", 6, 2), new LootItemInterface[]{LootItem.offset("goldlamp", 3, 1)});
   public static final OneOfLootItems extra = new OneOfLootItems(LootItem.offset("coin", 50, 30), new LootItemInterface[]{LootItem.offset("cannonball", 25, 5), LootItem.offset("healthpotion", 10, 3), LootItem.offset("mapfragment", 3, 1), new OneOfTicketLootItems(new Object[]{3, LootItem.between("ironbomb", 5, 10), 1, LootItem.between("dynamitestick", 3, 5)}), new ChanceLootItem(0.25F, "enchantingscroll")});
   public static final RotationLootItem vinyls = RotationLootItem.presetRotation(new LootItem("awayvinyl"));
   public static final PirateChestLootTable instance = new PirateChestLootTable();

   private PirateChestLootTable() {
      super(mainItems, goldItems, extra, new ChanceLootItemList(0.25F, new LootItemInterface[]{vinyls}));
   }
}
