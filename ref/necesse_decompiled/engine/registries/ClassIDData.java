package necesse.engine.registries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassIDData<C> extends IDData {
   public final Class<? extends C> aClass;
   public final Constructor<? extends C> constructor;

   public ClassIDData(Class<? extends C> var1, Class<?>... var2) throws NoSuchMethodException {
      this.aClass = var1;
      this.constructor = var1.getConstructor(var2);
   }

   public C newInstance(Object... var1) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      return this.constructor.newInstance(var1);
   }
}
