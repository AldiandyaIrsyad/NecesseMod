package necesse.gfx.gameTexture;

import java.awt.Color;
import necesse.engine.util.GameMath;

@FunctionalInterface
public interface MergeFunction {
   MergeFunction GLBLEND = (var0, var1) -> {
      int var2 = (int)((float)var0.getRed() / 255.0F * ((float)var1.getRed() / 255.0F) * 255.0F);
      int var3 = (int)((float)var0.getGreen() / 255.0F * ((float)var1.getGreen() / 255.0F) * 255.0F);
      int var4 = (int)((float)var0.getBlue() / 255.0F * ((float)var1.getBlue() / 255.0F) * 255.0F);
      return new Color(var2, var3, var4, var0.getAlpha());
   };
   MergeFunction NORMAL = (var0, var1) -> {
      float var2 = (float)var0.getRed() / 255.0F;
      float var3 = (float)var0.getGreen() / 255.0F;
      float var4 = (float)var0.getBlue() / 255.0F;
      float var5 = (float)var0.getAlpha() / 255.0F;
      float var6 = (float)var1.getRed() / 255.0F;
      float var7 = (float)var1.getGreen() / 255.0F;
      float var8 = (float)var1.getBlue() / 255.0F;
      float var9 = (float)var1.getAlpha() / 255.0F;
      float var10 = var5 * (1.0F - var9);
      float var11 = var9 + var10;
      int var12 = (int)((var6 * var9 + var2 * var10) / var11 * 255.0F);
      int var13 = (int)((var7 * var9 + var3 * var10) / var11 * 255.0F);
      int var14 = (int)((var8 * var9 + var4 * var10) / var11 * 255.0F);
      int var15 = (int)(var11 * 255.0F);
      return new Color(var12, var13, var14, var15);
   };
   MergeFunction ALPHA_OVERRIDE = (var0, var1) -> {
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), var1.getAlpha());
   };
   MergeFunction MULTIPLY = (var0, var1) -> {
      float var2 = (float)var0.getRed() / 255.0F;
      float var3 = (float)var0.getGreen() / 255.0F;
      float var4 = (float)var0.getBlue() / 255.0F;
      float var5 = (float)var0.getAlpha() / 255.0F;
      float var6 = (float)var1.getRed() / 255.0F;
      float var7 = (float)var1.getGreen() / 255.0F;
      float var8 = (float)var1.getBlue() / 255.0F;
      float var9 = (float)var1.getAlpha() / 255.0F;
      return new Color(var2 * var6, var3 * var7, var4 * var8, var5 * var9);
   };
   MergeFunction ALPHA_MASK = (var0, var1) -> {
      float var2 = (float)GameMath.max(var1.getRed(), var1.getGreen(), var1.getBlue()) / 255.0F;
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), (int)((float)var0.getAlpha() * var2));
   };

   Color merge(Color var1, Color var2);
}
