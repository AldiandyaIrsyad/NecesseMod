package necesse.engine.modLoader;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ModLoadException extends Exception {
   public final LoadedMod mod;

   public ModLoadException(LoadedMod var1, String var2) {
      super(var2);
      this.mod = var1;
   }

   public ModLoadException(LoadedMod var1, String var2, Throwable var3) {
      super(var2, var3);
      this.mod = var1;
   }

   public void printStackTrace(PrintStream var1) {
      if (this.getCause() != null) {
         this.getCause().printStackTrace(var1);
      } else {
         super.printStackTrace(var1);
      }

   }

   public void printStackTrace(PrintWriter var1) {
      if (this.getCause() != null) {
         this.getCause().printStackTrace(var1);
      } else {
         super.printStackTrace(var1);
      }

   }
}
