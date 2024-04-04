package necesse.gfx.gameFont;

import java.awt.Color;
import java.util.function.BooleanSupplier;
import necesse.engine.Settings;
import org.lwjgl.opengl.GL11;

public class FontBasicOptions {
   public static final float[] defaultColorArray;
   public static final float[] defaultStrokeColorArray;
   private float[] defaultColor;
   private float[] defaultStrokeColor;
   private BooleanSupplier pixelFont;
   private int size;
   private float[] color;
   private float[] strokeColor;

   public static float[] getColorArray(Color var0) {
      return new float[]{(float)var0.getRed() / 255.0F, (float)var0.getGreen() / 255.0F, (float)var0.getBlue() / 255.0F, (float)var0.getAlpha() / 255.0F};
   }

   public FontBasicOptions(int var1) {
      this.defaultColor = new float[defaultColorArray.length];
      this.defaultStrokeColor = new float[defaultStrokeColorArray.length];
      this.pixelFont = () -> {
         return Settings.pixelFont;
      };
      System.arraycopy(defaultColorArray, 0, this.defaultColor, 0, this.defaultColor.length);
      System.arraycopy(defaultStrokeColorArray, 0, this.defaultStrokeColor, 0, this.defaultStrokeColor.length);
      this.size = var1;
      this.color = null;
      this.strokeColor = null;
   }

   public FontBasicOptions(FontBasicOptions var1) {
      this.defaultColor = new float[defaultColorArray.length];
      this.defaultStrokeColor = new float[defaultStrokeColorArray.length];
      this.pixelFont = () -> {
         return Settings.pixelFont;
      };
      this.defaultColor = copyArray(var1.defaultColor);
      this.defaultStrokeColor = copyArray(var1.defaultStrokeColor);
      this.size = var1.size;
      this.color = copyArray(var1.color);
      this.strokeColor = copyArray(var1.strokeColor);
   }

   public FontBasicOptions copy() {
      return new FontBasicOptions(this);
   }

   protected static float[] copyArray(float[] var0) {
      if (var0 == null) {
         return null;
      } else {
         float[] var1 = new float[var0.length];
         System.arraycopy(var0, 0, var1, 0, var1.length);
         return var1;
      }
   }

   public int getSize() {
      return this.size;
   }

   public FontBasicOptions size(int var1) {
      this.size = var1;
      return this;
   }

   public boolean isPixelFont() {
      return this.pixelFont.getAsBoolean();
   }

   public FontBasicOptions forcePixelFont() {
      this.pixelFont = () -> {
         return true;
      };
      return this;
   }

   public FontBasicOptions forceNonPixelFont() {
      this.pixelFont = () -> {
         return false;
      };
      return this;
   }

   public boolean hasColor() {
      return this.color != null;
   }

   public float[] getColor() {
      return this.color == null ? this.defaultColor : this.color;
   }

   public void applyGLColor() {
      float[] var1 = this.getColor();
      GL11.glColor4f(var1[0], var1[1], var1[2], var1[3]);
   }

   public float[] getStrokeColor() {
      return this.strokeColor == null ? this.defaultStrokeColor : this.strokeColor;
   }

   public void applyGLStrokeColor() {
      float[] var1 = this.getColor();
      float[] var2 = this.getStrokeColor();
      GL11.glColor4f(var2[0], var2[1], var2[2], var1[3]);
   }

   public FontBasicOptions color(float[] var1) {
      this.color = var1;
      return this;
   }

   public FontBasicOptions colorf(float var1, float var2, float var3, float var4) {
      return this.color(new float[]{var1, var2, var3, var4});
   }

   public FontBasicOptions colorf(float var1, float var2, float var3) {
      return this.color(new float[]{var1, var2, var3, 1.0F});
   }

   public FontBasicOptions color(Color var1) {
      return this.color(getColorArray(var1));
   }

   public FontBasicOptions color(int var1, int var2, int var3, int var4) {
      return this.color(new Color(var1, var2, var3, var4));
   }

   public FontBasicOptions color(int var1, int var2, int var3) {
      return this.color(new Color(var1, var2, var3));
   }

   public FontBasicOptions alphaf(float var1) {
      this.getColor()[3] = var1;
      return this;
   }

   public FontBasicOptions alpha(int var1) {
      return this.alphaf((float)var1 / 255.0F);
   }

   public float getAlpha() {
      return this.getColor()[3];
   }

   public FontBasicOptions defaultColor(float[] var1) {
      this.defaultColor = var1;
      return this;
   }

   public FontBasicOptions defaultColorf(float var1, float var2, float var3, float var4) {
      return this.defaultColor(new float[]{var1, var2, var3, var4});
   }

   public FontBasicOptions defaultColorf(float var1, float var2, float var3) {
      return this.defaultColor(new float[]{var1, var2, var3, 1.0F});
   }

   public FontBasicOptions defaultColor(Color var1) {
      return this.defaultColor(getColorArray(var1));
   }

   public FontBasicOptions defaultColor(int var1, int var2, int var3, int var4) {
      return this.defaultColor(new Color(var1, var2, var3, var4));
   }

   public FontBasicOptions defaultColor(int var1, int var2, int var3) {
      return this.defaultColor(new Color(var1, var2, var3));
   }

   public FontBasicOptions outline(float[] var1) {
      this.strokeColor = var1;
      return this;
   }

   public FontBasicOptions clearOutlineColor() {
      return this.outline((float[])null);
   }

   public FontBasicOptions outlinef(float var1, float var2, float var3, float var4) {
      return this.outline(new float[]{var1, var2, var3, var4});
   }

   public FontBasicOptions outlinef(float var1, float var2, float var3) {
      return this.outline(new float[]{var1, var2, var3, 1.0F});
   }

   public FontBasicOptions outline(Color var1) {
      return var1 == null ? this.outline((float[])null) : this.outline(getColorArray(var1));
   }

   public FontBasicOptions outline(int var1, int var2, int var3, int var4) {
      return this.outline(new Color(var1, var2, var3, var4));
   }

   public FontBasicOptions outline(int var1, int var2, int var3) {
      return this.outline(new Color(var1, var2, var3));
   }

   static {
      defaultColorArray = getColorArray(Color.WHITE);
      defaultStrokeColorArray = new float[]{0.0F, 0.0F, 0.0F, 1.0F};
   }
}
