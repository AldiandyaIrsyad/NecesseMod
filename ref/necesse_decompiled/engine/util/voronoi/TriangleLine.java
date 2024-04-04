package necesse.engine.util.voronoi;

import java.awt.geom.Point2D;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;

public class TriangleLine {
   public final Point2D.Float p1;
   public final Point2D.Float p2;

   public TriangleLine(Point2D.Float var1, Point2D.Float var2) {
      if (TriangleData.compare(var1, var2) == -1) {
         this.p1 = var1;
         this.p2 = var2;
      } else {
         this.p2 = var1;
         this.p1 = var2;
      }

   }

   public DrawOptions getDrawOptions(GameCamera var1) {
      DrawOptionsList var2 = new DrawOptionsList(3);
      int var3 = var1.getDrawX(this.p1.x);
      int var4 = var1.getDrawY(this.p1.y);
      int var5 = var1.getDrawX(this.p2.x);
      int var6 = var1.getDrawY(this.p2.y);
      var2.add(() -> {
         Screen.drawLineRGBA(var3, var4, var5, var6, 0.0F, 1.0F, 1.0F, 1.0F);
      });
      return var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TriangleLine)) {
         return false;
      } else {
         TriangleLine var2 = (TriangleLine)var1;
         return this.p1.equals(var2.p1) && this.p2.equals(var2.p2);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.p1, this.p2});
   }

   public String toString() {
      return "L[" + this.p1.x + "x" + this.p1.y + ", " + this.p2.x + "x" + this.p2.y + "]";
   }
}
