package necesse.inventory.recipe;

import necesse.engine.Settings;

public class CanCraft {
   private Recipe recipe;
   private int canCraft;
   private int hasAnyItems;
   public final int[] haveIngredients;
   public final boolean countAllIngredients;

   public CanCraft(Recipe var1, boolean var2) {
      this.recipe = var1;
      this.haveIngredients = new int[var1.ingredients.length];
      this.countAllIngredients = var2 && Settings.showIngredientsAvailable;
   }

   public void addIngredient(int var1, int var2) {
      Ingredient var3 = this.recipe.ingredients[var1];
      if (var3.getIngredientAmount() == 0) {
         if (this.haveIngredients[var1] == 0) {
            this.haveIngredients[var1] = -1;
            ++this.canCraft;
            ++this.hasAnyItems;
         }
      } else if (var2 > 0) {
         if (this.haveIngredients[var1] == 0) {
            ++this.hasAnyItems;
         }

         boolean var4 = this.haveIngredients[var1] >= var3.getIngredientAmount();
         int[] var10000 = this.haveIngredients;
         var10000[var1] += var2;
         boolean var5 = this.haveIngredients[var1] >= var3.getIngredientAmount();
         if (!var4 && var5) {
            ++this.canCraft;
         }
      }

   }

   public boolean hasAnyIngredients(int var1) {
      Ingredient var2 = this.recipe.ingredients[var1];
      if (var2.getIngredientAmount() == 0) {
         return this.haveIngredients[var1] == -1;
      } else {
         return this.haveIngredients[var1] > 0;
      }
   }

   public boolean canCraft() {
      return this.canCraft >= this.haveIngredients.length;
   }

   public boolean hasAnyItems() {
      return this.hasAnyItems > 0;
   }

   public boolean hasAnyOfAllItems() {
      return this.hasAnyItems >= this.haveIngredients.length;
   }

   public static CanCraft allTrue(Recipe var0) {
      CanCraft var1 = new CanCraft(var0, false);

      for(int var2 = 0; var2 < var0.ingredients.length; ++var2) {
         var1.addIngredient(var2, var0.ingredients[var2].getIngredientAmount());
      }

      return var1;
   }
}
