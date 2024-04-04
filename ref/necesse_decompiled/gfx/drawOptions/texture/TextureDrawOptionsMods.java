package necesse.gfx.drawOptions.texture;

import java.awt.Color;
import java.awt.Dimension;
import necesse.engine.world.GameClock;
import necesse.level.maps.light.GameLight;

public abstract class TextureDrawOptionsMods {
   protected final TextureDrawOptionsObj opts;

   protected TextureDrawOptionsMods(TextureDrawOptionsObj var1) {
      this.opts = var1;
   }

   protected TextureDrawOptionsMods rotate(float var1, int var2, int var3) {
      this.opts.setRotation(var1, var2, var3);
      return this;
   }

   protected TextureDrawOptionsMods rotate(float var1) {
      return this.rotate(var1, this.opts.width / 2, this.opts.height / 2);
   }

   protected TextureDrawOptionsMods addRotation(float var1, int var2, int var3) {
      this.opts.addRotation(var1, var2, var3);
      return this;
   }

   protected TextureDrawOptionsMods rotateTexture(int var1, int var2, int var3) {
      if (var1 < -4 || var1 > 4) {
         var1 %= 4;
      }

      TextureDrawOptionsObj var10000;
      int var4;
      float var5;
      float var6;
      int var7;
      if (var1 < 0) {
         for(var4 = 0; var4 > var1; --var4) {
            var5 = this.opts.spriteX1;
            this.opts.spriteX1 = this.opts.spriteX2;
            this.opts.spriteX2 = this.opts.spriteX3;
            this.opts.spriteX3 = this.opts.spriteX4;
            this.opts.spriteX4 = var5;
            var6 = this.opts.spriteY1;
            this.opts.spriteY1 = this.opts.spriteY2;
            this.opts.spriteY2 = this.opts.spriteY3;
            this.opts.spriteY3 = this.opts.spriteY4;
            this.opts.spriteY4 = var6;
            var10000 = this.opts;
            var10000.translateX -= this.opts.width / 2 - var2;
            var10000 = this.opts;
            var10000.translateY -= this.opts.height / 2 - var3;
            var7 = this.opts.width;
            this.opts.width = this.opts.height;
            this.opts.height = var7;
         }
      } else {
         for(var4 = 0; var4 < var1; ++var4) {
            var5 = this.opts.spriteX1;
            this.opts.spriteX1 = this.opts.spriteX4;
            this.opts.spriteX4 = this.opts.spriteX3;
            this.opts.spriteX3 = this.opts.spriteX2;
            this.opts.spriteX2 = var5;
            var6 = this.opts.spriteY1;
            this.opts.spriteY1 = this.opts.spriteY4;
            this.opts.spriteY4 = this.opts.spriteY3;
            this.opts.spriteY3 = this.opts.spriteY2;
            this.opts.spriteY2 = var6;
            var10000 = this.opts;
            var10000.translateX += this.opts.width / 2 - var2;
            var10000 = this.opts;
            var10000.translateY += this.opts.height / 2 - var3;
            var7 = this.opts.width;
            this.opts.width = this.opts.height;
            this.opts.height = var7;
         }
      }

      return this;
   }

   protected TextureDrawOptionsMods rotateTexture(int var1) {
      return this.rotateTexture(var1, this.opts.width / 2, this.opts.height / 2);
   }

   protected TextureDrawOptionsMods size(int var1, int var2) {
      this.opts.width = var1;
      this.opts.height = var2;
      return this;
   }

   protected TextureDrawOptionsMods size(Dimension var1) {
      this.opts.width = var1.width;
      this.opts.height = var1.height;
      return this;
   }

   protected TextureDrawOptionsMods shrinkWidth(int var1, boolean var2) {
      float var3 = (float)this.opts.height / (float)this.opts.width;
      this.opts.height = (int)((float)var1 * var3);
      this.opts.width = var1;
      if (var2) {
         TextureDrawOptionsObj var10000 = this.opts;
         var10000.translateY += (var1 - this.opts.height) / 2;
      }

      return this;
   }

   protected TextureDrawOptionsMods shrinkHeight(int var1, boolean var2) {
      float var3 = (float)this.opts.width / (float)this.opts.height;
      this.opts.width = (int)((float)var1 * var3);
      this.opts.height = var1;
      if (var2) {
         TextureDrawOptionsObj var10000 = this.opts;
         var10000.translateX += (var1 - this.opts.width) / 2;
      }

      return this;
   }

