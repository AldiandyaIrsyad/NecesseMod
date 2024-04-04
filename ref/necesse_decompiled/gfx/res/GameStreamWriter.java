package necesse.gfx.res;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import necesse.engine.util.GameMath;

public class GameStreamWriter implements Closeable, Flushable {
   private OutputStream stream;
   private long writtenBytes;

   public GameStreamWriter(OutputStream var1) {
      this.stream = var1;
   }

   public void writeBytes(byte[] var1) throws IOException {
      this.stream.write(var1);
      this.writtenBytes += (long)var1.length;
   }

   public void writeByte(byte var1) throws IOException {
      this.stream.write(var1);
      ++this.writtenBytes;
   }

   public void writeShort(short var1) throws IOException {
      for(int var2 = 0; var2 < 2; ++var2) {
         this.writeByte(GameMath.getByte(var1, var2));
      }

   }

   public void writeInt(int var1) throws IOException {
      for(int var2 = 0; var2 < 4; ++var2) {
         this.writeByte(GameMath.getByte(var1, var2));
      }

   }

   public void writeLong(long var1) throws IOException {
      for(int var3 = 0; var3 < 8; ++var3) {
         this.writeByte(GameMath.getByte(var1, var3));
      }

   }

   public void close() throws IOException {
      this.stream.close();
   }

   public void flush() throws IOException {
      this.stream.flush();
   }

   public long getTotalWritten() {
      return this.writtenBytes;
   }
}
