package necesse.gfx;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.util.ObjectValue;

public class ImpossibleDrawException extends RuntimeException {
   private static final LinkedList<ObjectValue<Long, Throwable>> drawErrors = new LinkedList();
   private final Iterable<Throwable> causes;

   public static void submitDrawError(Throwable var0) {
      var0.printStackTrace();
      long var1 = System.currentTimeMillis();
      drawErrors.add(new ObjectValue(var1, var0));
      if (drawErrors.size() > 5) {
         ObjectValue var3 = (ObjectValue)drawErrors.removeFirst();
         long var4 = var1 - (Long)var3.object;
         if (var4 <= 5000L) {
            ArrayList var6 = new ArrayList();

            while(!drawErrors.isEmpty()) {
               var6.add((Throwable)((ObjectValue)drawErrors.removeLast()).value);
            }

            throw new ImpossibleDrawException(var6);
         }
      }

   }

   public ImpossibleDrawException(Iterable<Throwable> var1) {
      this.causes = var1;
   }

   public void printStackTrace(PrintWriter var1) {
      var1.println("Impossible draw exception causes:");
      Iterator var2 = this.causes.iterator();

      while(var2.hasNext()) {
         Throwable var3 = (Throwable)var2.next();
         var3.printStackTrace(var1);
      }

   }

   public void printStackTrace(PrintStream var1) {
      var1.println("Impossible draw exception causes:");
      Iterator var2 = this.causes.iterator();

      while(var2.hasNext()) {
         Throwable var3 = (Throwable)var2.next();
         var3.printStackTrace(var1);
      }

   }
}
