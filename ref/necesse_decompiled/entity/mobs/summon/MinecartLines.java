package necesse.entity.mobs.summon;

import necesse.engine.util.GameMath;

public class MinecartLines {
   public int tileX;
   public int tileY;
   public MinecartLine up;
   public MinecartLine right;
   public MinecartLine down;
   public MinecartLine left;

   public MinecartLines(int var1, int var2) {
      this.tileX = var1;
      this.tileY = var2;
   }

   public MinecartLinePos getMinecartPos(float var1, float var2, int var3) {
      if (var3 == 0) {
         return this.bestPos(var1, var2, var3, this.up, this.right, this.left, this.down);
      } else if (var3 == 1) {
         return this.bestPos(var1, var2, var3, this.right, this.up, this.down, this.left);
      } else {
         return var3 == 2 ? this.bestPos(var1, var2, var3, this.down, this.right, this.left, this.up) : this.bestPos(var1, var2, var3, this.left, this.up, this.down, this.right);
      }
   }

   private MinecartLinePos bestPos(float var1, float var2, int var3, MinecartLine... var4) {
      MinecartLinePos var5 = null;
      float var6 = 0.0F;
      MinecartLine[] var7 = var4;
      int var8 = var4.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         MinecartLine var10 = var7[var9];
         if (var10 != null) {
            MinecartLinePos var11 = this.linePos(var10, var1, var2, var3);
            float var12 = GameMath.diamondDistance(var11.x, var11.y, var1, var2);
            if (var5 == null || var12 < var6) {
               var5 = var11;
               var6 = var12;
            }
         }
      }

      return var5;
   }

   private MinecartLinePos linePos(MinecartLine var1, float var2, float var3, int var4) {
      int var5;
      float var6;
      if (var1.dir != 1 && var1.dir != 3) {
         var5 = var4 != 1 && var4 != 0 ? 2 : 0;
         if (var3 <= var1.y1) {
            var6 = 0.0F;
         } else if (var3 >= var1.y2) {
            var6 = var1.distance;
         } else {
            var6 = var3 - var1.y1;
         }

         if (var4 == 0 || var4 == 2) {
            if (var1.dir == 2 && var6 >= var1.distance) {
               var5 = var1.dir;
            } else if (var1.dir == 0 && var6 <= 0.0F) {
               var5 = var1.dir;
            }
         }

         return new MinecartLinePos(var1, GameMath.limit(var2, var1.x1, var1.x2), GameMath.limit(var3, var1.y1, var1.y2), var6, var5);
      } else {
         var5 = var4 != 1 && var4 != 0 ? 3 : 1;
         if (var2 <= var1.x1) {
            var6 = 0.0F;
         } else if (var2 >= var1.x2) {
            var6 = var1.distance;
         } else {
            var6 = var2 - var1.x1;
         }

         if (var4 == 0 || var4 == 2) {
            if (var1.dir == 3 && var6 >= var1.distance) {
               var5 = var1.dir;
            } else if (var1.dir == 1 && var6 <= 0.0F) {
               var5 = var1.dir;
            }
         }

         return new MinecartLinePos(var1, GameMath.limit(var2, var1.x1, var1.x2), GameMath.limit(var3, var1.y1, var1.y2), var6, var5);
      }
   }
}
