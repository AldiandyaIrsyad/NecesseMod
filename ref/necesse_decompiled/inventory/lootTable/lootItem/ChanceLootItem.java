package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.function.Function;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class ChanceLootItem extends LootItem {
   public final float chance;

   public ChanceLootItem(float var1, String var2, Function<GameRandom, Integer> var3, GNDItemMap var4) {
      super(var2, var3, var4);
      this.chance = var1;
   }

   public ChanceLootItem(float var1, String var2, Function<GameRandom, Integer> var3) {
      super(var2, var3);
      this.chance = var1;
   }

   public ChanceLootItem(float var1, String var2, int var3, GNDItemMap var4) {
      super(var2, var3, var4);
      this.chance = var1;
   }

   public ChanceLootItem(float var1, String var2, int var3) {
      super(var2, var3);
      this.chance = var1;
   }

   public ChanceLootItem(float var1, String var2, GNDItemMap var3) {
      super(var2, var3);
      this.chance = var1;
   }

   public ChanceLootItem(float var1, String var2) {
      super(var2);
      this.chance = var1;
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      LootTable.runChance(var2, this.chance, var3, (var4x) -> {
         super.addItems(var1, var2, var4x, var4);
      });
   }

   public static ChanceLootItem between(float var0, String var1, int var2, int var3) {
      return new ChanceLootItem(var0, var1, (var2x) -> {
         return var2x.getIntBetween(var2, var3);
      });
   }

   public static ChanceLootItem offset(float var0, String var1, int var2, int var3) {
      return new ChanceLootItem(var0, var1, (var2x) -> {
         return var2x.getIntOffset(var2, var3);
      });
   }
}
