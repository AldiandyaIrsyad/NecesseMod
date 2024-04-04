package necesse.engine.localization;

public abstract class LocalizationChangeListener {
   public LocalizationChangeListener() {
   }

   public abstract void onChange(Language var1);

   public abstract boolean isDisposed();
}
