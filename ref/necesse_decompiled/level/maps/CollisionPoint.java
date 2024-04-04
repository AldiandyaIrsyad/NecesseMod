package necesse.level.maps;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;

public class CollisionPoint<T> {
   public final T target;
   public final Rectangle rectangle;
   public final Line2D line;
   public final boolean checkInsideRect;
   private boolean calculatedPoint = false;
   private IntersectionPoint<T> point;

   public CollisionPoint(T var1, Rectangle var2, Line2D var3, boolean var4) {
      this.target = var1;
      this.rectangle = var2;
      this.line = var3;
      this.checkInsideRect = var4;
   }

   public IntersectionPoint<T> getPoint() {
      if (!this.calculatedPoint) {
         this.point = GameMath.getIntersectionPoint(this.target, this.line, this.rectangle, this.checkInsideRect);
         this.calculatedPoint = true;
      }

      return this.point;
   }

   public static <T extends Rectangle> IntersectionPoint<T> getClosestCollision(List<T> var0, Line2D var1, boolean var2) {
      return var0.isEmpty() ? null : getClosestCollision(var0.stream(), var1, var2);
   }

   public static <T extends Rectangle> IntersectionPoint<T> getClosestCollision(Stream<T> var0, Line2D var1, boolean var2) {
      return (IntersectionPoint)var0.map((var2x) -> {
         return new CollisionPoint(var2x, var2x, var1, var2);
      }).sorted(Comparator.comparing((var1x) -> {
         return var1.getP1().distance(var1x.rectangle.getCenterX(), var1x.rectangle.getCenterY());
      })).filter((var0x) -> {
         return var0x.getPoint() != null;
      }).map(CollisionPoint::getPoint).findFirst().orElse((Object)null);
   }
}
