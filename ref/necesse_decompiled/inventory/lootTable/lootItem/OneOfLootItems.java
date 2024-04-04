package necesse.inventory.lootTable.lootItem;

import java.util.Arrays;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;

public class OneOfLootItems extends LootItemList {
   public OneOfLootItems(LootItemInterface var1, LootItemInterface... var2) {
      super();
      this.add(var1);
      this.addAll(Arrays.asList(var2));
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      ((LootItemInterface)var2.getOneOf((List)this)).addItems(var1, var2, var3, var4);
   }
}
