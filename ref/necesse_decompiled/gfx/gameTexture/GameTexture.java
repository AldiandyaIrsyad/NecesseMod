package necesse.gfx.gameTexture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.res.ResourceEncoder;
import necesse.gfx.ui.GameTextureData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

public class GameTexture implements Serializable {
   private static final HashSet<Integer> generatedTextures = new HashSet();
   private static final HashMap<String, GameTexture> loadedTextures = new HashMap();
   private static boolean shouldFinalizeLoaded = false;
   public static boolean memoryDebug;
   public static long timeSpentFinalizing;
   public static long timeSpentRestoring;
   public static final int BYTES_PER_PIXEL = 4;
   public static BlendQuality overrideBlendQuality = null;
   public boolean onlyPreloaded;
   private int width;
   private int height;
   public ByteBuffer buffer;
   public String debugName;
   private int textureID;
   private int hash;
   private boolean resetTexture;
   private boolean generatedTexture;
   private BlendQuality blendQuality;
   private boolean finalizeLater;
   private boolean isFinal;

   public static void listUnloadedTextures(List<String> var0) {
      if (var0 == null) {
         var0 = new ArrayList();
      }

      listUnloadedTextures("", (List)var0);
   }

   private static void listUnloadedTextures(String var0, List<String> var1) {
      var1.replaceAll((var0x) -> {
         return GameUtils.formatFileExtension(var0x, "png");
      });
      File var2 = new File(GlobalData.rootPath() + "res/" + var0);
      String var3 = var0.isEmpty() ? "" : var0 + "/";
      if (var2.exists() && var2.isDirectory()) {
         File[] var4 = var2.listFiles();
         if (var4 != null) {
            File[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File var8 = var5[var7];
               if (var8.isDirectory()) {
                  listUnloadedTextures(var3 + var8.getName(), var1);
               } else {
                  String var9 = var8.getName();
                  String var10 = var3 + var9;
                  if (var9.endsWith(".png")) {
                     GameTexture var11 = (GameTexture)loadedTextures.get(var10);
                     if ((var11 == null || var11.onlyPreloaded) && !var1.contains(var10)) {
                        GameLog.warn.println("Texture " + var10 + " is never loaded");
                     }
                  }
               }
            }
         }
      } else {
         System.err.println("Tried to check unloaded texture in a non existent directory: " + var2.getPath());
      }

   }

   public static Iterable<Map.Entry<String, GameTexture>> getLoadedTextures() {
      return loadedTextures.entrySet();
   }

   private static void deleteTexture(int var0, boolean var1) {
      GL11.glDeleteTextures(var0);
      if (var1) {
         generatedTextures.remove(var0);
      }

   }

   private static void deleteTexture(int var0) {
      deleteTexture(var0, true);
   }

   public static void deleteGeneratedTextures() {
      generatedTextures.forEach((var0) -> {
         deleteTexture(var0, false);
      });
      generatedTextures.clear();
   }

   public static int getGeneratedTextureCount() {
      return generatedTextures.size();
   }

   public static GameTexture fromFile(String var0, GameTexture var1, boolean var2) {
      try {
         return fromFileRaw(var0, var2);
      } catch (FileNotFoundException var4) {
         return var1;
      }
   }

   public static GameTexture fromFile(String var0, GameTexture var1) {
      return fromFile(var0, var1, false);
   }

   public static GameTexture fromFile(String var0, boolean var1) {
      return fromFile(var0, GameResources.error, var1);
   }

   public static GameTexture fromFile(String var0) {
      return fromFile(var0, GameResources.error);
   }

   private static GameTexture fromFileRawUnknown(String var0, boolean var1, boolean var2) throws FileNotFoundException {
      Objects.requireNonNull(var0);
      var0 = GameUtils.formatFileExtension(var0, "png");
      GameTexture var3 = (GameTexture)loadedTextures.get(var0);
      if (var3 == null) {
         var3 = new GameTexture(var0, var1, true);
         if (!var2) {
            var3.makeFinal();
         }

         var3.finalizeLater();
         loadedTextures.put(var0, var3);
      }

      var3.onlyPreloaded = false;
      return var3;
   }

