package necesse.inventory.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.GameRegistry;
import necesse.engine.util.GameUtils;

public class ItemCategoryManager {
   private final HashMap<Integer, ItemCategory> itemIDCategory = new HashMap();
   private final ArrayList<ItemCategory> categories = new ArrayList();
   public final ItemCategory masterCategory;

   public ItemCategoryManager(ItemCategory var1) {
      this.masterCategory = var1;
      this.masterCategory.manager = this;
      this.categories.add(var1);
   }

   public ItemCategory createCategory(String var1, String... var2) {
      return this.createCategory(var1, new LocalMessage("itemcategory", var2[var2.length - 1]), var2);
   }

   public ItemCategory createCategory(String var1, GameMessage var2, String... var3) {
      if (var3.length == 0) {
         throw new IllegalArgumentException("Must have at least one category");
      } else {
         ItemCategory var4 = this.masterCategory;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            String var6 = var3[var5];
            ItemCategory var7 = (ItemCategory)var4.children.get(var6);
            if (var5 == var3.length - 1) {
               if (var7 != null) {
                  GameLog.debug.println("Tried to create duplicate item category: " + GameUtils.join(var3, "."));
                  return var7;
               }

               if (!GameRegistry.validStringID(var6)) {
                  throw new IllegalArgumentException("Item category with stringID \"" + var6 + "\" is not a valid stringID");
               }

               ItemCategory var8 = new ItemCategory(this.categories.size(), var6, var4, var1, var2);
               var8.manager = this;
               this.categories.add(var8);
               var4.children.put(var6, var8);
               return var8;
            }

            if (var7 == null) {
               throw new IllegalStateException("Must first create " + GameUtils.join(Arrays.copyOfRange(var3, 0, var5 + 1), ".") + " item category " + GameUtils.join(var3, "."));
            }

            var4 = var7;
         }

         return var4;
      }
   }

   public ItemCategory getCategory(String... var1) {
      ItemCategory var2 = this.masterCategory;
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var2 = (ItemCategory)var2.children.get(var6);
         if (var2 == null) {
            throw new IllegalStateException("Must first create item category " + GameUtils.join(var1, "."));
         }
      }

      return var2;
   }

   public ItemCategory getCategory(int var1) {
      return var1 >= 0 && var1 < this.categories.size() ? (ItemCategory)this.categories.get(var1) : null;
   }

   public ItemCategory setItemCategory(Item var1, String... var2) {
      return this.setItemCategory(var1, this.getCategory(var2));
   }

   public ItemCategory setItemCategory(Item var1, ItemCategory var2) {
      this.itemIDCategory.compute(var1.getID(), (var2x, var3) -> {
         if (var3 != null) {
            var3.itemIDs.remove(var2x);

            while(var3 != null) {
               var3.thisOrChildrenItemIDs.remove(var2x);
               var3 = var3.parent;
            }
         }

         var2.itemIDs.put(var2x, var1);

         for(ItemCategory var4 = var2; var4 != null; var4 = var4.parent) {
            var4.thisOrChildrenItemIDs.put(var2x, var1);
         }

         return var2;
      });
      this.itemIDCategory.put(var1.getID(), var2);
      return var2;
   }

   public ItemCategory getItemsCategory(Item var1) {
      return (ItemCategory)this.itemIDCategory.get(var1.getID());
   }
}
