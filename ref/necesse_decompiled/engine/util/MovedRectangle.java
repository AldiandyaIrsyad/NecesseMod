package necesse.engine.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import necesse.entity.mobs.Mob;

public class MovedRectangle extends Polygon {
   public MovedRectangle(Mob var1, int var2, int var3) {
      this(var1.getCollision(), var2 - var1.getX(), var3 - var1.getY());
   }

   public MovedRectangle(Mob var1, int var2, int var3, int var4, int var5) {
      this(var1.getCollision(var2, var3), var4 - var2, var5 - var3);
   }

   public MovedRectangle(Rectangle var1, int var2, int var3, int var4, int var5) {
      this(var1, var4 - var2, var5 - var3);
   }

   public MovedRectangle(Rectangle var1, int var2, int var3) {
      Rectangle var4 = new Rectangle(var1.x + var2, var1.y + var3, var1.width, var1.height);
      if (var2 > 0) {
         if (var3 > 0) {
            this.build(botLeft(var1), topLeft(var1), topRight(var1), topRight(var4), botRight(var4), botLeft(var4));
         } else if (var3 < 0) {
            this.build(botRight(var1), botLeft(var1), topLeft(var1), topLeft(var4), topRight(var4), botRight(var4));
         } else {
            this.build(botLeft(var1), topLeft(var1), topRight(var4), botRight(var4));
         }
      } else if (var2 < 0) {
         if (var3 > 0) {
            this.build(topLeft(var1), topRight(var1), botRight(var1), botRight(var4), botLeft(var4), topLeft(var4));
         } else if (var3 < 0) {
            this.build(topRight(var1), botRight(var1), botLeft(var1), botLeft(var4), topLeft(var4), topRight(var4));
         } else {
            this.build(topRight(var1), botRight(var1), botLeft(var4), topLeft(var4));
         }
      } else if (var3 > 0) {
         this.build(topLeft(var1), topRight(var1), botRight(var4), botLeft(var4));
      } else if (var3 < 0) {
         this.build(botLeft(var1), botRight(var1), topRight(var4), topLeft(var4));
      } else {
         this.build(topLeft(var1), topRight(var1), botRight(var1), botLeft(var1));
      }

   }

   private void build(Point... var1) {
      this.xpoints = new int[var1.length];
      this.ypoints = new int[var1.length];
      this.npoints = var1.length;

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.xpoints[var2] = var1[var2].x;
         this.ypoints[var2] = var1[var2].y;
      }

   }

   private static Point topLeft(Rectangle var0) {
      return new Point(var0.x, var0.y);
   }

   private static Point topRight(Rectangle var0) {
      return new Point(var0.x + var0.width, var0.y);
   }

   private static Point botLeft(Rectangle var0) {
      return new Point(var0.x, var0.y + var0.height);
   }

   private static Point botRight(Rectangle var0) {
      return new Point(var0.x + var0.width, var0.y + var0.height);
   }
}
