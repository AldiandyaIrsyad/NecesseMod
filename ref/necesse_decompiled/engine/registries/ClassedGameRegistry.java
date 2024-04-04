package necesse.engine.registries;

import java.util.HashMap;
import java.util.NoSuchElementException;

public abstract class ClassedGameRegistry<C, T extends ClassIDDataContainer<? extends C>> extends GameRegistry<T> {
   private final HashMap<Class<? extends C>, Integer> classToIDMap = new HashMap();

   public ClassedGameRegistry(String var1, int var2) {
      super(var1, var2);
   }

   public ClassedGameRegistry(String var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected void onRegister(T var1, int var2, String var3, boolean var4) {
      this.classToIDMap.put(var1.data.aClass, var2);
   }

   public int getElementID(Class<? extends C> var1) {
      try {
         return this.getElementIDRaw(var1);
      } catch (NoSuchElementException var3) {
         return -1;
      }
   }

   public int getElementIDRaw(Class<? extends C> var1) throws NoSuchElementException {
      Integer var2 = (Integer)this.classToIDMap.get(var1);
      if (var2 == null) {
         throw new NoSuchElementException();
      } else {
         return var2;
      }
   }

   public void applyIDData(Class<? extends C> var1, IDData var2) {
      try {
         int var3 = this.getElementIDRaw(var1);
         ClassIDData var4 = ((ClassIDDataContainer)this.getElement(var3)).data;
         var2.setData(var4.getID(), var4.getStringID());
      } catch (NoSuchElementException var5) {
         throw new IllegalStateException("Cannot construct unregistered " + this.objectCallName + " class " + var1.getSimpleName());
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((ClassIDDataContainer)var1, var2, var3, var4);
   }
}
