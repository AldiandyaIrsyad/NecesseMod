package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;

public class CountOfLootItems extends OneOfLootItems {
   protected int count;

   public CountOfLootItems(int var1, LootItemInterface var2, LootItemInterface... var3) {
      super(var2, var3);
      this.count = var1;
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      ArrayList var5 = new ArrayList(this);

      for(int var6 = 0; var6 < this.count; ++var6) {
         if (var5.isEmpty()) {
            return;
         }

         ((LootItemInterface)var5.remove(var2.nextInt(var5.size()))).addItems(var1, var2, var3, var4);
      }

   }
}
