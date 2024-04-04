package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.level.maps.levelData.jobs.ConsumeFoodLevelJob;
import necesse.level.maps.levelData.jobs.FertilizeLevelJob;
import necesse.level.maps.levelData.jobs.FillApiaryFrameLevelJob;
import necesse.level.maps.levelData.jobs.FishingPositionLevelJob;
import necesse.level.maps.levelData.jobs.ForestryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestApiaryLevelJob;
import necesse.level.maps.levelData.jobs.HarvestCropLevelJob;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;
import necesse.level.maps.levelData.jobs.HuntMobLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.jobs.ManageEquipmentLevelJob;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.PlantCropLevelJob;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.StorePickupItemLevelJob;
import necesse.level.maps.levelData.jobs.UseWorkstationLevelJob;

public class LevelJobRegistry extends ClassedGameRegistry<LevelJob, RegistryItem> {
   public static final LevelJobRegistry instance = new LevelJobRegistry();
   public static int consumeFoodID;
   public static int equipArmorID;
   public static int hasStorageID;
   public static int haulFromID;
   public static int storePickupItemID;
   public static int useWorkstationID;
   public static int forestryID;
   public static int plantSaplingID;
   public static int fertilizeID;
   public static int slaughterHusbandryMobID;
   public static int milkHusbandryMobID;
   public static int shearHusbandryMobID;
   public static int harvestApiaryID;
   public static int fillApiaryFrameID;
   public static int harvestCropID;
   public static int plantCropID;
   public static int harvestFruitID;
   public static int fishingPositionID;
   public static int huntMobID;

   private LevelJobRegistry() {
      super("LevelJob", 32762);
   }

   public void registerCore() {
      consumeFoodID = registerJob("consumefood", ConsumeFoodLevelJob.class, "needs");
      equipArmorID = registerJob("manageequipment", ManageEquipmentLevelJob.class, "needs");
      hasStorageID = registerJob("hasstorage", HasStorageLevelJob.class, "needs", 100);
      haulFromID = registerJob("haulfrom", HaulFromLevelJob.class, "hauling");
      storePickupItemID = registerJob("storepickupitem", StorePickupItemLevelJob.class, "hauling", 100);
      useWorkstationID = registerJob("useworkstation", UseWorkstationLevelJob.class, "crafting", 0);
      forestryID = registerJob("forestry", ForestryLevelJob.class, "forestry");
      plantSaplingID = registerJob("plantsapling", PlantSaplingLevelJob.class, "forestry", 100);
      fertilizeID = registerJob("fertilize", FertilizeLevelJob.class, "fertilize");
      slaughterHusbandryMobID = registerJob("slaughterhusbandry", SlaughterHusbandryMobLevelJob.class, "husbandry");
      milkHusbandryMobID = registerJob("milkhusbandry", MilkHusbandryMobLevelJob.class, "husbandry");
      shearHusbandryMobID = registerJob("shearhusbandry", ShearHusbandryMobLevelJob.class, "husbandry");
      harvestApiaryID = registerJob("harvestapiary", HarvestApiaryLevelJob.class, "husbandry");
      fillApiaryFrameID = registerJob("fillapiaryframe", FillApiaryFrameLevelJob.class, "husbandry");
      fishingPositionID = registerJob("fishingposition", FishingPositionLevelJob.class, "fishing");
      harvestFruitID = registerJob("harvestfruit", HarvestFruitLevelJob.class, "farming");
      harvestCropID = registerJob("harvestcrop", HarvestCropLevelJob.class, "farming");
      plantCropID = registerJob("plantcrop", PlantCropLevelJob.class, "farming", 100);
      huntMobID = registerJob("huntmob", HuntMobLevelJob.class, "hunting");
   }

   protected void onRegistryClose() {
   }

   public static int registerJob(String var0, Class<? extends LevelJob> var1, String var2) {
      return registerJob(var0, var1, var2, 0);
   }

   public static int registerJob(String var0, Class<? extends LevelJob> var1, String var2, int var3) {
      try {
         return instance.register(var0, new RegistryItem(var1, var2, var3));
      } catch (NoSuchMethodException var5) {
         throw new RuntimeException(instance.objectCallName + " must have a constructor with LoadData parameter", var5);
      }
   }

   public static LevelJob loadJob(String var0, LoadData var1) {
      RegistryItem var2 = (RegistryItem)instance.getElement(var0);
      if (var2 == null) {
         throw new LoadDataException(instance.objectCallName + " not found with stringID \"" + var0 + "\"");
      } else {
         try {
            return (LevelJob)var2.newInstance(new Object[]{var1});
         } catch (InvocationTargetException | InstantiationException | IllegalAccessException var4) {
            throw new LoadDataException("Could not load " + instance.objectCallName, var4);
         }
      }
   }

   public static int getJobID(Class<? extends LevelJob> var0) {
      return instance.getElementID(var0);
   }

   public static int getJobTypeID(Class<? extends LevelJob> var0) {
      int var1 = instance.getElementID(var0);
      return var1 != -1 ? ((RegistryItem)instance.getElement(var1)).jobTypeID : -1;
   }

   protected static class RegistryItem extends ClassIDDataContainer<LevelJob> {
      public final int jobTypeID;
      public final int sameTypePriority;

      public RegistryItem(Class<? extends LevelJob> var1, String var2, int var3) throws NoSuchMethodException {
         super(var1, LoadData.class);
         this.jobTypeID = JobTypeRegistry.getJobTypeID(var2);
         if (this.jobTypeID == -1) {
            throw new IllegalArgumentException("Must first register job type with stringID \"" + var2 + "\"");
         } else {
            this.sameTypePriority = var3;
         }
      }
   }
}
