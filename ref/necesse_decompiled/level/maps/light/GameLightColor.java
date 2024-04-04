package necesse.level.maps.light;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.util.GameMath;
import org.lwjgl.opengl.GL11;

public class GameLightColor extends GameLight {
   protected byte red;
   protected byte green;
   protected byte blue;

   private GameLightColor(byte var1, byte var2, byte var3, float var4) {
      super(var4);
      this.red = var1;
      this.green = var2;
      this.blue = var3;
      this.level = var4;
   }

   protected GameLightColor(float var1) {
      super(var1);
      this.red = -1;
      this.green = -1;
      this.blue = -1;
   }

   protected GameLightColor(float var1, float var2, float var3) {
      super(var3);
      float var4 = var2 * (1.0F - Math.abs(var1 / 60.0F % 2.0F - 1.0F));
      float var5 = 1.0F - var2;
      float var6;
      float var7;
      float var8;
      if (var1 < 60.0F) {
         var6 = var2;
         var7 = var4;
         var8 = 0.0F;
      } else if (var1 < 120.0F) {
         var6 = var4;
         var7 = var2;
         var8 = 0.0F;
      } else if (var1 < 180.0F) {
         var6 = 0.0F;
         var7 = var2;
         var8 = var4;
      } else if (var1 < 240.0F) {
         var6 = 0.0F;
         var7 = var4;
         var8 = var2;
      } else if (var1 < 300.0F) {
         var6 = var4;
         var7 = 0.0F;
         var8 = var2;
      } else {
         var6 = var2;
         var7 = 0.0F;
         var8 = var4;
      }

      this.red = (byte)((int)((var6 + var5) * 255.0F));
      this.green = (byte)((int)((var7 + var5) * 255.0F));
      this.blue = (byte)((int)((var8 + var5) * 255.0F));
   }

   protected static GameLightColor fromColor(Color var0, float var1) {
      return new GameLightColor((byte)var0.getRed(), (byte)var0.getGreen(), (byte)var0.getBlue(), var1);
   }

   protected static GameLightColor fromColor(Color var0, float var1, float var2) {
      float var3 = Math.abs(var1 - 1.0F) * 255.0F;
      byte var4 = (byte)((int)Math.min(255.0F, var3 + (float)var0.getRed() * var1));
      byte var5 = (byte)((int)Math.min(255.0F, var3 + (float)var0.getGreen() * var1));
      byte var6 = (byte)((int)Math.min(255.0F, var3 + (float)var0.getBlue() * var1));
      return new GameLightColor(var4, var5, var6, var2);
   }

   public GameLightColor copy() {
      return new GameLightColor(this.red, this.green, this.blue, this.level);
   }

   public GameLight minLevelCopy(float var1) {
      GameLightColor var2 = new GameLightColor(var1);
      var2.combine(this, 0.5F);
      return var2;
   }

   public boolean combine(GameLight var1, float var2) {
      if (!(var1 instanceof GameLightColor)) {
         return super.combine(var1, var2);
      } else {
         GameLightColor var3 = (GameLightColor)var1;
         if ((this.red != var3.red || this.green != var3.green || this.blue != var3.blue) && var1.level > 0.0F) {
            float var4 = this.getFloatLevel();
            float var5 = var1.getFloatLevel();
            float var6 = Math.min(1.0F, var4 / var5 * var2 * 0.5F);
            int var8 = this.unsigned(this.red);
            int var9 = this.unsigned(var3.red);
            byte var10 = (byte)Math.min(255, var9 + (int)((float)(var8 - var9) * var6));
            boolean var7 = var10 != this.red;
            this.red = var10;
            var8 = this.unsigned(this.green);
            var9 = this.unsigned(var3.green);
            var10 = (byte)Math.min(255, var9 + (int)((float)(var8 - var9) * var6));
            var7 = var7 || var10 != this.green;
            this.green = var10;
            var8 = this.unsigned(this.blue);
            var9 = this.unsigned(var3.blue);
            var10 = (byte)Math.min(255, var9 + (int)((float)(var8 - var9) * var6));
            var7 = var7 || var10 != this.blue;
            this.blue = var10;
            float var11 = Math.max(var1.level, this.level);
            var7 = var7 || var11 != this.level;
            this.level = var11;
            return var7;
         } else {
            return super.combine(var1, var2);
         }
      }
   }

   public int getRed() {
      return this.unsigned(this.red);
   }

   public int getGreen() {
      return this.unsigned(this.green);
   }

   public int getBlue() {
      return this.unsigned(this.blue);
   }

   public float getFloatRed() {
      return (float)this.getRed() / 255.0F;
   }

   public float getFloatGreen() {
      return (float)this.getGreen() / 255.0F;
   }

   public float getFloatBlue() {
      return (float)this.getBlue() / 255.0F;
   }

   public int getColorHash() {
      return (this.getRed() & 255) << 16 | (this.getGreen() & 255) << 8 | this.getBlue() & 255;
   }

   private int unsigned(byte var1) {
      return var1 & 255;
   }

   public String toString() {
      return "L[" + this.unsigned(this.red) + "," + this.unsigned(this.green) + "," + this.unsigned(this.blue) + "," + GameMath.toDecimals(this.getLevel(), 2) + "]";
   }

   public Runnable getGLColorSetter(float var1, float var2, float var3, float var4) {
      float var5 = this.getFloatLevel();
      float var6;
      float var7;
      float var8;
      if (Settings.lights == Settings.LightSetting.Color) {
         var6 = this.getFloatRed() * var5;
         var7 = this.getFloatGreen() * var5;
         var8 = this.getFloatBlue() * var5;
      } else {
         var6 = var5;
         var7 = var5;
         var8 = var5;
      }

      return () -> {
         GL11.glColor4f(var1 * var6, var2 * var7, var3 * var8, var4);
      };
   }

   public float[] getAdvColor() {
      float var1 = this.getFloatLevel();
      float var2;
      float var3;
      float var4;
      if (Settings.lights == Settings.LightSetting.Color) {
         var2 = this.getFloatRed() * var1;
         var3 = this.getFloatGreen() * var1;
         var4 = this.getFloatBlue() * var1;
      } else {
         var2 = var1;
         var3 = var1;
         var4 = var1;
      }

      return new float[]{var2, var3, var4, 1.0F, var2, var3, var4, 1.0F, var2, var3, var4, 1.0F, var2, var3, var4, 1.0F};
   }

   public float[] getAdvColor(GameLight var1, GameLight var2, GameLight var3) {
      float var4 = this.getFloatLevel();
      float var5 = var1.getFloatLevel();
      float var6 = var2.getFloatLevel();
      float var7 = var3.getFloatLevel();
      return Settings.lights == Settings.LightSetting.Color ? new float[]{this.getFloatRed() * var4, this.getFloatGreen() * var4, this.getFloatBlue() * var4, 1.0F, var1.getFloatRed() * var5, var1.getFloatGreen() * var5, var1.getFloatBlue() * var5, 1.0F, var2.getFloatRed() * var6, var2.getFloatGreen() * var6, var2.getFloatBlue() * var6, 1.0F, var3.getFloatRed() * var7, var3.getFloatGreen() * var7, var3.getFloatBlue() * var7, 1.0F} : new float[]{var4, var4, var4, 1.0F, var5, var5, var5, 1.0F, var6, var6, var6, 1.0F, var7, var7, var7, 1.0F};
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GameLight copy() {
      return this.copy();
   }
}
