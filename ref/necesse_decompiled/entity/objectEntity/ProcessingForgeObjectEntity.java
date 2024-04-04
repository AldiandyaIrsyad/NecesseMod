package necesse.entity.objectEntity;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;

public class ProcessingForgeObjectEntity extends AnyLogFueledProcessingTechInventoryObjectEntity {
   public static int logFuelTime = 40000;
   public static int recipeProcessTime = 8000;

   public ProcessingForgeObjectEntity(Level var1, int var2, int var3) {
      super(var1, "forge", var2, var3, 2, 2, false, false, true, RecipeTechRegistry.FORGE);
   }

   public int getFuelTime(InventoryItem var1) {
      return logFuelTime;
   }

   public int getProcessTime(Recipe var1) {
      return recipeProcessTime;
   }

   public boolean shouldBeAbleToChangeKeepFuelRunning() {
      return false;
   }
}
