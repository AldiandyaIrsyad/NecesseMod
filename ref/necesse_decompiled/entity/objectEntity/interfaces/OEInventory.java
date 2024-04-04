package necesse.entity.objectEntity.interfaces;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketOEInventoryUpdate;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.server.Server;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;

public interface OEInventory {
   default void triggerInteracted() {
   }

   Inventory getInventory();

   GameMessage getInventoryName();

   default void setInventoryName(String var1) {
   }

   default boolean canSetInventoryName() {
      return false;
   }

   default boolean canQuickStackInventory() {
      return true;
   }

   default boolean canRestockInventory() {
      return true;
   }

   default boolean canSortInventory() {
      return true;
   }

   default boolean canUseForNearbyCrafting() {
      return true;
   }

   default InventoryRange getSettlementStorage() {
      Inventory var1 = this.getInventory();
      return var1 != null ? new InventoryRange(var1) : null;
   }

   default SettlementRequestOptions getSettlementFuelRequestOptions() {
      return null;
   }

   default InventoryRange getSettlementFuelInventoryRange() {
      return null;
   }

   default boolean isSettlementStorageItemDisabled(Item var1) {
      return false;
   }

   default void setupDefaultSettlementStorage(SettlementInventory var1) {
   }

   default void serverTickInventorySync(Server var1, ObjectEntity var2) {
      if (var1 != null) {
         Inventory var3 = this.getInventory();
         if (var3.isDirty()) {
            if (var3.isFullDirty()) {
               var1.network.sendToClientsAt(new PacketObjectEntity(var2), (Level)var2.getLevel());
               var3.clean();
            } else {
               for(int var4 = 0; var4 < var3.getSize(); ++var4) {
                  if (var3.isDirty(var4)) {
                     var1.network.sendToClientsAt(new PacketOEInventoryUpdate(this, var4), (Level)var2.getLevel());
                     var3.clean(var4);
                  }
               }
            }
         }

      }
   }
}
