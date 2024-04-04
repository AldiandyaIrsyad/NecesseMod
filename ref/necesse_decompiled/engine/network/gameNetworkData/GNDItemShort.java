package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemShort extends GNDItem.GNDPrimitive {
   private short data;

   public GNDItemShort(short var1) {
      this.data = var1;
   }

   public GNDItemShort(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemShort(LoadData var1) {
      this.data = var1.getShort("value", (short)0);
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

   public GNDItemShort copy() {
      return new GNDItemShort(this.data);
   }

   public String toString() {
      return Short.toString(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addShort("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextShort(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextShort();
   }

   public boolean getBoolean() {
      return this.data != 0;
   }

   public byte getByte() {
      return (byte)this.data;
   }

   public short getShort() {
      return this.data;
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
