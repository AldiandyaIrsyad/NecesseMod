package necesse.inventory.lootTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;

public interface LootItemInterface {
   void addPossibleLoot(LootList var1, Object... var2);

   default void addPossibleCustomLoot(LootList var1, Object... var2) {
      ArrayList var3 = new ArrayList();
      this.addItems(var3, GameRandom.globalRandom, 1.0F, var2);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         InventoryItem var5 = (InventoryItem)var4.next();
         var1.addCustom(var5);
      }

   }

   void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4);
}
