package necesse.engine;

import java.awt.Point;
import java.awt.geom.Point2D;

public class GameBezierPoint {
   public float startX;
   public float startY;
   public float targetX;
   public float targetY;

   public GameBezierPoint(float var1, float var2, float var3, float var4) {
      this.startX = var1;
      this.startY = var2;
      this.targetX = var3;
      this.targetY = var4;
   }

   public GameBezierPoint(Point2D.Float var1, Point2D.Float var2) {
      this(var1.x, var1.y, var2.x, var2.y);
   }

   public GameBezierPoint(Point var1, Point var2) {
      this((float)var1.x, (float)var1.y, (float)var2.x, (float)var2.y);
   }

   public float getPointXOnCurve(GameBezierPoint var1, float var2) {
      return (float)(Math.pow((double)(1.0F - var2), 3.0) * (double)this.startX + (double)(3.0F * var2) * Math.pow((double)(1.0F - var2), 2.0) * (double)this.targetX + 3.0 * Math.pow((double)var2, 2.0) * (double)(1.0F - var2) * (double)var1.targetX + Math.pow((double)var2, 3.0) * (double)var1.startX);
   }

   public float getPointYOnCurve(GameBezierPoint var1, float var2) {
      return (float)(Math.pow((double)(1.0F - var2), 3.0) * (double)this.startY + (double)(3.0F * var2) * Math.pow((double)(1.0F - var2), 2.0) * (double)this.targetY + 3.0 * Math.pow((double)var2, 2.0) * (double)(1.0F - var2) * (double)var1.targetY + Math.pow((double)var2, 3.0) * (double)var1.startY);
   }
}
