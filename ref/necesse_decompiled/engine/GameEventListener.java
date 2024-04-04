package necesse.engine;

public abstract class GameEventListener<T> implements GameEventInterface<T> {
   private boolean isDisposed;
   private Runnable disposeLogic;

   public GameEventListener() {
   }

   public void init(Runnable var1) {
      this.disposeLogic = var1;
   }

   public boolean isDisposed() {
      return this.isDisposed;
   }

   public void dispose() {
      this.isDisposed = true;
      if (this.disposeLogic != null) {
         this.disposeLogic.run();
      }

   }
}
