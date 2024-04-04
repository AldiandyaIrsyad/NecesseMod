package necesse.level.maps.biomes;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;

public class FishLootOptions {
   private final FishingLootTable lootTable;
   private final int tickets;
   private Predicate<FishingSpot> isValid = (var0) -> {
      return true;
   };

   public FishLootOptions(FishingLootTable var1, int var2) {
      this.lootTable = var1;
      this.tickets = var2;
   }

   public FishLootOptions filter(Predicate<FishingSpot> var1) {
      this.isValid = this.isValid.and(var1);
      return this;
   }

   public FishLootOptions onlyTile(String var1) {
      this.isValid = this.isValid.and((var1x) -> {
         return var1x.tile.tile.getStringID().equals(var1);
      });
      return this;
   }

   public FishLootOptions onlyWater() {
      return this.onlyTile("watertile");
   }

   public FishLootOptions onlyLava() {
      return this.onlyTile("lavatile");
   }

   @SafeVarargs
   public final FishLootOptions onlyBiomes(Class<? extends Biome>... var1) {
      return this.filter((var1x) -> {
         return Arrays.stream(var1).anyMatch((var1xx) -> {
            return var1xx.isInstance(var1x.tile.level.biome);
         });
      });
   }

   public FishLootOptions filterBait(Predicate<BaitItem> var1) {
      return this.filter((var1x) -> {
         return var1.test(var1x.bait);
      });
   }

   public FishLootOptions filterFishingRod(Predicate<FishingRodItem> var1) {
      return this.filter((var1x) -> {
         return var1.test(var1x.fishingRod);
      });
   }

   public FishLootOptions onlySaltWater() {
      return this.onlyWater().filter((var0) -> {
         return var0.tile.level.liquidManager.isSaltWater(var0.tile.tileX, var0.tile.tileY);
      });
   }

   public FishLootOptions onlyFreshWater() {
      return this.onlyWater().filter((var0) -> {
         return var0.tile.level.liquidManager.isFreshWater(var0.tile.tileX, var0.tile.tileY);
      });
   }

   public FishLootOptions minDepth(int var1) {
      this.isValid = this.isValid.and((var1x) -> {
         return var1x.tile.level.liquidManager.getHeight(var1x.tile.tileX, var1x.tile.tileY) <= -var1;
      });
      return this;
   }

   public FishLootOptions maxDepth(int var1) {
      this.isValid = this.isValid.and((var1x) -> {
         return var1x.tile.level.liquidManager.getHeight(var1x.tile.tileX, var1x.tile.tileY) >= -var1;
      });
      return this;
   }

   public FishingLootTable end(BiFunction<FishingSpot, GameRandom, InventoryItem> var1) {
      return this.lootTable.add(this.tickets, this.isValid, var1);
   }

   public FishingLootTable end(String var1) {
      return this.end((var1x, var2) -> {
         return ItemRegistry.getItem(var1).getDefaultLootItem(var2, 1);
      });
   }
}
