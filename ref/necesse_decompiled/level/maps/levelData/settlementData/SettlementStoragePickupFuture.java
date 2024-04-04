package necesse.level.maps.levelData.settlementData;

import necesse.inventory.InventoryItem;

public abstract class SettlementStoragePickupFuture {
   public final LevelStorage storage;
   public final InventoryItem item;

   public SettlementStoragePickupFuture(LevelStorage var1, InventoryItem var2) {
      this.storage = var1;
      this.item = var2;
   }

   public abstract SettlementStoragePickupSlot accept(int var1);
}
