package necesse.inventory;

public class InventoryRange {
   public final Inventory inventory;
   public final int startSlot;
   public final int endSlot;

   public InventoryRange(Inventory var1, int var2, int var3) {
      this.inventory = var1;
      this.startSlot = var2;
      this.endSlot = var3;
   }

   public InventoryRange(Inventory var1) {
      this(var1, 0, var1.getSize() - 1);
   }
}