   protected TextureDrawOptionsMods size(int var1, boolean var2) {
      if (this.opts.width < this.opts.height) {
         return this.shrinkHeight(var1, var2);
      } else if (this.opts.height < this.opts.width) {
         return this.shrinkWidth(var1, var2);
      } else {
         this.opts.width = var1;
         this.opts.height = var1;
         return this;
      }
   }

   protected TextureDrawOptionsMods size(int var1) {
      return this.size(var1, true);
   }

   protected TextureDrawOptionsMods color(float var1, float var2, float var3, float var4) {
      this.opts.red = var1;
      this.opts.green = var2;
      this.opts.blue = var3;
      this.opts.alpha = var4;
      return this;
   }

   protected TextureDrawOptionsMods color(float var1) {
      this.opts.red = var1;
      this.opts.green = var1;
      this.opts.blue = var1;
      return this;
   }

   protected TextureDrawOptionsMods brightness(float var1) {
      TextureDrawOptionsObj var10000 = this.opts;
      var10000.red *= var1;
      var10000 = this.opts;
      var10000.green *= var1;
      var10000 = this.opts;
      var10000.blue *= var1;
      return this;
   }

   protected TextureDrawOptionsMods color(float var1, float var2, float var3) {
      this.opts.red = var1;
      this.opts.green = var2;
      this.opts.blue = var3;
      return this;
   }

   protected TextureDrawOptionsMods color(Color var1, boolean var2) {
      return var2 ? this.color((float)var1.getRed() / 255.0F, (float)var1.getGreen() / 255.0F, (float)var1.getBlue() / 255.0F, (float)var1.getAlpha() / 255.0F) : this.color((float)var1.getRed() / 255.0F, (float)var1.getGreen() / 255.0F, (float)var1.getBlue() / 255.0F);
   }

   protected TextureDrawOptionsMods color(Color var1) {
      return this.color(var1, true);
   }

   protected TextureDrawOptionsMods alpha(float var1) {
      this.opts.alpha = var1;
      return this;
   }

   protected TextureDrawOptionsMods light(GameLight var1) {
      float var2 = var1.getFloatLevel();
      TextureDrawOptionsObj var10000 = this.opts;
      var10000.red *= var1.getFloatRed();
      var10000 = this.opts;
      var10000.green *= var1.getFloatGreen();
      var10000 = this.opts;
      var10000.blue *= var1.getFloatBlue();
      return this.brightness(var2);
   }

   protected TextureDrawOptionsMods colorLight(float var1, float var2, float var3, GameLight var4) {
      this.color(var1, var2, var3);
      return this.light(var4);
   }

   protected TextureDrawOptionsMods colorLight(float var1, float var2, float var3, float var4, GameLight var5) {
      this.color(var1, var2, var3, var4);
      return this.light(var5);
   }

   protected TextureDrawOptionsMods colorLight(Color var1, GameLight var2) {
      this.color(var1);
      return this.light(var2);
   }

   protected TextureDrawOptionsMods colorLight(Color var1, boolean var2, GameLight var3) {
      this.color(var1, var2);
      return this.light(var3);
   }

   protected TextureDrawOptionsMods colorMult(Color var1) {
      TextureDrawOptionsObj var10000 = this.opts;
      var10000.red *= (float)var1.getRed() / 255.0F;
      var10000 = this.opts;
      var10000.green *= (float)var1.getGreen() / 255.0F;
      var10000 = this.opts;
      var10000.blue *= (float)var1.getBlue() / 255.0F;
      var10000 = this.opts;
      var10000.alpha *= (float)var1.getAlpha() / 255.0F;
      return this;
   }

   protected TextureDrawOptionsMods spelunkerColorLight(float var1, float var2, float var3, float var4, GameLight var5, boolean var6, long var7, GameClock var9, long var10, float var12, int var13) {
      if (var6) {
         long var14 = var9.getTime();
         var14 += var10 * var7;
         float var16 = (float)Math.floorMod(var14, var10) / (float)var10;
         int var17 = Color.HSBtoRGB(var16, var12, 1.0F);
         float var18 = (float)(var17 >> 16 & 255) / 255.0F;
         float var19 = (float)(var17 >> 8 & 255) / 255.0F;
         float var20 = (float)(var17 & 255) / 255.0F;
         var1 *= var18;
         var2 *= var19;
         var3 *= var20;
         return this.colorLight(var1, var2, var3, var4, var5.minLevelCopy((float)var13));
      } else {
         return this.colorLight(var1, var2, var3, var4, var5);
      }
   }

