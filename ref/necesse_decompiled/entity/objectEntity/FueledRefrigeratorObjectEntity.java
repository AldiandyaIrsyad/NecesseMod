package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class FueledRefrigeratorObjectEntity extends InventoryObjectEntity {
   public static int FUEL_TIME_ADDED = 240000;
   public int fuelSlots;
   public float spoilRate;
   public boolean forceUpdate = true;
   protected int remainingFuelTime;
   protected int usedFuelTime;
   protected long lastTickedTime;

   public FueledRefrigeratorObjectEntity(Level var1, int var2, int var3, int var4, int var5, float var6) {
      super(var1, var2, var3, var5 + var4);
      this.fuelSlots = var4;
      this.spoilRate = var6;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("remainingFuelTime", this.remainingFuelTime);
      var1.addInt("usedFuelTime", this.usedFuelTime);
      var1.addLong("lastTickedTime", this.lastTickedTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.remainingFuelTime = var1.getInt("remainingFuelTime", 0);
      this.usedFuelTime = var1.getInt("usedFuelTime", 0);
      this.lastTickedTime = var1.getLong("lastTickedTime", 0L);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      var1.putNextInt(this.remainingFuelTime);
      var1.putNextInt(this.usedFuelTime);
      var1.putNextLong(this.lastTickedTime);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.remainingFuelTime = var1.getNextInt();
      this.usedFuelTime = var1.getNextInt();
      this.lastTickedTime = var1.getNextLong();
   }

   protected void onInventorySlotUpdated(int var1) {
      super.onInventorySlotUpdated(var1);
      this.forceUpdate = true;
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      if (var1 >= this.fuelSlots) {
         return super.isItemValid(var1, var2);
      } else {
         return var2 == null || this.isFuel(var2);
      }
   }

   public void init() {
      super.init();
      this.updateInventorySpoilRate();
   }

   public void clientTick() {
      super.clientTick();
      this.updateFuel();
   }

   public void serverTick() {
      super.serverTick();
      this.updateFuel();
   }

   public void updateFuel() {
      long var1 = this.getWorldTime();
      long var3 = this.lastTickedTime == 0L ? var1 : this.lastTickedTime;
      long var5 = Math.max(0L, var1 - var3);

      while(var5 > 0L || this.forceUpdate) {
         if (this.forceUpdate) {
            this.forceUpdate = false;
            if (this.remainingFuelTime <= 0) {
               this.tryUseFuel();
            }
         }

         boolean var7 = false;
         if (this.remainingFuelTime <= 0 && !this.tryUseFuel()) {
            var7 = true;
         }

         long var8 = Math.max(0L, GameMath.min((long)this.remainingFuelTime, var5));
         this.remainingFuelTime = (int)((long)this.remainingFuelTime - var8);
         this.usedFuelTime = (int)((long)this.usedFuelTime + var8);
         var5 -= var8;
         if (this.remainingFuelTime <= 0) {
            this.usedFuelTime = 0;
            if (var7) {
               break;
            }

            this.forceUpdate = true;
         }
      }

      this.updateInventorySpoilRate();
      this.lastTickedTime = var1;
   }

   public boolean tryUseFuel() {
      for(int var1 = 0; var1 < this.fuelSlots; ++var1) {
         InventoryItem var2 = this.inventory.getItem(var1);
         if (this.isFuel(var2)) {
            this.remainingFuelTime += FUEL_TIME_ADDED;
            var2.setAmount(var2.getAmount() - 1);
            if (var2.getAmount() <= 0) {
               this.inventory.setItem(var1, (InventoryItem)null);
            }

            this.inventory.markDirty(var1);
            return true;
         }
      }

      return false;
   }

   public boolean isFuel(InventoryItem var1) {
      return var1 != null && var1.item.isGlobalIngredient("anycoolingfuel");
   }

   public void updateInventorySpoilRate() {
      if (this.remainingFuelTime > 0) {
         this.inventory.spoilRateModifier = this.spoilRate;
      } else {
         this.inventory.spoilRateModifier = 1.0F;
      }

   }

   public boolean hasFuel() {
      return this.remainingFuelTime > 0;
   }

   public float getFuelProgress() {
      if (this.remainingFuelTime > 0) {
         int var1 = this.usedFuelTime + this.remainingFuelTime;
         return Math.abs(GameMath.limit((float)this.usedFuelTime / (float)var1, 0.0F, 1.0F) - 1.0F);
      } else {
         return 0.0F;
      }
   }

   public InventoryRange getSettlementStorage() {
      Inventory var1 = this.getInventory();
      return new InventoryRange(var1, this.fuelSlots, var1.getSize() - 1);
   }

   public InventoryRange getFuelInventoryRange() {
      return new InventoryRange(this.getInventory(), 0, this.fuelSlots - 1);
   }

   public InventoryRange getSettlementFuelInventoryRange() {
      return this.getFuelInventoryRange();
   }

   public SettlementRequestOptions getSettlementFuelRequestOptions() {
      return new SettlementRequestOptions(5, 10) {
         public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords var1) {
            return ((SettlementStorageGlobalIngredientIDIndex)var1.getIndex(SettlementStorageGlobalIngredientIDIndex.class)).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anycoolingfuel"));
         }
      };
   }
}