   public static GameTexture fromFileRaw(String var0, boolean var1) throws FileNotFoundException {
      return fromFileRawUnknown(var0, false, var1);
   }

   public static GameTexture fromFileRawOutside(String var0, boolean var1) throws FileNotFoundException {
      return fromFileRawUnknown(var0, true, var1);
   }

   public static GameTexture fromFileRaw(String var0) throws FileNotFoundException {
      return fromFileRaw(var0, false);
   }

   public static GameTexture fromFileRawOutside(String var0) throws FileNotFoundException {
      return fromFileRawOutside(var0, false);
   }

   public static void finalizeLoadedTextures() {
      shouldFinalizeLoaded = true;
      if (memoryDebug) {
         System.out.println("TIME SPENT FINALIZING: " + GameUtils.getTimeStringNano(timeSpentFinalizing));
      }

      loadedTextures.values().forEach(GameTexture::makeFinal);
      if (memoryDebug) {
         System.out.println("TIME SPENT FINALIZING AFTER: " + GameUtils.getTimeStringNano(timeSpentFinalizing));
      }

      if (memoryDebug) {
         System.out.println("TIME SPENT RESTORING: " + GameUtils.getTimeStringNano(timeSpentRestoring));
      }

   }

   public GameTexture(String var1, int var2, int var3) {
      this.debugName = var1;
      this.width = var2;
      this.height = var3;
      this.buffer = BufferUtils.createByteBuffer(this.width * this.height * 4);
      this.resetTexture();
      this.setBlendQuality(GameTexture.BlendQuality.LINEAR);
   }

   public GameTexture(GameTexture var1, int var2, int var3, int var4) {
      this(var1.debugName + " copy", var4, var4);
      this.copy(var1, 0, 0, var2 * var4, var3 * var4, var4, var4);
      this.resetTexture();
   }

   public GameTexture(GameTexture var1, int var2, int var3, int var4, int var5) {
      this(var1.debugName + " copy", var4, var5);
      this.copy(var1, 0, 0, var2, var3, var4, var5);
      this.resetTexture();
   }

   public GameTexture(GameTexture var1) {
      this(var1, 0, 0, var1.getWidth(), var1.getHeight());
   }

   public GameTexture(String var1, byte[] var2) {
      this.debugName = var1;
      this.loadBytes(var2);
      this.setBlendQuality(GameTexture.BlendQuality.LINEAR);
      this.resetTexture();
   }

