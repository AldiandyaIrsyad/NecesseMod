package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class LootInventoryItem implements LootItemInterface {
   public final InventoryItem item;

   public LootInventoryItem(InventoryItem var1) {
      this.item = var1;
   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      var1.add(this.item.item);
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      var1.add(this.item.copy());
   }
}
