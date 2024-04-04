package necesse.level.maps.levelData.settlementData;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.level.maps.Level;

public abstract class SettlementOEInventory extends LevelStorage {
   public OEInventory oeInventory;

   public SettlementOEInventory(Level var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3);
      if (var4) {
         this.refreshOEInventory();
      }

   }

   public void refreshOEInventory() {
      ObjectEntity var1 = this.level.entityManager.getObjectEntity(this.tileX, this.tileY);
      if (var1 instanceof OEInventory) {
         this.oeInventory = (OEInventory)var1;
      } else {
         this.oeInventory = null;
      }

   }

   public GameMessage getInventoryName() {
      this.refreshOEInventory();
      return this.oeInventory == null ? this.level.getObjectName(this.tileX, this.tileY) : this.oeInventory.getInventoryName();
   }
}
