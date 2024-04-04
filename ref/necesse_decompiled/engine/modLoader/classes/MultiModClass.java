package necesse.engine.modLoader.classes;

import java.util.LinkedList;
import java.util.List;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;

public abstract class MultiModClass extends ModClass {
   private final boolean requiredClass;
   private List<Class<?>> modClasses = new LinkedList();

   public MultiModClass(boolean var1) {
      this.requiredClass = var1;
   }

   public Iterable<Class<?>> getModClasses() {
      return this.modClasses;
   }

   public final void registerModClass(LoadedMod var1, Class<?> var2) {
      this.modClasses.add(var2);
   }

   public final void finalizeLoading(LoadedMod var1) throws ModLoadException {
      if (this.requiredClass && this.modClasses.size() == 0) {
         throw new ModLoadException(var1, var1.id + " did not contain a " + this.getErrorName());
      } else {
         this.finalizeMultiLoading(var1);
      }
   }

   public abstract String getErrorName();

   public abstract void finalizeMultiLoading(LoadedMod var1) throws ModLoadException;
}
