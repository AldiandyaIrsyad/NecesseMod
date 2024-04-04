package necesse.engine.modLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModMethod {
   private final LoadedMod mod;
   private final Class<? extends Annotation> annotationClass;
   private final Object caller;
   private final Method method;

   public ModMethod(LoadedMod var1, Class<? extends Annotation> var2, Object var3, Method var4) {
      this.mod = var1;
      this.annotationClass = var2;
      this.caller = var3;
      this.method = var4;
   }

   public ModMethod(LoadedMod var1, Class<? extends Annotation> var2, Object var3, Class<?> var4, String var5, Class<?>... var6) {
      this.mod = var1;
      this.annotationClass = var2;
      this.caller = var3;

      Method var7;
      try {
         var7 = var4.getMethod(var5, var6);
      } catch (NoSuchMethodException var9) {
         var7 = null;
      }

      this.method = var7;
   }

   public boolean foundMethod() {
      return this.method != null;
   }

   public Class<?> getReturnType() {
      return this.method.getReturnType();
   }

   public Object invoke(Object... var1) {
      LoadedMod.runningMod = this.mod;

      Object var2;
      try {
         if (this.method != null) {
            var2 = this.method.invoke(this.caller, var1);
            return var2;
         }

         var2 = null;
      } catch (InvocationTargetException | IllegalAccessException var6) {
         if (var6.getCause() != null) {
            throw new ModRuntimeException(this.mod, "Error in running mod " + this.mod.id + " @" + this.getAnnotationClassName() + " " + this.method.getName() + ":", var6.getCause());
         }

         throw new ModRuntimeException(this.mod, "Unknown error in running mod " + this.mod.id + " @" + this.getAnnotationClassName() + " " + this.method.getName(), var6);
      } finally {
         LoadedMod.runningMod = null;
      }

      return var2;
   }

   private String getAnnotationClassName() {
      return this.annotationClass == null ? "NULL" : this.annotationClass.getSimpleName();
   }
}
