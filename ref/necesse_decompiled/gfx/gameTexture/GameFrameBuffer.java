package necesse.gfx.gameTexture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.util.GameUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBImageWrite;

public abstract class GameFrameBuffer {
   private Consumer<GameFrameBuffer> binder;

   public GameFrameBuffer(Consumer<GameFrameBuffer> var1) {
      this.binder = var1;
   }

   public abstract boolean isComplete();

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract int getColorBufferTextureID();

   public abstract int getDepthBufferTextureID();

   public abstract void applyDraw(Runnable var1, Runnable var2);

   public void clearColor() {
      GL11.glClear(16384);
   }

   public void clearDepth() {
      GL11.glClear(256);
   }

   public void bind() {
      this.glBind();
      this.binder.accept(this);
   }

   public void unbind() {
      this.glUnbind();
      this.binder.accept((Object)null);
   }

   protected abstract void glBind();

   protected abstract void glUnbind();

   public abstract void dispose();

   public static void drawSimple(int var0, int var1, int var2, int var3, int var4, Runnable var5) {
      GL11.glDepthMask(false);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var0);
      if (var5 != null) {
         var5.run();
      }

      GL11.glBegin(7);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f((float)var1, (float)var2);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f((float)(var1 + var3), (float)var2);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f((float)(var1 + var3), (float)(var2 + var4));
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f((float)var1, (float)(var2 + var4));
      GL11.glEnd();
      GL11.glDepthMask(true);
      GL14.glBlendFuncSeparate(770, 771, 1, 771);
   }

   public static void draw(int var0, int var1, int var2, int var3, int var4, GameTexture.BlendQuality var5, Runnable var6) {
      GL11.glBlendFunc(1, 771);
      drawSimple(var0, var1, var2, var3, var4, () -> {
         GL11.glTexParameteri(3553, 10241, var5.minFilter);
         GL11.glTexParameteri(3553, 10240, var5.magFilter);
         GL11.glLoadIdentity();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var6 != null) {
            var6.run();
         }

      });
   }

   public static void draw(int var0, int var1, int var2, int var3, int var4, Runnable var5) {
      draw(var0, var1, var2, var3, var4, GameTexture.BlendQuality.LINEAR, var5);
   }

   public static byte[] getTextureData(int var0, int var1, int var2) {
      ByteBuffer var3 = BufferUtils.createByteBuffer(var1 * var2 * 4);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var0);
      GL11.glGetTexImage(3553, 0, 6408, 5121, var3);
      byte[] var4 = new byte[var3.limit()];
      var3.get(var4);
      var3.clear();
      GL11.glBindTexture(3553, 0);
      return var4;
   }

   public void saveTextureImage(int var1, int var2, int var3, String var4) {
      byte[] var5 = getTextureData(var1, var2, var3);
      File var6 = new File(var4 + ".png");
      GameUtils.mkDirs(var6);
      ByteBuffer var7 = BufferUtils.createByteBuffer(var2 * var3 * 4);
      var7.put(var5);
      var7.position(0);
      STBImageWrite.stbi_write_png(var6.getAbsolutePath(), var2, var3, 4, var7, 0);
      GameLog.debug.println("Saved frame buffer texture image to " + var6.getAbsolutePath());
   }
}
