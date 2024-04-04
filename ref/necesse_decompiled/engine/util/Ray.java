package necesse.engine.util;

import java.awt.geom.Line2D;

public class Ray<T> extends Line2D.Double {
   public final double dist = this.getP1().distance(this.getP2());
   public final T targetHit;

   public Ray(double var1, double var3, double var5, double var7, T var9) {
      super(var1, var3, var5, var7);
      this.targetHit = var9;
   }
}
