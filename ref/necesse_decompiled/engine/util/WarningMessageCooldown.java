package necesse.engine.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class WarningMessageCooldown<T> {
   public int millisecondCooldown;
   protected final HashMap<T, WarningMessageCooldown<T>.WarningTimer> warningTimers = new HashMap();
   protected final int timeoutMilliseconds;
   protected final LinkedList<WarningMessageCooldown<T>.WarningTimeout> timeouts;
   protected final HashSet<T> hasTimeout = new HashSet();

   public WarningMessageCooldown(int var1, int var2) {
      this.millisecondCooldown = var1;
      this.timeoutMilliseconds = var2;
      if (var2 > 0) {
         this.timeouts = new LinkedList();
      } else {
         this.timeouts = null;
      }

   }

   public synchronized void tickTimeouts() {
      if (this.timeouts == null) {
         throw new IllegalStateException("Warning message cooldown are not set up to handle timeouts");
      } else {
         while(!this.timeouts.isEmpty()) {
            WarningTimeout var1 = (WarningTimeout)this.timeouts.getFirst();
            WarningTimer var2 = (WarningTimer)this.warningTimers.get(var1.key);
            if (var2 == null) {
               this.timeouts.removeFirst();
               this.hasTimeout.remove(var1.key);
               return;
            }

            long var3 = System.currentTimeMillis() - var1.timeAtLastSubmit;
            if (var3 < (long)(this.millisecondCooldown + this.timeoutMilliseconds)) {
               break;
            }

            this.timeouts.removeFirst();
            if (var2.timeAtLastSubmit == var1.timeAtLastSubmit) {
               this.warningTimers.remove(var1.key);
               this.hasTimeout.remove(var1.key);
            } else {
               this.timeouts.addLast(new WarningTimeout(var1.key, var2.timeAtLastSubmit));
            }
         }

      }
   }

   public synchronized void submit(T var1, WarningHandler var2) {
      WarningTimer var3 = (WarningTimer)this.warningTimers.compute(var1, (var1x, var2x) -> {
         return var2x == null ? new WarningTimer() : var2x;
      });
      var3.submit(var1, var2);
   }

   private class WarningTimeout {
      public final T key;
      public final long timeAtLastSubmit;

      public WarningTimeout(T var2, long var3) {
         this.key = var2;
         this.timeAtLastSubmit = var3;
      }
   }

   private class WarningTimer {
      public int countSinceLastWarning;
      public long timeAtLastWarning;
      public long timeAtLastSubmit;

      private WarningTimer() {
      }

      public void submit(T var1, WarningHandler var2) {
         this.timeAtLastSubmit = System.currentTimeMillis();
         long var3 = System.currentTimeMillis() - this.timeAtLastWarning;
         if (var3 >= (long)WarningMessageCooldown.this.millisecondCooldown) {
            this.timeAtLastWarning = System.currentTimeMillis();
            var2.handleWarning(this.countSinceLastWarning);
            this.countSinceLastWarning = 0;
         } else {
            ++this.countSinceLastWarning;
         }

         if (WarningMessageCooldown.this.timeouts != null && !WarningMessageCooldown.this.hasTimeout.contains(var1)) {
            WarningMessageCooldown.this.timeouts.addLast(WarningMessageCooldown.this.new WarningTimeout(var1, this.timeAtLastSubmit));
            WarningMessageCooldown.this.hasTimeout.add(var1);
         }

      }

      // $FF: synthetic method
      WarningTimer(Object var2) {
         this();
      }
   }

   @FunctionalInterface
   public interface WarningHandler {
      void handleWarning(int var1);
   }
}
