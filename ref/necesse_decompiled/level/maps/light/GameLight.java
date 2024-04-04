package necesse.level.maps.light;

import necesse.engine.util.GameMath;
import org.lwjgl.opengl.GL11;

public class GameLight {
   protected float level;
   private static final int whiteHash = 16777215;

   public GameLight(float var1) {
      this.level = var1;
   }

   public GameLight copy() {
      return new GameLight(this.level);
   }

   public GameLight minLevelCopy(float var1) {
      if (this.level < var1) {
         GameLight var2 = this.copy();
         var2.level = var1;
         return var2;
      } else {
         return this;
      }
   }

   public boolean combine(GameLight var1) {
      return this.combine(var1, 1.0F);
   }

   public boolean combine(GameLight var1, float var2) {
      if (this.level < var1.level) {
         this.level = var1.level;
         return true;
      } else {
         return false;
      }
   }

   public boolean setLevel(float var1) {
      if (this.level == var1) {
         return false;
      } else {
         this.level = var1;
         return true;
      }
   }

   public float getLevel() {
      return this.level;
   }

   public float getFloatLevel() {
      return Math.min(1.0F, this.getLevel() / 150.0F);
   }

   public float getFloatRed() {
      return 1.0F;
   }

   public float getFloatGreen() {
      return 1.0F;
   }

   public float getFloatBlue() {
      return 1.0F;
   }

   public boolean isSameColor(GameLight var1) {
      return this.getFloatRed() == var1.getFloatRed() && this.getFloatGreen() == var1.getFloatGreen() && this.getFloatBlue() == var1.getFloatBlue();
   }

   public int getColorHash() {
      return 16777215;
   }

   public String toString() {
      return "L[" + GameMath.toDecimals(this.getLevel(), 2) + "]";
   }

   public Runnable getGLColorSetter(float var1, float var2, float var3, float var4) {
      float var5 = this.getFloatLevel();
      return () -> {
         GL11.glColor4f(var1 * var5, var2 * var5, var3 * var5, var4);
      };
   }

   public float[] getAdvColor() {
      float var1 = this.getFloatLevel();
      return new float[]{var1, var1, var1, 1.0F, var1, var1, var1, 1.0F, var1, var1, var1, 1.0F, var1, var1, var1, 1.0F};
   }

   public float[] getAdvColor(GameLight var1, GameLight var2, GameLight var3) {
      float var4 = this.getFloatLevel();
      float var5 = var1.getFloatLevel();
      float var6 = var2.getFloatLevel();
      float var7 = var3.getFloatLevel();
      return new float[]{var4, var4, var4, 1.0F, var5, var5, var5, 1.0F, var6, var6, var6, 1.0F, var7, var7, var7, 1.0F};
   }
}
