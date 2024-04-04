package necesse.entity.mobs.ai.path;

import java.awt.Point;

public enum PathDir {
   UP(0, -1),
   UP_RIGHT(1, -1),
   RIGHT(1, 0),
   DOWN_RIGHT(1, 1),
   DOWN(0, 1),
   DOWN_LEFT(-1, 1),
   LEFT(-1, 0),
   UP_LEFT(-1, -1);

   public final Point point;
   public final int x;
   public final int y;
   public final boolean isDiagonal;
   public final int dir;

   private PathDir(int var3, int var4) {
      if (Math.abs(var3) <= 1 && Math.abs(var4) <= 1) {
         this.point = new Point(var3, var4);
         this.x = var3;
         this.y = var4;
         this.isDiagonal = var3 != 0 && var4 != 0;
         if (var3 == 0 && var4 < 0) {
            this.dir = 0;
         } else if (var3 > 0 && var4 < 0) {
            this.dir = 1;
         } else if (var3 > 0 && var4 == 0) {
            this.dir = 2;
         } else if (var3 > 0) {
            this.dir = 3;
         } else if (var3 == 0 && var4 > 0) {
            this.dir = 4;
         } else if (var3 < 0 && var4 > 0) {
            this.dir = 5;
         } else if (var3 < 0 && var4 == 0) {
            this.dir = 6;
         } else {
            this.dir = 7;
         }

      } else {
         throw new IllegalArgumentException("Cannot be offset more than 1");
      }
   }

   public static PathDir getDir(Point var0, Point var1) {
      int var2 = var0.x - var1.x;
      int var3 = var0.y - var1.y;
      if (var2 == 0 && var3 < 0) {
         return UP;
      } else if (var2 > 0 && var3 < 0) {
         return UP_RIGHT;
      } else if (var2 > 0 && var3 == 0) {
         return RIGHT;
      } else if (var2 > 0) {
         return DOWN_RIGHT;
      } else if (var2 == 0 && var3 > 0) {
         return DOWN;
      } else if (var2 < 0 && var3 > 0) {
         return DOWN_LEFT;
      } else if (var2 < 0 && var3 == 0) {
         return LEFT;
      } else {
         return var2 < 0 ? UP_LEFT : null;
      }
   }

   // $FF: synthetic method
   private static PathDir[] $values() {
      return new PathDir[]{UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT};
   }
}
