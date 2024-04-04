package necesse.engine.util;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;

public class FloatDimension extends Dimension2D {
   public float width;
   public float height;

   public FloatDimension() {
      this(0.0F, 0.0F);
   }

   public FloatDimension(FloatDimension var1) {
      this(var1.width, var1.height);
   }

   public FloatDimension(float var1, float var2) {
      this.width = var1;
      this.height = var2;
   }

   public double getWidth() {
      return (double)this.width;
   }

   public double getHeight() {
      return (double)this.height;
   }

   public Dimension toInt() {
      return new Dimension(GameMath.ceil((double)this.width), GameMath.ceil((double)this.height));
   }

   public void setSize(double var1, double var3) {
      this.setSize((float)var1, (float)var3);
   }

   public void setSize(float var1, float var2) {
      this.width = var1;
      this.height = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof FloatDimension)) {
         return false;
      } else {
         FloatDimension var2 = (FloatDimension)var1;
         return this.width == var2.width && this.height == var2.height;
      }
   }

   public int hashCode() {
      float var1 = this.width + this.height;
      return (int)(var1 * (var1 + 1.0F) / 2.0F + this.width);
   }

   public String toString() {
      return this.getClass().getName() + "[width=" + this.width + ",height=" + this.height + "]";
   }
}
