package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AnyLogFueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class FueledCraftingStationObject extends CraftingStationObject {
   public FueledCraftingStationObject() {
   }

   public FueledCraftingStationObject(Rectangle var1) {
      super(var1);
   }

   public FueledInventoryObjectEntity getFueledObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof FueledInventoryObjectEntity ? (FueledInventoryObjectEntity)var4 : null;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         CraftingStationContainer.openAndSendContainer(ContainerRegistry.FUELED_CRAFTING_STATION_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new AnyLogFueledInventoryObjectEntity(var1, this.getStringID(), var2, var3, false);
   }

   public boolean canCurrentlyCraft(Level var1, int var2, int var3, Recipe var4) {
      FueledInventoryObjectEntity var5 = this.getFueledObjectEntity(var1, var2, var3);
      return var5 != null && (var5.isFueled() || var5.canFuel());
   }

   public void tickCrafting(Level var1, int var2, int var3, Recipe var4) {
      FueledInventoryObjectEntity var5 = this.getFueledObjectEntity(var1, var2, var3);
      if (var5 != null && !var5.isFueled()) {
         var5.useFuel();
      }

   }

   public void onCraftFinished(Level var1, int var2, int var3, Recipe var4) {
      FueledInventoryObjectEntity var5 = this.getFueledObjectEntity(var1, var2, var3);
      if (var5 != null && !var5.isFueled()) {
         var5.useFuel();
      }

   }

   public SettlementRequestOptions getFuelRequestOptions(Level var1, int var2, int var3) {
      return new SettlementRequestOptions(5, 10) {
         public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords var1) {
            return ((SettlementStorageGlobalIngredientIDIndex)var1.getIndex(SettlementStorageGlobalIngredientIDIndex.class)).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anylog"));
         }
      };
   }

   public InventoryRange getFuelInventoryRange(Level var1, int var2, int var3) {
      FueledInventoryObjectEntity var4 = this.getFueledObjectEntity(var1, var2, var3);
      Inventory var5 = var4.getInventory();
      return var5 != null ? new InventoryRange(var5) : null;
   }
}
