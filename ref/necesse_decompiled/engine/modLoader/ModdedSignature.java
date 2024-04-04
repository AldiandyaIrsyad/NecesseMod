package necesse.engine.modLoader;

import java.util.Objects;

public class ModdedSignature {
   public final String className;
   public final String methodName;
   public final boolean checkCLInit;

   public ModdedSignature(String var1, String var2) {
      Objects.requireNonNull(var1, "className cannot be null");
      this.className = var1;
      this.methodName = var2;
      this.checkCLInit = var2 != null && var2.equals("<init>");
   }

   public boolean matches(StackTraceElement var1) {
      if (this.className.equals(var1.getClassName())) {
         if (this.methodName == null) {
            return true;
         } else {
            return this.checkCLInit && "<clinit>".equals(var1.getMethodName()) ? true : this.methodName.equals(var1.getMethodName());
         }
      } else {
         return false;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ModdedSignature var2 = (ModdedSignature)var1;
         return Objects.equals(this.className, var2.className) && Objects.equals(this.methodName, var2.methodName);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.className, this.methodName});
   }
}
