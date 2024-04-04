package necesse.inventory;

public abstract class InventoryUpdateListener {
   private Runnable disposeLogic;

   public InventoryUpdateListener() {
   }

   public void init(Runnable var1) {
      this.disposeLogic = var1;
   }

   public abstract void onSlotUpdate(int var1);

   public abstract boolean isDisposed();

   public void dispose() {
      if (this.disposeLogic != null) {
         this.disposeLogic.run();
      }

   }
}
