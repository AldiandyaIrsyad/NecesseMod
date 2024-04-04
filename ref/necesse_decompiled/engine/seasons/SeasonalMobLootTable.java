package necesse.engine.seasons;

import java.util.ArrayList;
import java.util.function.Supplier;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class SeasonalMobLootTable {
   public Supplier<Boolean> isActive;
   public LootTable lootTable;

   public SeasonalMobLootTable(Supplier<Boolean> var1, LootTable var2) {
      this.isActive = var1;
      this.lootTable = var2;
   }

   public void addDrops(Mob var1, ArrayList<InventoryItem> var2, GameRandom var3, float var4) {
      this.lootTable.addItems(var2, var3, var4, var1);
   }
}
