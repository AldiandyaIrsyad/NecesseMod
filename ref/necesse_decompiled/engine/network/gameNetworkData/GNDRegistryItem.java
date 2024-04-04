package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class GNDRegistryItem extends GNDItem {
   protected int offsetID;

   public GNDRegistryItem(String var1) {
      try {
         this.setRegistryID(this.toID(var1));
      } catch (Exception var3) {
         this.offsetID = 0;
      }

   }

   public GNDRegistryItem(int var1) {
      this.setRegistryID(var1);
   }

   public GNDRegistryItem(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDRegistryItem(LoadData var1) {
      if (var1.hasLoadDataByName("value")) {
         this.setRegistryID(this.toID(var1.getUnsafeString("value")));
      } else {
         this.offsetID = 0;
      }

   }

   public int getRegistryID() {
      return this.offsetID - 1;
   }

   public void setRegistryID(int var1) {
      this.offsetID = Math.max(0, var1 + 1);
   }

   public String toString() {
      return this.offsetID <= 0 ? "null" : this.toStringID(this.getRegistryID());
   }

   public boolean isDefault() {
      return this.offsetID <= 0;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDRegistryItem) {
         return this.offsetID == ((GNDRegistryItem)var1).offsetID;
      } else if (var1 instanceof GNDItemString) {
         return this.toString().equals(var1.toString());
      } else if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.offsetID == Math.max(0, ((GNDItem.GNDPrimitive)var1).getInt() + 1);
      } else {
         return false;
      }
   }

   public void addSaveData(SaveData var1) {
      try {
         if (this.offsetID <= 0) {
            throw new RuntimeException();
         }

         var1.addUnsafeString("value", this.toStringID(this.getRegistryID()));
      } catch (Exception var4) {
         String var3 = this.getDefaultStringID();
         if (var3 != null) {
            var1.addUnsafeString("value", var3);
         }
      }

   }

   public void writePacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.offsetID);
   }

   public void readPacket(PacketReader var1) {
      this.offsetID = var1.getNextShortUnsigned();
   }

   protected abstract int toID(String var1);

   protected abstract String toStringID(int var1);

   protected String getDefaultStringID() {
      return null;
   }
}
