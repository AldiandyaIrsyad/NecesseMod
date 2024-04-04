package necesse.engine.util.voronoi;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;

public class TriangleData {
   public final Point2D.Float p1;
   public final Point2D.Float p2;
   public final Point2D.Float p3;
   public final Point2D.Float average;
   boolean complete;
   public static Comparator<Point2D.Float> comparator = (var0, var1) -> {
      return Float.compare(var0.x, var1.x);
   };

   public TriangleData(Point2D.Float var1, Point2D.Float var2, Point2D.Float var3) {
      Point2D.Float[] var4 = new Point2D.Float[]{var1, var2, var3};
      Arrays.sort(var4, comparator);
      this.p1 = var4[0];
      this.p2 = var4[1];
      this.p3 = var4[2];
      this.average = new Point2D.Float((var1.x + var2.x + var3.x) / 3.0F, (var1.y + var2.y + var3.y) / 3.0F);
   }

   public DrawOptions getDrawOptions(GameCamera var1) {
      DrawOptionsList var2 = new DrawOptionsList(3);
      int var3 = var1.getDrawX(this.p1.x);
      int var4 = var1.getDrawY(this.p1.y);
      int var5 = var1.getDrawX(this.p2.x);
      int var6 = var1.getDrawY(this.p2.y);
      int var7 = var1.getDrawX(this.p3.x);
      int var8 = var1.getDrawY(this.p3.y);
      var2.add(() -> {
         Screen.drawLineRGBA(var3, var4, var5, var6, 0.0F, 0.0F, 1.0F, 1.0F);
      });
      var2.add(() -> {
         Screen.drawLineRGBA(var5, var6, var7, var8, 0.0F, 0.0F, 1.0F, 1.0F);
      });
      var2.add(() -> {
         Screen.drawLineRGBA(var7, var8, var3, var4, 0.0F, 0.0F, 1.0F, 1.0F);
      });
      return var2;
   }

   public String toString() {
      return "L[" + this.p1.x + "x" + this.p1.y + ", " + this.p2.x + "x" + this.p2.y + ", " + this.p3.x + "x" + this.p3.y + "]";
   }

   public static int compare(Point2D.Float var0, Point2D.Float var1) {
      return Objects.compare(var0, var1, comparator);
   }

   static {
      comparator = comparator.thenComparing((var0, var1) -> {
         return Float.compare(var0.y, var1.y);
      });
   }
}
