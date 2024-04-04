package necesse.engine.registries;

import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.save.LoadData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.PathBreakDownLevelData;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.VillageShopsData;

public class LevelDataRegistry extends ClassedGameRegistry<LevelData, LevelDataRegistryElement> {
   public static final LevelDataRegistry instance = new LevelDataRegistry();

   private LevelDataRegistry() {
      super("LevelData", 32762);
   }

   public void registerCore() {
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "leveldata"));
      registerLevelData("settlement", SettlementLevelData.class);
      registerLevelData("villageshops", VillageShopsData.class);
      registerLevelData("jobs", JobsLevelData.class);
      registerLevelData("pathbreak", PathBreakDownLevelData.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerLevelData(String var0, Class<? extends LevelData> var1) {
      try {
         return instance.register(var0, new LevelDataRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         System.err.println("Could not register LevelData " + var1.getSimpleName() + ": Missing constructor with no parameters");
         return -1;
      }
   }

   public static LevelData loadLevelData(Level var0, LoadData var1) {
      String var2 = var1.hasLoadDataByName("stringID") ? var1.getUnsafeString("stringID") : null;

      try {
         LevelData var3 = (LevelData)((LevelDataRegistryElement)instance.getElement(var2)).newInstance(new Object[0]);
         var3.setLevel(var0);
         var3.applyLoadData(var1);
         return var3;
      } catch (Exception var4) {
         System.err.println("Could not instantiate level data with id " + var2);
         var4.printStackTrace();
         return null;
      }
   }

   protected static class LevelDataRegistryElement extends ClassIDDataContainer<LevelData> {
      public LevelDataRegistryElement(Class<? extends LevelData> var1) throws NoSuchMethodException {
         super(var1);
      }
   }
}
