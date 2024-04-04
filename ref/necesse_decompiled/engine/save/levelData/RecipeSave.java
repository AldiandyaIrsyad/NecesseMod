package necesse.engine.save.levelData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeData;

public class RecipeSave {
   public RecipeSave() {
   }

   public static Recipe loadSave(LoadData var0) {
      try {
         RecipeData var1 = new RecipeData(var0);

         try {
            return var1.validate();
         } catch (Exception var4) {
            String var3 = var4.getMessage();
            System.err.println("Could not load recipe for '" + var1.resultID + "'" + (var3 != null && var3.length() != 0 ? " - " + var3 : ""));
         }
      } catch (SaveSyntaxException var5) {
         System.err.println("Syntax error on recipe load:");
         System.err.println(var5.getMessage());
      } catch (Exception var6) {
         System.err.println("Unknown error in loading recipe");
         var6.printStackTrace();
      }

      return null;
   }

   public static SaveData getSave(Recipe var0) {
      SaveData var1 = new SaveData("");
      (new RecipeData(var0)).addSaveData(var1);
      return var1;
   }

   public static void putRecipeSave(SaveData var0, Iterable<Recipe> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         var0.addSaveData(getSave(var3));
      }

   }

   public static List<Recipe> loadRecipesSave(LoadData var0) {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < var0.getLoadData().size(); ++var2) {
         LoadData var3 = (LoadData)var0.getLoadData().get(var2);
         Recipe var4 = loadSave(var3);
         if (var4 != null) {
            var1.add(var4);
         }
      }

      return var1;
   }
}
