package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.container.Container;

public class ContainerRecipeCraftedEvent extends RecipeCraftedEvent {
   public final Container container;

   public ContainerRecipeCraftedEvent(Recipe var1, ArrayList<InventoryItemsRemoved> var2, Container var3) {
      super(var1, var2);
      this.container = var3;
   }
}
