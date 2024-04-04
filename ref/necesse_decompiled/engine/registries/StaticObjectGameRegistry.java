package necesse.engine.registries;

import java.util.NoSuchElementException;

public abstract class StaticObjectGameRegistry<T extends IDDataContainer> extends GameRegistry<T> {
   public StaticObjectGameRegistry(String var1, int var2) {
      super(var1, var2);
   }

   public StaticObjectGameRegistry(String var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   public int registerObject(String var1, T var2) {
      return this.register(var1, var2);
   }

   public T getObject(int var1) {
      try {
         return this.getElement(var1);
      } catch (NoSuchElementException var3) {
         return null;
      }
   }

   public T getObject(String var1) {
      int var2 = this.getElementID(var1);
      return var2 == -1 ? null : this.getObject(var2);
   }

   public int getObjectID(String var1) {
      return this.getElementID(var1);
   }
}
