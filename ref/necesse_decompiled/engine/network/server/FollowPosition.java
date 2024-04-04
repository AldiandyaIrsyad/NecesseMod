package necesse.engine.network.server;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;

public class FollowPosition {
   public static FollowPosition WALK_CLOSE = new FollowPosition((var0, var1, var2) -> {
      return null;
   }, (var0, var1, var2) -> {
      return null;
   });
   public static FollowPosition FLYING_CIRCLE = new FollowPosition((var0, var1, var2) -> {
      return null;
   }, (var0, var1, var2) -> {
      return circlingPos(var0, var1, var2, 50, 30.0F, 50, 10);
   });
   public static FollowPosition FLYING_CIRCLE_FAST = new FollowPosition((var0, var1, var2) -> {
      return null;
   }, (var0, var1, var2) -> {
      return circlingPos(var0, var1, var2, 40, 60.0F, 50, 10);
   });
   public static FollowPosition SLIME_CIRCLE_MOVEMENT = new FollowPosition((var0, var1, var2) -> {
      return null;
   }, (var0, var1, var2) -> {
      return circlingPos(var0, var1, var2, 40, 85.0F, 70, 10);
   });
   public static FollowPosition FLYING = newFlying(0, 0, 30, -40);
   public static FollowPosition PYRAMID = newPyramid(30, 30);
   public final FollowPositionGetter getDefaultPoint;
   public final FollowPositionGetter getRelativePos;

   public static FollowerPosition circlingPos(Mob var0, int var1, int var2, int var3, float var4, int var5, int var6) {
      int var7 = 1;

      while(true) {
         int var8 = var6 + var7 * var5;
         int var10 = (int)(Math.PI * (double)var8 * 2.0);
         int var9 = var10 / var3;
         if (var9 > var1) {
            boolean var11 = var7 % 2 == 0;
            float var12 = 360.0F / (float)Math.min(var2, var9);
            float var13 = var12 * (float)var1;
            float var14 = MobMovementCircle.convertToRotSpeed(var8, var4);
            Point var15 = MobMovementCircle.getOffsetPosition(var0, var8, var14, var13, false);
            return new FollowerPosition(var15.x, var15.y, (var5x) -> {
               return new MobMovementCircleRelative(var0, var5x, var8, var14, var13, var11);
            });
         }

         ++var7;
         var1 -= var9;
         var2 -= var9;
      }
   }

   public static FollowPosition newFlying(int var0, int var1, int var2, int var3) {
      return new FollowPosition((var4, var5, var6) -> {
         int var7 = var5 / 8 + 1;
         int var8 = var5 % 8 + 1;
         return new FollowerPosition(var0 + var2 * var8, var1 + var3 * var7);
      }, (var4, var5, var6) -> {
         int var7 = var5 / 8 + 1;
         int var8 = var5 % 8 + 1;
         if (var4.dir == 1) {
            return new FollowerPosition(-var0 - var2 * var8, var1 + var3 * var7);
         } else {
            return var4.dir == 3 ? new FollowerPosition(var0 + var2 * var8, var1 + var3 * var7) : null;
         }
      });
   }

   public static FollowPosition newPyramid(int var0, int var1) {
      return new FollowPosition((var0x, var1x, var2) -> {
         return null;
      }, (var2, var3, var4) -> {
         byte var5 = 4;
         byte var6 = 2;
         int var7 = (int)((-1.0 + Math.sqrt((double)(1 + 8 * (var3 + var5)))) / 2.0) - var6;
         int var8 = (int)((-1.0 + Math.sqrt((double)(1 + 8 * (var4 + var5)))) / 2.0) - var6;
         int var9 = var7 + var6;
         int var10 = (var9 * var9 + var9) / 2 - var5;
         int var11 = var3 - var10;
         int var12 = 3 + var7 + var7 % 2;
         if (var8 == var7) {
            var12 = var4 - var10 + var7 % 2;
         }

         int var13 = var0 * var7;
         int var14;
         if (var7 == 0) {
            var14 = var3 == 0 ? var1 : -var1;
         } else {
            var14 = var1 * var12 / 2 - var11 * var1 - var7 % 2 * var1 / 2 - var1 / 2;
         }

         if (var2.dir == 0) {
            return new FollowerPosition(var14, var13);
         } else if (var2.dir == 1) {
            return new FollowerPosition(-var13, var14);
         } else {
            return var2.dir == 2 ? new FollowerPosition(var14, -var13) : new FollowerPosition(var13, var14);
         }
      });
   }

   FollowPosition(FollowPositionGetter var1, FollowPositionGetter var2) {
      this.getDefaultPoint = var1;
      this.getRelativePos = var2;
   }

   public FollowerPosition getRelativePos(Mob var1, FollowerPosition var2, int var3, int var4) {
      FollowerPosition var5 = this.getRelativePos.getPosition(var1, var3, var4);
      if (var2 == null && var5 == null) {
         return this.getDefaultPoint.getPosition(var1, var3, var4);
      } else {
         return var5 == null ? var2 : var5;
      }
   }
}
