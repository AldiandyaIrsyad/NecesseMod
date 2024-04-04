package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.modLoader.LoadedMod;

public abstract class EmptyConstructorGameRegistry<T extends IDDataContainer> extends ClassedGameRegistry<T, ClassIDDataContainer<T>> {
   public EmptyConstructorGameRegistry(String var1, int var2) {
      super(var1, var2);
   }

   public EmptyConstructorGameRegistry(String var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   public int registerClass(String var1, Class<? extends T> var2) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register " + this.objectCallName);
      } else {
         try {
            return this.register(var1, new EmptyConstructorClass(var2));
         } catch (NoSuchMethodException var4) {
            System.err.println("Could not register " + this.objectCallName + " " + var2.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   public T getNewInstance(int var1) {
      try {
         ClassIDDataContainer var2 = (ClassIDDataContainer)this.getElement(var1);
         return var2 == null ? null : (IDDataContainer)var2.newInstance();
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public T getNewInstance(String var1) {
      int var2 = this.getElementID(var1);
      return var2 == -1 ? null : this.getNewInstance(var2);
   }

   private class EmptyConstructorClass extends ClassIDDataContainer<T> {
      public EmptyConstructorClass(Class<? extends T> var2) throws NoSuchMethodException {
         super(var2);
      }
   }
}
