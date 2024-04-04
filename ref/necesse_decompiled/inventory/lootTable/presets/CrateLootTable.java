package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class CrateLootTable extends LootTable {
   public static final OneOfLootItems basicBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), new LootItemInterface[]{LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 1)});
   public static final OneOfLootItems snowBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), new LootItemInterface[]{LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("frostshard", 1, 2)});
   public static final OneOfLootItems swampBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), new LootItemInterface[]{LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("ivybar", 1, 2)});
   public static final OneOfLootItems desertBars = new OneOfLootItems(LootItem.between("ironbar", 1, 2), new LootItemInterface[]{LootItem.between("copperbar", 1, 2), LootItem.between("goldbar", 1, 2), LootItem.between("quartz", 1, 2)});
   public static final OneOfLootItems potions = new OneOfLootItems(new LootItem("attackspeedpotion"), new LootItemInterface[]{new LootItem("healthregenpotion"), new LootItem("manaregenpotion"), new LootItem("fishingpotion"), new LootItem("fireresistancepotion"), new LootItem("resistancepotion"), new LootItem("speedpotion"), new LootItem("battlepotion"), new LootItem("thornspotion"), new LootItem("accuracypotion"), new LootItem("knockbackpotion"), new LootItem("rapidpotion")});
   public static final CrateLootTable basicCrate;
   public static final CrateLootTable snowCrate;
   public static final CrateLootTable swampCrate;
   public static final CrateLootTable desertCrate;
   public OneOfTicketLootItems oneOfItems;

   private CrateLootTable(LootItemInterface var1) {
      super(LootItem.offset("coin", 12, 5).splitItems(5));
      this.oneOfItems = new OneOfTicketLootItems(new Object[]{100, LootItem.offset("stonearrow", 10, 5), 100, LootItem.offset("firearrow", 10, 5), 100, LootItem.offset("ironarrow", 10, 5), 75, LootItem.between("healthpotion", 1, 2), 50, LootItem.between("manapotion", 1, 2), 100, LootItem.offset("ninjastar", 8, 3), 100, LootItem.offset("torch", 7, 3), 25, new LootItem("recallscroll"), 50, var1, 50, potions});
      this.items.add(this.oneOfItems);
   }

   static {
      basicCrate = new CrateLootTable(basicBars);
      snowCrate = new CrateLootTable(snowBars);
      swampCrate = new CrateLootTable(swampBars);
      desertCrate = new CrateLootTable(desertBars);
   }
}
