package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class LootItemList extends ArrayList<LootItemInterface> implements LootItemInterface {
   public LootItemList(LootItemInterface... var1) {
      this.addAll(Arrays.asList(var1));
   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         LootItemInterface var4 = (LootItemInterface)var3.next();
         var4.addPossibleLoot(var1, var2);
      }

   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      Iterator var5 = this.iterator();

      while(var5.hasNext()) {
         LootItemInterface var6 = (LootItemInterface)var5.next();
         var6.addItems(var1, var2, var3, var4);
      }

   }
}
