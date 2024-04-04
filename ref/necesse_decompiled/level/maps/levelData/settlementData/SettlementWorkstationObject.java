package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;

public interface SettlementWorkstationObject {
   Stream<Recipe> streamSettlementRecipes(Level var1, int var2, int var3);

   default boolean canCurrentlyCraft(Level var1, int var2, int var3, Recipe var4) {
      return true;
   }

   default int getMaxCraftsAtOnce(Level var1, int var2, int var3, Recipe var4) {
      return 1;
   }

   default void tickCrafting(Level var1, int var2, int var3, Recipe var4) {
   }

   default void onCraftFinished(Level var1, int var2, int var3, Recipe var4) {
   }

   default SettlementRequestOptions getFuelRequestOptions(Level var1, int var2, int var3) {
      return null;
   }

   default InventoryRange getFuelInventoryRange(Level var1, int var2, int var3) {
      return null;
   }

   default boolean isProcessingInventory(Level var1, int var2, int var3) {
      return false;
   }

   default InventoryRange getProcessingInputRange(Level var1, int var2, int var3) {
      return null;
   }

   default InventoryRange getProcessingOutputRange(Level var1, int var2, int var3) {
      return null;
   }

   default ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level var1, int var2, int var3) {
      return new ArrayList();
   }
}
