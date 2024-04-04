package necesse.engine.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Comparator;
import java.util.Iterator;

public class ExpandingPolygon extends Polygon {
   private GameLinkedList<Point> points = new GameLinkedList();

   public ExpandingPolygon() {
      this.updatePolygon();
   }

   public ExpandingPolygon(Shape... var1) {
      Shape[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Shape var5 = var2[var4];
         this.addShape(var5);
      }

   }

   public ExpandingPolygon(Point... var1) {
      Point[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Point var5 = var2[var4];
         this.addPoint(var5);
      }

   }

   public void addShape(Shape var1) {
      float[] var2 = new float[6];

      for(PathIterator var3 = var1.getPathIterator((AffineTransform)null); !var3.isDone(); var3.next()) {
         int var4 = var3.currentSegment(var2);
         if (var4 == 4) {
            break;
         }

         if (var4 == 0 || var4 == 1) {
            this.addPoint(new Point((int)var2[0], (int)var2[1]));
         }
      }

   }

   public void addPoint(Point var1) {
      if (this.points.size() < 2) {
         this.points.addLast(var1);
      } else if (this.points.size() == 2) {
         Point var2 = (Point)this.points.getFirst();
         Point var3 = (Point)this.points.getLast();
         double var4 = (double)((var3.x - var2.x) * (var1.y - var2.y) - (var3.y - var2.y) * (var1.x - var2.x));
         if (var4 < 0.0) {
            this.points.getFirstElement().insertAfter(var1);
         } else {
            this.points.addLast(var1);
         }
      } else {
         GameLinkedList.Element var13 = this.points.getLastElement();
         double var14 = ((Point)var13.object).distance(var1);
         GameLinkedList var5 = new GameLinkedList();

         GameLinkedList.Element var7;
         double var9;
         for(Iterator var6 = this.points.elements().iterator(); var6.hasNext(); var14 = var9) {
            var7 = (GameLinkedList.Element)var6.next();
            Point var8 = (Point)var7.object;
            var9 = var8.distance(var1);
            double var11 = (double)((var8.x - ((Point)var13.object).x) * (var1.y - ((Point)var13.object).y) - (var8.y - ((Point)var13.object).y) * (var1.x - ((Point)var13.object).x));
            var5.addLast(new TempLine(var13, var7, var11, var14 + var9));
            var13 = var7;
         }

         GameLinkedList.Element var15 = (GameLinkedList.Element)var5.streamElements().filter((var0) -> {
            return ((TempLine)var0.object).d < 0.0;
         }).min(Comparator.comparingDouble((var0) -> {
            return ((TempLine)var0.object).distance;
         })).orElse((Object)null);
         if (var15 == null) {
            return;
         }

         ((TempLine)var15.object).p1.insertAfter(var1);
         var7 = var15.prevWrap();

         while(((TempLine)var7.object).d < 0.0 && !((TempLine)var7.object).p2.isRemoved()) {
            ((TempLine)var7.object).p2.remove();
            var7 = var7.prevWrap();
            if (var7 == var15) {
               break;
            }
         }

         GameLinkedList.Element var16 = var15.nextWrap();

         while(((TempLine)var16.object).d < 0.0 && !((TempLine)var16.object).p1.isRemoved()) {
            ((TempLine)var16.object).p1.remove();
            var16 = var16.nextWrap();
            if (var16 == var15) {
               break;
            }
         }
      }

      this.updatePolygon();
   }

   private void updatePolygon() {
      this.xpoints = new int[this.points.size()];
      this.ypoints = new int[this.points.size()];
      this.npoints = this.points.size();
      int var1 = 0;

      for(Iterator var2 = this.points.iterator(); var2.hasNext(); ++var1) {
         Point var3 = (Point)var2.next();
         this.xpoints[var1] = var3.x;
         this.ypoints[var1] = var3.y;
      }

      this.invalidate();
   }

   private static class TempLine {
      public GameLinkedList<Point>.Element p1;
      public GameLinkedList<Point>.Element p2;
      public double d;
      public double distance;

      public TempLine(GameLinkedList<Point>.Element var1, GameLinkedList<Point>.Element var2, double var3, double var5) {
         this.p1 = var1;
         this.p2 = var2;
         this.d = var3;
         this.distance = var5;
      }
   }
}
