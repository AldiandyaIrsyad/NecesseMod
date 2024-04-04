package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemByte extends GNDItem.GNDPrimitive {
   private byte data;

   public GNDItemByte(byte var1) {
      this.data = var1;
   }

   public GNDItemByte(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemByte(LoadData var1) {
      this.data = var1.getByte("value", (byte)0);
   }

   public String toString() {
      return Byte.toString(this.data);
   }

   public boolean isDefault() {
      return this.data == 0;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.getLong() == ((GNDItem.GNDPrimitive)var1).getLong();
      } else {
         return false;
      }
   }

   public GNDItemByte copy() {
      return new GNDItemByte(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addByte("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextByte(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextByte();
   }

   public boolean getBoolean() {
      return this.data != 0;
   }

   public byte getByte() {
      return this.data;
   }

   public short getShort() {
      return (short)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public long getLong() {
      return (long)this.data;
   }

   public float getFloat() {
      return (float)this.data;
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
