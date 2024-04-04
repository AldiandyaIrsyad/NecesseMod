package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class SettlementWorkstationLevelObject extends LevelObject {
   public SettlementWorkstationLevelObject(Level var1, int var2, int var3) {
      super(var1, var2, var3);
      if (!(this.object instanceof SettlementWorkstationObject)) {
         throw new IllegalStateException("Object not workstation");
      }
   }

   public Stream<Recipe> streamSettlementRecipes() {
      return ((SettlementWorkstationObject)this.object).streamSettlementRecipes(this.level, this.tileX, this.tileY);
   }

   public boolean canCurrentlyCraft(Recipe var1) {
      return ((SettlementWorkstationObject)this.object).canCurrentlyCraft(this.level, this.tileX, this.tileY, var1);
   }

   public int getMaxCraftsAtOnce(Recipe var1) {
      return ((SettlementWorkstationObject)this.object).getMaxCraftsAtOnce(this.level, this.tileX, this.tileY, var1);
   }

   public void tickCrafting(Recipe var1) {
      ((SettlementWorkstationObject)this.object).tickCrafting(this.level, this.tileX, this.tileY, var1);
   }

   public void onCraftFinished(Recipe var1) {
      ((SettlementWorkstationObject)this.object).onCraftFinished(this.level, this.tileX, this.tileY, var1);
   }

   public SettlementRequestOptions getFuelRequestOptions() {
      return ((SettlementWorkstationObject)this.object).getFuelRequestOptions(this.level, this.tileX, this.tileY);
   }

   public InventoryRange getFuelInventoryRange() {
      return ((SettlementWorkstationObject)this.object).getFuelInventoryRange(this.level, this.tileX, this.tileY);
   }

   public boolean isProcessingInventory() {
      return ((SettlementWorkstationObject)this.object).isProcessingInventory(this.level, this.tileX, this.tileY);
   }

   public InventoryRange getProcessingInputRange() {
      return ((SettlementWorkstationObject)this.object).getProcessingInputRange(this.level, this.tileX, this.tileY);
   }

   public InventoryRange getProcessingOutputRange() {
      return ((SettlementWorkstationObject)this.object).getProcessingOutputRange(this.level, this.tileX, this.tileY);
   }

   public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs() {
      return ((SettlementWorkstationObject)this.object).getCurrentAndFutureProcessingOutputs(this.level, this.tileX, this.tileY);
   }
}
