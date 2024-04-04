package necesse.engine.modLoader.classes;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;

public abstract class SingleModClass extends ModClass {
   private final boolean requiredClass;
   private Class modClass;

   public SingleModClass(boolean var1) {
      this.requiredClass = var1;
   }

   public Class getModClass() {
      return this.modClass;
   }

   public final void registerModClass(LoadedMod var1, Class<?> var2) throws ModLoadException {
      if (this.modClass != null) {
         throw new ModLoadException(var1, this.getErrorName() + " cannot be used multiple times.");
      } else {
         this.modClass = var2;
      }
   }

   public final void finalizeLoading(LoadedMod var1) throws ModLoadException {
      if (this.requiredClass && this.modClass == null) {
         throw new ModLoadException(var1, "Did not contain a " + this.getErrorName());
      } else {
         this.finalizeSingleLoading(var1);
      }
   }

   public abstract String getErrorName();

   public abstract void finalizeSingleLoading(LoadedMod var1) throws ModLoadException;
}
