package necesse.level.maps.biomes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;

public class FishingLootTable {
   private final LinkedList<FishingLootTable> includes = new LinkedList();
   private final LinkedList<FishLoot> table = new LinkedList();

   public FishingLootTable(FishingLootTable var1) {
      this.addAll(var1);
   }

   public FishingLootTable() {
   }

   public FishingLootTable addAll(FishingLootTable var1) {
      this.includes.add(var1);
      return this;
   }

   public FishingLootTable clear() {
      this.includes.clear();
      this.table.clear();
      return this;
   }

   public FishingLootTable add(int var1, Predicate<FishingSpot> var2, BiFunction<FishingSpot, GameRandom, InventoryItem> var3) {
      this.table.add(new FishLoot(var1, var2, var3));
      return this;
   }

   public FishLootOptions startCustom(int var1) {
      return new FishLootOptions(this, var1);
   }

   public FishingLootTable add(int var1, Predicate<FishingSpot> var2, String var3) {
      return this.startCustom(var1).filter(var2).end(var3);
   }

   public FishingLootTable addWater(int var1, String var2) {
      return this.startCustom(var1).onlyWater().end(var2);
   }

   public FishingLootTable addWater(int var1, BiFunction<FishingSpot, GameRandom, InventoryItem> var2) {
      return this.startCustom(var1).onlyWater().end(var2);
   }

   public FishingLootTable addFreshWater(int var1, String var2) {
      return this.startCustom(var1).onlyFreshWater().end(var2);
   }

   public FishingLootTable addSaltWater(int var1, String var2) {
      return this.startCustom(var1).onlySaltWater().end(var2);
   }

   public InventoryItem getRandomItem(FishingSpot var1, GameRandom var2) {
      int var3 = 0;
      LinkedList var4 = new LinkedList();
      var3 = this.addValidLoot(var4, var3, var1);
      if (var3 <= 0) {
         return null;
      } else {
         int var5 = var2.nextInt(var3);
         int var6 = 0;

         FishLoot var8;
         for(Iterator var7 = var4.iterator(); var7.hasNext(); var6 += var8.tickets) {
            var8 = (FishLoot)var7.next();
            if (var5 >= var6 && var5 < var6 + var8.tickets) {
               return (InventoryItem)var8.itemProducer.apply(var1, var2);
            }
         }

         return null;
      }
   }

   private int addValidLoot(LinkedList<FishLoot> var1, int var2, FishingSpot var3) {
      Iterator var4;
      FishingLootTable var5;
      for(var4 = this.includes.iterator(); var4.hasNext(); var2 = var5.addValidLoot(var1, var2, var3)) {
         var5 = (FishingLootTable)var4.next();
      }

      var4 = this.table.iterator();

      while(var4.hasNext()) {
         FishLoot var6 = (FishLoot)var4.next();
         if (var6.isValid.test(var3)) {
            var1.add(var6);
            var2 += var6.tickets;
         }
      }

      return var2;
   }

   public static InventoryItem getRandomItem(FishingSpot var0, GameRandom var1, List<FishingLootTable> var2) {
      FishingLootTable var3 = new FishingLootTable();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         FishingLootTable var5 = (FishingLootTable)var4.next();
         var3.addAll(var5);
      }

      return var3.getRandomItem(var0, var1);
   }

   public static InventoryItem getRandomItem(FishingSpot var0, GameRandom var1, FishingLootTable... var2) {
      return getRandomItem(var0, var1, Arrays.asList(var2));
   }

   private static class FishLoot {
      public final int tickets;
      public final Predicate<FishingSpot> isValid;
      public final BiFunction<FishingSpot, GameRandom, InventoryItem> itemProducer;

      public FishLoot(int var1, Predicate<FishingSpot> var2, BiFunction<FishingSpot, GameRandom, InventoryItem> var3) {
         if (var1 <= 0) {
            throw new IllegalArgumentException("Tickets must be above 0");
         } else {
            this.tickets = var1;
            this.isValid = var2;
            this.itemProducer = var3;
         }
      }
   }
}
