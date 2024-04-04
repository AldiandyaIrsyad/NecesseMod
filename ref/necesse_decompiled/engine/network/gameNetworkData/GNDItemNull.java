package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemNull extends GNDItem {
   public GNDItemNull() {
   }

   public GNDItemNull(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemNull(LoadData var1) {
   }

   public String toString() {
      return "null";
   }

   public boolean isDefault() {
      return true;
   }

   public boolean equals(GNDItem var1) {
      return var1 == null || var1 instanceof GNDItemNull;
   }

   public GNDItemNull copy() {
      return new GNDItemNull();
   }

   public void addSaveData(SaveData var1) {
   }

   public void writePacket(PacketWriter var1) {
   }

   public void readPacket(PacketReader var1) {
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
