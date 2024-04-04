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

public class DeepCaveRuinsLootTable {
   public static final RotationLootItem deepSeeds1 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("onionseed", 2, 6), LootItem.between("onion", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("pumpkinseed", 2, 6), LootItem.between("pumpkin", 4, 12)}));
   public static final RotationLootItem deepSeeds2 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("pumpkinseed", 2, 6), LootItem.between("pumpkin", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("strawberryseed", 2, 6), LootItem.between("strawberry", 4, 12)}));
   public static final RotationLootItem deepSeeds3 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("pumpkinseed", 2, 6), LootItem.between("pumpkin", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("strawberryseed", 2, 6), LootItem.between("strawberry", 4, 12)}));
   public static final RotationLootItem deepSeeds4 = RotationLootItem.presetRotation(new LootItemList(new LootItemInterface[]{LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("strawberryseed", 2, 6), LootItem.between("strawberry", 4, 12)}), new LootItemList(new LootItemInterface[]{LootItem.between("coffeebeans", 2, 6), LootItem.between("coffeebeans", 4, 12)}));
   public static final LootTable basicDeepItems;
   public static final LootTable snowDeepItems;
   public static final LootTable swampDeepItems;
   public static final LootTable desertDeepItems;
   public static final LootTable extraItems;
   public static final LootTable basicDeepChest;
   public static final LootTable snowDeepChest;
   public static final LootTable swampDeepChest;
   public static final LootTable desertDeepChest;

   public DeepCaveRuinsLootTable() {
   }

   static {
      basicDeepItems = new LootTable(new LootItemInterface[]{deepSeeds1, DeepCrateLootTable.basicDeepCrate, DeepCrateLootTable.basicDeepCrate});
      snowDeepItems = new LootTable(new LootItemInterface[]{deepSeeds2, DeepCrateLootTable.snowDeepCrate, DeepCrateLootTable.snowDeepCrate});
      swampDeepItems = new LootTable(new LootItemInterface[]{deepSeeds3, DeepCrateLootTable.swampDeepCrate, DeepCrateLootTable.swampDeepCrate});
      desertDeepItems = new LootTable(new LootItemInterface[]{deepSeeds4, DeepCrateLootTable.desertDeepCrate, DeepCrateLootTable.desertDeepCrate});
      extraItems = new LootTable(new LootItemInterface[]{ChanceLootItem.offset(0.5F, "torch", 10, 5), new ChanceLootItemList(0.5F, new LootItemInterface[]{new OneOfTicketLootItems(new Object[]{3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3)})}), new ChanceLootItemList(0.2F, new LootItemInterface[]{LootTablePresets.oldVinylsLootTable}), new ChanceLootItem(0.2F, "enchantingscroll")});
      basicDeepChest = new LootTable(new LootItemInterface[]{basicDeepItems, extraItems, new ChanceLootItem(0.2F, "shadowgate")});
      snowDeepChest = new LootTable(new LootItemInterface[]{snowDeepItems, extraItems, new ChanceLootItem(0.2F, "icecrown")});
      swampDeepChest = new LootTable(new LootItemInterface[]{swampDeepItems, extraItems, new ChanceLootItem(0.2F, "decayingleaf")});
      desertDeepChest = new LootTable(new LootItemInterface[]{desertDeepItems, extraItems, new ChanceLootItem(0.2F, "dragonsouls")});
   }
}
