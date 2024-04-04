package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class LinesGeneration {
   public final int x1;
   public final int y1;
   public final int x2;
   public final int y2;
   public final float width;
   protected boolean lineWidthPriority;
   protected final LinesGeneration root;
   protected LinkedList<LinesGeneration> lines;

   private LinesGeneration(LinesGeneration var1, int var2, int var3, int var4, int var5, float var6) {
      this.lines = new LinkedList();
      this.root = var1;
      this.x1 = var2;
      this.y1 = var3;
      this.x2 = var4;
      this.y2 = var5;
      this.width = var6;
   }

   public LinesGeneration(int var1, int var2, int var3, int var4, float var5) {
      this((LinesGeneration)null, var1, var2, var3, var4, var5);
   }

   public LinesGeneration(int var1, int var2, float var3) {
      this((LinesGeneration)null, var1, var2, var1, var2, var3);
   }

   public LinesGeneration(int var1, int var2) {
      this((LinesGeneration)null, var1, var2, var1, var2, 0.0F);
   }

   public LinesGeneration lineWidthPriority(boolean var1) {
      this.lineWidthPriority = var1;
      return this;
   }

   public LinesGeneration addLine(int var1, int var2, int var3, int var4, float var5) {
      if (this.root != null) {
         return this.root.addLine(var1, var2, var3, var4, var5);
      } else {
         LinesGeneration var6 = new LinesGeneration(this, var1, var2, var3, var4, var5);
         this.lines.addLast(var6);
         return var6;
      }
   }

   public LinesGeneration addLineTo(int var1, int var2, float var3) {
      if (this.root != null) {
         return this.root.addLine(this.x2, this.y2, var1, var2, var3);
      } else {
         LinesGeneration var4 = new LinesGeneration(this, this.x2, this.y2, var1, var2, var3);
         this.lines.addLast(var4);
         return var4;
      }
   }

   public LinesGeneration addLineToDelta(int var1, int var2, float var3) {
      return this.addLineTo(this.x2 + var1, this.y2 + var2, var3);
   }

   public LinesGeneration addPoint(int var1, int var2, float var3) {
      return this.addLine(var1, var2, var1, var2, var3);
   }

   public LinesGeneration addPointDelta(int var1, int var2, float var3) {
      return this.addLine(this.x2 + var1, this.y2 + var2, this.x2 + var1, this.y2 + var2, var3);
   }

   public LinesGeneration addRandomArms(GameRandom var1, int var2, float var3, float var4, float var5, float var6) {
      int var7 = var1.nextInt(360);
      int var8 = 360 / var2;

      for(int var9 = 0; var9 < var2; ++var9) {
         var7 += var1.getIntOffset(var8, var8 / 2);
         float var10 = var1.getFloatBetween(var3, var4);
         float var11 = var1.getFloatBetween(var5, var6);
         this.addArm((float)var7, var10, var11);
      }

      return this;
   }

   public LinesGeneration addArm(float var1, float var2, float var3) {
      Point2D.Float var4 = GameMath.getAngleDir(var1);
      return this.addLineToDelta((int)(var4.x * var2), (int)(var4.y * var2), var3);
   }

   public LinesGeneration addMultiArm(GameRandom var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, Predicate<LinesGeneration> var9) {
      float var10 = 0.0F;
      LinesGeneration var11 = this;

      while(var10 < (float)var4) {
         var2 = var1.getIntOffset(var2, var3);
         float var12 = var1.getFloatBetween(var5, var6);
         float var13 = var1.getFloatBetween(var7, var8);
         var10 += var12;
         var11 = var11.addArm((float)var2, var12, var13);
         if (var9 != null && var9.test(var11)) {
            break;
         }
      }

      return var11;
   }

   public LinesGeneration addMultiArm(GameRandom var1, Level var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      return this.addMultiArm(var1, var3, var4, var5, var6, var7, var8, var9, (var1x) -> {
         return var1x.x2 < 0 || var1x.x2 > var2.width || var1x.y2 < 0 || var1x.y2 > var2.height;
      });
   }

   public CellAutomaton toCellularAutomaton(BiFunction<Point, PointDistance, Boolean> var1, boolean var2) {
      CellAutomaton var3 = new CellAutomaton();
      if (var2) {
         Comparator var4 = Comparator.comparingInt((var0) -> {
            return ((Point)var0.getKey()).x;
         });
         var4 = var4.thenComparingInt((var0) -> {
            return ((Point)var0.getKey()).y;
         });
         this.getSmoothPoints().entrySet().stream().sorted(var4).forEachOrdered((var2x) -> {
            Point var3x = (Point)var2x.getKey();
            PointDistance var4 = (PointDistance)var2x.getValue();
            if ((Boolean)var1.apply(var3x, var4)) {
               var3.setAlive(var3x.x, var3x.y);
            }

         });
      } else {
         this.getSmoothPoints().forEach((var2x, var3x) -> {
            if ((Boolean)var1.apply(var2x, var3x)) {
               var3.setAlive(var2x.x, var2x.y);
            }

         });
      }

      return var3;
   }

   public CellAutomaton toCellularAutomaton(GameRandom var1) {
      return this.toCellularAutomaton((var1x, var2) -> {
         if (var2.lineWidth == 0.0) {
            return false;
         } else {
            double var3 = Math.abs(var2.dist / var2.lineWidth - 1.0);
            return var1.getChance(var3);
         }
      }, true);
   }

   public CellAutomaton doCellularAutomaton(BiFunction<Point, PointDistance, Boolean> var1, boolean var2, int var3, int var4, int var5) {
      CellAutomaton var6 = this.toCellularAutomaton(var1, var2);
      var6.doCellularAutomaton(var3, var4, var5);
      return var6;
   }

   public CellAutomaton doCellularAutomaton(GameRandom var1, int var2, int var3, int var4) {
      CellAutomaton var5 = this.toCellularAutomaton(var1);
      var5.doCellularAutomaton(var2, var3, var4);
      return var5;
   }

   public CellAutomaton doCellularAutomaton(BiFunction<Point, PointDistance, Boolean> var1, boolean var2) {
      return this.doCellularAutomaton(var1, var2, 4, 3, 4);
   }

   public CellAutomaton doCellularAutomaton(GameRandom var1) {
      return this.doCellularAutomaton(var1, 4, 3, 4);
   }

   public LinesGeneration getRoot() {
      return this.root == null ? this : this.root;
   }

   public HashMap<Point, PointDistance> getSmoothPoints() {
      LinesGeneration var1 = this.root == null ? this : this.root;
      HashMap var2 = new HashMap();
      this.addSmoothPoints(var2, this.width);
      Iterator var3 = var1.lines.iterator();

      while(var3.hasNext()) {
         LinesGeneration var4 = (LinesGeneration)var3.next();
         var4.addSmoothPoints(var2, var4.width);
      }

      return var2;
   }

   public HashSet<Point> getDiamondPoints() {
      LinesGeneration var1 = this.root == null ? this : this.root;
      HashSet var2 = new HashSet();
      this.addDiamondPoints(var2, this.width);
      Iterator var3 = var1.lines.iterator();

      while(var3.hasNext()) {
         LinesGeneration var4 = (LinesGeneration)var3.next();
         var4.addDiamondPoints(var2, var4.width);
      }

      return var2;
   }

   public Line2D.Float getTileLine() {
      return new Line2D.Float((float)this.x1, (float)this.y1, (float)this.x2, (float)this.y2);
   }

   public Line2D.Float getPosLine() {
      return new Line2D.Float((float)(this.x1 * 32 + 16), (float)(this.y1 * 32 + 16), (float)(this.x2 * 32 + 16), (float)(this.y2 * 32 + 16));
   }

   public Iterable<LinesGeneration> getLines() {
      return this.lines;
   }

   public void recursiveLines(Function<LinesGeneration, Boolean> var1) {
      Iterator var2 = this.lines.iterator();

      while(var2.hasNext()) {
         LinesGeneration var3 = (LinesGeneration)var2.next();
         if ((Boolean)var1.apply(var3)) {
            var3.recursiveLines(var1);
         }
      }

   }

   public static void pathTiles(Line2D.Float var0, boolean var1, BiConsumer<Point, Point> var2) {
      pathTilesBreak(var0, var1, (var1x, var2x) -> {
         var2.accept(var1x, var2x);
         return true;
      });
   }

   public static void pathTilesBreak(Line2D.Float var0, boolean var1, BiPredicate<Point, Point> var2) {
      HashSet var3 = new HashSet();
      Point var4 = new Point((int)var0.x1, (int)var0.y1);
      Point var5 = new Point((int)var0.x2, (int)var0.y2);
      Line2D.Float var6 = new Line2D.Float(var0.x1 * 32.0F + 16.0F, var0.y1 * 32.0F + 16.0F, var0.x2 * 32.0F + 16.0F, var0.y2 * 32.0F + 16.0F);
      if (var2.test((Object)null, var4)) {
         var3.add(var4);

         Point var7;
         while((var7 = getNextPathLine(var4, var3, var6, var1)) != null) {
            var3.add(var7);
            Point var8 = var4;
            var4 = var7;
            if (!var2.test(var8, var7) || var7.x == var5.x && var7.y == var5.y) {
               break;
            }
         }

      }
   }

   private static Point getNextPathLine(Point var0, HashSet<Point> var1, Line2D.Float var2, boolean var3) {
      if (pathLineIntersects(var0.x, var0.y - 1, var1, var2)) {
         if (var2.getX1() < var2.getX2()) {
            var1.add(new Point(var0.x + 1, var0.y));
         } else if (var2.getX1() > var2.getX2()) {
            var1.add(new Point(var0.x - 1, var0.y));
         }

         return new Point(var0.x, var0.y - 1);
      } else if (pathLineIntersects(var0.x + 1, var0.y, var1, var2)) {
         if (var2.getY1() < var2.getY2()) {
            var1.add(new Point(var0.x, var0.y + 1));
         } else if (var2.getY1() > var2.getY2()) {
            var1.add(new Point(var0.x, var0.y - 1));
         }

         return new Point(var0.x + 1, var0.y);
      } else if (pathLineIntersects(var0.x, var0.y + 1, var1, var2)) {
         if (var2.getX1() < var2.getX2()) {
            var1.add(new Point(var0.x + 1, var0.y));
         } else if (var2.getX1() > var2.getX2()) {
            var1.add(new Point(var0.x - 1, var0.y));
         }

         return new Point(var0.x, var0.y + 1);
      } else if (pathLineIntersects(var0.x - 1, var0.y, var1, var2)) {
         if (var2.getY1() < var2.getY2()) {
            var1.add(new Point(var0.x, var0.y - 1));
         } else if (var2.getY1() > var2.getY2()) {
            var1.add(new Point(var0.x, var0.y + 1));
         }

         return new Point(var0.x - 1, var0.y);
      } else if (pathLineIntersects(var0.x + 1, var0.y - 1, var1, var2)) {
         if (var3) {
            var1.add(new Point(var0.x + 1, var0.y));
            return new Point(var0.x, var0.y - 1);
         } else {
            var1.add(new Point(var0.x, var0.y - 1));
            return new Point(var0.x + 1, var0.y);
         }
      } else if (pathLineIntersects(var0.x + 1, var0.y + 1, var1, var2)) {
         if (var3) {
            var1.add(new Point(var0.x, var0.y + 1));
            return new Point(var0.x + 1, var0.y);
         } else {
            var1.add(new Point(var0.x + 1, var0.y));
            return new Point(var0.x, var0.y + 1);
         }
      } else if (pathLineIntersects(var0.x - 1, var0.y + 1, var1, var2)) {
         if (var3) {
            var1.add(new Point(var0.x, var0.y + 1));
            return new Point(var0.x - 1, var0.y);
         } else {
            var1.add(new Point(var0.x - 1, var0.y));
            return new Point(var0.x, var0.y + 1);
         }
      } else if (pathLineIntersects(var0.x - 1, var0.y - 1, var1, var2)) {
         if (var3) {
            var1.add(new Point(var0.x - 1, var0.y));
            return new Point(var0.x, var0.y - 1);
         } else {
            var1.add(new Point(var0.x, var0.y - 1));
            return new Point(var0.x - 1, var0.y);
         }
      } else {
         return null;
      }
   }

   private static boolean pathLineIntersects(int var0, int var1, HashSet<Point> var2, Line2D.Float var3) {
      return !var2.contains(new Point(var0, var1)) && var3.intersects(getTileRectangle(var0, var1));
   }

   private static Rectangle getTileRectangle(int var0, int var1) {
      return new Rectangle(var0 * 32, var1 * 32, 32, 32);
   }

   private void addTestPoints(HashSet<Point> var1, float var2) {
      int var3;
      int var4;
      Point var5;
      for(var3 = (int)Math.floor((double)((float)this.x1 - var2)); (float)var3 <= (float)this.x1 + var2; ++var3) {
         for(var4 = (int)Math.floor((double)((float)this.y1 - var2)); (float)var4 <= (float)this.y1 + var2; ++var4) {
            var5 = new Point(var3, var4);
            if (!var1.contains(var5) && GameMath.diamondDistance((float)this.x1, (float)this.y1, (float)var5.x, (float)var5.y) <= var2) {
               var1.add(var5);
            }
         }
      }

      for(var3 = (int)Math.floor((double)((float)this.x2 - var2)); (float)var3 <= (float)this.x2 + var2; ++var3) {
         for(var4 = (int)Math.floor((double)((float)this.y2 - var2)); (float)var4 <= (float)this.y2 + var2; ++var4) {
            var5 = new Point(var3, var4);
            if (!var1.contains(var5) && GameMath.diamondDistance((float)this.x2, (float)this.y2, (float)var5.x, (float)var5.y) <= var2) {
               var1.add(var5);
            }
         }
      }

      var3 = this.x1 - this.x2;
      var4 = this.y1 - this.y2;
      if (var3 != 0 || var4 != 0) {
         int var17 = Math.abs(var3);
         int var6 = Math.abs(var4);
         Point var7;
         Point var8;
         int var9;
         int var10;
         int var11;
         float var12;
         int var13;
         int var14;
         int var15;
         Point var16;
         if (var6 <= var17) {
            if (this.x1 < this.x2) {
               var7 = new Point(this.x1, this.y1);
               var8 = new Point(this.x2, this.y2);
            } else {
               var7 = new Point(this.x2, this.y2);
               var8 = new Point(this.x1, this.y1);
            }

            var9 = var8.y - var7.y;
            var10 = var8.x - var7.x;

            for(var11 = var7.x + 1; var11 < var8.x; ++var11) {
               var12 = (float)var7.y + (float)var9 * ((float)(var11 - var7.x) / (float)var10);
               System.out.println("On X " + var11 + ", " + var12);
               var13 = (int)Math.floor((double)(var12 - var2));
               var14 = (int)Math.ceil((double)(var12 + var2));

               for(var15 = var13; var15 <= var14; ++var15) {
                  System.out.println("Checking Y " + var15);
                  var16 = new Point(var11, var15);
                  if (!var1.contains(var16) && Math.abs((float)var15 - var12) <= var2) {
                     var1.add(var16);
                  }
               }
            }
         } else {
            if (this.y1 < this.y2) {
               var7 = new Point(this.x1, this.y1);
               var8 = new Point(this.x2, this.y2);
            } else {
               var7 = new Point(this.x2, this.y2);
               var8 = new Point(this.x1, this.y1);
            }

            var9 = var8.y - var7.y;
            var10 = var8.x - var7.x;

            for(var11 = var7.y + 1; var11 < var8.y; ++var11) {
               var12 = (float)var7.x + (float)var10 * ((float)(var11 - var7.y) / (float)var9);
               var13 = (int)Math.floor((double)(var12 - var2));
               var14 = (int)Math.ceil((double)(var12 + var2));

               for(var15 = var13; var15 <= var14; ++var15) {
                  var16 = new Point(var15, var11);
                  if (!var1.contains(var16) && Math.abs((float)var15 - var12) <= var2) {
                     var1.add(var16);
                  }
               }
            }
         }
      }

   }

   private void addSmoothPoints(HashMap<Point, PointDistance> var1, float var2) {
      int var5;
      int var6;
      int var7;
      if (this.x1 == this.x2 && this.y1 == this.y2) {
         int var17 = (int)Math.floor((double)((float)this.x1 - var2));
         int var18 = (int)Math.ceil((double)((float)this.x1 + var2));
         var5 = (int)Math.floor((double)((float)this.y1 - var2));
         var6 = (int)Math.ceil((double)((float)this.y1 + var2));

         for(var7 = var17; var7 <= var18; ++var7) {
            for(int var19 = var5; var19 <= var6; ++var19) {
               double var20 = (new Point(this.x1, this.y1)).distance((double)var7, (double)var19);
               if (!(var20 > (double)var2)) {
                  var1.compute(new Point(var7, var19), (var4x, var5x) -> {
                     if (var5x == null) {
                        return new PointDistance(var20, (double)var2, this.lineWidthPriority);
                     } else {
                        return !(var20 < var5x.dist) ? var5x : new PointDistance(var20, this.lineWidthPriority && !var5x.lineWidthPriority ? (double)var2 : Math.min((double)var2, var5x.lineWidth), var5x.lineWidthPriority || this.lineWidthPriority);
                     }
                  });
               }
            }
         }
      } else {
         Point var3;
         Point var4;
         float var8;
         int var9;
         int var10;
         int var11;
         int var12;
         int var13;
         int var14;
         double var15;
         if (Math.abs(this.y1 - this.y2) <= Math.abs(this.x1 - this.x2)) {
            if (this.x1 < this.x2) {
               var3 = new Point(this.x1, this.y1);
               var4 = new Point(this.x2, this.y2);
            } else {
               var3 = new Point(this.x2, this.y2);
               var4 = new Point(this.x1, this.y1);
            }

            var5 = var4.y - var3.y;
            var6 = var4.x - var3.x;

            for(var7 = var3.x; var7 <= var4.x; ++var7) {
               var8 = (float)var3.y + (float)var5 * ((float)(var7 - var3.x) / (float)var6);
               var9 = (int)Math.floor((double)((float)var7 - var2));
               var10 = (int)Math.ceil((double)((float)var7 + var2));
               var11 = (int)Math.floor((double)(var8 - var2));
               var12 = (int)Math.ceil((double)(var8 + var2));

               for(var13 = var9; var13 <= var10; ++var13) {
                  for(var14 = var11; var14 <= var12; ++var14) {
                     var15 = this.getDistToPoint(var13, var14);
                     if (!(var15 > (double)var2)) {
                        var1.compute(new Point(var13, var14), (var4x, var5x) -> {
                           if (var5x == null) {
                              return new PointDistance(var15, (double)var2, this.lineWidthPriority);
                           } else {
                              return !(var15 < var5x.dist) ? var5x : new PointDistance(var15, this.lineWidthPriority && !var5x.lineWidthPriority ? (double)var2 : Math.min((double)var2, var5x.lineWidth), var5x.lineWidthPriority || this.lineWidthPriority);
                           }
                        });
                     }
                  }
               }
            }
         } else {
            if (this.y1 < this.y2) {
               var3 = new Point(this.x1, this.y1);
               var4 = new Point(this.x2, this.y2);
            } else {
               var3 = new Point(this.x2, this.y2);
               var4 = new Point(this.x1, this.y1);
            }

            var5 = var4.y - var3.y;
            var6 = var4.x - var3.x;

            for(var7 = var3.y; var7 <= var4.y; ++var7) {
               var8 = (float)var3.x + (float)var6 * ((float)(var7 - var3.y) / (float)var5);
               var9 = (int)Math.floor((double)(var8 - var2));
               var10 = (int)Math.ceil((double)(var8 + var2));
               var11 = (int)Math.floor((double)((float)var7 - var2));
               var12 = (int)Math.ceil((double)((float)var7 + var2));

               for(var13 = var9; var13 <= var10; ++var13) {
                  for(var14 = var11; var14 <= var12; ++var14) {
                     var15 = this.getDistToPoint(var13, var14);
                     if (!(var15 > (double)var2)) {
                        var1.compute(new Point(var13, var14), (var4x, var5x) -> {
                           if (var5x == null) {
                              return new PointDistance(var15, (double)var2, this.lineWidthPriority);
                           } else {
                              return !(var15 < var5x.dist) ? var5x : new PointDistance(var15, this.lineWidthPriority && !var5x.lineWidthPriority ? (double)var2 : Math.min((double)var2, var5x.lineWidth), var5x.lineWidthPriority || this.lineWidthPriority);
                           }
                        });
                     }
                  }
               }
            }
         }
      }

   }

   private double getDistToPoint(int var1, int var2) {
      int var3 = var1 - this.x1;
      int var4 = var2 - this.y1;
      int var5 = this.x2 - this.x1;
      int var6 = this.y2 - this.y1;
      int var7 = var3 * var5 + var4 * var6;
      int var8 = var5 * var5 + var6 * var6;
      float var9 = -1.0F;
      if (var8 != 0) {
         var9 = (float)var7 / (float)var8;
      }

      float var10;
      float var11;
      if (var9 < 0.0F) {
         var10 = (float)this.x1;
         var11 = (float)this.y1;
      } else if (var9 > 1.0F) {
         var10 = (float)this.x2;
         var11 = (float)this.y2;
      } else {
         var10 = (float)this.x1 + var9 * (float)var5;
         var11 = (float)this.y1 + var9 * (float)var6;
      }

      float var12 = (float)var1 - var10;
      float var13 = (float)var2 - var11;
      return Math.sqrt((double)(var12 * var12 + var13 * var13));
   }

   private void addDiamondPoints(HashSet<Point> var1, float var2) {
      int var5;
      int var6;
      int var7;
      if (this.x1 == this.x2 && this.y1 == this.y2) {
         int var16 = (int)Math.floor((double)((float)this.x1 - var2));
         int var17 = (int)Math.ceil((double)((float)this.x1 + var2));
         var5 = (int)Math.floor((double)((float)this.y1 - var2));
         var6 = (int)Math.ceil((double)((float)this.y1 + var2));

         for(var7 = var16; var7 <= var17; ++var7) {
            for(int var18 = var5; var18 <= var6; ++var18) {
               Point var19 = new Point(var7, var18);
               if (!var1.contains(var19) && (float)(Math.abs(var18 - this.x1) + Math.abs(var7 - this.y1)) <= var2) {
                  var1.add(var19);
               }
            }
         }
      } else {
         Point var3;
         Point var4;
         float var8;
         int var9;
         int var10;
         int var11;
         int var12;
         int var13;
         int var14;
         Point var15;
         if (Math.abs(this.y1 - this.y2) <= Math.abs(this.x1 - this.x2)) {
            if (this.x1 < this.x2) {
               var3 = new Point(this.x1, this.y1);
               var4 = new Point(this.x2, this.y2);
            } else {
               var3 = new Point(this.x2, this.y2);
               var4 = new Point(this.x1, this.y1);
            }

            var5 = var4.y - var3.y;
            var6 = var4.x - var3.x;

            for(var7 = var3.x; var7 <= var4.x; ++var7) {
               var8 = (float)var3.y + (float)var5 * ((float)(var7 - var3.x) / (float)var6);
               var9 = (int)Math.floor((double)((float)var7 - var2));
               var10 = (int)Math.ceil((double)((float)var7 + var2));
               var11 = (int)Math.floor((double)(var8 - var2));
               var12 = (int)Math.ceil((double)(var8 + var2));

               for(var13 = var9; var13 <= var10; ++var13) {
                  for(var14 = var11; var14 <= var12; ++var14) {
                     var15 = new Point(var13, var14);
                     if (!var1.contains(var15) && Math.abs((float)var14 - var8) + (float)Math.abs(var13 - var7) <= var2) {
                        var1.add(var15);
                     }
                  }
               }
            }
         } else {
            if (this.y1 < this.y2) {
               var3 = new Point(this.x1, this.y1);
               var4 = new Point(this.x2, this.y2);
            } else {
               var3 = new Point(this.x2, this.y2);
               var4 = new Point(this.x1, this.y1);
            }

            var5 = var4.y - var3.y;
            var6 = var4.x - var3.x;

            for(var7 = var3.y; var7 <= var4.y; ++var7) {
               var8 = (float)var3.x + (float)var6 * ((float)(var7 - var3.y) / (float)var5);
               var9 = (int)Math.floor((double)(var8 - var2));
               var10 = (int)Math.ceil((double)(var8 + var2));
               var11 = (int)Math.floor((double)((float)var7 - var2));
               var12 = (int)Math.ceil((double)((float)var7 + var2));

               for(var13 = var9; var13 <= var10; ++var13) {
                  for(var14 = var11; var14 <= var12; ++var14) {
                     var15 = new Point(var13, var14);
                     if (!var1.contains(var15) && (float)Math.abs(var14 - var7) + Math.abs((float)var13 - var8) <= var2) {
                        var1.add(var15);
                     }
                  }
               }
            }
         }
      }

   }

   public static class PointDistance {
      public final double dist;
      public final double lineWidth;
      protected boolean lineWidthPriority;

      protected PointDistance(double var1, double var3, boolean var5) {
         this.dist = var1;
         this.lineWidth = var3;
         this.lineWidthPriority = var5;
      }
   }
}
