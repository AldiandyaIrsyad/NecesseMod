package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemFloat extends GNDItem.GNDPrimitive {
   private float data;

   public GNDItemFloat(float var1) {
      this.data = var1;
   }

   public GNDItemFloat(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemFloat(LoadData var1) {
      this.data = var1.getFloat("value", 0.0F);
   }

   public String toString() {
      return this.data + "f";
   }

   public boolean isDefault() {
      return this.data == 0.0F;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.getDouble() == ((GNDItem.GNDPrimitive)var1).getDouble();
      } else {
         return false;
      }
   }

   public GNDItemFloat copy() {
      return new GNDItemFloat(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addFloat("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextFloat(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextFloat();
   }

   public boolean getBoolean() {
      return this.data != 0.0F;
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
      return this.data;
   }

   public double getDouble() {
      return (double)this.data;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
