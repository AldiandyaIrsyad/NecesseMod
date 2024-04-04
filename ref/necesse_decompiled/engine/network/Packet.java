package necesse.engine.network;

import java.nio.ByteBuffer;
import java.util.Arrays;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;

public class Packet {
   public static final int CHUNK_SIZE = 512;
   public static final int BYTE_SIZE = 1;
   public static final int SHORT_SIZE = 2;
   public static final int INT_SIZE = 4;
   public static final int LONG_SIZE = 8;
   public static final int FLOAT_SIZE = 4;
   public static final int DOUBLE_SIZE = 8;
   public static final int CHAR_SIZE = 2;
   public static final int BOOLEAN_SIZE = 1;
   private int size;
   private byte[] data;
   private ByteBuffer buffer;

   public Packet() {
      this.data = new byte[0];
      this.buffer = ByteBuffer.wrap(this.data);
   }

   public Packet(byte[] var1) {
      this.data = var1;
      this.buffer = ByteBuffer.wrap(this.data);
      this.size = var1.length;
   }

   public final void wrap(Packet var1) {
      this.data = var1.data;
      this.buffer = var1.buffer;
      this.size = var1.size;
   }

   public final void ensureCapacity(int var1) {
      if (this.data.length < var1) {
         int var2;
         for(var2 = this.data.length; var2 < var1; var2 += 512) {
         }

         this.data = Arrays.copyOf(this.data, var2);
         this.buffer = ByteBuffer.wrap(this.data);
      }

   }

   public final int getCapacity() {
      return this.data.length;
   }

   private void ensureCapacityAndSetSize(int var1) {
      this.ensureCapacity(var1);
      this.size = Math.max(this.size, var1);
   }

   public final Packet putByte(int var1, byte var2) {
      this.ensureCapacityAndSetSize(var1 + 1);
      this.buffer.put(var1, var2);
      return this;
   }

   public final Packet putByteUnsigned(int var1, int var2) {
      return this.putByte(var1, (byte)var2);
   }

   public final byte getByte(int var1) {
      return var1 + 1 > this.data.length ? 0 : this.buffer.get(var1);
   }

   public final int getByteUnsigned(int var1) {
      return this.getByte(var1) & 255;
   }

   public final Packet putShort(int var1, short var2) {
      this.ensureCapacityAndSetSize(var1 + 2);
      this.buffer.putShort(var1, var2);
      return this;
   }

   public final Packet putShortUnsigned(int var1, int var2) {
      return this.putShort(var1, (short)var2);
   }

   public final short getShort(int var1) {
      return var1 + 2 > this.data.length ? 0 : this.buffer.getShort(var1);
   }

   public final int getShortUnsigned(int var1) {
      return this.getShort(var1) & '\uffff';
   }

   public final Packet putInt(int var1, int var2) {
      this.ensureCapacityAndSetSize(var1 + 4);
      this.buffer.putInt(var1, var2);
      return this;
   }

   public final int getInt(int var1) {
      return var1 + 4 > this.data.length ? 0 : this.buffer.getInt(var1);
   }

   public final Packet putIntUnsigned(int var1, long var2) {
      return this.putInt(var1, (int)var2);
   }

   public final long getIntUnsigned(int var1, long var2) {
      return Integer.toUnsignedLong(this.getInt(var1));
   }

   public final Packet putLong(int var1, long var2) {
      this.ensureCapacityAndSetSize(var1 + 8);
      this.buffer.putLong(var1, var2);
      return this;
   }

   public final long getLong(int var1) {
      return var1 + 8 > this.data.length ? 0L : this.buffer.getLong(var1);
   }

   public final Packet putFloat(int var1, float var2) {
      this.ensureCapacityAndSetSize(var1 + 4);
      this.buffer.putFloat(var1, var2);
      return this;
   }

   public final float getFloat(int var1) {
      return var1 + 4 > this.data.length ? 0.0F : this.buffer.getFloat(var1);
   }

   public final Packet putDouble(int var1, double var2) {
      this.ensureCapacityAndSetSize(var1 + 8);
      this.buffer.putDouble(var1, var2);
      return this;
   }

   public final double getDouble(int var1) {
      return var1 + 8 > this.data.length ? 0.0 : this.buffer.getDouble(var1);
   }

   public final Packet putChar(int var1, char var2) {
      this.ensureCapacityAndSetSize(var1 + 2);
      this.buffer.putChar(var1, var2);
      return this;
   }

   public final char getChar(int var1) {
      return var1 + 2 > this.data.length ? '\u0000' : this.buffer.getChar(var1);
   }

   public final Packet putBoolean(int var1, int var2, boolean var3) {
      this.ensureCapacityAndSetSize(var1 + 1);
      if (var2 <= 7 && var2 >= 0) {
         this.buffer.put(var1, GameMath.setBit(this.getByte(var1), var2, var3));
         return this;
      } else {
         return this;
      }
   }

   public final boolean getBoolean(int var1, int var2) {
      return var1 + 1 <= this.data.length && var2 <= 7 && var2 >= 0 ? GameMath.getBit((long)this.getByte(var1), var2) : false;
   }

   public final Packet putString(int var1, String var2) {
      this.ensureCapacityAndSetSize(var1 + var2.length() * 2);
      char[] var3 = var2.toCharArray();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         this.buffer.putChar(var1 + var4 * 2, var3[var4]);
      }

      return this;
   }

   public final String getString(int var1, int var2) {
      char[] var3 = new char[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = this.getChar(var1 + var4 * 2);
      }

      return new String(var3);
   }

   public final Packet putBytes(int var1, byte[] var2) {
      this.ensureCapacityAndSetSize(var1 + var2.length);
      System.arraycopy(var2, 0, this.data, var1, var2.length);
      return this;
   }

   public final byte[] getBytes(int var1, int var2) {
      byte[] var3 = new byte[var2];
      System.arraycopy(this.data, var1, var3, 0, var2);
      return var3;
   }

   public final Packet putContentPacket(int var1, Packet var2) {
      this.ensureCapacityAndSetSize(var1 + var2.getSize());
      if (var2.getSize() >= 0) {
         System.arraycopy(var2.data, 0, this.data, var1, var2.getSize());
      }

      return this;
   }

   public final Packet getContentPacket(int var1, int var2) {
      if (var1 > this.data.length) {
         var1 = this.data.length;
      }

      if (var1 + var2 > this.data.length) {
         var2 = this.data.length - var1;
      }

      byte[] var3 = Arrays.copyOfRange(this.data, var1, var1 + var2);
      return new Packet(var3);
   }

   public final byte[] getPacketData() {
      return Arrays.copyOf(this.data, this.size);
   }

   public final int getSize() {
      return this.size;
   }

   public final void printBitContent() {
      System.out.println("Packet size: " + this.getSize());

      for(int var1 = 0; var1 < this.getSize(); ++var1) {
         StringBuilder var2 = new StringBuilder("Index " + var1 + ": ");

         for(int var3 = 7; var3 >= 0; --var3) {
            var2.append(this.getBoolean(var1, var3) ? "1" : "0");
         }

         System.out.println(var2);
      }

   }

   protected Class<? extends Packet> getPacketClass() {
      return this.getClass();
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      System.out.println("Server received unknown packet: " + this.getPacketClass().getSimpleName());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      System.out.println("Client received unknown packet: " + this.getPacketClass().getSimpleName());
   }
}
