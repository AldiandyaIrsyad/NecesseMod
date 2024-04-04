package necesse.gfx.gameFont;

import java.awt.Color;
import java.awt.Point;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGameFont extends GameFont {
   public static int START_TEXTURE_SIZE = 1024;
   public final int fontSize;
   public final int addedFontSize;
   public final int strokeSize;
   private TrueTypeGameFontSize[] fonts;
   private final HashMap<Point, Float> strokeDistanceMap = new HashMap();
   private final HashMap<Integer, CharacterInfo> charactersData = new HashMap();
   private STBTTPackedchar.Buffer nullBuffer;
   private LinkedList<STBTTPackedchar.Buffer> buffers = new LinkedList();
   private STBTTPackContext packContext;
   private int bitmapWidth;
   private int bitmapHeight;
   private ByteBuffer fontBitmap;
   private ByteBuffer strokeBitmap;
   private int fontTexture;
   private int strokeTexture;
   private final STBTTAlignedQuad alignedQuad;
   private final FloatBuffer xBuffer;
   private final FloatBuffer yBuffer;
   private final Object bufferLock;
   private final int yDrawOffset;
   private final CustomGameFont.CharArray preDefinedChars;

   public TrueTypeGameFont(int var1, int var2, int var3, int var4, CustomGameFont.CharArray var5, TrueTypeGameFontInfo... var6) {
      this.bitmapWidth = START_TEXTURE_SIZE;
      this.bitmapHeight = START_TEXTURE_SIZE;
      this.alignedQuad = STBTTAlignedQuad.malloc();
      this.xBuffer = MemoryUtil.memAllocFloat(1);
      this.yBuffer = MemoryUtil.memAllocFloat(1);
      this.bufferLock = new Object();
      this.fontSize = var1;
      this.strokeSize = var2;
      this.addedFontSize = var3;
      this.yDrawOffset = var4;
      this.preDefinedChars = var5;
      this.fonts = new TrueTypeGameFontSize[var6.length];

      int var7;
      for(var7 = 0; var7 < var6.length; ++var7) {
         this.fonts[var7] = new TrueTypeGameFontSize(var6[var7], var1);
      }

      if (var2 > 0) {
         for(var7 = -var2; var7 <= var2; ++var7) {
            for(int var8 = -var2; var8 <= var2; ++var8) {
               Point var9 = new Point(var8, var7);
               double var10 = var9.distance(0.0, 0.0);
               this.strokeDistanceMap.put(var9, (float)var10);
            }
         }
      }

      HashSet var13 = new HashSet();
      String[] var14 = CustomGameFont.fontLayout;
      int var15 = var14.length;

      for(int var16 = 0; var16 < var15; ++var16) {
         String var11 = var14[var16];

         for(int var12 = 0; var12 < var11.length(); ++var12) {
            var13.add(var11.codePointAt(var12));
         }
      }

      this.addCharacters(var13);
   }

   private void increaseSize() {
      this.bitmapWidth *= 2;
      this.bitmapHeight *= 2;
      GameLog.debug.println("Increased " + this.fontSize + "x" + this.strokeSize + " font map to " + this.bitmapWidth + "x" + this.bitmapHeight);
      if (this.packContext != null) {
         STBTruetype.stbtt_PackEnd(this.packContext);
         this.packContext.free();
      }

      this.buffers.forEach(CustomBuffer::free);
      this.buffers.clear();
      this.nullBuffer = null;
      this.packContext = null;
      this.charactersData.clear();
   }

   private void addCharacters(HashSet<Integer> var1) {
      HashMap var10001 = this.charactersData;
      Objects.requireNonNull(var10001);
      var1.removeIf(var10001::containsKey);
      if (!var1.isEmpty()) {
         if (this.packContext == null) {
            this.packContext = STBTTPackContext.malloc();
            this.fontBitmap = BufferUtils.createByteBuffer(this.bitmapWidth * this.bitmapHeight);
            if (this.strokeSize > 0) {
               this.strokeBitmap = BufferUtils.createByteBuffer(this.bitmapWidth * this.bitmapHeight);
            }

            int var2 = this.strokeSize * 2 + 1;
            STBTruetype.stbtt_PackBegin(this.packContext, this.fontBitmap, this.bitmapWidth, this.bitmapHeight, 0, var2);
         }

         TrueTypeGameFontSize[] var12 = this.fonts;
         int var3 = var12.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            TrueTypeGameFontSize var5 = var12[var4];
            ArrayList var6 = new ArrayList(var1.size());
            int var7 = Integer.MAX_VALUE;
            Iterator var8 = var1.iterator();

            int var9;
            while(var8.hasNext()) {
               var9 = (Integer)var8.next();
               if (var5.info.canDisplay(var9)) {
                  var6.add(var9);
                  var7 = Math.min(var7, var9);
               }
            }

            if (!var6.isEmpty()) {
               STBTTPackedchar.Buffer var17 = this.packCharacters(var5, var7, var6);
               if (var17 == null) {
                  HashSet var18 = new HashSet(this.charactersData.keySet());
                  var18.addAll(var1);
                  this.increaseSize();
                  this.addCharacters(var18);
                  return;
               }

               Objects.requireNonNull(var1);
               var6.forEach(var1::remove);
               this.buffers.add(var17);
               var9 = 0;
               Iterator var10 = var6.iterator();

               while(var10.hasNext()) {
                  int var11 = (Integer)var10.next();
                  this.charactersData.put(var11, new CharacterInfo(var11, var9++, var17));
               }
            }
         }

         if (!var1.isEmpty()) {
            if (this.nullBuffer == null) {
               this.nullBuffer = this.packCharacters(this.fonts[0], 0, Collections.singleton(0));
               if (this.nullBuffer == null) {
                  HashSet var15 = new HashSet(this.charactersData.keySet());
                  this.increaseSize();
                  this.addCharacters(var15);
                  return;
               }

               this.buffers.add(this.nullBuffer);
            }

            Iterator var13 = var1.iterator();

            while(var13.hasNext()) {
               var3 = (Integer)var13.next();
               this.charactersData.put(var3, new CharacterInfo(0, 0, this.nullBuffer));
            }
         }

         if (this.fontTexture != 0) {
            GL11.glDeleteTextures(this.fontTexture);
         }

         this.fontTexture = GL11.glGenTextures();
         GL11.glBindTexture(3553, this.fontTexture);
         ByteBuffer var14 = this.bitBufferToColorBuffer(this.fontBitmap, this.bitmapWidth, this.bitmapHeight);
         GL11.glTexImage2D(3553, 0, 32856, this.bitmapWidth, this.bitmapHeight, 0, 6408, 5121, var14);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         if (this.strokeBitmap != null) {
            if (this.strokeTexture != 0) {
               GL11.glDeleteTextures(this.strokeTexture);
            }

            this.strokeTexture = GL11.glGenTextures();
            GL11.glBindTexture(3553, this.strokeTexture);
            ByteBuffer var16 = this.bitBufferToColorBuffer(this.strokeBitmap, this.bitmapWidth, this.bitmapHeight);
            GL11.glTexImage2D(3553, 0, 32856, this.bitmapWidth, this.bitmapHeight, 0, 6408, 5121, var16);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
         }

      }
   }

   private ByteBuffer generateFontBuffer(ByteBuffer var1, ByteBuffer var2, int var3, int var4) {
      ByteBuffer var5 = BufferUtils.createByteBuffer(var3 * var4 * 4);
      var1.position(0);
      var2.position(0);
      var5.position(0);

      while(var1.hasRemaining()) {
         int var6 = var1.get() & 255;
         int var7 = var2.get() & 255;
         Color var8 = MergeFunction.MULTIPLY.merge(new Color(0, 0, 0, var7), new Color(255, 255, 255, var6));
         var5.put(new byte[]{(byte)var8.getRed(), (byte)var8.getGreen(), (byte)var8.getBlue(), (byte)var8.getAlpha()});
      }

      var1.position(0);
      var2.position(0);
      var5.position(0);
      return var5;
   }

   private ByteBuffer bitBufferToColorBuffer(ByteBuffer var1, int var2, int var3) {
      ByteBuffer var4 = BufferUtils.createByteBuffer(var2 * var3 * 4);
      var1.position(0);
      var4.position(0);

      while(var1.hasRemaining()) {
         byte var5 = var1.get();
         var4.put(new byte[]{-1, -1, -1, var5});
      }

      var1.position(0);
      var4.position(0);
      return var4;
   }

   private synchronized STBTTPackedchar.Buffer packCharacters(TrueTypeGameFontSize var1, int var2, Collection<Integer> var3) {
      MemoryStack var4 = MemoryStack.stackPush();

      STBTTPackedchar.Buffer var15;
      label52: {
         try {
            byte var5 = 1;
            byte var6 = 1;
            STBTTPackedchar.Buffer var7 = STBTTPackedchar.malloc(var3.size());
            STBTTPackRange.Buffer var8 = STBTTPackRange.malloc(1, var4);
            IntBuffer var9 = var4.mallocInt(var3.size());
            Iterator var10 = var3.iterator();

            while(var10.hasNext()) {
               int var11 = (Integer)var10.next();
               var9.put(var11);
            }

            var9.position(0);
            var8.put(STBTTPackRange.malloc(var4).set(var1.fontSize + (float)this.addedFontSize, var2, var9, var3.size(), var7, var5, var6));
            var8.flip();
            boolean var14 = STBTruetype.stbtt_PackFontRanges(this.packContext, var1.info.getTTFBuffer(), 0, var8);
            var7.clear();
            if (!var14) {
               var7.free();
               var15 = null;
               break label52;
            }

            this.generateStrokeBitmap(var7);
            var15 = var7;
         } catch (Throwable var13) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (var4 != null) {
            var4.close();
         }

         return var15;
      }

      if (var4 != null) {
         var4.close();
      }

      return var15;
   }

   private synchronized void generateStrokeBitmap(STBTTPackedchar.Buffer var1) {
      if (this.strokeBitmap != null) {
         var1.position(0);

         while(var1.hasRemaining()) {
            STBTTPackedchar var2 = (STBTTPackedchar)var1.get();
            short var3 = var2.x0();
            short var4 = var2.x1();
            short var5 = var2.y0();
            short var6 = var2.y1();

            for(int var7 = var3; var7 < var4; ++var7) {
               for(int var8 = var5; var8 < var6; ++var8) {
                  int var9 = this.fontBitmap.get(var7 + var8 * this.bitmapHeight) & 255;
                  if (var9 > 0) {
                     Iterator var10 = this.strokeDistanceMap.entrySet().iterator();

                     while(var10.hasNext()) {
                        Map.Entry var11 = (Map.Entry)var10.next();
                        Point var12 = (Point)var11.getKey();
                        float var13 = (Float)var11.getValue();
                        if (!(var13 > (float)this.strokeSize + 0.5F)) {
                           Point var14 = new Point(var7 + var12.x, var8 + var12.y);
                           int var15 = this.strokeBitmap.get(var14.x + var14.y * this.bitmapHeight) & 255;
                           int var16;
                           if (var13 > (float)this.strokeSize - 0.5F) {
                              var16 = var9;
                           } else {
                              var16 = 255;
                           }

                           if (var15 < var16) {
                              this.strokeBitmap.put(var14.x + var14.y * this.bitmapHeight, (byte)var16);
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public void addCharOffset(String var1, float var2, float var3) {
      for(int var4 = 0; var4 < var1.length(); ++var4) {
         this.addCharOffset(var1.charAt(var4), var2, var3);
      }

   }

   public boolean addCharOffset(char var1, float var2, float var3) {
      CharacterInfo var4 = (CharacterInfo)this.charactersData.get(Integer.valueOf(var1));
      if (var4 != null) {
         var4.xOffset += var2;
         var4.yOffset += var3;
         return true;
      } else {
         return false;
      }
   }

   public synchronized float drawString(float var1, float var2, String var3, FontBasicOptions var4) {
      synchronized(this.bufferLock) {
         boolean var6 = var4.isPixelFont();
         float var7 = (float)var4.getSize() / (float)this.fontSize;
         float var8 = var7 * (float)this.fontSize;
         var2 += (float)this.yDrawOffset * var7;
         var2 -= (float)this.strokeSize * var7;
         var2 += var8;
         float var9 = 0.0F;
         float var10 = 0.0F;
         Object var11 = null;
         int var12;
         char var13;
         if (this.strokeTexture != 0) {
            var9 = (float)this.strokeSize / (float)this.bitmapWidth;
            var10 = (float)this.strokeSize / (float)this.bitmapHeight;
            this.xBuffer.put(0, var1);
            this.yBuffer.put(0, var2);
            var4.applyGLStrokeColor();

            for(var12 = 0; var12 < var3.length(); ++var12) {
               var13 = var3.charAt(var12);
               this.drawCharQuad(var13, var9, var10, var7, (TextureSwapper)var11, true, var6);
            }
         }

         this.xBuffer.put(0, var1);
         this.yBuffer.put(0, var2);
         var4.applyGLColor();

         for(var12 = 0; var12 < var3.length(); ++var12) {
            var13 = var3.charAt(var12);
            this.drawCharQuad(var13, var9, var10, var7, (TextureSwapper)var11, false, var6);
         }

         return this.xBuffer.get(0) - var1;
      }
   }

   private synchronized void drawCharQuad(char var1, float var2, float var3, float var4, TextureSwapper var5, boolean var6, boolean var7) {
      synchronized(this.bufferLock) {
         GameFontGlyphPositionTexture var9 = null;
         if (var7 && this.preDefinedChars != null && var1 < this.preDefinedChars.chars.length) {
            var9 = this.preDefinedChars.chars[var1];
         }

         float var11;
         float var12;
         if (var9 != null) {
            if (var6) {
               return;
            }

            float var10 = this.xBuffer.get(0);
            var11 = this.yBuffer.get(0);
            var12 = (float)var9.width * var4;
            var9.texture.bind();
            var9.draw(var10, var11 - (float)(var9.height + this.yDrawOffset) * var4, var12, (float)var9.height * var4);
            this.xBuffer.put(0, var10 + var12);
         } else {
            CharacterInfo var23 = (CharacterInfo)this.charactersData.get(Integer.valueOf(var1));
            if (var23 == null) {
               var23 = (CharacterInfo)this.charactersData.get(63);
            }

            var23.data.position(0);
            var11 = this.xBuffer.get(0);
            var12 = this.yBuffer.get(0);
            STBTruetype.stbtt_GetPackedQuad(var23.data, this.bitmapWidth, this.bitmapHeight, var23.charIndex, this.xBuffer, this.yBuffer, this.alignedQuad, true);
            float var13 = (this.xBuffer.get(0) - var11) * var4;
            this.xBuffer.put(0, var11 + var13);
            float var14 = this.alignedQuad.y0() - var12 + var23.yOffset;
            float var15 = this.alignedQuad.x0() + var23.xOffset * var4;
            float var16 = var12 + (var14 + (float)this.strokeSize) * var4 - (float)this.strokeSize;
            float var17 = this.alignedQuad.x1() - this.alignedQuad.x0();
            float var18 = this.alignedQuad.y1() - this.alignedQuad.y0();
            float var19 = (var17 + (float)this.strokeSize) * var4;
            float var20 = (var18 + (float)this.strokeSize) * var4;
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var6 ? this.strokeTexture : this.fontTexture);
            GL11.glBegin(7);
            this.drawQuad(var15, var16, var15 + var19, var16 + var20, this.alignedQuad.s0() - var2, this.alignedQuad.t0() - var3, this.alignedQuad.s1() + var2, this.alignedQuad.t1() + var3);
            GL11.glEnd();
         }

      }
   }

   private void drawQuad(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      GL11.glTexCoord2f(var5, var6);
      GL11.glVertex2f(var1, var2);
      GL11.glTexCoord2f(var7, var6);
      GL11.glVertex2f(var3, var2);
      GL11.glTexCoord2f(var7, var8);
      GL11.glVertex2f(var3, var4);
      GL11.glTexCoord2f(var5, var8);
      GL11.glVertex2f(var1, var4);
   }

   public synchronized float drawChar(float var1, float var2, char var3, FontBasicOptions var4) {
      return this.drawString(var1, var2, Character.toString(var3), var4);
   }

   public synchronized float getWidth(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.getFontHeight();
      GameFontGlyphPositionTexture var4 = null;
      if (var2.isPixelFont() && this.preDefinedChars != null && var1 < this.preDefinedChars.chars.length) {
         var4 = this.preDefinedChars.chars[var1];
      }

      if (var4 != null) {
         return (float)var4.width * var3;
      } else {
         synchronized(this.bufferLock) {
            CharacterInfo var6 = (CharacterInfo)this.charactersData.get(Integer.valueOf(var1));
            if (var6 == null) {
               var6 = (CharacterInfo)this.charactersData.get(63);
            }

            return var6.getWidth() * var3;
         }
      }
   }

   public synchronized float getWidth(String var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.getFontHeight();
      float var4 = 0.0F;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         char var6 = var1.charAt(var5);
         GameFontGlyphPositionTexture var7 = null;
         if (var2.isPixelFont() && this.preDefinedChars != null && var6 < this.preDefinedChars.chars.length) {
            var7 = this.preDefinedChars.chars[var6];
         }

         if (var7 != null) {
            var4 += (float)var7.width * var3;
         } else {
            synchronized(this.bufferLock) {
               CharacterInfo var9 = (CharacterInfo)this.charactersData.get(Integer.valueOf(var6));
               if (var9 == null) {
                  var9 = (CharacterInfo)this.charactersData.get(63);
               }

               float var10 = var9.getWidth() * var3;
               var4 += var10;
            }
         }
      }

      return var4;
   }

   public float getHeight(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontSize;
      return var3 * (float)this.fontSize;
   }

   public float getHeight(String var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontSize;
      return var3 * (float)this.fontSize;
   }

   public synchronized int getWidthCeil(String var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.getFontHeight();
      float var4 = 0.0F;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         char var6 = var1.charAt(var5);
         GameFontGlyphPositionTexture var7 = null;
         if (var2.isPixelFont() && this.preDefinedChars != null && var6 < this.preDefinedChars.chars.length) {
            var7 = this.preDefinedChars.chars[var6];
         }

         if (var7 != null) {
            var4 += (float)var7.width * var3;
         } else {
            synchronized(this.bufferLock) {
               CharacterInfo var9 = (CharacterInfo)this.charactersData.get(Integer.valueOf(var6));
               if (var9 == null) {
                  var9 = (CharacterInfo)this.charactersData.get(63);
               }

               float var10 = var9.getWidth() * var3;
               var4 += var10;
            }
         }
      }

      return (int)Math.ceil((double)var4);
   }

   public synchronized int getWidthCeil(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.getFontHeight();
      GameFontGlyphPositionTexture var4 = null;
      if (var2.isPixelFont() && this.preDefinedChars != null && var1 < this.preDefinedChars.chars.length) {
         var4 = this.preDefinedChars.chars[var1];
      }

      if (var4 != null) {
         return (int)Math.ceil((double)((float)var4.width * var3));
      } else {
         synchronized(this.bufferLock) {
            CharacterInfo var6 = (CharacterInfo)this.charactersData.get(Integer.valueOf(var1));
            if (var6 == null) {
               var6 = (CharacterInfo)this.charactersData.get(63);
            }

            float var7 = var6.getWidth() * var3;
            return (int)Math.ceil((double)var7);
         }
      }
   }

   public int getFontHeight() {
      return this.fontSize;
   }

   public int getHeightCeil(String var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontSize;
      return (int)(var3 * (float)this.fontSize);
   }

   public int getHeightCeil(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontSize;
      return (int)(var3 * (float)this.fontSize);
   }

   public boolean canDraw(char var1) {
      return this.charactersData.containsKey(Integer.valueOf(var1));
   }

   public void deleteTextures() {
      this.dispose();
   }

   public GameFont updateFont(String var1) {
      if (var1 == null) {
         return this;
      } else {
         HashSet var2 = new HashSet();

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            var2.add(var1.codePointAt(var3));
         }

         this.addCharacters(var2);
         return this;
      }
   }

   public void dispose() {
      if (this.preDefinedChars != null) {
         this.preDefinedChars.texture.delete();
      }

      if (this.packContext != null) {
         STBTruetype.stbtt_PackEnd(this.packContext);
         this.packContext.free();
      }

      this.buffers.forEach(CustomBuffer::free);
      this.buffers.clear();
      this.packContext = null;
      this.charactersData.clear();
      if (this.fontTexture != 0) {
         GL11.glDeleteTextures(this.fontTexture);
      }

      this.fontTexture = 0;
      if (this.strokeTexture != 0) {
         GL11.glDeleteTextures(this.strokeTexture);
      }

      this.strokeTexture = 0;
      MemoryUtil.memFree(this.xBuffer);
      MemoryUtil.memFree(this.yBuffer);
      this.alignedQuad.free();
   }

   private static class CharacterInfo {
      public final int codePoint;
      public final int charIndex;
      public final STBTTPackedchar.Buffer data;
      public final int x0;
      public final int y0;
      public final int x1;
      public final int y1;
      public final float xOff;
      public final float yOff;
      public final float xAdvance;
      public final float xOff2;
      public final float yOff2;
      public float yOffset;
      public float xOffset;

      public CharacterInfo(int var1, int var2, STBTTPackedchar.Buffer var3) {
         this.codePoint = var1;
         this.charIndex = var2;
         this.data = var3;
         STBTTPackedchar var4 = (STBTTPackedchar)var3.get(var2);
         this.x0 = var4.x0();
         this.y0 = var4.y0();
         this.x1 = var4.x1();
         this.y1 = var4.y1();
         this.xOff = var4.xoff();
         this.yOff = var4.yoff();
         this.xAdvance = var4.xadvance();
         this.xOff2 = var4.xoff2();
         this.yOff2 = var4.yoff2();
      }

      public float getWidth() {
         return this.xAdvance + this.xOff;
      }

      public int getHeight() {
         return this.y1 - this.y0;
      }
   }

   private static class TextureSwapper {
      private int currentTextureID;
      private boolean drawing;

      public TextureSwapper() {
      }

      public void useTexture(GameTexture var1) {
         if (this.currentTextureID != var1.getTextureID()) {
            if (this.drawing) {
               GL11.glEnd();
               this.drawing = false;
            }

            this.currentTextureID = var1.getTextureID();
            var1.bind();
            if (!this.drawing) {
               GL11.glBegin(7);
            }

            this.drawing = true;
         }
      }

      public void useTexture(int var1) {
         if (this.currentTextureID != var1) {
            if (this.drawing) {
               GL11.glEnd();
               this.drawing = false;
            }

            this.currentTextureID = var1;
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            if (!this.drawing) {
               GL11.glBegin(7);
            }

            this.drawing = true;
         }
      }

      public void end() {
         if (this.drawing) {
            GL11.glEnd();
            this.drawing = false;
         }

         this.currentTextureID = 0;
      }
   }
}
