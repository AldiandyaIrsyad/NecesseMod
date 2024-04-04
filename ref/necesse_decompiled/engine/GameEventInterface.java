package necesse.engine;

public interface GameEventInterface<T> {
   void init(Runnable var1);

   void onEvent(T var1);

   boolean isDisposed();
}
