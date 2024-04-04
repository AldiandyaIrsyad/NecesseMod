package necesse.engine.modLoader.classes;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModMethod;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.modLoader.annotations.ModEntry;

public class EntryClass extends SingleModClass {
   private LoadedMod mod;
   private ModMethod preInit;
   private ModMethod init;
   private ModMethod initResources;
   private ModMethod postInit;
   private ModMethod initSettings;
   private ModMethod dispose;

   public EntryClass() {
      super(true);
   }

   public boolean shouldRegisterModClass(Class<?> var1) {
      return var1.isAnnotationPresent(ModEntry.class);
   }

   public String getErrorName() {
      return "@ModEntry annotation";
   }

   public void finalizeSingleLoading(LoadedMod var1) throws ModLoadException {
      this.mod = var1;

      try {
         Class var2 = this.getModClass();
         Object var3 = var2.getDeclaredConstructor().newInstance();
         this.preInit = new ModMethod(var1, ModEntry.class, var3, var2, "preInit", new Class[0]);
         this.init = new ModMethod(var1, ModEntry.class, var3, var2, "init", new Class[0]);
         this.initResources = new ModMethod(var1, ModEntry.class, var3, var2, "initResources", new Class[0]);
         this.postInit = new ModMethod(var1, ModEntry.class, var3, var2, "postInit", new Class[0]);
         this.initSettings = new ModMethod(var1, ModEntry.class, var3, var2, "initSettings", new Class[0]);
         if (this.initSettings.foundMethod() && !ModSettings.class.isAssignableFrom(this.initSettings.getReturnType())) {
            throw new ModLoadException(var1, "initSettings must return a ModSettings class");
         } else {
            this.dispose = new ModMethod(var1, ModEntry.class, var3, var2, "dispose", new Class[0]);
         }
      } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException var4) {
         throw new ModLoadException(var1, "Error loading ModEntry class", var4);
      }
   }

   public void preInit() {
      GameLoadingScreen.drawLoadingSub(this.mod.name);
      this.preInit.invoke();
   }

   public void init() {
      GameLoadingScreen.drawLoadingSub(this.mod.name);
      this.init.invoke();
   }

   public void initResources() {
      GameLoadingScreen.drawLoadingSub(this.mod.name);
      this.initResources.invoke();
   }

   public void postInit() {
      GameLoadingScreen.drawLoadingSub(this.mod.name);
      this.postInit.invoke();
   }

   public ModSettings initSettings() {
      GameLoadingScreen.drawLoadingSub(this.mod.name);
      return (ModSettings)this.initSettings.invoke();
   }

   public void dispose() {
      this.dispose.invoke();
   }
}
