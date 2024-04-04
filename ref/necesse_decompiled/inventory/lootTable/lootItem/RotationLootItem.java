package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class RotationLootItem implements LootItemInterface {
   public ArrayList<LootItemInterface> items;
   public SeedGetter seedGetter;
   public Counter counter;

   public RotationLootItem(SeedGetter var1, Counter var2, LootItemInterface... var3) {
      this.items = new ArrayList(Arrays.asList(var3));
      this.seedGetter = var1;
      if (var2 == null) {
         var2 = (var0, var1x) -> {
            return var0.nextInt(Integer.MAX_VALUE);
         };
      }

      this.counter = var2;
   }

   public static RotationLootItem privateLootRotation(Function<ServerClient, Long> var0, BiFunction<Mob, ServerClient, Integer> var1, LootItemInterface... var2) {
      AtomicInteger var3 = new AtomicInteger(0);
      return new RotationLootItem((var1x, var2x) -> {
         ServerClient var3 = (ServerClient)LootTable.expectExtra(ServerClient.class, var2x, 1);
         return var3 != null ? (Long)var0.apply(var3) : (long)var1x.nextInt(Integer.MAX_VALUE);
      }, (var2x, var3x) -> {
         Mob var4 = (Mob)LootTable.expectExtra(Mob.class, var3x, 0);
         ServerClient var5 = (ServerClient)LootTable.expectExtra(ServerClient.class, var3x, 1);
         return var4 != null && var5 != null ? (Integer)var1.apply(var4, var5) : var3.getAndAdd(1);
      }, var2);
   }

   public static RotationLootItem privateLootRotation(BiFunction<Mob, ServerClient, Integer> var0, LootItemInterface... var1) {
      return privateLootRotation((var0x) -> {
         return var0x.authentication * var0x.getServer().world.getUniqueID();
      }, var0, var1);
   }

   public static RotationLootItem privateLootRotation(LootItemInterface... var0) {
      return privateLootRotation((var0x, var1) -> {
         return var1.characterStats().mob_kills.getKills(var0x.getStringID());
      }, var0);
   }

   public static RotationLootItem presetRotation(LootItemInterface... var0) {
      return new RotationLootItem((var0x, var1) -> {
         Level var2 = (Level)LootTable.expectExtra(Level.class, var1, 0);
         return var2 != null ? var2.getSeed() : (long)var0x.nextInt(Integer.MAX_VALUE);
      }, (var0x, var1) -> {
         AtomicInteger var2 = (AtomicInteger)LootTable.expectExtra(AtomicInteger.class, var1, 1);
         return var2 != null ? var2.getAndAdd(1) : var0x.nextInt(Integer.MAX_VALUE);
      }, var0);
   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         LootItemInterface var4 = (LootItemInterface)var3.next();
         var4.addPossibleLoot(var1, var2);
      }

   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      LootTable.runMultiplied(var2, var3, () -> {
         if (!this.items.isEmpty()) {
            long var4x = this.seedGetter == null ? 0L : this.seedGetter.get(var2, var4);
            int var6 = this.counter.get(var2, var4);
            int var7 = Math.abs(Math.abs((int)var4x) + var6) % this.items.size();
            long var8 = var4x * (long)(var6 / this.items.size());
            ArrayList var10 = new ArrayList(this.items);
            if (var4x != 0L) {
               Collections.shuffle(var10, new Random(var8));
            }

            ((LootItemInterface)var10.get(var7)).addItems(var1, var2, 1.0F, var4);
         }
      });
   }

   public interface SeedGetter {
      long get(Random var1, Object... var2);
   }

   public interface Counter {
      int get(Random var1, Object... var2);
   }
}
