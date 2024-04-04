package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.incursion.IncursionData;

public class GNDIncursionDataItem extends GNDItem {
   public IncursionData incursionData;

   public GNDIncursionDataItem(IncursionData var1) {
      this.incursionData = var1;
   }

   public GNDIncursionDataItem(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDIncursionDataItem(LoadData var1) {
      try {
         this.incursionData = IncursionData.fromLoadData(var1.getFirstLoadDataByName("value"));
      } catch (Exception var3) {
         this.incursionData = null;
      }

   }

   public String toString() {
      return this.incursionData == null ? "NULL" : this.incursionData.getStringID() + ":" + this.incursionData;
   }

   public boolean isDefault() {
      return this.incursionData == null;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDIncursionDataItem) {
         GNDIncursionDataItem var2 = (GNDIncursionDataItem)var1;
         if (this.incursionData == var2.incursionData) {
            return true;
         } else {
            return this.incursionData != null && var2.incursionData != null ? this.incursionData.isSameIncursion(var2.incursionData) : false;
         }
      } else {
         return false;
      }
   }

   public GNDItem copy() {
      return new GNDIncursionDataItem(this.incursionData == null ? null : IncursionData.makeCopy(this.incursionData));
   }

   public void addSaveData(SaveData var1) {
      if (this.incursionData != null) {
         SaveData var2 = new SaveData("value");
         this.incursionData.addSaveData(var2);
         var1.addSaveData(var2);
      }

   }

   public void writePacket(PacketWriter var1) {
      IncursionData.writePacket(this.incursionData, var1);
   }

   public void readPacket(PacketReader var1) {
      this.incursionData = IncursionData.fromPacket(var1);
   }
}
