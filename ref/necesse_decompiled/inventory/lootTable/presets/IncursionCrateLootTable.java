package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class IncursionCrateLootTable extends LootTable {
   public static final OneOfLootItems potions = new OneOfLootItems(new LootItem("attackspeedpotion"), new LootItemInterface[]{new LootItem("healthregenpotion"), new LootItem("manaregenpotion"), new LootItem("fishingpotion"), new LootItem("fireresistancepotion"), new LootItem("resistancepotion"), new LootItem("speedpotion"), new LootItem("battlepotion"), new LootItem("thornspotion"), new LootItem("accuracypotion"), new LootItem("minionpotion"), new LootItem("knockbackpotion"), new LootItem("rapidpotion"), new LootItem("treasurepotion"), new LootItem("spelunkerpotion")});
   public static final OneOfLootItems greaterPotions = new OneOfLootItems(new LootItem("greaterattackspeedpotion"), new LootItemInterface[]{new LootItem("greaterhealthregenpotion"), new LootItem("greatermanaregenpotion"), new LootItem("greaterfishingpotion"), new LootItem("fireresistancepotion"), new LootItem("greaterresistancepotion"), new LootItem("greaterspeedpotion"), new LootItem("greaterbattlepotion"), new LootItem("thornspotion"), new LootItem("greateraccuracypotion"), new LootItem("minionpotion"), new LootItem("knockbackpotion"), new LootItem("greaterrapidpotion"), new LootItem("treasurepotion"), new LootItem("spelunkerpotion")});
   public static final IncursionCrateLootTable instance = new IncursionCrateLootTable();
   public OneOfTicketLootItems oneOfItems;

   private IncursionCrateLootTable() {
      super(LootItem.offset("coin", 24, 5).splitItems(5));
      this.oneOfItems = new OneOfTicketLootItems(new Object[]{100, LootItem.offset("firearrow", 10, 5), 100, LootItem.offset("ironarrow", 10, 5), 100, LootItem.offset("bonearrow", 10, 5), 50, LootItem.between("greaterhealthpotion", 1, 1), 25, LootItem.between("greatermanapotion", 1, 2), 100, LootItem.offset("torch", 10, 3), 25, new LootItem("travelscroll"), 35, greaterPotions, 15, potions});
      this.items.add(this.oneOfItems);
   }
}
