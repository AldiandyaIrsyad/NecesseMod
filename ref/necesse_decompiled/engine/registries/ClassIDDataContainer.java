package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;

public class ClassIDDataContainer<C> implements IDDataContainer {
   protected final ClassIDData<? extends C> data;

   public ClassIDDataContainer(Class<? extends C> var1, Class<?>... var2) throws NoSuchMethodException {
      this.data = new ClassIDData(var1, var2);
   }

   public ClassIDData<? extends C> getIDData() {
      return this.data;
   }

   public C newInstance(Object... var1) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      return this.data.newInstance(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public IDData getIDData() {
      return this.getIDData();
   }
}
