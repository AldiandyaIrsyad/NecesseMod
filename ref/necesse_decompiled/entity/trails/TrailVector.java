package necesse.entity.trails;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;

public class TrailVector {
   public final Point2D.Float pos;
   public final float dx;
   public final float dy;
   public final float thickness;
   public final float height;

   public TrailVector(TrailVector var1) {
      this.pos = new Point2D.Float(var1.pos.x, var1.pos.y);
      this.dx = var1.dx;
      this.dy = var1.dy;
      this.thickness = var1.thickness;
      this.height = var1.height;
   }

   public TrailVector(Point2D.Float var1, float var2, float var3, float var4, float var5) {
      this.pos = var1;
      Point2D.Float var6 = GameMath.normalize(var2, var3);
      this.dx = var6.x;
      this.dy = var6.y;
      this.thickness = var4;
      this.height = var5;
   }

   public TrailVector(float var1, float var2, float var3, float var4, float var5, float var6) {
      this(new Point2D.Float(var1, var2), var3, var4, var5, var6);
   }

   public float getAngle() {
      return (float)Math.toDegrees(Math.atan2((double)this.dy, (double)this.dx));
   }

   public boolean isSame(TrailVector var1) {
      return this.pos.x == var1.pos.x && this.pos.y == var1.pos.y && this.dx == var1.dx && this.dy == var1.dy && this.thickness == var1.thickness;
   }
}
