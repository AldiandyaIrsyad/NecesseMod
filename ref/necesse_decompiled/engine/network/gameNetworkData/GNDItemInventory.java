package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class GNDItemInventory extends GNDItem {
   public Inventory inventory;

   public GNDItemInventory(Inventory var1) {
      this.inventory = var1;
   }

   public GNDItemInventory(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemInventory(LoadData var1) {
      this.inventory = InventorySave.loadSave(var1.getFirstLoadDataByName("value"));
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder("inv[")).append(this.inventory.getSize()).append("]{");

      for(int var2 = 0; var2 < this.inventory.getSize(); ++var2) {
         if (!this.inventory.isSlotClear(var2)) {
            var1.append("[").append(var2).append(":");
            var1.append(this.toString(this.inventory.getItem(var2)));
            var1.append("]");
         }
      }

      var1.append("}");
      return var1.toString();
   }

   private String toString(InventoryItem var1) {
      return var1.item.getStringID() + ":" + var1.getAmount() + ":" + var1.isLocked() + ":" + var1.isNew() + ":" + var1.getGndData().toString();
   }

   public boolean isDefault() {
      for(int var1 = 0; var1 < this.inventory.getSize(); ++var1) {
         if (this.inventory.getAmount(var1) > 0) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItemInventory) {
         GNDItemInventory var2 = (GNDItemInventory)var1;
         if (this.inventory.getSize() != var2.inventory.getSize()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.inventory.getSize(); ++var3) {
               if (this.inventory.isSlotClear(var3) != var2.inventory.isSlotClear(var3)) {
                  return false;
               }

               if (!this.inventory.isSlotClear(var3) && !this.inventory.getItem(var3).equals((Level)null, var2.inventory.getItem(var3), "equals")) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public GNDItemInventory copy() {
      return new GNDItemInventory(Inventory.getInventory(this.inventory.getContentPacket()));
   }

   public void addSaveData(SaveData var1) {
      var1.addSaveData(InventorySave.getSave(this.inventory, "value"));
   }

   public void writePacket(PacketWriter var1) {
      this.inventory.writeContent(var1);
   }

   public void readPacket(PacketReader var1) {
      if (this.inventory == null) {
         this.inventory = Inventory.getInventory(var1);
      } else {
         this.inventory.override(Inventory.getInventory(var1), true, true);
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
