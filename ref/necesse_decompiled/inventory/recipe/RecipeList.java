package necesse.inventory.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.HashMapArrayList;

public class RecipeList {
   private ArrayList<Recipe> recipes;
   private HashMapArrayList<String, Recipe> techRecipes;
   private boolean[] itemIsCraftingMat;
   private HashSet<Tech>[] ingredientTechs;
   private List<Recipe>[] ingredientResultItems;
   private int hash = 0;

   public RecipeList() {
      this.loadDefaultRecipes();
   }

   RecipeList(RecipeList var1) {
      this.recipes = new ArrayList(var1.recipes);
      this.techRecipes = new HashMapArrayList();
      Iterator var2 = var1.techRecipes.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         this.techRecipes.addAll((String)var3.getKey(), (Collection)var3.getValue());
      }

      this.itemIsCraftingMat = Arrays.copyOf(var1.itemIsCraftingMat, var1.itemIsCraftingMat.length);
      this.ingredientTechs = new HashSet[var1.ingredientTechs.length];

      int var4;
      for(var4 = 0; var4 < this.ingredientTechs.length; ++var4) {
         if (var1.ingredientTechs[var4] == null) {
            this.ingredientTechs[var4] = null;
         } else {
            this.ingredientTechs[var4] = new HashSet(var1.ingredientTechs[var4]);
         }
      }

      this.ingredientResultItems = new ArrayList[var1.ingredientResultItems.length];

      for(var4 = 0; var4 < this.ingredientResultItems.length; ++var4) {
         if (var1.ingredientResultItems[var4] == null) {
            this.ingredientResultItems[var4] = null;
         } else {
            this.ingredientResultItems[var4] = new ArrayList(var1.ingredientResultItems[var4]);
         }
      }

