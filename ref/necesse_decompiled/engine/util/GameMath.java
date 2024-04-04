package necesse.engine.util;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class GameMath {
   private static final int cosSinePrecision = 3600;
   private static final double cosSineDiv = 0.1;
   private static float[] cos = new float[3600];
   private static float[] sin = new float[3600];
   public static final double diagonalDistance;

   private GameMath() {
      throw new IllegalStateException("GameMath cannot be instantiated");
   }

   public static float cos(float var0) {
      return cos[(int)((double)fixAngle(var0) / 0.1)];
   }

   public static float sin(float var0) {
      return sin[(int)((double)fixAngle(var0) / 0.1)];
   }

   public static int limit(int var0, int var1, int var2) {
      return min(max(var0, var1), var2);
   }

   public static long limit(long var0, long var2, long var4) {
      return min(max(var0, var2), var4);
   }

   public static float limit(float var0, float var1, float var2) {
      return min(max(var0, var1), var2);
   }

   public static double limit(double var0, double var2, double var4) {
      return min(max(var0, var2), var4);
   }

   public static int max(int... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         int var1 = var0[0];

         for(int var2 = 1; var2 < var0.length; ++var2) {
            var1 = Math.max(var1, var0[var2]);
         }

         return var1;
      }
   }

   public static int min(int... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         int var1 = var0[0];

         for(int var2 = 1; var2 < var0.length; ++var2) {
            var1 = Math.min(var1, var0[var2]);
         }

         return var1;
      }
   }

   public static long max(long... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         long var1 = var0[0];

         for(int var3 = 1; var3 < var0.length; ++var3) {
            var1 = Math.max(var1, var0[var3]);
         }

         return var1;
      }
   }

   public static long min(long... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         long var1 = var0[0];

         for(int var3 = 1; var3 < var0.length; ++var3) {
            var1 = Math.min(var1, var0[var3]);
         }

         return var1;
      }
   }

   public static float max(float... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         float var1 = var0[0];

         for(int var2 = 1; var2 < var0.length; ++var2) {
            var1 = Math.max(var1, var0[var2]);
         }

         return var1;
      }
   }

   public static float min(float... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         float var1 = var0[0];

         for(int var2 = 1; var2 < var0.length; ++var2) {
            var1 = Math.min(var1, var0[var2]);
         }

         return var1;
      }
   }

   public static double max(double... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         double var1 = var0[0];

         for(int var3 = 1; var3 < var0.length; ++var3) {
            var1 = Math.max(var1, var0[var3]);
         }

         return var1;
      }
   }

   public static double min(double... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must give at least 1 value");
      } else {
         double var1 = var0[0];

         for(int var3 = 1; var3 < var0.length; ++var3) {
            var1 = Math.min(var1, var0[var3]);
         }

         return var1;
      }
   }

   public static int floor(double var0) {
      return (int)Math.floor(var0);
   }

   public static int ceil(double var0) {
      return (int)Math.ceil(var0);
   }

   public static float getPercentageBetweenTwoNumbers(float var0, float var1, float var2) {
      return (var0 - var1) / (var2 - var1);
   }

   public static float lerp(float var0, float var1, float var2) {
      return var1 + (var2 - var1) * var0;
   }

   public static float lerpExp(float var0, float var1, float var2, float var3) {
      return lerp((float)Math.pow((double)var0, (double)var1), var2, var3);
   }

   public static float lerpExpSmooth(float var0, float var1, float var2, float var3, float var4) {
      if (var1 == 0.0F) {
         return lerpExp(var0, var2, var3, var4);
      } else {
         float var5 = (float)Math.pow((double)var0, (double)var2);
         float var6 = (var5 + var0 / var1) / (1.0F + 1.0F / var1);
         return lerp(var6, var3, var4);
      }
   }

   public static float exp(float var0, float var1) {
      return (float)Math.pow((double)var0, (double)var1);
   }

   public static float expSmooth(float var0, float var1, float var2) {
      float var3 = exp(var0, var2);
      return var1 == 0.0F ? var3 : (var3 + var0 / var1) / (1.0F + 1.0F / var1);
   }

   public static int lerp(float var0, int var1, int var2) {
      return (int)((float)var1 + (float)(var2 - var1) * var0);
   }

   public static int lerpExp(float var0, float var1, int var2, int var3) {
      return lerp((float)Math.pow((double)var0, (double)var1), var2, var3);
   }

   public static int lerpExpSmooth(float var0, float var1, float var2, int var3, int var4) {
      if (var1 == 0.0F) {
         return lerpExp(var0, var2, var3, var4);
      } else {
         float var5 = (float)Math.pow((double)var0, (double)var2);
         float var6 = (var5 + var0 / var1) / (1.0F + 1.0F / var1);
         return lerp(var6, var3, var4);
      }
   }

   public static long lerp(double var0, long var2, long var4) {
      return (long)((double)var2 + (double)(var4 - var2) * var0);
   }

   public static long lerpExp(double var0, double var2, long var4, long var6) {
      return lerp(Math.pow(var0, var2), var4, var6);
   }

   public static long lerpExpSmooth(double var0, double var2, double var4, long var6, long var8) {
      if (var2 == 0.0) {
         return lerpExp(var0, var4, var6, var8);
      } else {
         double var10 = Math.pow(var0, var4);
         double var12 = (var10 + var0 / var2) / (1.0 + 1.0 / var2);
         return lerp(var12, var6, var8);
      }
   }

   public static int roundToNearest(float var0, int var1) {
      return Math.round(var0 / (float)var1) * var1;
   }

   public static int floorToNearest(float var0, int var1) {
      return (int)Math.floor((double)(var0 / (float)var1)) * var1;
   }

   public static int ceilToNearest(float var0, int var1) {
      return (int)Math.ceil((double)(var0 / (float)var1)) * var1;
   }

   public static float toDecimals(float var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Decimals must be equal or larger than 0");
      } else {
         float var2 = (float)Math.pow(10.0, (double)var1);
         return (float)Math.round(var0 * var2) / var2;
      }
   }

   public static double toDecimals(double var0, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("Decimals must be equal or larger than 0");
      } else {
         double var3 = Math.pow(10.0, (double)var2);
         return (double)Math.round(var0 * var3) / var3;
      }
   }

   public static String removeDecimalIfZero(float var0) {
      return (float)((int)var0) == var0 ? "" + (int)var0 : "" + var0;
   }

   public static String removeDecimalIfZero(double var0) {
      return (double)((int)var0) == var0 ? "" + (int)var0 : "" + var0;
   }

   public static double nthRoot(double var0, double var2) {
      return Math.pow(var0, 1.0 / var2);
   }

   public static float nthRoot(float var0, float var1) {
      return (float)Math.pow((double)var0, 1.0 / (double)var1);
   }

   public static int nthRoot(int var0, int var1) {
      return (int)Math.pow((double)var0, 1.0 / (double)var1);
   }

   public static float pixelsToCentimeters(float var0) {
      return var0 * 3.86F;
   }

   public static float pixelsToMeters(float var0) {
      return pixelsToCentimeters(var0) / 100.0F;
   }

   public static int metersToPixels(float var0) {
      return (int)(var0 / pixelsToMeters(1.0F));
   }

   public static float getAngleDifference(float var0, float var1) {
      var0 = fixAngle(var0);
      var1 = fixAngle(var1);
      float var2 = Math.abs(var0 - var1) % 360.0F;
      float var3 = var2 > 180.0F ? 360.0F - var2 : var2;
      int var4 = (!(var0 - var1 >= 0.0F) || !(var0 - var1 <= 180.0F)) && (!(var0 - var1 <= -180.0F) || !(var0 - var1 >= -360.0F)) ? -1 : 1;
      return var3 * (float)var4;
   }

   public static float fixAngle(float var0) {
      float var1 = var0 % 360.0F;
      if (var0 < 0.0F) {
         float var2 = 360.0F + var1;
         return var2 == 360.0F ? 0.0F : var2;
      } else {
         return var1;
      }
   }

   public static Point2D.Float getAngleDir(float var0) {
      float var1 = cos(var0);
      float var2 = sin(var0);
      return new Point2D.Float(var1, var2);
   }

   public static float getAngle(Point2D.Float var0) {
      return (float)Math.toDegrees(Math.atan2((double)var0.y, (double)var0.x));
   }

   public static double getAngle(Point2D.Double var0) {
      return Math.toDegrees(Math.atan2(var0.y, var0.x));
   }

   public static double getExactDistance(double var0, double var2, double var4, double var6) {
      var0 -= var4;
      var2 -= var6;
      return Math.sqrt(var0 * var0 + var2 * var2);
   }

   public static float getExactDistance(float var0, float var1, float var2, float var3) {
      var0 -= var2;
      var1 -= var3;
      return (float)Math.sqrt((double)(var0 * var0 + var1 * var1));
   }

   public static double diagonalMoveDistance(int var0, int var1, int var2, int var3) {
      int var4 = Math.abs(var0 - var2);
      int var5 = Math.abs(var1 - var3);
      int var6;
      if (var4 < var5) {
         var6 = var5 - var4;
         return (double)var4 * diagonalDistance + (double)var6;
      } else if (var5 < var4) {
         var6 = var4 - var5;
         return (double)var5 * diagonalDistance + (double)var6;
      } else {
         return (double)var4 * diagonalDistance;
      }
   }

   public static double diagonalMoveDistance(Point var0, Point var1) {
      return diagonalMoveDistance(var0.x, var0.y, var1.x, var1.y);
   }

   public static double diagonalMoveDistance(double var0, double var2) {
      double var4;
      if (var0 < var2) {
         var4 = var2 - var0;
         return var0 * diagonalDistance + var4;
      } else if (var2 < var0) {
         var4 = var0 - var2;
         return var2 * diagonalDistance + var4;
      } else {
         return var0 * diagonalDistance;
      }
   }

   public static float squareDistance(float var0, float var1, float var2, float var3) {
      return max(Math.abs(var0 - var2), Math.abs(var1 - var3));
   }

   public static float diamondDistance(float var0, float var1, float var2, float var3) {
      return Math.abs(var0 - var2) + Math.abs(var1 - var3);
   }

   public static float preciseDistance(float var0, float var1, float var2, float var3) {
      double var4 = (double)(var0 - var2);
      double var6 = (double)(var1 - var3);
      return (float)Math.sqrt(var4 * var4 + var6 * var6);
   }

   public static float getAverage(float... var0) {
      float var1 = 0.0F;
      float[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         float var5 = var2[var4];
         var1 += var5;
      }

      return var1 / (float)var0.length;
   }

   public static Point2D.Float normalize(float var0, float var1) {
      Point2D.Float var2 = new Point2D.Float(var0, var1);
      float var3 = (float)var2.distance(0.0, 0.0);
      float var4 = var3 == 0.0F ? 0.0F : var2.x / var3;
      float var5 = var3 == 0.0F ? 0.0F : var2.y / var3;
      return new Point2D.Float(var4, var5);
   }

   public static Point2D.Double normalize(double var0, double var2) {
      Point2D.Double var4 = new Point2D.Double(var0, var2);
      double var5 = var4.distance(0.0, 0.0);
      double var7 = var5 == 0.0 ? 0.0 : var4.x / var5;
      double var9 = var5 == 0.0 ? 0.0 : var4.y / var5;
      return new Point2D.Double(var7, var9);
   }

   public static <T> IntersectionPoint<T> getIntersectionPoint(T var0, Line2D var1, Rectangle2D var2, boolean var3) {
      double var4 = var1.getX1();
      double var6 = var1.getY1();
      Line2D.Double var8 = new Line2D.Double(var2.getX() - 1.0, var2.getY(), var2.getX() + var2.getWidth() + 1.0, var2.getY());
      Line2D.Double var9 = new Line2D.Double(var2.getX(), var2.getY() - 1.0, var2.getX(), var2.getY() + var2.getHeight() + 1.0);
      Line2D.Double var10 = new Line2D.Double(var2.getX() - 1.0, var2.getY() + var2.getHeight(), var2.getX() + var2.getWidth() + 1.0, var2.getY() + var2.getHeight());
      Line2D.Double var11 = new Line2D.Double(var2.getX() + var2.getWidth(), var2.getY() - 1.0, var2.getX() + var2.getWidth(), var2.getY() + var2.getHeight() + 1.0);
      IntersectionPoint var12 = null;
      if (var6 >= var10.y1) {
         var12 = toIP(getIntersectionPoint(var1, var10), var0, IntersectionPoint.Dir.UP);
      }

      if (var12 == null && var4 <= var9.x1) {
         var12 = toIP(getIntersectionPoint(var1, var9), var0, IntersectionPoint.Dir.RIGHT);
      }

      if (var12 == null && var6 <= var8.y1) {
         var12 = toIP(getIntersectionPoint(var1, var8), var0, IntersectionPoint.Dir.DOWN);
      }

      if (var12 == null && var4 >= var11.x1) {
         var12 = toIP(getIntersectionPoint(var1, var11), var0, IntersectionPoint.Dir.LEFT);
      }

      if (var3) {
         if (var12 == null && var6 >= var8.y1) {
            var12 = toIP(getIntersectionPoint(var1, var8), var0, IntersectionPoint.Dir.UP);
         }

         if (var12 == null && var4 <= var11.x1) {
            var12 = toIP(getIntersectionPoint(var1, var11), var0, IntersectionPoint.Dir.RIGHT);
         }

         if (var12 == null && var6 <= var10.y1) {
            var12 = toIP(getIntersectionPoint(var1, var10), var0, IntersectionPoint.Dir.DOWN);
         }

         if (var12 == null && var4 >= var9.x1) {
            var12 = toIP(getIntersectionPoint(var1, var9), var0, IntersectionPoint.Dir.LEFT);
         }
      }

      return var12;
   }

   private static <T> IntersectionPoint<T> toIP(Point2D var0, T var1, IntersectionPoint.Dir var2) {
      return var0 == null ? null : new IntersectionPoint(var0.getX(), var0.getY(), var1, var2);
   }

   public static Point2D getIntersectionPoint(Line2D var0, Line2D var1) {
      return getIntersectionPoint(var0, var1, false);
   }

   public static Point2D getIntersectionPoint(Line2D var0, Line2D var1, boolean var2) {
      if (!var2 && !var0.intersectsLine(var1)) {
         return null;
      } else {
         double var3 = var0.getX1();
         double var5 = var0.getY1();
         double var7 = var0.getX2();
         double var9 = var0.getY2();
         double var11 = var1.getX1();
         double var13 = var1.getY1();
         double var15 = var1.getX2();
         double var17 = var1.getY2();
         double var19 = var3 - var7;
         double var21 = var5 - var9;
         double var23 = var11 - var15;
         double var25 = var13 - var17;
         double var27 = var19 * var25 - var21 * var23;
         if (var27 != 0.0) {
            double var29 = ((var3 * var9 - var5 * var7) * var23 - var19 * (var11 * var17 - var13 * var15)) / var27;
            double var31 = ((var3 * var9 - var5 * var7) * var25 - var21 * (var11 * var17 - var13 * var15)) / var27;
            return new Point2D.Double(var29, var31);
         } else {
            return null;
         }
      }
   }

   public static double dot(Point2D var0, Point2D var1) {
      return var0.getX() * var1.getX() + var0.getY() * var1.getY();
   }

   public static float dot(Point2D.Float var0, Point2D.Float var1) {
      return var0.x * var1.x + var0.y * var1.y;
   }

   public static Point2D getClosestPointOnLine(Line2D var0, Point2D var1, boolean var2) {
      double var3 = var0.getP1().distance(var0.getP2());
      if (var3 == 0.0) {
         return var0.getP1();
      } else {
         Point2D.Double var5 = new Point2D.Double((var0.getX2() - var0.getX1()) / var3, (var0.getY2() - var0.getY1()) / var3);
         Point2D.Double var6 = new Point2D.Double(var1.getX() - var0.getX1(), var1.getY() - var0.getY1());
         double var7 = dot((Point2D)var6, (Point2D)var5);
         if (!var2) {
            var7 = limit(var7, 0.0, var3);
         }

         return new Point2D.Double(var0.getX1() + var5.getX() * var7, var0.getY1() + var5.getY() * var7);
      }
   }

   public static Point2D.Double getPerpendicularDir(double var0, double var2) {
      return new Point2D.Double(-var2, var0);
   }

   public static Point2D.Double getPerpendicularDir(Point2D var0) {
      return getPerpendicularDir(var0.getX(), var0.getY());
   }

   public static Point2D.Float getPerpendicularDir(float var0, float var1) {
      return new Point2D.Float(-var1, var0);
   }

   public static Point2D.Float getPerpendicularDir(Point2D.Float var0) {
      return getPerpendicularDir(var0.x, var0.y);
   }

   public static Point2D.Double getPerpendicularPoint(double var0, double var2, float var4, double var5, double var7) {
      Point2D.Double var9 = getPerpendicularDir(var5, var7);
      return new Point2D.Double(var0 - var9.x * (double)var4, var2 - var9.y * (double)var4);
   }

   public static Point2D.Double getPerpendicularPoint(double var0, double var2, float var4, Point2D.Double var5) {
      return getPerpendicularPoint(var0, var2, var4, var5.x, var5.y);
   }

   public static Point2D.Double getPerpendicularPoint(double var0, double var2, float var4, Point2D var5) {
      return getPerpendicularPoint(var0, var2, var4, var5.getX(), var5.getY());
   }

   public static Point2D.Double getPerpendicularPoint(Point2D.Double var0, float var1, double var2, double var4) {
      return getPerpendicularPoint(var0.x, var0.y, var1, var2, var4);
   }

   public static Point2D.Double getPerpendicularPoint(Point2D var0, float var1, double var2, double var4) {
      return getPerpendicularPoint(var0.getX(), var0.getY(), var1, var2, var4);
   }

   public static Point2D.Double getPerpendicularPoint(Point2D.Double var0, float var1, Point2D.Double var2) {
      return getPerpendicularPoint(var0.x, var0.y, var1, var2.x, var2.y);
   }

   public static Point2D.Double getPerpendicularPoint(Point2D var0, float var1, Point2D.Double var2) {
      return getPerpendicularPoint(var0.getX(), var0.getY(), var1, var2.x, var2.y);
   }

   public static Point2D.Double getPerpendicularPoint(Point2D.Double var0, float var1, Point2D var2) {
      return getPerpendicularPoint(var0.x, var0.y, var1, var2.getX(), var2.getY());
   }

   public static Point2D.Double getPerpendicularPoint(Point2D var0, float var1, Point2D var2) {
      return getPerpendicularPoint(var0.getX(), var0.getY(), var1, var2.getX(), var2.getY());
   }

   public static Point2D.Float getPerpendicularPoint(float var0, float var1, float var2, float var3, float var4) {
      Point2D.Float var5 = getPerpendicularDir(var3, var4);
      return new Point2D.Float(var0 - var5.x * var2, var1 - var5.y * var2);
   }

   public static Point2D.Float getPerpendicularPoint(float var0, float var1, float var2, Point2D.Float var3) {
      return getPerpendicularPoint(var0, var1, var2, var3.x, var3.y);
   }

   public static Point2D.Float getPerpendicularPoint(Point2D.Float var0, float var1, float var2, float var3) {
      return getPerpendicularPoint(var0.x, var0.y, var1, var2, var3);
   }

   public static Point2D.Float getPerpendicularPoint(Point2D.Float var0, float var1, Point2D.Float var2) {
      return getPerpendicularPoint(var0.x, var0.y, var1, var2.x, var2.y);
   }

   public static Line2D getPerpendicularLine(Line2D var0, float var1) {
      Point2D.Double var2 = normalize(var0.getX1() - var0.getX2(), var0.getY1() - var0.getY2());
      Point2D.Double var3 = getPerpendicularDir(var2.x, var2.y);
      return new Line2D.Double(var0.getX1() + var3.x * (double)var1, var0.getY1() + var3.y * (double)var1, var0.getX2() + var3.x * (double)var1, var0.getY2() + var3.y * (double)var1);
   }

   public static boolean getBit(long var0, int var2) {
      return (var0 >> var2 & 1L) == 1L;
   }

   public static long setBit(long var0, int var2, boolean var3) {
      return var3 ? var0 | 1L << var2 : var0 & ~(1L << var2);
   }

   public static byte getByte(long var0, int var2) {
      return (byte)((int)(var0 >> var2 * 8));
   }

   public static long setByte(long var0, int var2, byte var3) {
      long var4 = 255L << var2 * 8;
      long var6 = (long)var3 << var2 * 8 & var4;
      return var0 & ~var4 | var6;
   }

   public static int setBit(int var0, int var1, boolean var2) {
      return var2 ? var0 | 1 << var1 : var0 & ~(1 << var1);
   }

   public static byte getByte(int var0, int var1) {
      return (byte)(var0 >> var1 * 8);
   }

   public static int setByte(int var0, int var1, byte var2) {
      int var3 = 255 << var1 * 8;
      int var4 = var2 << var1 * 8 & var3;
      return var0 & ~var3 | var4;
   }

   public static short setBit(short var0, int var1, boolean var2) {
      return var2 ? (short)(var0 | 1 << var1) : (short)(var0 & ~(1 << var1));
   }

   public static byte getByte(short var0, int var1) {
      return (byte)(var0 >> var1 * 8);
   }

   public static short setByte(short var0, int var1, byte var2) {
      int var3 = 255 << var1 * 8;
      int var4 = var2 << var1 * 8 & var3;
      return (short)(var0 & ~var3 | var4);
   }

   public static byte setBit(byte var0, int var1, boolean var2) {
      return var2 ? (byte)(var0 | 1 << var1) : (byte)(var0 & ~(1 << var1));
   }

   public static double getSuccessAfterRuns(double var0, long var2) {
      return 1.0 - Math.pow(1.0 - var0, (double)var2);
   }

   public static double getRunsForSuccess(double var0, double var2) {
      return Math.log(1.0 - var2) / Math.log(1.0 - var0);
   }

   public static double getChanceAtRuns(double var0, double var2) {
      double var4 = Math.pow(1.0 - var0, 1.0 / var2);
      return 1.0 - var4;
   }

   public static double getAverageSuccessRuns(double var0) {
      return getChanceAtRuns(0.5, var0 * 0.705);
   }

   public static double getAverageRunsForSuccess(double var0, double var2) {
      return getRunsForSuccess(var0, var2) * 1.418;
   }

   public static double getAverageRunsForSuccess(double var0) {
      return getAverageRunsForSuccess(var0, 0.5);
   }

   static {
      for(int var0 = 0; var0 < 3600; ++var0) {
         float var1 = 0.1F * (float)var0;
         cos[var0] = (float)Math.cos(Math.toRadians((double)var1));
         sin[var0] = (float)Math.sin(Math.toRadians((double)var1));
      }

      diagonalDistance = Math.sqrt(2.0);
   }
}
