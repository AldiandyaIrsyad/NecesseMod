package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;

public class ConditionLootItem extends LootItem {
   public final BiFunction<GameRandom, Object[], Boolean> condition;

   public ConditionLootItem(String var1, Function<GameRandom, Integer> var2, GNDItemMap var3, BiFunction<GameRandom, Object[], Boolean> var4) {
      super(var1, var2, var3);
      this.condition = var4;
   }

   public ConditionLootItem(String var1, Function<GameRandom, Integer> var2, BiFunction<GameRandom, Object[], Boolean> var3) {
      super(var1, var2);
      this.condition = var3;
   }

   public ConditionLootItem(String var1, int var2, GNDItemMap var3, BiFunction<GameRandom, Object[], Boolean> var4) {
      super(var1, var2, var3);
      this.condition = var4;
   }

   public ConditionLootItem(String var1, int var2, BiFunction<GameRandom, Object[], Boolean> var3) {
      super(var1, var2);
      this.condition = var3;
   }

   public ConditionLootItem(String var1, GNDItemMap var2, BiFunction<GameRandom, Object[], Boolean> var3) {
      super(var1, var2);
      this.condition = var3;
   }

   public ConditionLootItem(String var1, BiFunction<GameRandom, Object[], Boolean> var2) {
      super(var1);
      this.condition = var2;
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      if ((Boolean)this.condition.apply(var2, var4)) {
         super.addItems(var1, var2, var3, var4);
      }

   }

   public static ConditionLootItem between(String var0, int var1, int var2, BiFunction<GameRandom, Object[], Boolean> var3) {
      return new ConditionLootItem(var0, (var2x) -> {
         return var2x.getIntBetween(var1, var2);
      }, var3);
   }

   public static ConditionLootItem offset(String var0, int var1, int var2, BiFunction<GameRandom, Object[], Boolean> var3) {
      return new ConditionLootItem(var0, (var2x) -> {
         return var2x.getIntOffset(var1, var2);
      }, var3);
   }
}
