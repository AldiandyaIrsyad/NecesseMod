package necesse.inventory.lootTable.lootItem;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;

public class LootItem implements LootItemInterface {
   public final String itemStringID;
   public final Function<GameRandom, Integer> amountSupplier;
   public final GNDItemMap itemGNDData;
   protected int minItemsPerStack;
   protected int maxSplitStacks;
   protected boolean preventLootMultiplier;

   public LootItem(String var1, Function<GameRandom, Integer> var2, GNDItemMap var3) {
      this.minItemsPerStack = 1;
      this.maxSplitStacks = 1;
      Objects.requireNonNull(var1);
      this.itemStringID = var1;
      Objects.requireNonNull(var2);
      this.amountSupplier = var2;
      this.itemGNDData = var3;
   }

   public LootItem(String var1, Function<GameRandom, Integer> var2) {
      this(var1, var2, (GNDItemMap)null);
   }

   public LootItem(String var1, int var2, GNDItemMap var3) {
      this(var1, (var1x) -> {
         return var2;
      }, var3);
   }

   public LootItem(String var1, int var2) {
      this(var1, var2, (GNDItemMap)null);
   }

   public LootItem(String var1, GNDItemMap var2) {
      this(var1, 1, var2);
   }

   public LootItem(String var1) {
      this(var1, 1);
   }

   public InventoryItem getItem(GameRandom var1) {
      Item var2 = ItemRegistry.getItem(this.itemStringID);
      if (var2 == null) {
         System.err.println("Could not find loot item with stringID " + this.itemStringID);
         return null;
      } else {
         InventoryItem var3 = var2.getDefaultLootItem(var1, (Integer)this.amountSupplier.apply(var1));
         if (this.itemGNDData != null) {
            var3.setGndData(this.itemGNDData.copy());
         }

         return var3;
      }
   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      var1.add(this.itemStringID);
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      if (this.preventLootMultiplier) {
         var3 = 1.0F;
      }

      InventoryItem var5 = this.getItem(var2);
      if (var5 != null) {
         int var6 = LootTable.getLootAmount(var2, var5.getAmount(), var3);
         if (var6 > 0) {
            if (this.maxSplitStacks > 1) {
               int var7 = Math.max(Math.min(this.maxSplitStacks, var6 / this.minItemsPerStack), 1);
               int var8 = var6 / var7;
               int var9 = var6 - var8 * var7;

               for(int var10 = 0; var10 < var7; ++var10) {
                  int var11 = var8 + (var10 < var9 ? 1 : 0);
                  var1.add(var5.copy(var11));
               }
            } else {
               var1.add(var5.copy(var6));
            }
         }

      }
   }

   public LootItem preventLootMultiplier() {
      this.preventLootMultiplier = true;
      return this;
   }

   public LootItem splitItems(int var1, int var2) {
      if (var1 < 1) {
         throw new IllegalArgumentException("minItemsPerStack must be more than one");
      } else if (var2 < 1) {
         throw new IllegalArgumentException("maxSplitStacks must be more than one");
      } else {
         this.minItemsPerStack = var1;
         this.maxSplitStacks = var2;
         return this;
      }
   }

   public LootItem splitItems(int var1) {
      return this.splitItems(this.minItemsPerStack, var1);
   }

   public static LootItem between(String var0, int var1, int var2, GNDItemMap var3) {
      return new LootItem(var0, (var2x) -> {
         return var2x.getIntBetween(var1, var2);
      }, var3);
   }

   public static LootItem between(String var0, int var1, int var2) {
      return between(var0, var1, var2, (GNDItemMap)null);
   }

   public static LootItem offset(String var0, int var1, int var2, GNDItemMap var3) {
      return new LootItem(var0, (var2x) -> {
         return var2x.getIntOffset(var1, var2);
      }, var3);
   }

   public static LootItem offset(String var0, int var1, int var2) {
      return offset(var0, var1, var2, (GNDItemMap)null);
   }

   public String toString() {
      return "LootItem@" + Integer.toHexString(this.hashCode()) + "[" + this.itemStringID + "]";
   }
}
