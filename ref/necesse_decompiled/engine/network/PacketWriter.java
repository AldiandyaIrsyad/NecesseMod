package necesse.engine.network;

import necesse.engine.GameLog;
import necesse.engine.util.GameMath;

public class PacketWriter extends PacketIterator {
   public PacketWriter(Packet var1, int var2) {
      super(var1, var2);
   }

   public PacketWriter(Packet var1) {
      super(var1);
   }

   public PacketWriter(PacketIterator var1) {
      super(var1);
   }

   public PacketWriter putNextBoolean(boolean var1) {
      this.packet.putBoolean(this.getNextBitIndex(), this.getNextBit(), var1);
      return this;
   }

   public PacketWriter putNextBitValue(int var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.putNextBoolean(GameMath.getBit((long)var1, var3));
      }

      return this;
   }

   public PacketWriter putNextMaxValue(int var1, int var2) {
      return this.putNextBitValue(var1, (int)(Math.log((double)var2) / Math.log(2.0) + 1.0));
   }

   public PacketWriter putNextByte(byte var1) {
      this.packet.putByte(this.getNextIndex(), var1);
      this.addIndex(1);
      return this;
   }

   public PacketWriter putNextByteUnsigned(int var1) {
      this.packet.putByteUnsigned(this.getNextIndex(), var1);
      this.addIndex(1);
      return this;
   }

   public PacketWriter putNextShort(short var1) {
      this.packet.putShort(this.getNextIndex(), var1);
      this.addIndex(2);
      return this;
   }

   public PacketWriter putNextShortUnsigned(int var1) {
      this.packet.putShortUnsigned(this.getNextIndex(), var1);
      this.addIndex(2);
      return this;
   }

   public PacketWriter putNextInt(int var1) {
      this.packet.putInt(this.getNextIndex(), var1);
      this.addIndex(4);
      return this;
   }

   public PacketWriter putNextIntUnsigned(long var1) {
      this.packet.putIntUnsigned(this.getNextIndex(), var1);
      this.addIndex(4);
      return this;
   }

   public PacketWriter putNextFloat(float var1) {
      this.packet.putFloat(this.getNextIndex(), var1);
      this.addIndex(4);
      return this;
   }

   public PacketWriter putNextLong(long var1) {
      this.packet.putLong(this.getNextIndex(), var1);
      this.addIndex(8);
      return this;
   }

   public PacketWriter putNextDouble(double var1) {
      this.packet.putDouble(this.getNextIndex(), var1);
      this.addIndex(8);
      return this;
   }

   public PacketWriter putNextEnum(Enum var1) {
      Enum[] var2 = (Enum[])var1.getClass().getEnumConstants();
      this.putNextMaxValue(var1.ordinal(), var2.length);
      return this;
   }

   public PacketWriter putNextString(String var1) {
      if (var1.length() > 65535) {
         GameLog.warn.println("Tried to put string longer than 65535. Use PacketWriter.putNextStringLong for long strings.");
         var1 = var1.substring(0, 65535);
      }

      this.putNextShortUnsigned(var1.length());
      this.packet.putString(this.getNextIndex(), var1);
      this.addIndex(var1.length() * 2);
      return this;
   }

   public PacketWriter putNextStringLong(String var1) {
      this.putNextInt(var1.length());
      this.packet.putString(this.getNextIndex(), var1);
      this.addIndex(var1.length() * 2);
      return this;
   }

   public PacketWriter putNextBytes(byte[] var1) {
      this.packet.putBytes(this.getNextIndex(), var1);
      this.addIndex(var1.length);
      return this;
   }

   public PacketWriter putNextContentPacket(Packet var1) {
      this.putNextInt(var1.getSize());
      this.packet.putContentPacket(this.getNextIndex(), var1);
      this.addIndex(var1.getSize());
      return this;
   }
}
