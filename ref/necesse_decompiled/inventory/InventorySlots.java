package necesse.inventory;

import java.util.LinkedList;

public class InventorySlots {
   public final Inventory inventory;
   public final LinkedList<Integer> slots;

   public InventorySlots(Inventory var1) {
      this.inventory = var1;
      this.slots = new LinkedList();
   }
}
