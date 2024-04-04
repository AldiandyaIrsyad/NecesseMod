package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeHuntIncursionData;
import necesse.level.maps.incursion.BiomeTrialIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class IncursionDataRegistry extends ClassedGameRegistry<IncursionData, IncursionDataRegistryElement> {
   public static final IncursionDataRegistry instance = new IncursionDataRegistry();

   private IncursionDataRegistry() {
      super("IncursionData", 250);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "incursions"));
      registerIncursionData("hunt", BiomeHuntIncursionData.class);
      registerIncursionData("extraction", BiomeExtractionIncursionData.class);
      registerIncursionData("trial", BiomeTrialIncursionData.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerIncursionData(String var0, Class<? extends IncursionData> var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register pickups");
      } else {
         try {
            return instance.register(var0, new IncursionDataRegistryElement(var1));
         } catch (NoSuchMethodException var3) {
            System.err.println("Could not register PickupEntity " + var1.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
         }
      }
   }

   public static IncursionData getNewIncursionData(int var0) {
      try {
         return (IncursionData)((IncursionDataRegistryElement)instance.getElement(var0)).newInstance(new Object[0]);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static IncursionData getNewIncursionData(String var0) {
      return getNewIncursionData(getIncursionDataID(var0));
   }

   public static <C extends IncursionData> void applyIncursionDataIDData(C var0) {
      instance.applyIDData(var0.getClass(), var0.idData);
   }

   public static int getIncursionDataID(String var0) {
      return instance.getElementID(var0);
   }

   public static int getIncursionDataID(Class<? extends IncursionData> var0) {
      return instance.getElementID(var0);
   }

   public static String getIncursionDataStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   protected static class IncursionDataRegistryElement extends ClassIDDataContainer<IncursionData> {
      public IncursionDataRegistryElement(Class<? extends IncursionData> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
