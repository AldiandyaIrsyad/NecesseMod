package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.entity.mobs.job.activeJob.CraftSettlementRecipeActiveJob;
import necesse.inventory.InventoryItemsRemoved;

public class SettlementRecipeCraftedEvent extends RecipeCraftedEvent {
   public final CraftSettlementRecipeActiveJob job;

   public SettlementRecipeCraftedEvent(Recipe var1, ArrayList<InventoryItemsRemoved> var2, CraftSettlementRecipeActiveJob var3) {
      super(var1, var2);
      this.job = var3;
   }
}
