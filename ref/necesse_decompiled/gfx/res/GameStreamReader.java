package necesse.gfx.res;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import necesse.engine.modLoader.InputStreamSupplier;
import necesse.engine.util.GameMath;

public class GameStreamReader implements Closeable {
   private InputStreamSupplier streamSupplier;
   private InputStream stream;
   private long readBytes;

   public GameStreamReader(InputStreamSupplier var1) throws IOException {
      this.streamSupplier = var1;
      this.stream = var1.get();
   }

   public long skipBytes(long var1) throws IOException {
      this.readBytes += var1;
      return this.stream.skip(var1);
   }

   public InputStreamSupplier getSupplierAtCurrent() {
      long var1 = this.readBytes;
      return () -> {
         InputStream var3 = this.streamSupplier.get();
         long var4 = var3.skip(var1);
         if (var4 != var1) {
            throw new IOException("Only skipped " + var4 + " bytes out of " + var1);
         } else {
            return var3;
         }
      };
   }

   public int readBytes(byte[] var1) throws IOException {
      int var2 = this.stream.read(var1);
      if (var2 != -1) {
         this.readBytes += (long)var2;
      }

      return var2;
   }

   public byte[] readBytes(int var1) throws IOException {
      byte[] var2 = new byte[var1];
      this.readBytes(var2);
      return var2;
   }

   public byte readByte() throws IOException {
      ++this.readBytes;
      return (byte)this.stream.read();
   }

   public short readShort() throws IOException {
      short var1 = 0;

      for(int var2 = 0; var2 < 8; ++var2) {
         var1 = GameMath.setByte(var1, var2, this.readByte());
      }

      return var1;
   }

   public int readInt() throws IOException {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         var1 = GameMath.setByte(var1, var2, this.readByte());
      }

      return var1;
   }

   public long readLong() throws IOException {
      int var1 = 0;

      for(int var2 = 0; var2 < 8; ++var2) {
         var1 = GameMath.setByte(var1, var2, this.readByte());
      }

      return (long)var1;
   }

   public void close() throws IOException {
      this.stream.close();
   }
}