   protected TextureDrawOptionsMods spelunkerColorLight(float var1, float var2, float var3, GameLight var4, boolean var5, long var6, GameClock var8, long var9, float var11, int var12) {
      return this.spelunkerColorLight(var1, var2, var3, this.opts.alpha, var4, var5, var6, var8, var9, var11, var12);
   }

   protected TextureDrawOptionsMods spelunkerColorLight(Color var1, boolean var2, GameLight var3, boolean var4, long var5, GameClock var7, long var8, float var10, int var11) {
      return this.spelunkerColorLight((float)var1.getRed() / 255.0F, (float)var1.getGreen() / 255.0F, (float)var1.getBlue() / 255.0F, var2 ? (float)var1.getAlpha() / 255.0F : this.opts.alpha, var3, var4, var5, var7, var8, var10, var11);
   }

   protected TextureDrawOptionsMods spelunkerColorLight(Color var1, GameLight var2, boolean var3, long var4, GameClock var6, long var7, float var9, int var10) {
      return this.spelunkerColorLight(var1, true, var2, var3, var4, var6, var7, var9, var10);
   }

   protected TextureDrawOptionsMods spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5, long var6, float var8, int var9) {
      if (var2) {
         long var10 = var5.getTime();
         var10 += var6 * var3;
         float var12 = (float)Math.floorMod(var10, var6) / (float)var6;
         return this.colorLight(Color.getHSBColor(var12, var8, 1.0F), var1.minLevelCopy((float)var9));
      } else {
         return this.light(var1);
      }
   }

   protected TextureDrawOptionsMods spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5) {
      return this.spelunkerLight(var1, var2, var3, var5, 2500L, 0.2F, 100);
   }

   protected TextureDrawOptionsMods advColor(float[] var1) {
      if (var1 != null && var1.length != 16) {
         throw new IllegalArgumentException("Colors must have 16 indexes");
      } else {
         this.opts.advCol = var1;
         return this;
      }
   }

   protected TextureDrawOptionsMods translatePos(int var1, int var2) {
      this.opts.translateX = var1;
      this.opts.translateY = var2;
      return this;
   }

   protected TextureDrawOptionsMods mirrorX() {
      float var1 = this.opts.spriteX1;
      this.opts.spriteX1 = this.opts.spriteX2;
      this.opts.spriteX2 = var1;
      float var2 = this.opts.spriteX3;
      this.opts.spriteX3 = this.opts.spriteX4;
      this.opts.spriteX4 = var2;
      return this;
   }

   protected TextureDrawOptionsMods mirrorY() {
      float var1 = this.opts.spriteY1;
      this.opts.spriteY1 = this.opts.spriteY4;
      this.opts.spriteY4 = var1;
      float var2 = this.opts.spriteY2;
      this.opts.spriteY2 = this.opts.spriteY3;
      this.opts.spriteY3 = var2;
      return this;
   }

   protected TextureDrawOptionsMods mirror(boolean var1, boolean var2) {
      if (var1) {
         this.mirrorX();
      }

      if (var2) {
         this.mirrorY();
      }

      return this;
   }

   protected TextureDrawOptionsMods depth(float var1) {
      this.opts.drawDepth = var1;
      return this;
   }

   protected TextureDrawOptionsMods blendFunc(int var1, int var2, int var3, int var4) {
      this.opts.setBlend = true;
      this.opts.blendSourceRGB = var1;
      this.opts.blendDestinationRGB = var2;
      this.opts.blendSourceAlpha = var3;
      this.opts.blendDestinationAlpha = var4;
      return this;
   }

   protected TextureDrawOptionsMods pos(int var1, int var2) {
      this.opts.pos(var1, var2);
      return this;
   }

   protected TextureDrawOptionsMods pos(int var1, int var2, boolean var3) {
      return this.pos(var1 - (var3 ? this.opts.translateX : 0), var2 - (var3 ? this.opts.translateY : 0));
   }

   protected TextureDrawOptionsMods posMiddle(int var1, int var2, boolean var3) {
      return this.pos(var1 - this.opts.width / 2, var2 - this.opts.height / 2, var3);
   }

   protected TextureDrawOptionsMods posMiddle(int var1, int var2) {
      return this.posMiddle(var1, var2, false);
   }

   protected int getWidth() {
      return this.opts.width;
   }

   protected int getHeight() {
      return this.opts.height;
   }
}
