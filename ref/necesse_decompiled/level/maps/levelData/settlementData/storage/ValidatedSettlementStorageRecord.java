package necesse.level.maps.levelData.settlementData.storage;

import necesse.engine.util.GameLinkedList;
import necesse.inventory.InventoryItem;

public class ValidatedSettlementStorageRecord {
   public final GameLinkedList<SettlementStorageRecord>.Element element;
   public final SettlementStorageRecord record;
   public final InventoryItem invItem;

   public ValidatedSettlementStorageRecord(SettlementStorageRecord var1, InventoryItem var2) {
      this.element = null;
      this.record = var1;
      this.invItem = var2;
   }

   public ValidatedSettlementStorageRecord(GameLinkedList<SettlementStorageRecord>.Element var1, InventoryItem var2) {
      this.element = var1;
      this.record = (SettlementStorageRecord)var1.object;
      this.invItem = var2;
   }
}
