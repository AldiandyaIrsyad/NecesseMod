package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class GNDItemString extends GNDItem {
   private String data;

   public GNDItemString(String var1) {
      this.data = var1;
   }

   public GNDItemString(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemString(LoadData var1) {
      this.data = var1.getSafeString("value");
   }

   public String toString() {
      return this.data == null ? "" : this.data;
   }

   public boolean isDefault() {
      return this.data == null || this.data.length() == 0;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItemString) {
         return this.toString().equals(var1.toString());
      } else if (var1 instanceof GNDItem.GNDPrimitive) {
         double var2 = ((GNDItem.GNDPrimitive)var1).getDouble();
         return GameUtils.formatNumber(var2).equals(this.data);
      } else {
         return false;
      }
   }

   public GNDItemString copy() {
      return new GNDItemString(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addSafeString("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextString(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextString();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
