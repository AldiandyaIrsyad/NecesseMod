package necesse.entity.levelEvent.fishingEvent;

public class SwingFishingPhase extends FishingPhase {
   public SwingFishingPhase(FishingEvent var1) {
      super(var1);
   }

   public void tickMovement(float var1) {
      if (this.event.getFishingMob().isFishingSwingDone()) {
         this.event.setPhase(new HookFishingPhase(this.event));
      }

   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void end() {
   }

   public void over() {
   }
}
