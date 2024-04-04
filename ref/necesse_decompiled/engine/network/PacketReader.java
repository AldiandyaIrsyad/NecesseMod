package necesse.engine.network;

import necesse.engine.util.GameMath;

public class PacketReader extends PacketIterator {
   public PacketReader(Packet var1, int var2) {
      super(var1, var2);
   }

   public PacketReader(Packet var1) {
      super(var1);
   }

   public PacketReader(PacketIterator var1) {
      super(var1);
   }

   public boolean getNextBoolean() {
      return this.packet.getBoolean(this.getNextBitIndex(), this.getNextBit());
   }

   public int getNextBitValue(int var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var1; ++var3) {
         var2 = GameMath.setBit(var2, var3, this.getNextBoolean());
      }

      return var2;
   }

   public int getNextMaxValue(int var1) {
      return this.getNextBitValue((int)(Math.log((double)var1) / Math.log(2.0) + 1.0));
   }

   public byte getNextByte() {
      byte var1 = this.packet.getByte(this.getNextIndex());
      this.addIndex(1);
      return var1;
   }

   public int getNextByteUnsigned() {
      int var1 = this.packet.getByteUnsigned(this.getNextIndex());
      this.addIndex(1);
      return var1;
   }

   public short getNextShort() {
      short var1 = this.packet.getShort(this.getNextIndex());
      this.addIndex(2);
      return var1;
   }

   public int getNextShortUnsigned() {
      int var1 = this.packet.getShortUnsigned(this.getNextIndex());
      this.addIndex(2);
      return var1;
   }

   public int getNextInt() {
      int var1 = this.packet.getInt(this.getNextIndex());
      this.addIndex(4);
      return var1;
   }

   public float getNextFloat() {
      float var1 = this.packet.getFloat(this.getNextIndex());
      this.addIndex(4);
      return var1;
   }

   public long getNextLong() {
      long var1 = this.packet.getLong(this.getNextIndex());
      this.addIndex(8);
      return var1;
   }

   public double getNextDouble() {
      double var1 = this.packet.getDouble(this.getNextIndex());
      this.addIndex(8);
      return var1;
   }

   public <T extends Enum<T>> T getNextEnum(Class<T> var1) {
      Enum[] var2 = (Enum[])var1.getEnumConstants();
      return var2[this.getNextMaxValue(var2.length)];
   }

   private String getNextString(int var1) {
      String var2 = this.packet.getString(this.getNextIndex(), var1);
      this.addIndex(var1 * 2);
      return var2;
   }

   public String getNextString() {
      int var1 = this.getNextShortUnsigned();
      return this.getNextString(var1);
   }

   public String getNextStringLong() {
      int var1 = this.getNextInt();
      return this.getNextString(var1);
   }

   public byte[] getNextBytes(int var1) {
      byte[] var2 = this.packet.getBytes(this.getNextIndex(), var1);
      this.addIndex(var1);
      return var2;
   }

   public byte[] getRemainingBytes() {
      int var1 = this.packet.getSize() - this.getNextIndex();
      return this.getNextBytes(var1);
   }

   public Packet getRemainingBytesPacket() {
      return new Packet(this.getRemainingBytes());
   }

   public int getRemainingSize() {
      return this.packet.getSize() - this.getNextIndex();
   }

   private Packet getNextContentPacket(int var1) {
      Packet var2 = this.packet.getContentPacket(this.getNextIndex(), var1);
      this.addIndex(var1);
      return var2;
   }

   public Packet getNextContentPacket() {
      int var1 = this.getNextInt();
      return this.getNextContentPacket(var1);
   }
}
