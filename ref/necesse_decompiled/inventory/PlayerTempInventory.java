package necesse.inventory;

import necesse.entity.mobs.PlayerMob;

public abstract class PlayerTempInventory extends PlayerInventory {
   private boolean isDisposed;

   public PlayerTempInventory(PlayerMob var1, int var2, int var3) {
      super(var1, var2, false, false, false, var3);
   }

   public abstract boolean shouldDispose();

   public boolean isDisposed() {
      return this.isDisposed;
   }

   public void dispose() {
      this.isDisposed = true;
      if (this.player.isServer()) {
         for(int var1 = 0; var1 < this.getSize(); ++var1) {
            if (!this.isSlotClear(var1)) {
               this.player.getInv().addItemsDropRemaining(this.getItem(var1), "addback", this.player, false, false);
               this.clearSlot(var1);
            }
         }
      }

   }
}
