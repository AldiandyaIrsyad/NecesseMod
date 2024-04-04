package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class DeepCrateLootTable extends LootTable {
   public static final OneOfLootItems basicBars = new OneOfLootItems(LootItem.between("obsidian", 3, 4), new LootItemInterface[]{LootItem.between("tungstenbar", 3, 4), LootItem.between("lifequartz", 2, 3)});
   public static final OneOfLootItems snowBars = new OneOfLootItems(LootItem.between("tungstenbar", 3, 4), new LootItemInterface[]{LootItem.between("lifequartz", 2, 3), LootItem.between("glacialbar", 2, 3)});
   public static final OneOfLootItems swampBars = new OneOfLootItems(LootItem.between("tungstenbar", 3, 4), new LootItemInterface[]{LootItem.between("lifequartz", 2, 3), LootItem.between("myceliumbar", 2, 3)});
   public static final OneOfLootItems desertBars = new OneOfLootItems(LootItem.between("tungstenbar", 3, 4), new LootItemInterface[]{LootItem.between("lifequartz", 2, 3), LootItem.between("ancientfossilbar", 2, 3)});
   public static final OneOfLootItems potions = new OneOfLootItems(new LootItem("attackspeedpotion"), new LootItemInterface[]{new LootItem("healthregenpotion"), new LootItem("manaregenpotion"), new LootItem("fishingpotion"), new LootItem("fireresistancepotion"), new LootItem("resistancepotion"), new LootItem("speedpotion"), new LootItem("battlepotion"), new LootItem("thornspotion"), new LootItem("accuracypotion"), new LootItem("minionpotion"), new LootItem("knockbackpotion"), new LootItem("rapidpotion"), new LootItem("treasurepotion"), new LootItem("spelunkerpotion")});
   public static final DeepCrateLootTable basicDeepCrate;
   public static final DeepCrateLootTable snowDeepCrate;
   public static final DeepCrateLootTable swampDeepCrate;
   public static final DeepCrateLootTable desertDeepCrate;
   public OneOfTicketLootItems oneOfItems;

   private DeepCrateLootTable(LootItemInterface var1) {
      super(LootItem.offset("coin", 18, 5).splitItems(5));
      this.oneOfItems = new OneOfTicketLootItems(new Object[]{100, LootItem.offset("firearrow", 10, 5), 100, LootItem.offset("ironarrow", 10, 5), 100, LootItem.offset("bonearrow", 10, 5), 50, LootItem.between("greaterhealthpotion", 1, 1), 25, LootItem.between("greatermanapotion", 1, 2), 100, LootItem.offset("torch", 7, 3), 25, new LootItem("travelscroll"), 50, var1, 50, potions});
      this.items.add(this.oneOfItems);
   }

   static {
      basicDeepCrate = new DeepCrateLootTable(basicBars);
      snowDeepCrate = new DeepCrateLootTable(snowBars);
      swampDeepCrate = new DeepCrateLootTable(swampBars);
      desertDeepCrate = new DeepCrateLootTable(desertBars);
   }
}
