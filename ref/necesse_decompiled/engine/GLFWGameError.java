package necesse.engine;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.APIUtil;

public class GLFWGameError {
   private static final Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((var0, var1) -> {
      return 65536 < var1 && var1 < 131072;
   }, (Map)null, new Class[]{GLFW.class});
   private static final Object throwLock = new Object();
   private static int[] handledCodes;
   public final int errorCode;
   public final String errorName;
   public final long errorDescriptionPointer;
   public final String errorDescription;
   public final StackTraceElement[] stackTrace;
   protected boolean caught = false;

   public static void throwError(int var0, long var1) {
      synchronized(throwLock) {
         GLFWGameError var4 = new GLFWGameError(var0, var1);
         if (handledCodes == null || handledCodes.length != 0 && !IntStream.of(handledCodes).anyMatch((var1x) -> {
            return var4.errorCode == var1x;
         })) {
            var4.print();
         } else {
            throw new GLFWGameErrorException(var4);
         }
      }
   }

   private GLFWGameError(int var1, long var2) {
      this.errorCode = var1;
      this.errorName = (String)ERROR_CODES.getOrDefault(var1, "GLFW_UNKNOWN_ERROR");
      this.errorDescriptionPointer = var2;
      this.errorDescription = var2 == 0L ? "No description" : GLFWErrorCallback.getDescription(var2);
      StackTraceElement[] var4 = Thread.currentThread().getStackTrace();
      this.stackTrace = (StackTraceElement[])Arrays.copyOfRange(var4, 6, var4.length);
   }

   public void print(PrintStream var1) {
      var1.println(this.errorName + ": " + this.errorDescription);
      StackTraceElement[] var2 = this.stackTrace;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         StackTraceElement var5 = var2[var4];
         var1.println("\t" + var5.toString());
      }

   }

   public void print() {
      this.print(System.err);
   }

   public static void tryGLFWError(Handler var0) {
      synchronized(throwLock) {
         try {
            handledCodes = var0.errorCodes;
            var0.run();
         } catch (GLFWGameErrorException var8) {
            var0.onCatch(var8.error);
         } finally {
            handledCodes = null;
            var0.onFinally();
         }

      }
   }

   public static <T> T tryGLFWError(Supplier<T> var0) {
      synchronized(throwLock) {
         Object var3;
         try {
            handledCodes = var0.errorCodes;
            Object var2 = var0.run();
            return var2;
         } catch (GLFWGameErrorException var9) {
            var3 = var0.onCatch(var9.error);
         } finally {
            handledCodes = null;
            var0.onFinally();
         }

         return var3;
      }
   }

   private static class GLFWGameErrorException extends RuntimeException {
      public final GLFWGameError error;

      public GLFWGameErrorException(GLFWGameError var1) {
         this.error = var1;
      }
   }

   public abstract static class Handler {
      protected final int[] errorCodes;

      public Handler(int... var1) {
         this.errorCodes = var1;
      }

      public abstract void run();

      public abstract void onCatch(GLFWGameError var1);

      public void onFinally() {
      }
   }

   public abstract static class Supplier<T> {
      protected final int[] errorCodes;

      public Supplier(int... var1) {
         this.errorCodes = var1;
      }

      public abstract T run();

      public abstract T onCatch(GLFWGameError var1);

      public void onFinally() {
      }
   }
}
