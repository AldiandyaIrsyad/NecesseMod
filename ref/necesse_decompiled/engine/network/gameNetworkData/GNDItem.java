package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.save.SaveData;

public abstract class GNDItem {
   public final IDData idData = new IDData();

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public GNDItem() {
      GNDRegistry.applyIDData(this);
   }

   public abstract String toString();

   public abstract boolean isDefault();

   public static boolean isDefault(GNDItem var0) {
      return var0 == null || var0.isDefault();
   }

   public abstract boolean equals(GNDItem var1);

   public boolean equals(Object var1) {
      return var1 instanceof GNDItem ? this.equals((GNDItem)var1) : super.equals(var1);
   }

   public abstract GNDItem copy();

   public abstract void addSaveData(SaveData var1);

   public abstract void writePacket(PacketWriter var1);

   public abstract void readPacket(PacketReader var1);

   public static boolean equals(GNDItem var0, GNDItem var1) {
      return var0 == var1 || var0 != null && var0.equals(var1);
   }

   public abstract static class GNDPrimitive extends GNDItem {
      public GNDPrimitive() {
      }

      public abstract boolean getBoolean();

      public abstract byte getByte();

      public abstract short getShort();

      public abstract int getInt();

      public abstract long getLong();

      public abstract float getFloat();

      public abstract double getDouble();
   }
}
