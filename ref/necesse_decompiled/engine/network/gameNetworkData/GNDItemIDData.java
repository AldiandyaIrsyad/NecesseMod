package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class GNDItemIDData<T> extends GNDItem.GNDPrimitive {
   protected int id;

   public GNDItemIDData(int var1) {
      this.id = var1;
   }

   public GNDItemIDData(String var1) {
      if (var1 == null) {
         this.id = -1;
      } else {
         this.id = this.getID(var1);
      }

   }

   public GNDItemIDData(T var1) {
      if (var1 == null) {
         this.id = -1;
      } else {
         this.id = this.getID(var1);
      }

   }

   public GNDItemIDData(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemIDData(LoadData var1) {
      String var2 = var1.getUnsafeString("value", (String)null);
      if (var2 == null) {
         this.id = -1;
      } else {
         this.id = this.getID(var2);
      }

   }

   protected abstract String getStringID(int var1);

   protected abstract int getID(String var1);

   protected abstract int getID(T var1);

   public String getItemStringID() {
      return this.id == -1 ? null : this.getStringID(this.id);
   }

   public String toString() {
      return this.getStringID(this.id);
   }

   public boolean isDefault() {
      return this.id == -1;
   }

   public boolean equals(GNDItem var1) {
      if (var1 instanceof GNDItem.GNDPrimitive) {
         return this.getInt() == ((GNDItem.GNDPrimitive)var1).getInt();
      } else {
         return false;
      }
   }

   public void addSaveData(SaveData var1) {
      if (this.id != -1) {
         var1.addUnsafeString("value", this.getStringID(this.id));
      }

   }

   public void writePacket(PacketWriter var1) {
      var1.putNextInt(this.id);
   }

   public void readPacket(PacketReader var1) {
      this.id = var1.getNextInt();
   }

   public boolean getBoolean() {
      return this.id != -1;
   }

   public byte getByte() {
      return (byte)this.id;
   }

   public short getShort() {
      return (short)this.id;
   }

   public int getInt() {
      return this.id;
   }

   public long getLong() {
      return (long)this.id;
   }

   public float getFloat() {
      return (float)this.id;
   }

   public double getDouble() {
      return (double)this.id;
   }

   public static int getItemID(GNDItemMap var0, String var1) {
      return var0.getInt(var1, -1);
   }
}
