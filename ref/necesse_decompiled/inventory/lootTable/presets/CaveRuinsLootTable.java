package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class CaveRuinsLootTable {
   public static final RotationLootItem seeds1 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("wheatseed", 2, 6), LootItem.between("wheat", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("cornseed", 2, 6), LootItem.between("corn", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("tomatoseed", 2, 6), LootItem.between("tomato", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("cabbageseed", 2, 6), LootItem.between("cabbage", 4, 12)}));
   public static final RotationLootItem seeds2 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("tomatoseed", 2, 6), LootItem.between("tomato", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("cabbageseed", 2, 6), LootItem.between("cabbage", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("chilipepperseed", 2, 6), LootItem.between("chilipepper", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("sugarbeetseed", 2, 6), LootItem.between("sugarbeet", 4, 12)}));
   public static final RotationLootItem seeds3 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("chilipepperseed", 2, 6), LootItem.between("chilipepper", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("sugarbeetseed", 2, 6), LootItem.between("sugarbeet", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("eggplantseed", 2, 6), LootItem.between("eggplant", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("potatoseed", 2, 6), LootItem.between("potato", 4, 12)}));
   public static final RotationLootItem seeds4 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("eggplantseed", 2, 6), LootItem.between("eggplant", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("potatoseed", 2, 6), LootItem.between("potato", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("riceseed", 2, 6), LootItem.between("riceseed", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("carrotseed", 2, 6), LootItem.between("carrot", 4, 12)}));
   public static final LootTable basicItems;
   public static final LootTable snowItems;
   public static final LootTable swampItems;
   public static final LootTable desertItems;
   public static final LootTable extraItems;
   public static final LootTable basicChest;
   public static final LootTable snowChest;
   public static final LootTable desertChest;
   public static final LootTable swampChest;

   public CaveRuinsLootTable() {
   }

   static {
      basicItems = new LootTable(new LootItemInterface[]{seeds1, CrateLootTable.basicCrate, CrateLootTable.basicCrate});
      snowItems = new LootTable(new LootItemInterface[]{seeds2, CrateLootTable.snowCrate, CrateLootTable.snowCrate});
      swampItems = new LootTable(new LootItemInterface[]{seeds3, CrateLootTable.swampCrate, CrateLootTable.swampCrate});
      desertItems = new LootTable(new LootItemInterface[]{seeds4, CrateLootTable.desertCrate, CrateLootTable.desertCrate});
      extraItems = new LootTable(new LootItemInterface[]{ChanceLootItem.offset(0.5F, "torch", 10, 5), new ChanceLootItemList(0.5F, new LootItemInterface[]{new OneOfTicketLootItems(new Object[]{3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3)})}), new ChanceLootItemList(0.2F, new LootItemInterface[]{LootTablePresets.oldVinylsLootTable}), new ChanceLootItem(0.2F, "enchantingscroll")});
      basicChest = new LootTable(new LootItemInterface[]{basicItems, extraItems, new ChanceLootItem(0.2F, "mysteriousportal")});
      snowChest = new LootTable(new LootItemInterface[]{snowItems, extraItems, new ChanceLootItem(0.2F, "royalegg")});
      desertChest = new LootTable(new LootItemInterface[]{desertItems, extraItems, new ChanceLootItem(0.2F, "ancientstatue")});
      swampChest = new LootTable(new LootItemInterface[]{swampItems, extraItems, new ChanceLootItem(0.2F, "spikedfossil")});
   }
}
