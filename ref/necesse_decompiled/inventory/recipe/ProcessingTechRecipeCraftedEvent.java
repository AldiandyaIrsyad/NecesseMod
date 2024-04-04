package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItemsRemoved;

public class ProcessingTechRecipeCraftedEvent extends RecipeCraftedEvent {
   public final ProcessingTechInventoryObjectEntity entity;

   public ProcessingTechRecipeCraftedEvent(Recipe var1, ArrayList<InventoryItemsRemoved> var2, ProcessingTechInventoryObjectEntity var3) {
      super(var1, var2);
      this.entity = var3;
   }
}
