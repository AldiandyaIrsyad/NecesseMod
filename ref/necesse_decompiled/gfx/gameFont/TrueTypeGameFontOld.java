package necesse.gfx.gameFont;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.res.ResourceEncoder;

public class TrueTypeGameFontOld extends GameFont {
   public static boolean debug = false;
   private static boolean debugColor = true;
   private static final char[] ignoredCharacters = new char[]{'\n', '\t', '\b', '\r'};
   private static final int padding = 1;
   private static final int defaultChars = 0;
   public final boolean antiAlias;
   public final int fontHeight;
   public final String file;
   public final Font font;
   public final boolean addOutline;
   private final int drawOffset;
   private final CustomGameFont.CharArray preDefinedChars;
   private GameFontGlyphPositionTexture[] charArray;
   public LinkedList<GameTexture> textures;

   private static boolean isIgnored(char var0) {
      char[] var1 = ignoredCharacters;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1[var3];
         if (var0 == var4) {
            return true;
         }
      }

      return false;
   }

   public TrueTypeGameFontOld(String var1, int var2, boolean var3, boolean var4, int var5, int var6, CustomGameFont.CharArray var7, String var8) {
      this.textures = new LinkedList();
      this.file = var1;
      this.font = loadTrueTypeFont(var1, var2 - (var4 ? 2 : 0) + var5);
      this.antiAlias = var3;
      this.addOutline = var4;
      this.drawOffset = var6;
      this.preDefinedChars = var7;
      this.fontHeight = var2;
      if (var7 == null) {
         if (var8 == null) {
            var8 = GameUtils.join(CustomGameFont.fontLayout, "");
         } else {
            var8 = GameUtils.join(CustomGameFont.fontLayout, "") + var8;
         }
      }

      this.generateFont(var8);
   }

   public TrueTypeGameFontOld(String var1, int var2, boolean var3, boolean var4, int var5, int var6, CustomGameFont.CharArray var7) {
      this(var1, var2, var3, var4, var5, var6, var7, (String)null);
   }

   public TrueTypeGameFontOld updateFont(String var1) {
      if (var1 == null) {
         return this;
      } else {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (var3 >= this.charArray.length || this.charArray[var3] == null) {
               return this.generateFont(var1);
            }
         }

         return this;
      }
   }

   private TrueTypeGameFontOld generateFont(String var1) {
      try {
         int var2 = 0;
         if (this.preDefinedChars != null) {
            var2 = Math.max(var2, this.preDefinedChars.chars.length);
         }

         if (var1 != null) {
            for(int var3 = 0; var3 < var1.length(); ++var3) {
               var2 = Math.max(var2, var1.charAt(var3) + 1);
            }
         }

         GameFontGlyphPosition[] var13 = new GameFontGlyphPosition[var2];
         TextureSizeGenerator var4 = new TextureSizeGenerator();

         int var5;
         for(var5 = 0; var5 < 0; ++var5) {
            if (this.font.canDisplay((char)var5) && !isIgnored((char)var5) && (this.preDefinedChars == null || !this.preDefinedChars.canDraw((char)var5))) {
               var13[var5] = var4.addCharacter(var5);
            }
         }

         int var6;
         if (var1 != null) {
            for(var5 = 0; var5 < var1.length(); ++var5) {
               var6 = var1.charAt(var5);
               if ((this.charArray == null || var6 >= this.charArray.length || this.charArray[var6] == null) && var13[var6] == null && this.font.canDisplay((char)var6) && !isIgnored((char)var6)) {
                  var13[var6] = var4.addCharacter(var6);
               }
            }
         }

         TextureGenerator var14 = null;
         if (var4.textureWidth > 0 && var4.textureHeight > 0) {
            var14 = new TextureGenerator(var4.textureWidth, var4.textureHeight);

            for(var6 = 0; var6 < var13.length; ++var6) {
               if (var13[var6] != null) {
                  var14.drawCharacter((char)var6, var13[var6]);
               }
            }
         }

         synchronized(this) {
            if (this.charArray != null) {
               this.charArray = (GameFontGlyphPositionTexture[])Arrays.copyOf(this.charArray, Math.max(var13.length, this.charArray.length));
            } else {
               this.charArray = new GameFontGlyphPositionTexture[var13.length];
            }

            GameTexture var7 = var14 != null ? new GameTexture(this.file + "", var14.image) : new GameTexture(this.file + " font", 0, 0);

            for(int var8 = 0; var8 < var13.length; ++var8) {
               if (this.preDefinedChars != null && this.preDefinedChars.canDraw((char)var8)) {
                  this.charArray[var8] = this.preDefinedChars.chars[var8];
               } else if (var13[var8] != null) {
                  GameFontGlyphPositionTexture var9 = new GameFontGlyphPositionTexture(var7, var13[var8]);
                  var9.drawYOffset += this.drawOffset;
                  this.charArray[var8] = var9;
               }
            }

            this.textures.add(var7);
         }
      } catch (Exception var12) {
         System.err.println("Failed to create font.");
         var12.printStackTrace();
      }

      return this;
   }

   public synchronized float drawChar(float var1, float var2, char var3, FontBasicOptions var4) {
      GameFontGlyphPositionTexture var5 = null;
      if (var3 < this.charArray.length) {
         var5 = this.charArray[var3];
      }

      if (var5 == null) {
         var5 = this.charArray[63];
      }

      float var6 = this.getGlyphWidth(var5, var4.getSize());
      float var7 = this.getGlyphHeight(var5, var4.getSize());
      var5.texture.bind();
      var4.applyGLColor();
      var5.draw(var1, var2, var6, var7);
      return var6;
   }

   private float getGlyphWidth(GameFontGlyphPosition var1, int var2) {
      float var3 = (float)var2 / (float)this.fontHeight;
      return var3 * (float)var1.width;
   }

   private float getGlyphHeight(GameFontGlyphPosition var1, int var2) {
      float var3 = (float)var2 / (float)this.fontHeight;
      return var3 * (float)var1.height;
   }

   public synchronized float getWidth(char var1, FontBasicOptions var2) {
      GameFontGlyphPositionTexture var3 = null;
      if (var1 < this.charArray.length) {
         var3 = this.charArray[var1];
      }

      if (var3 == null) {
         var3 = this.charArray[63];
      }

      return this.getGlyphWidth(var3, var2.getSize());
   }

   public float getHeight(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontHeight;
      return var3 * (float)this.font.getSize();
   }

   public synchronized int getWidthCeil(char var1, FontBasicOptions var2) {
      GameFontGlyphPositionTexture var3 = null;
      if (var1 < this.charArray.length) {
         var3 = this.charArray[var1];
      }

      if (var3 == null) {
         var3 = this.charArray[63];
      }

      return (int)this.getGlyphWidth(var3, var2.getSize());
   }

   public int getFontHeight() {
      return this.fontHeight;
   }

   public synchronized int getHeightCeil(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontHeight;
      return (int)(var3 * (float)this.font.getSize());
   }

   public synchronized boolean canDraw(char var1) {
      return var1 < this.charArray.length && this.charArray[var1] != null;
   }

   public synchronized void deleteTextures() {
      Iterator var1 = this.textures.iterator();

      while(var1.hasNext()) {
         GameTexture var2 = (GameTexture)var1.next();
         var2.delete();
      }

      this.textures = new LinkedList();
      if (this.preDefinedChars != null) {
         this.preDefinedChars.texture.delete();
      }

   }

   private static Font loadTrueTypeFont(String var0, int var1) {
      try {
         File var3 = new File(GlobalData.rootPath() + "res/fonts/" + var0 + ".ttf");
         Object var2;
         if (var3.exists()) {
            var2 = new FileInputStream(var3);
         } else {
            try {
               var2 = ResourceEncoder.getResourceInputStream("fonts/" + var0 + ".ttf");
            } catch (FileNotFoundException var5) {
               var2 = new FileInputStream(GlobalData.rootPath() + "res/fonts/" + var0 + ".ttf");
            }
         }

         Font var4 = Font.createFont(0, (InputStream)var2);
         var4 = var4.deriveFont(1, (float)var1);
         ((InputStream)var2).close();
         return var4;
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GameFont updateFont(String var1) {
      return this.updateFont(var1);
   }

   private class TextureSizeGenerator {
      public static final int maxWidth = 2048;
      private Canvas canvas;
      public int textureWidth;
      public int textureHeight;
      private int actualPadding;
      private int positionX;
      private int positionY;
      private int rowHeight;

      private TextureSizeGenerator() {
         this.canvas = new Canvas();
         this.actualPadding = 1 + (TrueTypeGameFontOld.this.addOutline ? 2 : 0);
         this.positionX = this.actualPadding;
         this.positionY = this.actualPadding;
      }

      public GameFontGlyphPosition addCharacter(int var1) {
         FontMetrics var2 = this.canvas.getFontMetrics(TrueTypeGameFontOld.this.font);
         int var3 = var2.charWidth(var1);
         int var4 = var2.getHeight();
         if (this.positionX + var3 + this.actualPadding >= 2048) {
            this.positionX = this.actualPadding;
            this.positionY += this.rowHeight;
            this.rowHeight = 0;
            this.textureWidth = 2048;
         }

         GameFontGlyphPosition var5 = new GameFontGlyphPosition(this.positionX - (TrueTypeGameFontOld.this.addOutline ? 1 : 0), this.positionY - (TrueTypeGameFontOld.this.addOutline ? 1 : 0), var3 + (TrueTypeGameFontOld.this.addOutline ? 2 : 0), var4 + (TrueTypeGameFontOld.this.addOutline ? 2 : 0));
         if (this.positionX + var3 + this.actualPadding > this.textureWidth) {
            this.textureWidth = this.positionX + var3 + this.actualPadding;
         }

         if (var4 + this.actualPadding > this.rowHeight) {
            this.rowHeight = var4 + this.actualPadding;
            this.textureHeight = this.positionY + this.rowHeight + this.actualPadding;
         }

         this.positionX += var3 + this.actualPadding;
         return var5;
      }

      // $FF: synthetic method
      TextureSizeGenerator(Object var2) {
         this();
      }
   }

   private class TextureGenerator {
      public final BufferedImage image;
      public final Graphics2D graphics;
      public final FontMetrics fontMetrics;

      public TextureGenerator(int var2, int var3) {
         this.image = new BufferedImage(var2, var3, 2);
         this.graphics = (Graphics2D)this.image.getGraphics();
         this.graphics.setColor(new Color(0, 0, 0, 0));
         this.graphics.fillRect(0, 0, var2, var3);
         this.graphics.setFont(TrueTypeGameFontOld.this.font);
         this.fontMetrics = this.graphics.getFontMetrics();
         this.graphics.setColor(Color.WHITE);
         if (TrueTypeGameFontOld.this.antiAlias) {
            this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         }

      }

      public void drawCharacter(char var1, GameFontGlyphPosition var2) {
         int var3 = var2.textureX + (TrueTypeGameFontOld.this.addOutline ? 1 : 0);
         int var4 = var2.textureY + this.fontMetrics.getAscent() + (TrueTypeGameFontOld.this.addOutline ? 1 : 0);
         if (TrueTypeGameFontOld.this.addOutline) {
            this.graphics.setColor(Color.BLACK);
            this.graphics.drawString(String.valueOf(var1), var3 - 1, var4);
            this.graphics.drawString(String.valueOf(var1), var3 + 1, var4);
            this.graphics.drawString(String.valueOf(var1), var3, var4 - 1);
            this.graphics.drawString(String.valueOf(var1), var3, var4 + 1);
            this.graphics.setColor(Color.WHITE);
         }

         this.graphics.drawString(String.valueOf(var1), var3, var4);
         if (TrueTypeGameFontOld.debug) {
            System.out.println("Drawing " + var1 + " at " + var3 + ", " + var4);
            var3 = var2.textureX;
            var4 = var2.textureY;
            this.graphics.setColor(TrueTypeGameFontOld.debugColor ? Color.RED : Color.GREEN);
            TrueTypeGameFontOld.debugColor = !TrueTypeGameFontOld.debugColor;
            this.graphics.drawLine(var3, var4, var3, var4 + var2.height);
            this.graphics.drawLine(var3, var4, var3 + var2.width, var4);
            this.graphics.drawLine(var3 + var2.width, var4, var3 + var2.width, var4 + var2.height);
            this.graphics.drawLine(var3, var4 + var2.height, var3 + var2.width, var4 + var2.height);
            this.graphics.setColor(Color.WHITE);
         }

      }

      public void saveImage(String var1) {
         try {
            GameLog.debug.println("Saving image");
            ImageIO.write(this.image, "PNG", new File(var1 + ".png"));
            GameLog.debug.println("Saved image");
         } catch (IOException var3) {
            var3.printStackTrace();
         }

      }
   }
}
