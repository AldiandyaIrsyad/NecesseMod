package necesse.gfx.gameFont;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.gfx.res.ResourceEncoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

public class TrueTypeGameFontInfo {
   private STBTTFontinfo info;
   private ByteBuffer ttfBuffer;
   public final String file;
   private final int ascent;
   private final int descent;
   private final int lineGap;

   public TrueTypeGameFontInfo(String var1) throws IOException {
      this.file = var1;
      File var3 = new File(GlobalData.rootPath() + "res/fonts/" + var1 + ".ttf");
      byte[] var2;
      if (var3.exists()) {
         var2 = GameUtils.loadByteFile(var3);
      } else {
         try {
            var2 = ResourceEncoder.getResourceBytes("fonts/" + var1 + ".ttf");
         } catch (FileNotFoundException var9) {
            var2 = GameUtils.loadByteFile(var3);
         }
      }

      this.ttfBuffer = BufferUtils.createByteBuffer(var2.length);
      this.ttfBuffer.put(var2);
      this.ttfBuffer.flip();
      this.info = STBTTFontinfo.malloc();
      if (!STBTruetype.stbtt_InitFont(this.info, this.ttfBuffer)) {
         throw new IOException("Could not load font " + var1);
      } else {
         MemoryStack var4 = MemoryStack.stackPush();

         try {
            IntBuffer var5 = var4.mallocInt(1);
            IntBuffer var6 = var4.mallocInt(1);
            IntBuffer var7 = var4.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(this.info, var5, var6, var7);
            this.ascent = var5.get(0);
            this.descent = var6.get(0);
            this.lineGap = var7.get(0);
         } catch (Throwable var10) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var10.addSuppressed(var8);
               }
            }

            throw var10;
         }

         if (var4 != null) {
            var4.close();
         }

      }
   }

   public synchronized float getFontSize(int var1) {
      return STBTruetype.stbtt_ScaleForMappingEmToPixels(this.info, (float)var1) * (float)(this.ascent - this.descent);
   }

   public synchronized float getLineGap(int var1) {
      return STBTruetype.stbtt_ScaleForMappingEmToPixels(this.info, (float)var1) * (float)this.lineGap;
   }

   public synchronized boolean canDisplay(int var1) {
      int var2 = STBTruetype.stbtt_FindGlyphIndex(this.info, var1);
      return var2 != 0;
   }

   public synchronized ByteBuffer getTTFBuffer() {
      return this.ttfBuffer;
   }

   public synchronized void dispose() {
      if (this.info != null) {
         this.info.free();
      }

      this.info = null;
   }
}
