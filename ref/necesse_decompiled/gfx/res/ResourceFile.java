package necesse.gfx.res;

import java.io.IOException;
import java.io.InputStream;
import necesse.engine.modLoader.InputStreamSupplier;
import necesse.engine.util.GameUtils;

public class ResourceFile {
   public final String path;
   private final int length;
   private final InputStreamSupplier inputStreamSupplier;

   public ResourceFile(String var1, int var2, InputStreamSupplier var3) {
      this.path = var1;
      this.length = var2;
      this.inputStreamSupplier = var3;
   }

   public byte[] loadBytes(boolean var1) throws IOException {
      InputStream var2 = this.inputStreamSupplier.get();

      byte[] var3;
      label49: {
         byte[] var5;
         try {
            if (this.length == -1) {
               var3 = GameUtils.loadInputStream(var2);
               break label49;
            }

            var3 = new byte[this.length];
            int var4 = var2.read(var3, 0, this.length);
            if (var1 && var4 != this.length) {
               throw new IOException("Read unexpected size of resource file " + this.path + ": " + var4 + " bytes read out of " + this.length);
            }

            var5 = var3;
         } catch (Throwable var7) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var2 != null) {
            var2.close();
         }

         return var5;
      }

      if (var2 != null) {
         var2.close();
      }

      return var3;
   }
}
