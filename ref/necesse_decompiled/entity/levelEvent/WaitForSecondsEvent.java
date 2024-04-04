package necesse.entity.levelEvent;

public abstract class WaitForSecondsEvent extends LevelEvent {
   protected int tickCounter;
   protected float timeToWaitInSeconds = 2.0F;

   public WaitForSecondsEvent() {
   }

   public WaitForSecondsEvent(float var1) {
      super(false);
      this.timeToWaitInSeconds = var1;
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
   }

   public void clientTick() {
      ++this.tickCounter;
      if ((float)this.tickCounter > 20.0F * this.timeToWaitInSeconds) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if ((float)this.tickCounter > 20.0F * this.timeToWaitInSeconds) {
         this.over();
      } else {
         super.serverTick();
      }

   }

   public void over() {
      boolean var1 = this.isOver();
      super.over();
      if (!var1) {
         this.onWaitOver();
      }

   }

   public abstract void onWaitOver();
}
