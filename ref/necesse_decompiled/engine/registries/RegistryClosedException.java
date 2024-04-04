package necesse.engine.registries;

public class RegistryClosedException extends RuntimeException {
   public RegistryClosedException() {
   }

   public RegistryClosedException(String var1) {
      super(var1);
   }

   public RegistryClosedException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public RegistryClosedException(Throwable var1) {
      super(var1);
   }

   public RegistryClosedException(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
