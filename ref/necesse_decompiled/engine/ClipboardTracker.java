package necesse.engine;

import java.util.Objects;

public abstract class ClipboardTracker<T> {
   public long pollingRate;
   public boolean useDefault;
   private long lastPoll;
   private String lastClipboard;
   private T lastValue;

   public ClipboardTracker(long var1, boolean var3) {
      this.pollingRate = var1;
      this.useDefault = var3;
      this.forceUpdate();
   }

   public ClipboardTracker(long var1) {
      this(var1, false);
   }

   public ClipboardTracker(boolean var1) {
      this(500L, var1);
   }

   public ClipboardTracker() {
      this(500L);
   }

   public abstract T parse(String var1);

   public abstract void onUpdate(T var1);

   public void update() {
      if (this.lastPoll + this.pollingRate < System.currentTimeMillis()) {
         this.forceUpdate();
      }

   }

   public void forceUpdate() {
      this.lastPoll = System.currentTimeMillis();
      String var1 = this.getNewClipboard();
      if (!Objects.equals(var1, this.lastClipboard)) {
         this.lastClipboard = var1;
         this.lastValue = this.parse(this.lastClipboard);
         this.onUpdate(this.lastValue);
      }

   }

   public T getValue() {
      return this.lastValue;
   }

   protected String getNewClipboard() {
      return this.useDefault ? Screen.getClipboardDefault() : Screen.getClipboard();
   }
}
