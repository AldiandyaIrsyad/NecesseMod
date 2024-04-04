package necesse.engine.tickManager;

import java.util.HashMap;

public abstract class AbstractPerformanceTimer<T extends AbstractPerformanceTimer<T>> implements Comparable<AbstractPerformanceTimer> {
   public final String name;
   private final T parent;
   private final HashMap<String, T> children = new HashMap();

   public AbstractPerformanceTimer(String var1, T var2) {
      this.name = var1;
      this.parent = var2;
   }

   public int compareTo(AbstractPerformanceTimer var1) {
      return this.name.compareTo(var1.name);
   }

   public final synchronized T getParent() {
      return this.parent;
   }

   public final synchronized HashMap<String, T> getChildren() {
      return this.children;
   }

   public final synchronized T getPerformanceTimer(String var1) {
      return var1.equals("") ? this : PerformanceTimerUtils.getPerformanceTimer(var1, this.getChildren());
   }

   public final synchronized HashMap<String, T> getPerformanceTimers(String var1) {
      if (var1.equals("")) {
         return this.getChildren();
      } else {
         AbstractPerformanceTimer var2 = this.getPerformanceTimer(var1);
         return var2 == null ? null : var2.getChildren();
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((AbstractPerformanceTimer)var1);
   }
}
