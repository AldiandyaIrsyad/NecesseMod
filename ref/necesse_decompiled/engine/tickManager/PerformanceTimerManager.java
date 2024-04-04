package necesse.engine.tickManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;

public class PerformanceTimerManager {
   private PerformanceTimer runningRootPerformanceTimer;
   private PerformanceTimer runningPerformanceTimer;
   private boolean runOnlyConstantTimers;
   public boolean nextRunOnlyConstantTimers;
   private boolean appendRootTime;
   private int maxHistorySize;
   private int historyFrames;
   private final LinkedList<PerformanceTimer> history;
   private final TreeSet<PerformanceDump> performanceDumps;
   private final LinkedList<PerformanceDump> nextPerformanceDumps;
   private GameLinkedList<PerformanceTimerManager> runningCustomTimers;
   public final Object historyLock;

   public PerformanceTimerManager(boolean var1) {
      this.runningRootPerformanceTimer = new PerformanceTimer("root", true, 0, 0L);
      this.runningPerformanceTimer = this.runningRootPerformanceTimer;
      this.appendRootTime = true;
      this.maxHistorySize = 1000;
      this.history = new LinkedList();
      this.performanceDumps = new TreeSet(Comparator.comparingLong((var0) -> {
         return var0.overTimeMS;
      }));
      this.nextPerformanceDumps = new LinkedList();
      this.runningCustomTimers = new GameLinkedList();
      this.historyLock = new Object();
      this.runOnlyConstantTimers = var1;
      this.nextRunOnlyConstantTimers = var1;
   }

   public PerformanceTimerManager() {
      this(true);
   }

   public PerformanceTimerManager getChild() {
      PerformanceTimerManager var1 = new PerformanceTimerManager();
      this.applyChildProperties(var1);
      return var1;
   }

   protected void applyChildProperties(PerformanceTimerManager var1) {
      var1.runningPerformanceTimer = this.runningPerformanceTimer;
      var1.runningRootPerformanceTimer = this.runningPerformanceTimer;
      var1.runOnlyConstantTimers = this.runOnlyConstantTimers;
      var1.runningCustomTimers = this.runningCustomTimers;
      var1.appendRootTime = false;
   }

   public String getTimerPath() {
      LinkedList var1 = new LinkedList();

      for(PerformanceTimer var2 = this.runningPerformanceTimer; var2 != null; var2 = (PerformanceTimer)var2.getParent()) {
         var1.addFirst(var2.name);
      }

      StringBuilder var3 = new StringBuilder();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.append(var5);
         if (var4.hasNext()) {
            var3.append("/");
         }
      }

