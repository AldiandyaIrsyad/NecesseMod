package necesse.engine.tickManager;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;

public class PerformanceTimerUtils {
   public PerformanceTimerUtils() {
   }

   public static <T extends AbstractPerformanceTimer<T>> T getPerformanceTimer(String var0, HashMap<String, T> var1) {
      if (var0.equals("")) {
         return null;
      } else {
         String[] var2 = var0.split("/");
         AbstractPerformanceTimer var3 = (AbstractPerformanceTimer)var1.get(var2[0]);
         if (var3 == null) {
            return null;
         } else {
            for(int var4 = 1; var4 < var2.length; ++var4) {
               var3 = (AbstractPerformanceTimer)var3.getChildren().getOrDefault(var2[var4], (Object)null);
               if (var3 == null) {
                  return null;
               }
            }

            return var3;
         }
      }
   }

   public static PerformanceTotal combineTimers(Collection<PerformanceTimer> var0) {
      if (var0.isEmpty()) {
         return null;
      } else {
         PerformanceTotal var1 = new PerformanceTotal("total", true);
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            PerformanceTimer var3 = (PerformanceTimer)var2.next();
            var1.append(var3);
         }

         return var1;
      }
   }

   public static void printPerformanceTimer(PerformanceTimer var0) {
      PrintStream var10001 = System.out;
      Objects.requireNonNull(var10001);
      printPerformanceTimer(var0, var10001::println);
   }

   public static void printPerformanceTimer(PerformanceTimer var0, Consumer<String> var1) {
      printPerformanceTimer("", 0, var0, var1);
   }

   private static void printPerformanceTimer(String var0, int var1, PerformanceTimer var2, Consumer<String> var3) {
      int var4 = var1 - var2.name.length();
      StringBuilder var5 = (new StringBuilder(var0)).append(var2.name).append(" ");

      for(int var6 = 0; var6 < var4; ++var6) {
         var5.append(" ");
      }

      var5.append(GameUtils.getTimeStringNano(var2.getTime()));
      PerformanceTimer var11 = (PerformanceTimer)var2.getParent();
      if (var11 != null) {
         double var7 = (double)var2.getTime() / (double)var11.getTime() * 100.0;
         var5.append(" - ").append(GameMath.toDecimals(var7, 2)).append("%").append(" - ").append(var2.getCalls()).append(" calls");
      }

      var3.accept(var5.toString());
      int var12 = 0;
      ArrayList var8 = new ArrayList(var2.getChildren().size());
      Iterator var9 = var2.getChildren().values().iterator();

      PerformanceTimer var10;
      while(var9.hasNext()) {
         var10 = (PerformanceTimer)var9.next();
         var12 = Math.max(var12, var10.name.length());
         var8.add(var10);
      }

      var8.sort(Comparator.comparing(PerformanceTimer::getTime).reversed());
      var9 = var8.iterator();

      while(var9.hasNext()) {
         var10 = (PerformanceTimer)var9.next();
         printPerformanceTimer(var0 + "\t", var12, var10, var3);
      }

   }
}
