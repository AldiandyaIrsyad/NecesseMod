package necesse.inventory.recipe;

import java.util.Iterator;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;

public class IngredientData {
   public final String ingredientStringID;
   public final int itemAmount;
   public final boolean requiredToShow;

   public IngredientData(String var1, int var2, boolean var3) {
      this.ingredientStringID = var1;
      this.itemAmount = var2;
      this.requiredToShow = var3;
   }

   public IngredientData(Ingredient var1) {
      this.ingredientStringID = var1.ingredientStringID;
      this.itemAmount = var1.getIngredientAmount();
      this.requiredToShow = var1.requiredToShow();
   }

   public IngredientData(LoadData var1, RecipeData var2) throws SaveSyntaxException {
      Iterator var3 = var1.getLoadData().iterator();
      if (var3.hasNext()) {
         LoadData var4 = (LoadData)var3.next();
         if (!var4.isData()) {
            throw new SaveSyntaxException("Recipe for '" + (var2 == null ? "null" : var2.resultID) + "' ingredient must only have data components");
         } else {
            this.ingredientStringID = LoadData.getUnsafeString(var4);
            if (var3.hasNext()) {
               var4 = (LoadData)var3.next();
               if (!var4.isData()) {
                  throw new SaveSyntaxException("Recipe for '" + (var2 == null ? "null" : var2.resultID) + "' ingredient '" + this.ingredientStringID + "' must only have data components");
               }

               try {
                  this.itemAmount = LoadData.getInt(var4);
               } catch (NumberFormatException var6) {
                  throw new SaveSyntaxException("Recipe for '" + (var2 == null ? "null" : var2.resultID) + "' ingredient '" + this.ingredientStringID + "' amount must be a number");
               }
            } else {
               this.itemAmount = 1;
            }

            if (var3.hasNext()) {
               var4 = (LoadData)var3.next();
               if (!var4.isData()) {
                  throw new SaveSyntaxException("Recipe for '" + (var2 == null ? "null" : var2.resultID) + "' ingredient '" + this.ingredientStringID + "' must only have data components");
               }

               this.requiredToShow = LoadData.getBoolean(var4);
            } else {
               this.requiredToShow = false;
            }

         }
      } else {
         throw new SaveSyntaxException("Recipe for '" + (var2 == null ? "null" : var2.resultID) + "' ingredient needs at least one data component");
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("", this.ingredientStringID);
      if (this.itemAmount != 1 || this.requiredToShow) {
         var1.addInt("", this.itemAmount);
      }

      if (this.requiredToShow) {
         var1.addBoolean("", true);
      }

   }

   public Ingredient validate() {
      return new Ingredient(this.ingredientStringID, this.itemAmount, this.requiredToShow);
   }
}
