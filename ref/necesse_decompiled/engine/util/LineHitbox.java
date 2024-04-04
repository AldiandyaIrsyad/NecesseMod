package necesse.engine.util;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import necesse.gfx.GameResources;
import org.lwjgl.opengl.GL11;

public class LineHitbox extends Polygon {
   private final float width;
   private boolean circular = false;
   private float circleX;
   private float circleY;

   public LineHitbox(Line2D var1, float var2) {
      this.width = var2;
      this.calculatePolygon(var1);
   }

   public LineHitbox(Line2D var1, float var2, float var3, float var4) {
      this.width = var4;
      this.calculatePolygon(var1);
      this.circular = true;
      this.circleX = var2;
      this.circleY = var3;
   }

   public LineHitbox(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.width = var6;
      Point2D.Float var7 = GameMath.normalize(var3, var4);
      this.calculatePolygon(var1, var2, var7.x, var7.y, var5);
   }

   public LineHitbox(float var1, float var2, float var3, float var4, float var5) {
      this.width = var5;
      Point2D.Float var6 = new Point2D.Float(var3 - var1, var4 - var2);
      float var7 = (float)var6.distance(0.0, 0.0);
      float var8 = var7 == 0.0F ? 0.0F : var6.x / var7;
      float var9 = var7 == 0.0F ? 0.0F : var6.y / var7;
      this.calculatePolygon(var1, var2, var8, var9, var7);
   }

   public static LineHitbox fromAngled(float var0, float var1, float var2, float var3, float var4) {
      var2 = GameMath.fixAngle(var2);
      float var5 = (float)Math.cos(Math.toRadians((double)(var2 - 90.0F)));
      float var6 = (float)Math.sin(Math.toRadians((double)(var2 - 90.0F)));
      return new LineHitbox(var0, var1, var5, var6, var3, var4);
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      boolean var9 = super.intersects(var1, var3, var5, var7);
      if (var9 && this.circular) {
         double var10 = var1 + var5 / 2.0;
         double var12 = var3 + var7 / 2.0;
         Point2D.Float var14 = GameMath.normalize((float)var10 - this.circleX, (float)var12 - this.circleY);
         Line2D.Float var15 = new Line2D.Float(this.circleX, this.circleY, this.circleX + var14.x * this.width / 2.0F, this.circleY + var14.y * this.width / 2.0F);
         return var15.intersects(var1, var3, var5, var7);
      } else {
         return var9;
      }
   }

   protected void calculatePolygon(float var1, float var2, float var3, float var4, float var5) {
      this.calculatePolygon(new Line2D.Float(var1, var2, var1 + var3 * var5, var2 + var4 * var5));
   }

   protected void calculatePolygon(Line2D var1) {
      Point2D.Float var2 = new Point2D.Float((float)var1.getX1(), (float)var1.getY1());
      Point2D.Float var3 = new Point2D.Float((float)var1.getX2(), (float)var1.getY2());
      Point2D.Float var4 = GameMath.normalize(var2.x - var3.x, var2.y - var3.y);
      Point2D.Float var5 = GameMath.getPerpendicularPoint(var2, -this.width / 2.0F, var4);
      Point2D.Float var6 = GameMath.getPerpendicularPoint(var2, this.width / 2.0F, var4);
      Point2D.Float var7 = GameMath.getPerpendicularPoint(var3, -this.width / 2.0F, var4);
      Point2D.Float var8 = GameMath.getPerpendicularPoint(var3, this.width / 2.0F, var4);
      this.xpoints = new int[]{(int)var5.x, (int)var6.x, (int)var8.x, (int)var7.x};
      this.ypoints = new int[]{(int)var5.y, (int)var6.y, (int)var8.y, (int)var7.y};
      this.npoints = 4;
   }

   public void draw(float var1, float var2, float var3, float var4) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glBegin(5);
      GL11.glColor4f(var1, var2, var3, var4);
      GL11.glVertex2f((float)this.xpoints[1], (float)this.ypoints[1]);
      GL11.glVertex2f((float)this.xpoints[0], (float)this.ypoints[0]);
      GL11.glVertex2f((float)this.xpoints[2], (float)this.ypoints[2]);
      GL11.glVertex2f((float)this.xpoints[3], (float)this.ypoints[3]);
      GL11.glEnd();
   }
}
