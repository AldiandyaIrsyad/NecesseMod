package necesse.inventory.lootTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class LootTable implements LootItemInterface {
   public final List<LootItemInterface> items;

   public LootTable() {
      this.items = new ArrayList();
   }

   public LootTable(LootItemInterface... var1) {
      this();
      this.items.addAll(Arrays.asList(var1));
   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      Iterator var5 = this.items.iterator();

      while(var5.hasNext()) {
         LootItemInterface var6 = (LootItemInterface)var5.next();
         var6.addItems(var1, var2, var3, var4);
      }

   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         LootItemInterface var4 = (LootItemInterface)var3.next();
         var4.addPossibleLoot(var1, var2);
      }

   }

   public void addItemsToInventory(GameRandom var1, float var2, Inventory var3, String var4, Object... var5) {
      ArrayList var6 = new ArrayList();
      this.addItems(var6, var1, var2, var5);
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         InventoryItem var8 = (InventoryItem)var7.next();
         var3.addItem((Level)null, (PlayerMob)null, var8, var4, (InventoryAddConsumer)null);
      }

   }

   public final ArrayList<InventoryItem> getNewList(GameRandom var1, float var2, Object... var3) {
      ArrayList var4 = new ArrayList();
      this.addItems(var4, var1, var2, var3);
      return var4;
   }

   public final void applyToLevel(GameRandom var1, float var2, Level var3, int var4, int var5, Object... var6) {
      try {
         ObjectEntity var7 = var3.entityManager.getObjectEntity(var4, var5);
         if (var7 != null && var7.implementsOEInventory()) {
            this.addItemsToInventory(var1, var2, ((OEInventory)var7).getInventory(), "addloot", var6);
         } else if (var3.isServer()) {
            throw new NullPointerException("Could not find an objectEntity with inventory for loot table at " + var4 + ", " + var5);
         }
      } catch (Exception var8) {
         System.err.println(var8.getMessage());
      }

   }

   public static <T> T expectExtra(Class<T> var0, Object[] var1, int var2) {
      if (var2 >= var1.length) {
         return null;
      } else {
         try {
            return var0.cast(var1[var2]);
         } catch (ClassCastException var4) {
            return null;
         }
      }
   }

   public static int getLootAmount(GameRandom var0, int var1, float var2) {
      float var3 = (float)var1 * var2;
      int var4 = (int)var3;
      if (var3 > (float)var4) {
         float var5 = var3 - (float)var4;
         if (var0.getChance(var5)) {
            ++var4;
         }
      }

      return var4;
   }

   public static void runChance(GameRandom var0, float var1, float var2, Consumer<Float> var3) {
      while(true) {
         if (var2 >= 1.0F) {
            if (!var0.getChance(var1)) {
               --var2;
               continue;
            }

            var3.accept(var2);
         } else if (var0.getChance(var2) && var0.getChance(var1)) {
            var3.accept(1.0F);
         }

         return;
      }
   }

   public static void runChanceTimes(GameRandom var0, float var1, float var2, Runnable var3) {
      while(var2 >= 1.0F) {
         --var2;
         if (var0.getChance(var1)) {
            var3.run();
         }
      }

      if (var0.getChance(var2) && var0.getChance(var1)) {
         var3.run();
      }

   }

   public static void runMultiplied(GameRandom var0, float var1, Runnable var2) {
      while(var1 >= 1.0F) {
         --var1;
         var2.run();
      }

      if (var0.getChance(var1)) {
         var2.run();
      }

   }
}
