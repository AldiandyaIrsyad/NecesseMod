package necesse.engine.tickManager;

import java.util.function.Supplier;

public class Performance {
   public Performance() {
   }

   public static void record(PerformanceTimerManager var0, String var1, Runnable var2) {
      if (var0 != null) {
         var0.recordPerformance(var1, var2);
      } else {
         var2.run();
      }

   }

   public static void recordConstant(PerformanceTimerManager var0, String var1, Runnable var2) {
      if (var0 != null) {
         var0.recordConstantPerformance(var1, var2);
      } else {
         var2.run();
      }

   }

   public static <T> T record(PerformanceTimerManager var0, String var1, Supplier<T> var2) {
      return var0 != null ? var0.recordPerformance(var1, var2) : var2.get();
   }

   public static <T> T recordConstant(PerformanceTimerManager var0, String var1, Supplier<T> var2) {
      return var0 != null ? var0.recordConstantPerformance(var1, var2) : var2.get();
   }

   public static void recordGlobal(PerformanceTimerManager var0, String var1, Runnable var2) {
      if (var0 != null) {
         var0.recordGlobalPerformance(var1, var2);
      } else {
         var2.run();
      }

   }

   public static void recordGlobalConstant(PerformanceTimerManager var0, String var1, Runnable var2) {
      if (var0 != null) {
         var0.recordGlobalConstantPerformance(var1, var2);
      } else {
         var2.run();
      }

   }

   public static <T> T recordGlobal(PerformanceTimerManager var0, String var1, Supplier<T> var2) {
      return var0 != null ? var0.recordGlobalPerformance(var1, var2) : var2.get();
   }

   public static <T> T recordGlobalConstant(PerformanceTimerManager var0, String var1, Supplier<T> var2) {
      return var0 != null ? var0.recordGlobalConstantPerformance(var1, var2) : var2.get();
   }

   public static PerformanceWrapper wrapTimer(PerformanceTimerManager var0, String var1) {
      return var0 != null ? var0.wrapTimer(var1) : new PerformanceWrapper() {
         protected void endLogic() {
         }
      };
   }

   public static PerformanceWrapper wrapConstantTimer(PerformanceTimerManager var0, String var1) {
      return var0 != null ? var0.wrapConstantTimer(var1) : new PerformanceWrapper() {
         protected void endLogic() {
         }
      };
   }

   public static void runCustomTimer(PerformanceTimerManager var0, PerformanceTimerManager var1, Runnable var2) {
      if (var0 != null) {
         var0.addCustomTimer(var1, var2);
      } else {
         var2.run();
      }

   }
}
