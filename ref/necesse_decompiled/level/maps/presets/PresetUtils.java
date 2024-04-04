package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.util.GameRandom;

public class PresetUtils {
   public PresetUtils() {
   }

   public static Preset randomizeRotationAndMirror(Preset var0, GameRandom var1) {
      var0 = randomizeXMirror(var0, var1);
      var0 = randomizeYMirror(var0, var1);
      var0 = randomizeRotation(var0, var1);
      return var0;
   }

   public static Preset randomizeXMirror(Preset var0, GameRandom var1) {
      return var1.nextBoolean() ? var0.tryMirrorX() : var0;
   }

   public static Preset randomizeYMirror(Preset var0, GameRandom var1) {
      return var1.nextBoolean() ? var0.tryMirrorY() : var0;
   }

   public static Preset randomizeRotation(Preset var0, GameRandom var1) {
      return var0.tryRotate((PresetRotation)var1.getOneOf((Object[])(PresetRotation.CLOCKWISE, PresetRotation.ANTI_CLOCKWISE, PresetRotation.HALF_180, null)));
   }

   public static Point getRotatedPoint(int var0, int var1, int var2, int var3, PresetRotation var4) {
      int var5;
      int var6;
      if (var4 == PresetRotation.CLOCKWISE) {
         var5 = var0 - var2;
         var6 = var1 - var3;
         return new Point(var2 - var6, var3 + var5);
      } else if (var4 == PresetRotation.HALF_180) {
         var5 = var0 - var2;
         var6 = var1 - var3;
         return new Point(var2 - var5, var3 - var6);
      } else if (var4 == PresetRotation.ANTI_CLOCKWISE) {
         var5 = var0 - var2;
         var6 = var1 - var3;
         return new Point(var2 + var6, var3 - var5);
      } else {
         return new Point(var0, var1);
      }
   }

   public static Point getRotatedPointInSpace(int var0, int var1, int var2, int var3, PresetRotation var4) {
      Point var5 = getRotatedPoint(var0, var1, 0, 0, var4);
      if (var4 == PresetRotation.CLOCKWISE) {
         var5.x += var3 - 1;
      } else if (var4 == PresetRotation.HALF_180) {
         var5.x += var2 - 1;
         var5.y += var3 - 1;
      } else if (var4 == PresetRotation.ANTI_CLOCKWISE) {
         var5.y += var2 - 1;
      }

      return var5;
   }

   public static Point getRotatedPoint(int var0, int var1, int var2, int var3, int var4) {
      return getRotatedPoint(var0, var1, var2, var3, PresetRotation.toRotationAngle(var4));
   }

   public static Point[] getRotatedPoints(int var0, int var1, PresetRotation var2, Point... var3) {
      Point[] var4 = new Point[var3.length];

      for(int var5 = 0; var5 < var3.length; ++var5) {
         Point var6 = var3[var5];
         var4[var5] = getRotatedPoint(var6.x, var6.y, var0, var1, var2);
      }

      return var4;
   }

   public static Point[] getRotatedPoints(int var0, int var1, int var2, Point... var3) {
      return getRotatedPoints(var0, var1, PresetRotation.toRotationAngle(var2), var3);
   }

   public static int getMirroredValue(int var0, int var1) {
      return var1 - 1 - var0;
   }

   public static Point getMirroredPoint(int var0, int var1, boolean var2, boolean var3, int var4, int var5) {
      if (var2) {
         var0 = getMirroredValue(var0, var4);
      }

      if (var3) {
         var1 = getMirroredValue(var1, var5);
      }

      return new Point(var0, var1);
   }
}
