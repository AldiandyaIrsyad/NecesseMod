package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class LootItemMultiplierIgnored implements LootItemInterface {
   protected LootItemInterface child;

   public LootItemMultiplierIgnored(LootItemInterface var1) {
      this.child = var1;
   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      this.child.addPossibleLoot(var1, var2);
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      this.child.addItems(var1, var2, 1.0F, var4);
   }
}
