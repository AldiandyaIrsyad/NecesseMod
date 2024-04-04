package necesse.inventory.container;

import necesse.inventory.recipe.Recipe;

public class ContainerRecipe {
   public final int id;
   public final Recipe recipe;
   public final boolean isInventory;

   public ContainerRecipe(int var1, Recipe var2, boolean var3) {
      this.recipe = var2;
      this.id = var1;
      this.isInventory = var3;
   }
}
