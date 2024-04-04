package necesse.engine.registries;

import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.save.LoadData;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.engine.world.worldData.WorldData;

public class WorldDataRegistry extends ClassedGameRegistry<WorldData, WorldDataRegistryElement> {
   public static final WorldDataRegistry instance = new WorldDataRegistry();

   private WorldDataRegistry() {
      super("WorldData", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "worlddata"));
      registerWorldData("settlers", SettlersWorldData.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerWorldData(String var0, Class<? extends WorldData> var1) {
      try {
         return instance.register(var0, new WorldDataRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         System.err.println("Could not register WorldData " + var1.getSimpleName() + ": Missing constructor with no parameters");
         return -1;
      }
   }

   public static WorldData loadWorldData(WorldEntity var0, LoadData var1) {
      String var2 = var1.hasLoadDataByName("stringID") ? var1.getFirstDataByName("stringID") : null;

      try {
         WorldData var3 = (WorldData)((WorldDataRegistryElement)instance.getElement(var2)).newInstance(new Object[0]);
         var3.setWorldEntity(var0);
         var3.applyLoadData(var1);
         return var3;
      } catch (Exception var4) {
         System.err.println("Could not instantiate world data with id " + var2);
         var4.printStackTrace();
         return null;
      }
   }

   protected static class WorldDataRegistryElement extends ClassIDDataContainer<WorldData> {
      public WorldDataRegistryElement(Class<? extends WorldData> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
