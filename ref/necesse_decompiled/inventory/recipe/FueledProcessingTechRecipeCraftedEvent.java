package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.entity.objectEntity.FueledProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItemsRemoved;

public class FueledProcessingTechRecipeCraftedEvent extends RecipeCraftedEvent {
   public final FueledProcessingTechInventoryObjectEntity entity;

   public FueledProcessingTechRecipeCraftedEvent(Recipe var1, ArrayList<InventoryItemsRemoved> var2, FueledProcessingTechInventoryObjectEntity var3) {
      super(var1, var2);
      this.entity = var3;
   }
}
