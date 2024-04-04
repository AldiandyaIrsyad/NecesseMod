package necesse.engine.tickManager;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;

public class PerformanceTimerAverage extends AbstractPerformanceTimer<PerformanceTimerAverage> {
   private LinkedList<PerformanceTimer> frames;
   private long totalTime;
   private int totalCalls;

   public PerformanceTimerAverage(Iterator<PerformanceTimer> var1) {
      this("root", (PerformanceTimerAverage)null, var1);
   }

   private PerformanceTimerAverage(String var1, PerformanceTimerAverage var2, Iterator<PerformanceTimer> var3) {
      super(var1, var2);
      this.frames = new LinkedList();
      this.applyFrames(var3);
   }

   private void applyFrames(Iterator<PerformanceTimer> var1) {
      label26:
      while(true) {
         if (var1.hasNext()) {
            PerformanceTimer var2 = (PerformanceTimer)var1.next();
            if (!var2.isFirstFrame) {
               if (!var2.name.equals(this.name)) {
                  continue;
               }

               this.frames.add(var2);
               this.totalTime += var2.getTime();
               this.totalCalls += var2.getCalls();
               Iterator var3 = var2.getChildren().values().iterator();

               while(true) {
                  if (!var3.hasNext()) {
                     continue label26;
                  }

                  PerformanceTimer var4 = (PerformanceTimer)var3.next();
                  PerformanceTimerAverage var5 = (PerformanceTimerAverage)this.getChildren().getOrDefault(var4.name, (Object)null);
                  if (var5 == null) {
                     this.getChildren().put(var4.name, new PerformanceTimerAverage(var4.name, this, var2.getChildren().values().iterator()));
                  } else {
                     var5.applyFrames(var2.getChildren().values().iterator());
                  }
               }
            }
         }

         return;
      }
   }

   public long getTotalTime() {
      return this.totalTime;
   }

   public long getAverageTime() {
      return this.frames.isEmpty() ? 0L : this.totalTime / (long)this.frames.size();
   }

   public int getTotalCalls() {
      return this.totalCalls;
   }

   public double getAverageCalls() {
      return this.frames.isEmpty() ? 0.0 : (double)this.totalCalls / (double)this.frames.size();
   }

   public float getAverageTimePercent() {
      if (this.getParent() == null) {
         return 100.0F;
      } else {
         long var1 = ((PerformanceTimerAverage)this.getParent()).getAverageTime();
         if (var1 == 0L) {
            return 100.0F;
         } else {
            double var3 = (double)this.getAverageTime() / (double)var1;
            return (float)((int)(var3 * 100000.0)) / 1000.0F;
         }
      }
   }

   public synchronized LinkedList<PerformanceTimer> getFrames() {
      return this.frames;
   }

   public void printTotalTimeTree() {
      this.printTotalTimeTree("", false);
   }

   private void printTotalTimeTree(String var1, boolean var2) {
      if (var2) {
         float var3 = this.getParent() == null ? 1.0F : (float)this.totalTime / (float)((PerformanceTimerAverage)this.getParent()).totalTime;
         System.out.println(var1 + this.name + " - " + GameMath.toDecimals(var3 * 100.0F, 2) + "% - " + GameUtils.getTimeStringNano(this.totalTime) + " - " + this.totalCalls);
      } else {
         System.out.println(var1 + this.name + " - " + GameUtils.getTimeStringNano(this.totalTime) + " - " + this.totalCalls);
      }

      Iterator var5 = this.getChildren().values().iterator();

      while(var5.hasNext()) {
         PerformanceTimerAverage var4 = (PerformanceTimerAverage)var5.next();
         var4.printTotalTimeTree(var1 + "\t", true);
      }

   }

   public float getPercent() {
      if (this.totalTime == 0L) {
         return 1.0F;
      } else {
         double var1 = (double)this.getAverageTime() / (double)this.totalTime;
         return (float)((int)(var1 * 100000.0)) / 1000.0F;
      }
   }

   public double getPercentDouble() {
      return this.totalTime == 0L ? 100.0 : (double)this.getAverageTime() / (double)this.totalTime;
   }
}
