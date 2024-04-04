package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemDouble extends GNDItem.GNDPrimitive {
   private double data;

   public GNDItemDouble(double var1) {
      this.data = var1;
   }

   public GNDItemDouble(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemDouble(LoadData var1) {
      this.data = var1.getDouble("value", 0.0);
   }

   public String toString() {
      return this.data + "d";
   }

   public boolean isDefault() {
      return this.data == 0.0;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.getDouble() == ((GNDItem.GNDPrimitive)var1).getDouble();
      } else {
         return false;
      }
   }

   public GNDItemDouble copy() {
      return new GNDItemDouble(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addDouble("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextDouble(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextDouble();
   }

   public boolean getBoolean() {
      return this.data != 0.0;
   }

   public byte getByte() {
      return (byte)((int)this.data);
   }

   public short getShort() {
      return (short)((int)this.data);
   }

   public int getInt() {
      return (int)this.data;
   }

   public long getLong() {
      return (long)this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public double getDouble() {
      return this.data;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
