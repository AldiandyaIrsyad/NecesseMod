package necesse.engine.tickManager;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;

public class PerformanceTotal {
   public final String name;
   public final boolean isFirst;
   private PerformanceTotal parent;
   private final HashMap<String, PerformanceTotal> children = new HashMap();
   private long totalTime;
   private int totalCalls;
   private int totalFrames;
   private long longestTime;
   private int largestCalls;

   public PerformanceTotal(String var1, boolean var2) {
      this.name = var1;
      this.isFirst = var2;
   }

   public PerformanceTotal getParent() {
      return this.parent;
   }

   public Collection<PerformanceTotal> getChildren() {
      return this.children.values();
   }

   public long getTotalTime() {
      return this.totalTime;
   }

   public int getTotalCalls() {
      return this.totalCalls;
   }

   public int getTotalFrames() {
      return this.totalFrames;
   }

   public long getLongestTime() {
      return this.longestTime;
   }

   public int getLargestCalls() {
      return this.largestCalls;
   }

   public void append(PerformanceTimer var1) {
      this.totalTime += var1.getTime();
      this.totalCalls += var1.getCalls();
      ++this.totalFrames;
      this.longestTime = Math.max(this.longestTime, var1.getTime());
      this.largestCalls = Math.max(this.largestCalls, var1.getCalls());
      Iterator var2 = var1.getChildren().values().iterator();

      while(var2.hasNext()) {
         PerformanceTimer var3 = (PerformanceTimer)var2.next();
         this.children.compute(var3.name, (var2x, var3x) -> {
            if (var3x == null) {
               var3x = new PerformanceTotal(var2x, false);
               var3x.parent = this;
            }

            var3x.append(var3);
            return var3x;
         });
      }

   }

   public void print(PrintStream var1) {
      if (this.isFirst) {
         Iterator var2 = this.children.values().iterator();

         while(var2.hasNext()) {
            PerformanceTotal var3 = (PerformanceTotal)var2.next();
            var3.print(var1, "", 0);
         }
      } else {
         this.print(var1, "", 0);
      }

   }

   private void print(PrintStream var1, String var2, int var3) {
      int var4 = var3 - this.name.length();
      StringBuilder var5 = (new StringBuilder(var2)).append(this.name).append(" ");

      for(int var6 = 0; var6 < var4; ++var6) {
         var5.append(" ");
      }

      long var14 = this.totalTime / (long)this.totalFrames;
      var5.append(GameUtils.getTimeStringNano(var14));
      double var8;
      if (this.parent != null) {
         var8 = (double)this.totalTime / (double)this.parent.totalTime * 100.0;
         var5.append(" - ").append(GameMath.toDecimals(var8, 2)).append("%");
      }

      var8 = (double)this.totalCalls / (double)this.totalFrames;
      var5.append(" - ").append(GameMath.toDecimals(var8, 2)).append(" calls");
      var5.append(" (").append("Longest: ").append(GameUtils.getTimeStringNano(this.longestTime)).append(" in ").append(this.largestCalls).append(" calls)");
      var1.println(var5.toString());
      int var10 = 0;
      TreeSet var11 = new TreeSet(Comparator.comparingLong((var0) -> {
         return var0.totalTime;
      }));
      Iterator var12 = this.children.values().iterator();

      PerformanceTotal var13;
      while(var12.hasNext()) {
         var13 = (PerformanceTotal)var12.next();
         var10 = Math.max(var10, var13.name.length());
         var11.add(var13);
      }

      var12 = var11.iterator();

      while(var12.hasNext()) {
         var13 = (PerformanceTotal)var12.next();
         var13.print(var1, var2 + "\t", var10);
      }

   }
}
