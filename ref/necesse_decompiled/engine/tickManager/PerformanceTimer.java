package necesse.engine.tickManager;

import java.util.Iterator;

public class PerformanceTimer extends AbstractPerformanceTimer<PerformanceTimer> {
   public final boolean isFirstFrame;
   public final int secondFrame;
   public final long totalFrame;
   private long time;
   private int calls;

   public PerformanceTimer(String var1, boolean var2, int var3, long var4) {
      this(var1, (PerformanceTimer)null, var2, var3, var4);
   }

   private PerformanceTimer(String var1, PerformanceTimer var2, boolean var3, int var4, long var5) {
      super(var1, var2);
      this.isFirstFrame = var3;
      this.secondFrame = var4;
      this.totalFrame = var5;
      this.time = 0L;
      this.calls = 0;
   }

   public synchronized PerformanceTimer startChild(String var1) {
      PerformanceTimer var2 = (PerformanceTimer)this.getChildren().get(var1);
      if (var2 == null) {
         var2 = new PerformanceTimer(var1, this, this.isFirstFrame, this.secondFrame, this.totalFrame);
         this.getChildren().put(var1, var2);
      }

      return var2;
   }

   public synchronized void appendTime(long var1) {
      this.time += var1;
      ++this.calls;
   }

   public long getTime() {
      return this.time;
   }

   public int getCalls() {
      return this.calls;
   }

   public PerformanceTimer copy() {
      PerformanceTimer var1 = new PerformanceTimer(this.name, (PerformanceTimer)this.getParent(), this.isFirstFrame, this.secondFrame, this.totalFrame);
      Iterator var2 = this.getChildren().values().iterator();

      while(var2.hasNext()) {
         PerformanceTimer var3 = (PerformanceTimer)var2.next();
         var1.getChildren().put(var3.name, var3.copy());
      }

      return var1;
   }
}
