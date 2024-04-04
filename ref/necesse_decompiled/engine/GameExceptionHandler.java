package necesse.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameExceptionHandler {
   public static int crashAfterConsecutiveExceptions = 1;
   public static int maxSavedExceptions = 20;
   private LinkedList<Throwable> savedExceptions = new LinkedList();
   private int frameExceptions;
   private int tickExceptions;
   private String inText;

   public GameExceptionHandler(String var1) {
      this.inText = var1;
   }

   public void clear(boolean var1) {
      if (var1) {
         this.tickExceptions = 0;
      }

      this.frameExceptions = 0;
   }

   public void submitException(boolean var1, Exception var2, Runnable var3) {
      this.addSavedException(var2, true);
      if (var1) {
         ++this.tickExceptions;
      } else {
         ++this.frameExceptions;
      }

      if (this.frameExceptions + this.tickExceptions >= crashAfterConsecutiveExceptions) {
         var3.run();
      }

   }

   private void addSavedException(Exception var1, boolean var2) {
      boolean var3 = this.savedExceptions.removeIf((var1x) -> {
         return isSameException(var1x, var1);
      });
      if (var2) {
         if (var3) {
            System.err.println("Another error in " + this.inText + ": " + var1.toString());
         } else {
            System.err.println("Error in " + this.inText + ":");
            var1.printStackTrace(System.err);
         }
      }

      this.savedExceptions.addFirst(var1);
      if (this.savedExceptions.size() > maxSavedExceptions) {
         this.savedExceptions.removeLast();
      }

   }

   public void addSavedException(Exception var1) {
      this.addSavedException(var1, false);
   }

   public List<Throwable> getSavedExceptions() {
      return this.savedExceptions;
   }

   public static boolean isSameException(Throwable var0, Throwable var1) {
      return isSameException(var0, var1, 0, 10);
   }

   public static boolean isSameException(Throwable var0, Throwable var1, int var2, int var3) {
      if (var0 == var1) {
         return true;
      } else if (!Objects.equals(var0.getMessage(), var1.getMessage())) {
         return false;
      } else {
         StackTraceElement[] var4 = var0.getStackTrace();
         StackTraceElement[] var5 = var1.getStackTrace();
         if (var4.length != var5.length) {
            return false;
         } else {
            for(int var6 = 0; var6 < var4.length; ++var6) {
               if (!var4[var6].equals(var5[var6])) {
                  return false;
               }
            }

            Throwable var8 = var0.getCause();
            Throwable var7 = var1.getCause();
            if (var8 != null && var7 != null && var2 < var3) {
               ++var2;
               return isSameException(var8, var7, var2, var3);
            } else {
               return var8 == null && var7 == null;
            }
         }
      }
   }
}
