package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;

public class ConditionLootItemList extends LootItemList {
   public final BiFunction<GameRandom, Object[], Boolean> condition;

   public ConditionLootItemList(BiFunction<GameRandom, Object[], Boolean> var1, LootItemInterface... var2) {
      super(var2);
      this.condition = var1;
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      if ((Boolean)this.condition.apply(var2, var4)) {
         super.addItems(var1, var2, var3, var4);
      }

   }
}
