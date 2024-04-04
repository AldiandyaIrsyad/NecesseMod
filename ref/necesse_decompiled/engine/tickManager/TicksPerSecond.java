package necesse.engine.tickManager;

public class TicksPerSecond {
   public int msPerTick;
   private float counter;

   private TicksPerSecond(int var1) {
      this.msPerTick = var1;
   }

   public static TicksPerSecond msPerTick(int var0) {
      return new TicksPerSecond(var0);
   }

   public static TicksPerSecond ticksPerSecond(int var0) {
      return msPerTick(1000 / var0);
   }

   public void tick(float var1) {
      this.counter += var1;
   }

   public void gameTick() {
      this.tick(50.0F);
   }

   public boolean peekTick() {
      return this.counter > (float)this.msPerTick;
   }

   public boolean shouldTick() {
      if (this.peekTick()) {
         this.counter -= (float)this.msPerTick;
         return true;
      } else {
         return false;
      }
   }
}
