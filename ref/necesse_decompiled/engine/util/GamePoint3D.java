package necesse.engine.util;

import java.awt.geom.Point2D;

public class GamePoint3D {
   public float x;
   public float y;
   public float height;

   public GamePoint3D(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.height = var3;
   }

   public GamePoint3D(Point2D.Float var1, float var2) {
      this(var1.x, var1.y, var2);
   }

   public float distF(float var1, float var2, float var3) {
      return (float)this.dist(var1, var2, var3);
   }

   public float distF(GamePoint3D var1) {
      return (float)this.dist(var1);
   }

   public double dist(float var1, float var2, float var3) {
      var1 -= this.x;
      var2 -= this.y;
      var3 -= this.height;
      return Math.sqrt((double)(var1 * var1 + var2 * var2 + var3 * var3));
   }

   public double dist(GamePoint3D var1) {
      return this.dist(var1.x, var1.y, var1.height);
   }

   public GamePoint3D normalize() {
      float var1 = this.distF(0.0F, 0.0F, 0.0F);
      float var2 = var1 == 0.0F ? 0.0F : this.x / var1;
      float var3 = var1 == 0.0F ? 0.0F : this.y / var1;
      float var4 = var1 == 0.0F ? 0.0F : this.height / var1;
      return new GamePoint3D(var2, var3, var4);
   }

   public GamePoint3D normalizeTo(float var1, float var2, float var3) {
      return (new GamePoint3D(var1 - this.x, var2 - this.y, var3 - this.height)).normalize();
   }

   public GamePoint3D dirFromLength(float var1, float var2, float var3, float var4) {
      GamePoint3D var5 = this.normalizeTo(var1, var2, var3);
      return new GamePoint3D(this.x + var5.x * var4, this.y + var5.y * var4, this.height + var5.height * var4);
   }
}
