package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemLong extends GNDItem.GNDPrimitive {
   private long data;

   public GNDItemLong(long var1) {
      this.data = var1;
   }

   public GNDItemLong(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemLong(LoadData var1) {
      this.data = var1.getLong("value", 0L);
   }

   public String toString() {
      return Long.toString(this.data);
   }

   public boolean isDefault() {
      return this.data == 0L;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.getLong() == ((GNDItem.GNDPrimitive)var1).getLong();
      } else {
         return false;
      }
   }

   public GNDItemLong copy() {
      return new GNDItemLong(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addLong("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextLong(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextLong();
   }

   public boolean getBoolean() {
      return this.data != 0L;
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
      return this.data;
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