      return var3.toString();
   }

   protected PerformanceWrapper wrapTimer(String var1) {
      return this.runOnlyConstantTimers && this.runningCustomTimers.isEmpty() ? new PerformanceWrapper() {
         protected void endLogic() {
         }
      } : this.wrapConstantTimer(var1);
   }

   protected PerformanceWrapper wrapConstantTimer(String var1) {
      final long var2 = System.nanoTime();
      this.startNewPerformanceTimer(var1);
      return new PerformanceWrapper() {
         protected void endLogic() {
            long var1 = System.nanoTime() - var2;
            PerformanceTimerManager.this.appendPerformanceTime(var1);
         }
      };
   }

   protected void recordPerformance(String var1, Runnable var2) {
      boolean var3 = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
      if (var3) {
         this.startNewPerformanceTimer(var1);
      }

      long var4 = System.nanoTime();
      var2.run();
      long var6 = System.nanoTime() - var4;
      if (var3) {
         this.appendPerformanceTime(var6);
      }

   }

   protected void recordConstantPerformance(String var1, Runnable var2) {
      this.startNewPerformanceTimer(var1);
      long var3 = System.nanoTime();
      var2.run();
      this.appendPerformanceTime(System.nanoTime() - var3);
   }

   protected <T> T recordPerformance(String var1, Supplier<T> var2) {
      boolean var3 = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
      if (var3) {
         this.startNewPerformanceTimer(var1);
      }

      long var4 = System.nanoTime();

      Object var6;
      try {
         var6 = var2.get();
      } finally {
         if (var3) {
            this.appendPerformanceTime(System.nanoTime() - var4);
         }

      }

      return var6;
   }

   protected <T> T recordConstantPerformance(String var1, Supplier<T> var2) {
      this.startNewPerformanceTimer(var1);
      long var3 = System.nanoTime();

      Object var5;
      try {
         var5 = var2.get();
      } finally {
         this.appendPerformanceTime(System.nanoTime() - var3);
      }

      return var5;
   }

   protected void recordGlobalPerformance(String var1, Runnable var2) {
      boolean var3 = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
      if (var3) {
         this.recordConstantPerformance(var1, var2);
      } else {
         var2.run();
      }

   }

   protected void recordGlobalConstantPerformance(String var1, Runnable var2) {
      this.recordGlobalConstantPerformance(var1, () -> {
         var2.run();
         return null;
      });
   }

   protected <T> T recordGlobalPerformance(String var1, Supplier<T> var2) {
      boolean var3 = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
      return var3 ? this.recordGlobalConstantPerformance(var1, var2) : var2.get();
   }

   protected <T> T recordGlobalConstantPerformance(String var1, Supplier<T> var2) {
      PerformanceTimer var3 = this.runningRootPerformanceTimer.startChild("global").startChild(var1);
      long var4 = System.nanoTime();

      Object var6;
      try {
         var6 = var2.get();
      } finally {
         var3.appendTime(System.nanoTime() - var4);
      }

      return var6;
   }

   protected void addCustomTimer(PerformanceTimerManager var1, Runnable var2) {
      GameLinkedList.Element var3 = this.runningCustomTimers.addLast(var1);
      var1.runOnlyConstantTimers = false;
      var2.run();
      var3.remove();
   }

   private void startNewPerformanceTimer(String var1) {
      this.runningPerformanceTimer = this.runningPerformanceTimer.startChild(var1);
      synchronized(this.runningCustomTimers) {
         Iterator var3 = this.runningCustomTimers.iterator();

         while(var3.hasNext()) {
            PerformanceTimerManager var4 = (PerformanceTimerManager)var3.next();
            var4.startNewPerformanceTimer(var1);
         }

      }
   }

   private void appendPerformanceTime(long var1) {
      this.runningPerformanceTimer.appendTime(var1);
      this.runningPerformanceTimer = (PerformanceTimer)this.runningPerformanceTimer.getParent();
      if (this.runningPerformanceTimer == this.runningRootPerformanceTimer) {
         if (this.appendRootTime) {
            this.runningPerformanceTimer.appendTime(var1);
         }
      } else if (this.runningPerformanceTimer == null) {
         System.err.println("Tried to stop root performance timer");
         this.runningPerformanceTimer = this.runningRootPerformanceTimer;
      }

      synchronized(this.runningCustomTimers) {
         Iterator var4 = this.runningCustomTimers.iterator();

         while(var4.hasNext()) {
            PerformanceTimerManager var5 = (PerformanceTimerManager)var4.next();
            var5.appendPerformanceTime(var1);
         }

      }
   }

   public void calcFrame(boolean var1, int var2, long var3) {
      synchronized(this.historyLock) {
         this.history.add(this.runningRootPerformanceTimer);
         if (this.runningRootPerformanceTimer.isFirstFrame) {
            ++this.historyFrames;
         }

         for(; this.getPerformanceHistorySize() > this.maxHistorySize; this.history.removeFirst()) {
            PerformanceTimer var6 = (PerformanceTimer)this.history.getFirst();
            if (var6.isFirstFrame) {
               if (this.historyFrames <= 2) {
                  break;
               }

               --this.historyFrames;
            }
         }

         long var13 = System.currentTimeMillis();
         this.performanceDumps.addAll(this.nextPerformanceDumps);
         this.nextPerformanceDumps.clear();
         if (!this.performanceDumps.isEmpty()) {
            for(PerformanceDump var8 = (PerformanceDump)this.performanceDumps.first(); var8.overTimeMS <= var13; var8 = (PerformanceDump)this.performanceDumps.first()) {
               try {
                  var8.overEvent.accept(var8.history);
               } catch (Exception var11) {
                  var11.printStackTrace();
               }

               this.performanceDumps.pollFirst();
               if (this.performanceDumps.isEmpty()) {
                  break;
               }
            }
         }

         Iterator var14 = this.performanceDumps.iterator();

         while(var14.hasNext()) {
            PerformanceDump var9 = (PerformanceDump)var14.next();
            var9.history.add(this.runningRootPerformanceTimer);
         }

         this.runOnlyConstantTimers = this.nextRunOnlyConstantTimers && this.performanceDumps.isEmpty();
         this.runningRootPerformanceTimer = new PerformanceTimer("root", var1, var2, var3);
         this.runningPerformanceTimer = this.runningRootPerformanceTimer;
      }
   }

   public String getCurrentPerformanceTimerPath() {
      StringBuilder var1 = new StringBuilder();

      for(PerformanceTimer var2 = this.runningPerformanceTimer; var2 != null; var2 = (PerformanceTimer)var2.getParent()) {
         var1.insert(0, var2.name + (var1.length() == 0 ? "" : "/"));
      }

      return var1.toString();
   }

   public PerformanceTimer getCurrentRootPerformanceTimer() {
      return this.runningRootPerformanceTimer;
   }

   public int getPerformanceHistorySize() {
      return this.history.size();
   }

   public LinkedList<PerformanceTimer> getPerformanceHistory() {
      return this.history;
   }

   public PerformanceTimer getLastPerformanceTimer() {
      synchronized(this.historyLock) {
         return this.history.isEmpty() ? this.runningRootPerformanceTimer : (PerformanceTimer)this.history.getLast();
      }
   }

   public PerformanceTimer getPerformanceTimer(String var1) {
      return (PerformanceTimer)this.getLastPerformanceTimer().getPerformanceTimer(var1);
   }

   public HashMap<String, PerformanceTimer> getPerformanceTimers(String var1) {
      return this.getLastPerformanceTimer().getPerformanceTimers(var1);
   }

   public PerformanceTimerAverage getPreviousAverage() {
      synchronized(this.historyLock) {
         Iterator var2 = this.history.descendingIterator();

         PerformanceTimer var3;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            var3 = (PerformanceTimer)var2.next();
         } while(!var3.isFirstFrame);

         return new PerformanceTimerAverage(var2);
      }
   }

   public void runPerformanceDump(int var1, Consumer<LinkedList<PerformanceTimer>> var2) {
      this.nextPerformanceDumps.add(new PerformanceDump(System.currentTimeMillis() + (long)(var1 * 1000), var2));
   }

   protected static class PerformanceDump {
      public final long overTimeMS;
      public final Consumer<LinkedList<PerformanceTimer>> overEvent;
      public final LinkedList<PerformanceTimer> history = new LinkedList();

      public PerformanceDump(long var1, Consumer<LinkedList<PerformanceTimer>> var3) {
         this.overTimeMS = var1;
         this.overEvent = var3;
      }
   }
}
