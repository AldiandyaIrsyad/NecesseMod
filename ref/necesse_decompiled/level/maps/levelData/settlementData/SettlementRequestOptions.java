package necesse.level.maps.levelData.settlementData;

import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public abstract class SettlementRequestOptions {
   public final int minAmount;
   public final int maxAmount;

   public SettlementRequestOptions(int var1, int var2) {
      this.minAmount = var1;
      this.maxAmount = var2;
   }

   public abstract SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords var1);
}
