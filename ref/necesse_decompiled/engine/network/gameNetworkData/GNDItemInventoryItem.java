package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GNDItemInventoryItem extends GNDItem {
   public InventoryItem invItem;

   public GNDItemInventoryItem(InventoryItem var1) {
      this.invItem = var1;
   }

   public GNDItemInventoryItem(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemInventoryItem(LoadData var1) {
      this.invItem = InventoryItem.fromLoadData(var1.getFirstLoadDataByName("value"));
   }

   public String toString() {
      return this.invItem == null ? "NULL" : this.invItem.item.getStringID() + ":" + this.invItem.getAmount() + ":" + this.invItem.isLocked() + ":" + this.invItem.isNew() + ":" + this.invItem.getGndData().toString();
   }

   public boolean isDefault() {
      return this.invItem == null;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItemInventoryItem) {
         GNDItemInventoryItem var2 = (GNDItemInventoryItem)var1;
         if (this.invItem == var2.invItem) {
            return true;
         } else {
            return this.invItem != null ? this.invItem.equals((Level)null, var2.invItem, "equals") : false;
         }
      } else {
         return false;
      }
   }

   public GNDItem copy() {
      return new GNDItemInventoryItem(this.invItem == null ? null : this.invItem.copy());
   }

   public void addSaveData(SaveData var1) {
      if (this.invItem != null) {
         SaveData var2 = new SaveData("value");
         this.invItem.addSaveData(var2);
         var1.addSaveData(var2);
      }

   }

   public void writePacket(PacketWriter var1) {
      InventoryItem.addPacketContent(this.invItem, var1);
   }

   public void readPacket(PacketReader var1) {
      this.invItem = InventoryItem.fromContentPacket(var1);
   }
}