      this.hash = var1.hash;
   }

   RecipeList copy() {
      return new RecipeList(this);
   }

   private void loadDefaultRecipes() {
      this.applyList(Recipes.getDefaultRecipes());
   }

   void addModRecipes(List<Recipe> var1) {
      this.recipes.ensureCapacity(this.recipes.size() + var1.size());
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         this.addRecipe(var3);
      }

      this.updateStatic();
      this.resetHash();
   }

   private void applyList(List<Recipe> var1) {
      this.recipes = new ArrayList(var1.size());
      this.techRecipes = new HashMapArrayList();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         this.addRecipe(var3);
      }

      this.updateStatic();
      this.resetHash();
   }

   private void addRecipe(Recipe var1) {
      int var2 = -1;
      int var3;
      Recipe var4;
      if (var1.shouldBeSorted()) {
         for(var3 = 0; var3 < this.recipes.size(); ++var3) {
            var4 = (Recipe)this.recipes.get(var3);
            if (var1.shouldShowBefore(var4)) {
               var2 = var3;
               this.recipes.add(var3, var1);
               break;
            }

            if (var1.shouldShowAfter(var4)) {
               var2 = var3 + 1;
               this.recipes.add(var3 + 1, var1);
               break;
            }
         }
      }

      if (var2 == -1) {
         var2 = this.recipes.size();
         this.recipes.add(var1);
      }

      for(var3 = 0; var3 < this.recipes.size(); ++var3) {
         var4 = (Recipe)this.recipes.get(var3);
         if (var4.shouldBeSorted()) {
            int var5;
            if (var4.shouldShowBefore(var1)) {
               var5 = var3 < var2 ? -1 : 0;
               this.recipes.remove(var3);
               this.recipes.add(var2 + var5, var4);
               break;
            }

            if (var4.shouldShowAfter(var1)) {
               var5 = var3 < var2 ? -1 : 0;
               this.recipes.remove(var3);
               this.recipes.add(var2 + var5 + 1, var4);
               break;
            }
         }
      }

      this.techRecipes.add(var1.tech.getStringID(), var1);
   }

   private void updateStatic() {
      this.itemIsCraftingMat = new boolean[ItemRegistry.getItems().size()];
      this.ingredientTechs = new HashSet[ItemRegistry.getItems().size()];
      this.ingredientResultItems = new ArrayList[ItemRegistry.getItems().size()];
      Iterator var1 = this.recipes.iterator();

      while(var1.hasNext()) {
         Recipe var2 = (Recipe)var1.next();
         Ingredient[] var3 = var2.ingredients;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Ingredient var6 = var3[var5];
            if (var6.isGlobalIngredient()) {
               Iterator var9 = var6.getGlobalIngredient().getRegisteredItemIDs().iterator();

               while(var9.hasNext()) {
                  int var8 = (Integer)var9.next();
                  this.itemIsCraftingMat[var8] = true;
                  this.addResultItem(var8, var2);
               }
            } else {
               int var7 = var6.getIngredientID();
               this.itemIsCraftingMat[var7] = true;
               this.addResultItem(var7, var2);
            }
         }
      }

   }

   private void addResultItem(int var1, Recipe var2) {
      HashSet var3 = this.ingredientTechs[var1];
      if (var3 == null) {
         var3 = new HashSet();
         this.ingredientTechs[var1] = var3;
      }

      var3.add(var2.tech);
      Object var4 = this.ingredientResultItems[var1];
      if (var4 == null) {
         var4 = new ArrayList();
         this.ingredientResultItems[var1] = (List)var4;
      }

      ((List)var4).add(var2);
   }

   public boolean isCraftingMat(int var1) {
      return this.itemIsCraftingMat[var1];
   }

   public HashSet<Tech> getCraftingMatTechs(int var1) {
      HashSet var2 = this.ingredientTechs[var1];
      return var2 == null ? new HashSet() : var2;
   }

   private static int compileHash(List<Recipe> var0) {
      int var1 = 1;

      Recipe var3;
      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = var1 * 23 + var3.getRecipeHash()) {
         var3 = (Recipe)var2.next();
      }

      return var1;
   }

   private void resetHash() {
      this.hash = 0;
   }

   public int getHash() {
      if (this.hash == 0) {
         this.hash = compileHash(this.recipes);
      }

      return this.hash;
   }

   public Recipe getRecipe(int var1) {
      return (Recipe)this.recipes.get(var1);
   }

   public Stream<Recipe> streamRecipes() {
      return this.recipes.stream();
   }

   public Iterable<Recipe> getRecipes() {
      return this.recipes;
   }

   public Stream<Recipe> streamRecipes(Tech... var1) {
      return Arrays.stream(var1).flatMap((var1x) -> {
         return this.techRecipes.stream(var1x.getStringID());
      });
   }

   public Iterable<Recipe> getRecipes(Tech var1) {
      return (Iterable)this.techRecipes.get(var1.getStringID());
   }

   public Iterable<Recipe> getRecipes(Tech... var1) {
      return () -> {
         return Arrays.stream(var1).flatMap((var1x) -> {
            return this.techRecipes.stream(var1x.getStringID());
         }).iterator();
      };
   }

   public int getTotalRecipes() {
      return this.recipes.size();
   }

   public List<Recipe> getResultItems(int var1) {
      Object var2 = this.ingredientResultItems[var1];
      if (var2 == null) {
         var2 = new ArrayList();
      }

      Iterator var3 = this.recipes.iterator();

      while(true) {
         Recipe var4;
         do {
            if (!var3.hasNext()) {
               return (List)var2;
            }

            var4 = (Recipe)var3.next();
         } while(var4.resultItem.item.getID() != var1);

         boolean var5 = true;
         int var6 = var4.getRecipeHash();
         Iterator var7 = ((List)var2).iterator();

         while(var7.hasNext()) {
            Recipe var8 = (Recipe)var7.next();
            if (var8.getRecipeHash() == var6) {
               var5 = false;
               break;
            }
         }

         if (var5) {
            ((List)var2).add(0, var4);
         }
      }
   }
}
