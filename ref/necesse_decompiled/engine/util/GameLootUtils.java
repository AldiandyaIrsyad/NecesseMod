package necesse.engine.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.level.maps.Level;

public class GameLootUtils {
   public static LootValueMap<InventoryItem> inventoryItemMapper = new LootValueMap<InventoryItem>() {
      public float getValuePerCount(InventoryItem var1) {
         return var1.item.getBrokerValue(var1);
      }

      public int getRemainingCount(InventoryItem var1) {
         return var1.getAmount();
      }

      public void setRemainingCount(InventoryItem var1, int var2) {
         var1.setAmount(var2);
      }

      public boolean canCombine(InventoryItem var1, InventoryItem var2) {
         return var1.canCombine((Level)null, (PlayerMob)null, var2, "lootcombine");
      }

      public void onCombine(InventoryItem var1, InventoryItem var2) {
         var1.item.onCombine((Level)null, (PlayerMob)null, (Inventory)null, 0, var1, var2, Integer.MAX_VALUE, var2.getAmount(), false, "lootcombine", (InventoryAddConsumer)null);
      }

      public InventoryItem copy(InventoryItem var1, int var2) {
         return var1.copy(var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public Object copy(Object var1, int var2) {
         return this.copy((InventoryItem)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onCombine(Object var1, Object var2) {
         this.onCombine((InventoryItem)var1, (InventoryItem)var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public boolean canCombine(Object var1, Object var2) {
         return this.canCombine((InventoryItem)var1, (InventoryItem)var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void setRemainingCount(Object var1, int var2) {
         this.setRemainingCount((InventoryItem)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public int getRemainingCount(Object var1) {
         return this.getRemainingCount((InventoryItem)var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public float getValuePerCount(Object var1) {
         return this.getValuePerCount((InventoryItem)var1);
      }
   };

   public GameLootUtils() {
   }

   private static Item[] convertStringIDs(String... var0) {
      Item[] var1 = new Item[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = ItemRegistry.getItem(var0[var2]);
      }

      return var1;
   }

   private static InventoryItem[] convertItems(Item... var0) {
      InventoryItem[] var1 = new InventoryItem[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = new InventoryItem(var0[var2]);
      }

      return var1;
   }

   public static ArrayList<InventoryItem> getItemsValuedAt(GameRandom var0, int var1, double var2, LootItemInterface var4, Object... var5) {
      ArrayList var6 = new ArrayList();
      var4.addItems(var6, var0, 1.0F, var5);
      ArrayList var7 = new ArrayList();

      boolean var8;
      do {
         var8 = false;
         ArrayList var9 = new ArrayList(var6);

         while(!var9.isEmpty()) {
            int var10 = var9.size();
            InventoryItem var11 = (InventoryItem)var9.remove(var0.nextInt(var10));
            if (var11.getAmount() <= 0) {
               var6.remove(var11);
            } else {
               InventoryItem var12 = var11.copy(1);
               double var13 = var0.getDoubleBetween(1.0 - var2, 1.0 + var2) / (double)var10 * (double)var1;
               int var15 = Math.min((int)(var13 / (double)Math.max(var12.getBrokerValue(), 0.01F)), var11.getAmount());
               if (var15 > 0) {
                  var8 = true;
                  InventoryItem var16 = var11.copy(var15);
                  float var17 = var16.getBrokerValue();
                  var1 = (int)((float)var1 - var17);
                  var11.setAmount(var11.getAmount() - var15);
                  addObject(var7, var16, inventoryItemMapper);
               }
            }
         }
      } while(var8);

      return var7;
   }

   public static ArrayList<InventoryItem> getItemsValuedAt(GameRandom var0, int var1, float var2, ProtectedTicketSystemList<InventoryItem> var3) {
      return getObjectsValuedAt(var0, var1, var2, var3, inventoryItemMapper);
   }

   public static <T> ArrayList<T> getObjectsValuedAt(GameRandom var0, int var1, float var2, ProtectedTicketSystemList<T> var3, LootValueMap<T> var4) {
      var2 = GameMath.limit(var2, 0.0F, 1.0F);
      ArrayList var5 = new ArrayList();

      while(!var3.isEmpty()) {
         Object var6 = var3.getRandomObject(var0);
         if (var4.getRemainingCount(var6) <= 0) {
            var3.removeObject(var6);
         } else {
            Object var7 = var4.copy(var6, 1);
            float var8 = Math.max(var4.getValuePerCount(var7) * (float)var4.getRemainingCount(var7), 0.01F);
            if (var8 > (float)var1) {
               var3.removeObject(var6);
            } else {
               int var9;
               if (var3.getTotalElements() == 1) {
                  var9 = Math.min((int)((float)var1 / var8), var4.getRemainingCount(var6));
               } else {
                  float var10 = Math.abs(var2 - 1.0F) * (float)var1;
                  var9 = Math.min((int)Math.ceil((double)(var10 / var8)), var4.getRemainingCount(var6));
               }

               if (var9 > 0) {
                  Object var12 = var4.copy(var6, var9);
                  float var11 = var4.getValuePerCount(var12) * (float)var9;
                  var1 = (int)((float)var1 - var11);
                  var4.setRemainingCount(var6, var4.getRemainingCount(var6) - var9);
                  addObject(var5, var12, var4);
               } else {
                  var3.removeObject(var6);
               }
            }
         }
      }

      return var5;
   }

   public static <T> void addObject(Collection<T> var0, T var1, LootValueMap<T> var2) {
      Iterator var3 = var0.iterator();

      Object var4;
      do {
         if (!var3.hasNext()) {
            var0.add(var1);
            return;
         }

         var4 = var3.next();
      } while(!var2.canCombine(var4, var1));

      var2.onCombine(var4, var1);
   }

   public interface LootValueMap<T> {
      float getValuePerCount(T var1);

      int getRemainingCount(T var1);

      void setRemainingCount(T var1, int var2);

      boolean canCombine(T var1, T var2);

      void onCombine(T var1, T var2);

      T copy(T var1, int var2);
   }
}
