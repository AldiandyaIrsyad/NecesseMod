package necesse.inventory.recipe;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerRecipe;
import necesse.inventory.item.ItemCategory;

public class RecipeFilter {
   private final LinkedList<FilterMonitor> monitors = new LinkedList();
   private final HashSet<String> categoryFilters = new HashSet();
   private boolean showHidden;
   private boolean craftableOnly;
   private String searchFilter = "";

   public RecipeFilter(boolean var1) {
      this.showHidden = var1;
   }

   public Supplier<Boolean> addMonitor(Object var1) {
      Objects.requireNonNull(var1);
      FilterMonitor var2 = new FilterMonitor(var1);
      this.monitors.add(var2);
      return () -> {
         boolean var1 = var2.hasUpdated;
         var2.hasUpdated = false;
         return var1;
      };
   }

   public void removeMonitor(Object var1) {
      this.monitors.removeIf((var1x) -> {
         return var1x.list.equals(var1);
      });
   }

   public void addCategoryFilter(String var1) {
      if (this.categoryFilters.add(var1)) {
         this.updateAll();
      }

   }

   public void removeCategoryFilter(String var1) {
      if (this.categoryFilters.remove(var1)) {
         this.updateAll();
      }

   }

   public boolean containsCategoryFilter(String var1) {
      return this.categoryFilters.contains(var1);
   }

   public void setShowHidden(boolean var1) {
      if (this.showHidden != var1) {
         this.showHidden = var1;
         this.updateAll();
      }

   }

   public boolean showHidden() {
      return this.showHidden;
   }

   public void setCraftableOnly(boolean var1) {
      if (this.craftableOnly != var1) {
         this.craftableOnly = var1;
         this.updateAll();
      }

   }

   public boolean craftableOnly() {
      return this.craftableOnly;
   }

   public void setSearchFilter(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      if (!var1.equals(this.searchFilter)) {
         this.searchFilter = var1;
         this.updateAll();
      }

   }

   public String getSearchFilter() {
      return this.searchFilter;
   }

   public boolean isValid(Recipe var1, boolean var2, boolean var3) {
      if (!this.categoryFilters.isEmpty() && this.categoryFilters.stream().noneMatch((var1x) -> {
         return ItemCategory.getItemsCategory(var1.resultItem.item).isOrHasParent(var1x);
      })) {
         return false;
      } else if (!this.showHidden && !var2) {
         return false;
      } else if (this.craftableOnly && !var3) {
         return false;
      } else {
         return this.searchFilter.isEmpty() || var1.resultItem.item.matchesSearch(var1.resultItem, (PlayerMob)null, this.searchFilter);
      }
   }

   public List<ContainerRecipe> getFilteredRecipes(List<ContainerRecipe> var1, Container var2) {
      return (List)var1.stream().filter((var2x) -> {
         if (!this.categoryFilters.isEmpty() && this.categoryFilters.stream().noneMatch((var1) -> {
            return ItemCategory.getItemsCategory(var2x.recipe.resultItem.item).isOrHasParent(var1);
         })) {
            return false;
         } else if (!this.showHidden && var2 != null && !var2.doesShowRecipe(var2x.recipe, var2.getCraftInventories())) {
            return false;
         } else if (this.craftableOnly && var2 != null && !var2.canCraftRecipe(var2x.recipe, var2.getCraftInventories(), false).canCraft()) {
            return false;
         } else {
            return this.searchFilter.isEmpty() || var2x.recipe.resultItem.item.matchesSearch(var2x.recipe.resultItem, (PlayerMob)null, this.searchFilter);
         }
      }).collect(Collectors.toList());
   }

   private void updateAll() {
      this.monitors.forEach((var0) -> {
         var0.hasUpdated = true;
      });
   }

   private static class FilterMonitor {
      public final Object list;
      private boolean hasUpdated = true;

      public FilterMonitor(Object var1) {
         this.list = var1;
      }
   }
}
