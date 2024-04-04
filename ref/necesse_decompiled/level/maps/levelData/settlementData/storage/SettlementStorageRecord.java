package necesse.level.maps.levelData.settlementData.storage;

import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.LevelStorage;

public class SettlementStorageRecord {
   public final LevelStorage storage;
   public final int inventorySlot;
   private InventoryItem item;
   public int itemAmount;

   public SettlementStorageRecord(LevelStorage var1, int var2, InventoryItem var3, int var4) {
      this.storage = var1;
      this.inventorySlot = var2;
      this.item = var3;
      this.itemAmount = var4;
   }

   public InventoryItem getItem() {
      return this.item;
   }
}
