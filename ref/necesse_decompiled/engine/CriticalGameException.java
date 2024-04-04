package necesse.engine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;

public class CriticalGameException extends RuntimeException {
   public CriticalGameException(Error var1) {
      super(var1);
      Objects.requireNonNull(var1);
   }

   public String getMessage() {
      return this.getCause().getMessage();
   }

   public String getLocalizedMessage() {
      return this.getCause().getLocalizedMessage();
   }

   public String toString() {
      return this.getCause().toString();
   }

   public void printStackTrace() {
      this.getCause().printStackTrace();
   }

   public void printStackTrace(PrintStream var1) {
      this.getCause().printStackTrace(var1);
   }

   public void printStackTrace(PrintWriter var1) {
      this.getCause().printStackTrace(var1);
   }
}
