package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;

public class RecipeCraftedEvent {
   public final Recipe recipe;
   public InventoryItem resultItem;
   public ArrayList<InventoryItemsRemoved> itemsUsed;

   public RecipeCraftedEvent(Recipe var1, ArrayList<InventoryItemsRemoved> var2) {
      this.recipe = var1;
      this.resultItem = var1.resultItem.copy(var1.resultAmount);
      this.itemsUsed = var2;
   }
}
