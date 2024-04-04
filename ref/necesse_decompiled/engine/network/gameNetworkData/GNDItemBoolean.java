package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemBoolean extends GNDItem.GNDPrimitive {
   private boolean data;

   public GNDItemBoolean(boolean var1) {
      this.data = var1;
   }

   public GNDItemBoolean(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemBoolean(LoadData var1) {
      this.data = var1.getBoolean("value", false);
   }

   public String toString() {
      return Boolean.toString(this.data);
   }

   public boolean isDefault() {
      return !this.data;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.getBoolean() == ((GNDItem.GNDPrimitive)var1).getBoolean();
      } else {
         return false;
      }
   }

   public GNDItemBoolean copy() {
      return new GNDItemBoolean(this.data);
   }

   public void addSaveData(SaveData var1) {
      var1.addBoolean("value", this.data);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextBoolean(this.data);
   }

   public void readPacket(PacketReader var1) {
      this.data = var1.getNextBoolean();
   }

   public boolean getBoolean() {
      return this.data;
   }

   public byte getByte() {
      return (byte)(this.data ? 1 : 0);
   }

   public short getShort() {
      return (short)(this.data ? 1 : 0);
   }

   public int getInt() {
      return this.data ? 1 : 0;
   }

   public long getLong() {
      return this.data ? 1L : 0L;
   }

   public float getFloat() {
      return this.data ? 1.0F : 0.0F;
   }

   public double getDouble() {
      return this.data ? 1.0 : 0.0;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
