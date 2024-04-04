package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageEquipmentTypeIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageFoodQualityIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;

public class SettlementStorageIndexRegistry extends ClassedGameRegistry<SettlementStorageIndex, IndexRegistryElement> {
   public static int ITEM_IDS;
   public static int GLOBAL_INGREDIENT_IDS;
   public static int FOOD_QUALITY;
   public static int ARMOR_TYPE;
   public static final SettlementStorageIndexRegistry instance = new SettlementStorageIndexRegistry();

   public SettlementStorageIndexRegistry() {
      super("SettlementStorageIndex", 32767);
   }

   public void registerCore() {
      ITEM_IDS = registerIndex("itemids", SettlementStorageItemIDIndex.class);
      GLOBAL_INGREDIENT_IDS = registerIndex("globalingredientids", SettlementStorageGlobalIngredientIDIndex.class);
      FOOD_QUALITY = registerIndex("foodquality", SettlementStorageFoodQualityIndex.class);
      ARMOR_TYPE = registerIndex("armortype", SettlementStorageEquipmentTypeIndex.class);
   }

   protected void onRegistryClose() {
   }

   public static int registerIndex(String var0, Class<? extends SettlementStorageIndex> var1) {
      try {
         return instance.register(var0, new IndexRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         throw new IllegalArgumentException(var1.getSimpleName() + " does not have a constructor with level parameter");
      }
   }

   public static int replaceIndex(String var0, Class<? extends SettlementStorageIndex> var1) {
      try {
         return instance.replace(var0, new IndexRegistryElement(var1));
      } catch (NoSuchMethodException var3) {
         throw new IllegalArgumentException(var1.getSimpleName() + " does not have a constructor with level parameter");
      }
   }

   public static int getIndexID(Class<? extends SettlementStorageIndex> var0) {
      return instance.getElementID(var0);
   }

   public static int getIndexID(String var0) {
      return instance.getElementID(var0);
   }

   public static SettlementStorageIndex[] getNewIndexesArray(Level var0) {
      SettlementStorageIndex[] var1 = new SettlementStorageIndex[instance.size()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         IndexRegistryElement var3 = (IndexRegistryElement)instance.getElement(var2);

         try {
            SettlementStorageIndex var4 = (SettlementStorageIndex)var3.newInstance(new Object[]{var0});
            var4.idData.setData(var3.getIDData().getID(), var3.getIDData().getStringID());
            var1[var2] = var4;
         } catch (InvocationTargetException | InstantiationException | IllegalAccessException var5) {
            throw new RuntimeException("Could not create new " + instance.objectCallName + " object for " + var3.indexClass.getSimpleName(), var5);
         }
      }

      return var1;
   }

   protected static class IndexRegistryElement extends ClassIDDataContainer<SettlementStorageIndex> {
      private Class<? extends SettlementStorageIndex> indexClass;

      public IndexRegistryElement(Class<? extends SettlementStorageIndex> var1) throws NoSuchMethodException {
         super(var1, Level.class);
         this.indexClass = var1;
      }
   }
}
