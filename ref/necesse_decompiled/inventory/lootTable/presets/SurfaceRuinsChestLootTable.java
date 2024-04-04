package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class SurfaceRuinsChestLootTable extends LootTable {
   public static final OneOfLootItems mainItem = new OneOfLootItems(new LootItem("copperpitchfork"), new LootItemInterface[]{new LootItem("nunchucks"), new LootItemList(new LootItemInterface[]{new LootItem("handgun"), LootItem.between("simplebullet", 50, 100)}), new LootItem("fuzzydice"), new LootItem("fins")});
   public static final OneOfLootItems secondItem = new OneOfLootItems(LootItem.between("healthpotion", 5, 7), new LootItemInterface[]{LootItem.between("mapfragment", 2, 4), LootItem.between("fertilizer", 20, 30), LootItem.offset("speedpotion", 2, 1), LootItem.offset("healthregenpotion", 2, 1), LootItem.offset("manaregenpotion", 2, 1), LootItem.offset("fireresistancepotion", 2, 1), LootItem.offset("attackspeedpotion", 2, 1), LootItem.between("recallscroll", 1, 2)});
   public static final SurfaceRuinsChestLootTable instance = new SurfaceRuinsChestLootTable();

   private SurfaceRuinsChestLootTable() {
      super(mainItem, secondItem, new ChanceLootItemList(0.5F, new LootItemInterface[]{new OneOfTicketLootItems(new Object[]{3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3)})}), ChanceLootItem.between(0.5F, "coin", 50, 100), ChanceLootItem.between(0.5F, "torch", 15, 25), new ChanceLootItem(0.5F, "enchantingscroll"), new ChanceLootItem(0.4F, "mysteriousportal"));
   }
}