   private GameTexture(String var1, boolean var2, boolean var3) throws FileNotFoundException {
      this.debugName = var1;
      this.width = 0;
      this.height = 0;
      this.buffer = BufferUtils.createByteBuffer(0);
      if (var3) {
         GameLoadingScreen.drawLoadingSub(var1);
      }

      boolean var4 = false;

      try {
         byte[] var5;
         if (var2) {
            var5 = GameUtils.loadByteFile(var1);
         } else {
            File var6 = new File(GlobalData.rootPath() + "res/" + var1);
            if (var6.exists()) {
               var5 = GameUtils.loadByteFile(var6);
               var4 = true;
            } else {
               try {
                  var5 = ResourceEncoder.getResourceBytes(var1);
                  var4 = true;
               } catch (FileNotFoundException var9) {
                  var5 = GameUtils.loadByteFile(var6);
               }
            }
         }

         try {
            this.loadBytes(var5);
         } catch (IllegalArgumentException var8) {
            throw new IOException("Error loading image file " + var1 + ": " + var8.getMessage());
         }
      } catch (FileNotFoundException var10) {
         throw var10;
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      if (!var2 && !var4 && !GlobalData.isDevMode()) {
         GameLog.warn.println(var1 + " was not found in resource file.");
      }

      this.setBlendQuality(GameTexture.BlendQuality.LINEAR);
      this.resetTexture();
   }

   private GameTexture(ByteBuffer var1, int var2, int var3) {
      this.buffer = var1;
      this.width = var2;
      this.height = var3;
      this.setBlendQuality(GameTexture.BlendQuality.LINEAR);
      this.resetTexture();
   }

   private void loadBytes(byte[] var1) {
      ByteBuffer var2 = null;

      try {
         IntBuffer var5 = null;
         IntBuffer var6 = null;
         IntBuffer var7 = null;
         ByteBuffer var8 = null;

         int var3;
         int var4;
         try {
            var5 = MemoryUtil.memAllocInt(1);
            var6 = MemoryUtil.memAllocInt(1);
            var7 = MemoryUtil.memAllocInt(1);
            var8 = MemoryUtil.memAlloc(var1.length);
            var8.put(var1);
            var8.position(0);
            var2 = STBImage.stbi_load_from_memory(var8, var5, var6, var7, 4);
            var3 = var5.get();
            var4 = var6.get();
         } finally {
            MemoryUtil.memFree(var5);
            MemoryUtil.memFree(var6);
            MemoryUtil.memFree(var7);
            MemoryUtil.memFree(var8);
         }

         if (var2 == null) {
            throw new IllegalArgumentException(STBImage.stbi_failure_reason());
         }

         this.width = var3;
         this.height = var4;
         var2.position(0);
         this.buffer = BufferUtils.createByteBuffer(this.width * this.height * 4);
         this.buffer.put(var2);
      } finally {
         MemoryUtil.memFree(var2);
      }

   }

   public boolean runPreAntialias(boolean var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.getWidth(); ++var3) {
         for(int var4 = 0; var4 < this.getHeight(); ++var4) {
            Color var5 = this.getColor(var3, var4);
            if (var5.getAlpha() == 0) {
               Color var6 = this.getSurroundingInvisColor(var3, var4);
               if (var5.getRGB() != var6.getRGB()) {
                  if (var1) {
                     return true;
                  }

                  this.setPixel(var3, var4, var6);
                  var2 = true;
               }
            }
         }
      }

      return var2;
   }

   private Color getSurroundingInvisColor(int var1, int var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      for(int var7 = var1 - 1; var7 <= var1 + 1; ++var7) {
         for(int var8 = var2 - 1; var8 <= var2 + 1; ++var8) {
            if (var7 >= 0 && var7 < this.getWidth() && var8 >= 0 && var8 < this.getHeight() && (var7 != 0 || var8 != 0)) {
               Color var9 = this.getColor(var7, var8);
               if (var9.getAlpha() != 0) {
                  ++var6;
                  var3 += var9.getRed();
                  var4 += var9.getGreen();
                  var5 += var9.getBlue();
               }
            }
         }
      }

      if (var6 != 0) {
         return new Color(var3 / var6, var4 / var6, var5 / var6, 0);
      } else {
         return new Color(255, 255, 255, 0);
      }
   }

   public GameTexture(String var1, int var2, int var3, ByteBuffer var4) {
      this(var1, var2, var3);
      var4.position(0);
      this.buffer.position(0);
      this.buffer.put(var4);
      this.resetTexture();
   }

