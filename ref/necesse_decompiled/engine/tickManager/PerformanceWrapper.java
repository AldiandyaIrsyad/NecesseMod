package necesse.engine.tickManager;

public abstract class PerformanceWrapper {
   private boolean hasEnded;

   public PerformanceWrapper() {
   }

   protected abstract void endLogic();

   public final void end() {
      if (this.hasEnded) {
         throw new IllegalStateException("Wrapper has already ended");
      } else {
         this.hasEnded = true;
         this.endLogic();
      }
   }
}
