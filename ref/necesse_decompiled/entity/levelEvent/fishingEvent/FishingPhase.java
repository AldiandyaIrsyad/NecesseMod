package necesse.entity.levelEvent.fishingEvent;

import necesse.inventory.InventoryItem;

public abstract class FishingPhase {
   public final FishingEvent event;

   public FishingPhase(FishingEvent var1) {
      this.event = var1;
   }

   public abstract void tickMovement(float var1);

   public abstract void clientTick();

   public abstract void serverTick();

   public abstract void end();

   public abstract void over();

   public void addNewCatch(int var1, int var2, InventoryItem var3) {
   }

   public void reel() {
   }

   public int getTicksToNextCatch() {
      return 500;
   }
}