   public GameTexture(String var1, int var2, int var3, byte[] var4) {
      this(var1, var2, var3);
      if (var4.length != var2 * var3 * 4) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         this.buffer.position(0);
         this.buffer.put(var4, 0, var4.length);
         this.resetTexture();
      }
   }

   public GameTexture(String var1, GameTextureData var2) {
      this(var1, var2.width, var2.height);
      byte[] var3 = var2.getBuffer();
      if (var3.length != var2.width * var2.height * 4) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         this.buffer.position(0);
         this.buffer.put(var3, 0, var3.length);
         this.setBlendQuality(var2.blendQuality);
         this.resetTexture();
         if (var2.isFinal) {
            this.makeFinal();
         }

      }
   }

   public GameTexture(String var1, BufferedImage var2) {
      this(var1, var2.getWidth(), var2.getHeight());
      int[] var3 = new int[this.width * this.height];
      var2.getRGB(0, 0, this.width, this.height, var3, 0, this.width);

      for(int var4 = 0; var4 < this.width; ++var4) {
         for(int var5 = 0; var5 < this.height; ++var5) {
            int var6 = var3[var4 + var5 * this.width];
            int var7 = var6 >> 16 & 255;
            int var8 = var6 >> 8 & 255;
            int var9 = var6 & 255;
            int var10 = var6 >> 24 & 255;
            this.setPixel(var4, var5, new Color(var7, var8, var9, var10));
         }
      }

      this.resetTexture();
      this.setBlendQuality(GameTexture.BlendQuality.LINEAR);
   }

   public void setBlendQuality(BlendQuality var1) {
      this.blendQuality = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void replaceColor(Color var1, Color var2) {
      this.ensureNotFinal();

      for(int var3 = 0; var3 < this.width; ++var3) {
         for(int var4 = 0; var4 < this.height; ++var4) {
            if (this.getColor(var3, var4).equals(var1)) {
               this.setPixel(var3, var4, var2);
            }
         }
      }

   }

   public void copy(GameTexture var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 == this) {
         throw new IllegalArgumentException("Texture cannot copy itself");
      } else {
         this.ensureNotFinal();
         var1.ensureNotFinal();
         if (var2 < 0) {
            var4 -= var2;
         }

         if (var3 < 0) {
            var5 -= var3;
         }

         if (var4 < 0) {
            var2 -= var4;
         }

         if (var5 < 0) {
            var3 -= var5;
         }

         if (var2 < 0) {
            var2 = 0;
         }

         if (var3 < 0) {
            var3 = 0;
         }

         if (var4 < 0) {
            var4 = 0;
         }

         if (var5 < 0) {
            var5 = 0;
         }

         var6 = Math.min(var6, this.width - var2);
         var7 = Math.min(var7, this.height - var3);
         var6 = Math.min(var6, var1.width - var4);
         var7 = Math.min(var7, var1.height - var5);

         for(int var8 = 0; var8 < var7; ++var8) {
            this.buffer.position((var2 + (var3 + var8) * this.width) * 4);
            var1.buffer.position((var4 + (var5 + var8) * var1.width) * 4);

            for(int var9 = 0; var9 < var6; ++var9) {
               for(int var10 = 0; var10 < 4; ++var10) {
                  this.buffer.put(var1.buffer.get());
               }
            }
         }

         this.resetTexture();
      }
   }

   public void copy(GameTexture var1, int var2, int var3) {
      this.copy(var1, var2, var3, 0, 0, var1.width, var1.height);
   }

   public void merge(GameTexture var1, int var2, int var3, int var4, int var5, int var6, int var7, MergeFunction var8) {
      if (var1 == this) {
         throw new IllegalArgumentException("Texture cannot merge itself");
      } else {
         this.ensureNotFinal();
         var1.ensureNotFinal();
         if (var2 < 0) {
            var4 -= var2;
         }

         if (var3 < 0) {
            var5 -= var3;
         }

         if (var4 < 0) {
            var2 -= var4;
         }

         if (var5 < 0) {
            var3 -= var5;
         }

         if (var2 < 0) {
            var2 = 0;
         }

         if (var3 < 0) {
            var3 = 0;
         }

         if (var4 < 0) {
            var4 = 0;
         }

         if (var5 < 0) {
            var5 = 0;
         }

         var6 = Math.min(var6, this.width - var2);
         var7 = Math.min(var7, this.height - var3);
         var6 = Math.min(var6, var1.width - var4);
         var7 = Math.min(var7, var1.height - var5);

         for(int var9 = 0; var9 < var6; ++var9) {
            for(int var10 = 0; var10 < var7; ++var10) {
               this.setPixel(var2 + var9, var3 + var10, this.mergeColor(this.getColor(var2 + var9, var3 + var10), var1.getColor(var4 + var9, var5 + var10), var8));
            }
         }

      }
   }

   public void merge(GameTexture var1, int var2, int var3, MergeFunction var4) {
      this.merge(var1, var2, var3, 0, 0, var1.width, var1.height, var4);
   }

   public void applyColor(Color var1, MergeFunction var2) {
      this.ensureNotFinal();

      for(int var3 = 0; var3 < this.width; ++var3) {
         for(int var4 = 0; var4 < this.height; ++var4) {
            int var5 = (var3 + var4 * this.width) * 4;
            this.setPixel(var3, var4, this.mergeColor(this.getColor(var5), var1, var2));
         }
      }

   }

   public GameTexture resize(int var1, int var2, int var3, int var4) {
      ByteBuffer var5 = BufferUtils.createByteBuffer(var1 * var2 * 4);
      this.ensureNotFinal();
      this.buffer.position(0);
      STBImageResize.stbir_resize(this.buffer, this.width, this.height, 0, var5, var1, var2, 0, 0, 4, 3, -1, 1, 1, var3, var4, 0);
      return new GameTexture(var5, var1, var2);
   }

   public GameTexture resize(int var1, int var2, int var3) {
      return this.resize(var1, var2, var3, var3);
   }

   public GameTexture resize(int var1, int var2) {
      return this.resize(var1, var2, 0);
   }

   public GameTexture flippedX() {
      GameTexture var1 = new GameTexture(this.debugName + " flippedX", this.getWidth(), this.getHeight());

      for(int var2 = 0; var2 < this.getWidth(); ++var2) {
         int var3 = this.getWidth() - var2 - 1;

         for(int var4 = 0; var4 < this.getHeight(); ++var4) {
            var1.setPixel(var3, var4, this.getPixel(var2, var4));
         }
      }

      return var1;
   }

   public GameTexture flippedY() {
      GameTexture var1 = new GameTexture(this.debugName + " flippedY", this.getWidth(), this.getHeight());

      for(int var2 = 0; var2 < this.getWidth(); ++var2) {
         for(int var3 = 0; var3 < this.getHeight(); ++var3) {
            int var4 = this.getHeight() - var3 - 1;
            var1.setPixel(var2, var4, this.getPixel(var2, var3));
         }
      }

      return var1;
   }

   public GameTexture rotatedClockwise() {
      GameTexture var1 = new GameTexture(this.debugName + " rotatedClockwise", this.getHeight(), this.getWidth());

      for(int var2 = 0; var2 < this.getWidth(); ++var2) {
         int var3 = var2;

         for(int var4 = 0; var4 < this.getHeight(); ++var4) {
            int var5 = this.getHeight() - var4 - 1;
            var1.setPixel(var5, var3, this.getPixel(var2, var4));
         }
      }

      return var1;
   }

   public GameTexture rotatedAnticlockwise() {
      GameTexture var1 = new GameTexture(this.debugName + " rotatedAnticlockwise", this.getHeight(), this.getWidth());

      for(int var2 = 0; var2 < this.getWidth(); ++var2) {
         int var3 = this.getWidth() - var2 - 1;

         for(int var4 = 0; var4 < this.getHeight(); ++var4) {
            var1.setPixel(var4, var3, this.getPixel(var2, var4));
         }
      }

      return var1;
   }

   private Color getColor(byte var1, byte var2, byte var3, byte var4) {
      return new Color(var1 & 255, var2 & 255, var3 & 255, var4 & 255);
   }

   private Color mergeColor(Color var1, Color var2, MergeFunction var3) {
      return var3.merge(var1, var2);
   }

   public void mergeSprite(GameTexture var1, int var2, int var3, int var4, int var5, int var6, MergeFunction var7) {
      this.merge(var1, var5, var6, var2 * var4, var3 * var4, var4, var4, var7);
   }

   public void mergeSprite(GameTexture var1, int var2, int var3, int var4, int var5, int var6) {
      this.copy(var1, var5, var6, var2 * var4, var3 * var4, var4, var4);
   }

   public Color getColor(int var1, int var2) {
      byte[] var3 = this.getPixel(var1, var2);
      return this.getColor(var3[0], var3[1], var3[2], var3[3]);
   }

   public Color getColor(int var1) {
      byte[] var2 = this.getPixel(var1);
      return this.getColor(var2[0], var2[1], var2[2], var2[3]);
   }

   public byte[] getPixel(int var1, int var2) {
      return this.getPixel((var1 + var2 * this.width) * 4);
   }

   public byte[] getPixel(int var1) {
      this.ensureNotFinal();
      byte[] var2 = new byte[4];
      this.buffer.position(var1);
      this.buffer.get(var2, 0, var2.length);
      return var2;
   }

   public int getRed(int var1, int var2) {
      return this.getPixel(var1, var2)[0] & 255;
   }

   public int getGreen(int var1, int var2) {
      return this.getPixel(var1, var2)[1] & 255;
   }

   public int getBlue(int var1, int var2) {
      return this.getPixel(var1, var2)[2] & 255;
   }

   public int getAlpha(int var1, int var2) {
      return this.getPixel(var1, var2)[3] & 255;
   }

   public void setPixel(int var1, int var2, Color var3) {
      this.setPixel(var1, var2, var3.getRed(), var3.getGreen(), var3.getBlue(), var3.getAlpha());
   }

   public void setPixel(int var1, int var2, int var3, int var4, int var5, int var6) {
      var3 = (var3 % 256 + 256) % 256;
      var4 = (var4 % 256 + 256) % 256;
      var5 = (var5 % 256 + 256) % 256;
      var6 = (var6 % 256 + 256) % 256;
      this.setPixel(var1, var2, new byte[]{(byte)var3, (byte)var4, (byte)var5, (byte)var6});
   }

   public void setPixel(int var1, int var2, byte[] var3) {
      this.putData((var1 + var2 * this.width) * 4, var3);
   }

   public void putData(int var1, byte[] var2) {
      this.ensureNotFinal();
      this.buffer.position(var1);
      this.buffer.put(var2, 0, var2.length);
      this.resetTexture();
   }

   public void fill(int var1, int var2, int var3, int var4) {
      this.fillRect(0, 0, this.width, this.height, var1, var2, var3, var4);
   }

   public void fillRect(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.ensureNotFinal();

      for(int var9 = 0; var9 < var3; ++var9) {
         for(int var10 = 0; var10 < var4; ++var10) {
            this.setPixel(var1 + var9, var2 + var10, var5, var6, var7, var8);
         }
      }

   }

   public void resetTexture() {
      this.resetTexture(false);
   }

   public void resetTexture(boolean var1) {
      this.ensureNotFinal();
      this.resetTexture = true;
      this.hash = 0;
      if (var1) {
         this.getTextureID();
      }

   }

   public int getTextureID() {
      if (this.resetTexture) {
         this.delete();
         this.resetTexture = false;
      }

      if (!this.generatedTexture) {
         this.buffer.position(0);
         int var1 = GL11.glGenTextures();
         GL11.glBindTexture(3553, var1);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, this.buffer);
         this.textureID = var1;
         this.generatedTexture = true;
         generatedTextures.add(this.textureID);
         if (this.finalizeLater) {
            this.isFinal = true;
            this.finalizeLater = false;
            this.buffer = null;
         }
      }

      return this.textureID;
   }

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         ArrayList var1 = new ArrayList();

         for(int var2 = 0; var2 < this.getHeight(); ++var2) {
            for(int var3 = 0; var3 < this.getWidth(); ++var3) {
               byte[] var4 = this.getPixel(var3, var2);
               int var5 = Arrays.hashCode(var4);
               var1.add(var5);
            }
         }

         return var1.hashCode();
      }
   }

   public void bind() {
      this.bind(33984);
   }

   public void bind(int var1) {
      GL13.glActiveTexture(var1);
      GL11.glBindTexture(3553, this.getTextureID());
      if (overrideBlendQuality != null) {
         GL11.glTexParameteri(3553, 10241, overrideBlendQuality.minFilter);
         GL11.glTexParameteri(3553, 10240, overrideBlendQuality.magFilter);
      } else {
         GL11.glTexParameteri(3553, 10241, this.blendQuality.minFilter);
         GL11.glTexParameteri(3553, 10240, this.blendQuality.magFilter);
      }

   }

   public void delete() {
      if (this.generatedTexture) {
         deleteTexture(this.textureID);
         generatedTextures.remove(this.textureID);
         this.textureID = 0;
         this.generatedTexture = false;
      }

   }

   public GameTexture finalizeLater() {
      this.finalizeLater = true;
      return this;
   }

   public GameTexture makeFinal() {
      long var1 = System.nanoTime();
      if (memoryDebug && !this.isFinal() && shouldFinalizeLoaded) {
         System.out.println("FINALIZED " + this.debugName);
      }

      this.getTextureID();
      this.isFinal = true;
      this.buffer = null;
      timeSpentFinalizing += System.nanoTime() - var1;
      return this;
   }

   public boolean isFinal() {
      return this.isFinal;
   }

   public void ensureNotFinal() {
      if (this.isFinal()) {
         this.restoreFinal();
         if (memoryDebug) {
            System.out.println("RESTORED " + this.debugName);
         }
      }

   }

   public GameTexture restoreFinal() {
      long var1 = System.nanoTime();
      if (!this.isFinal()) {
         return this;
      } else {
         this.buffer = this.getTextureData();
         this.isFinal = false;
         this.finalizeLater = false;
         timeSpentRestoring += System.nanoTime() - var1;
         return this;
      }
   }

   private ByteBuffer getTextureData() {
      ByteBuffer var1 = BufferUtils.createByteBuffer(this.width * this.height * 4);
      this.bind();
      GL11.glGetTexImage(3553, 0, 6408, 5121, var1);
      return var1;
   }

   private byte[] getTextureDataArray() {
      ByteBuffer var1 = this.getTextureData();
      byte[] var2 = new byte[var1.limit()];
      var1.get(var2);
      return var2;
   }

   public IntBuffer getIntBuffer() {
      IntBuffer var1 = BufferUtils.createIntBuffer(this.width * this.height * 4);
      this.bind();
      GL11.glGetTexImage(3553, 0, 6408, 5121, var1);
      return var1;
   }

   public BufferedImage getBufferedImage() {
      return getBufferedImage(this.width, this.height, this.buffer);
   }

   public File saveTextureImage(String var1) {
      return this.saveTextureImage(var1, true);
   }

   public File saveTextureImage(String var1, boolean var2) {
      ByteBuffer var3 = this.buffer;
      if (this.isFinal()) {
         var3 = this.getTextureData();
      }

      var1 = GameUtils.formatFileExtension(var1, "png");
      File var4 = new File(var1);
      GameUtils.mkDirs(var4);
      var3.position(0);
      STBImageWrite.stbi_write_png(var4.getAbsolutePath(), this.width, this.height, 4, var3, 0);
      if (var2) {
         GameLog.debug.println("Saved texture image to " + var4.getAbsolutePath());
      }

      return var4;
   }

   public static BufferedImage getBufferedImage(int var0, int var1, ByteBuffer var2) {
      try {
         BufferedImage var3 = new BufferedImage(var0, var1, 2);

         for(int var4 = 0; var4 < var0; ++var4) {
            for(int var5 = 0; var5 < var1; ++var5) {
               int var6 = (var4 + var5 * var0) * 4;
               int var7 = var2.get(var6) & 255;
               int var8 = var2.get(var6 + 1) & 255;
               int var9 = var2.get(var6 + 2) & 255;
               int var10 = var2.get(var6 + 3) & 255;
               int var11 = (new Color(var7, var8, var9, var10)).getRGB();
               var3.setRGB(var4, var5, var11);
            }
         }

         return var3;
      } catch (Exception var12) {
         var12.printStackTrace();
         return null;
      }
   }

   public GameTextureData getData() {
      ByteBuffer var1 = this.buffer;
      if (this.isFinal()) {
         var1 = this.getTextureData();
      }

      byte[] var2 = new byte[var1.limit()];
      var1.position(0);
      var1.get(var2, 0, var2.length);
      return new GameTextureData(this.width, this.height, var2, this.isFinal(), this.blendQuality);
   }

   public TextureDrawOptionsStart initDraw() {
      return TextureDrawOptions.initDraw(this);
   }

   public static enum BlendQuality {
      LINEAR(9729, 9729),
      NEAREST(9728, 9728);

      public final int minFilter;
      public final int magFilter;

      private BlendQuality(int var3, int var4) {
         this.minFilter = var3;
         this.magFilter = var4;
      }

      // $FF: synthetic method
      private static BlendQuality[] $values() {
         return new BlendQuality[]{LINEAR, NEAREST};
      }
   }
}
