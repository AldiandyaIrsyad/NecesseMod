package necesse.engine;

import java.util.HashMap;
import necesse.inventory.item.ItemCategory;

public class ItemCategoryExpandedSetting {
   public final ItemCategory category;
   private boolean defaultExpanded;
   private boolean isExpanded;
   private HashMap<String, ItemCategoryExpandedSetting> children;

   private ItemCategoryExpandedSetting(ItemCategory var1, boolean var2) {
      this.children = new HashMap();
      this.category = var1;
      this.defaultExpanded = var2;
      this.isExpanded = var2;
   }

   public ItemCategoryExpandedSetting(boolean var1) {
      this(ItemCategory.masterCategory, var1);
      this.isExpanded = true;
   }

   public ItemCategoryExpandedSetting getChild(ItemCategory var1) {
      return this.category.getChild(var1.stringID) == var1 ? (ItemCategoryExpandedSetting)this.children.compute(var1.stringID, (var2, var3) -> {
         return var3 == null ? new ItemCategoryExpandedSetting(var1, this.defaultExpanded) : var3;
      }) : null;
   }

   public void setExpanded(boolean var1) {
      this.isExpanded = var1;
   }

   public boolean isExpanded() {
      return this.isExpanded;
   }

   public void setChildExpanded(ItemCategory var1, boolean var2) {
      ItemCategoryExpandedSetting var3 = this.getChild(var1);
      if (var3 != null) {
         var3.setExpanded(var2);
      }

   }

   public boolean isChildExpanded(ItemCategory var1) {
      ItemCategoryExpandedSetting var2 = this.getChild(var1);
      return var2 != null && var2.isExpanded;
   }
}
