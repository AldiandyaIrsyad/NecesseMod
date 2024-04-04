package necesse.level.maps.levelData.settlementData.storage;

import necesse.engine.registries.SettlementStorageIndexRegistry;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class SettlementStorageRecords {
   protected Level level;
   public final SettlementStorageIndex[] indexes;

   public SettlementStorageRecords(Level var1) {
      this.level = var1;
      this.indexes = SettlementStorageIndexRegistry.getNewIndexesArray(var1);
   }

   public <T extends SettlementStorageIndex> T getIndex(int var1, Class<T> var2) {
      return (SettlementStorageIndex)var2.cast(this.indexes[var1]);
   }

   public <T extends SettlementStorageIndex> T getIndex(Class<T> var1) {
      return this.getIndex(SettlementStorageIndexRegistry.getIndexID(var1), var1);
   }

   public void clear() {
      SettlementStorageIndex[] var1 = this.indexes;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SettlementStorageIndex var4 = var1[var3];
         var4.clear();
      }

   }

   public void add(InventoryItem var1, SettlementStorageRecord var2) {
      SettlementStorageIndex[] var3 = this.indexes;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SettlementStorageIndex var6 = var3[var5];
         var6.add(var1, var2);
      }

   }
}
