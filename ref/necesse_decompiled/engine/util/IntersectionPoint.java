package necesse.engine.util;

import java.awt.geom.Point2D;

public class IntersectionPoint<T> extends Point2D.Double {
   public final T target;
   public final Dir dir;

   public IntersectionPoint(double var1, double var3, T var5, Dir var6) {
      super(var1, var3);
      this.target = var5;
      this.dir = var6;
   }

   public static enum Dir {
      UP,
      RIGHT,
      DOWN,
      LEFT;

      private Dir() {
      }

      // $FF: synthetic method
      private static Dir[] $values() {
         return new Dir[]{UP, RIGHT, DOWN, LEFT};
      }
   }
}
