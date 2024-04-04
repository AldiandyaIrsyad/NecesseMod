package necesse.gfx.gameFont;

import necesse.gfx.gameTexture.GameTexture;

public class CustomGameFont extends GameFont {
   public static final String[] fontLayout = new String[]{"abcdefghijklmnopqrstuvwyxz ", "ABCDEFGHIJKLMNOPQRSTUVWYXZ", "1234567890+-=,;.:-_/\\()[]{}", "!\"'#%&*|^<>$?`\u00b4~\u00a1\u00bf\u00a3\u20ac@\u00df", "\u00e0\u00e1\u00e2\u00e3\u00e4\u00e7\u00e8\u00e9\u00ea\u00eb\u00ec\u00ed\u00ee\u00ef\u00f1\u0144\u0148\u00f2\u00f3\u00f4\u00f5\u00f6\u00f9\u00fa\u00fb\u00fc", "\u00c0\u00c1\u00c2\u00c3\u00c4\u00c7\u00c8\u00c9\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf\u00d1\u0143\u0147\u00d2\u00d3\u00d4\u00d5\u00d6\u00d9\u00da\u00db\u00dc", "\u00e6\u00f8\u00e5", "\u00c6\u00d8\u00c5"};
   private final CharArray charArray;
   private int fontHeight;
   private float fontRatio;

   public static boolean fontTextureContains(char var0) {
      String[] var1 = fontLayout;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (var4.indexOf(var0) != -1) {
            return true;
         }
      }

      return false;
   }

   public CustomGameFont(GameTexture var1, int var2, int var3) {
      this.charArray = new CharArray(var1, var2, var3);
      this.fontHeight = var3;
      this.fontRatio = (float)var2 / (float)var3;
   }

   public float drawChar(float var1, float var2, char var3, FontBasicOptions var4) {
      int var5 = (int)((float)var4.getSize() * this.fontRatio);
      GameFontGlyphPositionTexture var6 = null;
      if (var3 < this.charArray.chars.length) {
         var6 = this.charArray.chars[var3];
      }

      if (var6 == null) {
         var6 = this.charArray.chars[63];
      }

      var6.texture.bind();
      var4.applyGLColor();
      var6.draw(var1, var2, (float)var5, (float)var4.getSize());
      return (float)var5;
   }

   public float getWidth(char var1, FontBasicOptions var2) {
      return (float)var2.getSize() * this.fontRatio;
   }

   public float getHeight(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontHeight;
      return var3 * (float)this.fontHeight;
   }

   public int getWidthCeil(char var1, FontBasicOptions var2) {
      return (int)((float)var2.getSize() * this.fontRatio);
   }

   public int getFontHeight() {
      return this.fontHeight;
   }

   public int getHeightCeil(char var1, FontBasicOptions var2) {
      float var3 = (float)var2.getSize() / (float)this.fontHeight;
      return (int)(var3 * (float)this.fontHeight);
   }

   public boolean canDraw(char var1) {
      return this.charArray.canDraw(var1);
   }

   public void deleteTextures() {
      this.charArray.texture.delete();
   }

   public GameFont updateFont(String var1) {
      return this;
   }

   public CharArray getCharArray() {
      return this.charArray;
   }

   public static class CharArray {
      public final GameFontGlyphPositionTexture[] chars;
      public final GameTexture texture;

      public CharArray(GameTexture var1, int var2, int var3) {
         this.texture = var1;
         var1.setBlendQuality(GameTexture.BlendQuality.NEAREST);
         int var4 = 0;
         String[] var5 = CustomGameFont.fontLayout;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];

            for(int var9 = 0; var9 < var8.length(); ++var9) {
               var4 = Math.max(var4, var8.charAt(var9) + 1);
            }
         }

         this.chars = new GameFontGlyphPositionTexture[var4];

         for(int var10 = 0; var10 < CustomGameFont.fontLayout.length; ++var10) {
            for(var6 = 0; var6 < CustomGameFont.fontLayout[var10].length(); ++var6) {
               char var11 = CustomGameFont.fontLayout[var10].charAt(var6);
               this.chars[var11] = new GameFontGlyphPositionTexture(var1, var6 * var2, var10 * var3, var2, var3);
            }
         }

      }

      public boolean canDraw(char var1) {
         return var1 < this.chars.length && this.chars[var1] != null;
      }
   }
}
