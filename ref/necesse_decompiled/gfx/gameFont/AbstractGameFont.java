package necesse.gfx.gameFont;

public abstract class AbstractGameFont<T extends FontBasicOptions> {
   public AbstractGameFont() {
   }

   public abstract float drawChar(float var1, float var2, char var3, T var4);

   public float drawString(float var1, float var2, String var3, T var4) {
      int var5 = 0;

      for(int var6 = 0; var6 < var3.length(); ++var6) {
         char var7 = var3.charAt(var6);
         var5 = (int)((float)var5 + this.drawChar(var1 + (float)var5, var2, var7, var4));
      }

      return (float)var5;
   }

   public abstract float getWidth(char var1, T var2);

   public float getWidth(String var1, T var2) {
      float var3 = 0.0F;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var3 += this.getWidth(var1.charAt(var4), var2);
      }

      return var3;
   }

   public abstract float getHeight(char var1, T var2);

   public float getHeight(String var1, T var2) {
      float var3 = 0.0F;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var3 = Math.max(var3, this.getHeight(var1.charAt(var4), var2));
      }

      return var3;
   }

   public abstract int getWidthCeil(char var1, T var2);

   public int getWidthCeil(String var1, T var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var3 += this.getWidthCeil(var1.charAt(var4), var2);
      }

      return var3;
   }

   public abstract int getFontHeight();

   public abstract int getHeightCeil(char var1, T var2);

   public int getHeightCeil(String var1, T var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var3 = Math.max(var3, this.getHeightCeil(var1.charAt(var4), var2));
      }

      return var3;
   }

   public abstract boolean canDraw(char var1);

   public abstract void deleteTextures();
}
