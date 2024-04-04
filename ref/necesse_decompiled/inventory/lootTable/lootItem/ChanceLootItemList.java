package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;

public class ChanceLootItemList extends LootItemList {
   public final float chance;

   public ChanceLootItemList(float var1) {
      super();
      this.chance = var1;
   }

   public ChanceLootItemList(float var1, LootItemInterface... var2) {
      super(var2);
      this.chance = var1;
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      LootTable.runChance(var2, this.chance, var3, (var4x) -> {
         super.addItems(var1, var2, var4x, var4);
      });
   }
}
