package necesse.entity.mobs.summon;

import java.awt.geom.Line2D;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MinecartLine extends Line2D.Float {
   public final int tileX;
   public final int tileY;
   public final int dir;
   public final float distance;
   public Supplier<MinecartLine> nextPositive;
   public Supplier<MinecartLine> nextNegative;

   protected MinecartLine(int var1, int var2, float var3, float var4, float var5, float var6, int var7, float var8) {
      super((float)(var1 * 32) + var3, (float)(var2 * 32) + var4, (float)(var1 * 32) + var5, (float)(var2 * 32) + var6);
      this.tileX = var1;
      this.tileY = var2;
      this.dir = var7;
      this.distance = var8;
   }

   public static MinecartLine up(int var0, int var1) {
      return new MinecartLine(var0, var1, 16.0F, 0.0F, 16.0F, 16.0F, 0, 16.0F);
   }

   public static MinecartLine down(int var0, int var1) {
      return new MinecartLine(var0, var1, 16.0F, 16.0F, 16.0F, 32.0F, 2, 16.0F);
   }

   public static MinecartLine left(int var0, int var1) {
      return new MinecartLine(var0, var1, 0.0F, 16.0F, 16.0F, 16.0F, 3, 16.0F);
   }

   public static MinecartLine right(int var0, int var1) {
      return new MinecartLine(var0, var1, 16.0F, 16.0F, 32.0F, 16.0F, 1, 16.0F);
   }

   public static MinecartLine upEnd(int var0, int var1) {
      return new MinecartLine(var0, var1, 16.0F, 14.0F, 16.0F, 16.0F, 0, 2.0F);
   }

   public static MinecartLine downEnd(int var0, int var1) {
      return new MinecartLine(var0, var1, 16.0F, 16.0F, 16.0F, 26.0F, 2, 10.0F);
   }

   public static MinecartLine leftEnd(int var0, int var1) {
      return new MinecartLine(var0, var1, 10.0F, 16.0F, 16.0F, 16.0F, 3, 6.0F);
   }

   public static MinecartLine rightEnd(int var0, int var1) {
      return new MinecartLine(var0, var1, 16.0F, 16.0F, 22.0F, 16.0F, 1, 6.0F);
   }

   public MinecartLinePos progressLines(MinecartLine var1, int var2, float var3, float var4, Consumer<MinecartLine> var5) {
      while(true) {
         if (var5 != null) {
            var5.accept(var1);
         }

         MinecartLine var7;
         if (var2 == 0) {
            if (!(var4 > var3)) {
               if (var1.dir != 1 && var1.dir != 3) {
                  return new MinecartLinePos(var1, var1.x1, var1.y1 + var3 - var4, var3 - var4, var2);
               }

               return new MinecartLinePos(var1, var1.x1 + var3 - var4, var1.y1, var3 - var4, 1);
            }

            var4 -= var3;
            var3 = 0.0F;
            if (var1.nextNegative != null) {
               var7 = (MinecartLine)var1.nextNegative.get();
               if (var7 != null) {
                  var3 = var7.distance;
                  var1 = var7;
                  if (var7.dir == 1) {
                     var2 = var7.dir;
                     var3 = 0.0F;
                     continue;
                  }

                  if (var7.dir == 3) {
                     var2 = var7.dir;
                     var3 = var7.distance;
                  }
                  continue;
               }
            }
         } else {
            float var6;
            if (var2 == 1) {
               var6 = var1.distance - var3;
               if (!(var4 > var6)) {
                  if (var1.dir != 1 && var1.dir != 3) {
                     return new MinecartLinePos(var1, var1.x1, var1.y1 + var3 + var4, var3 + var4, 0);
                  }

                  return new MinecartLinePos(var1, var1.x1 + var3 + var4, var1.y1, var3 + var4, var2);
               }

               var4 -= var6;
               var3 = var1.distance;
               if (var1.nextPositive != null) {
                  var7 = (MinecartLine)var1.nextPositive.get();
                  if (var7 != null) {
                     var3 = 0.0F;
                     var1 = var7;
                     if (var7.dir == 0) {
                        var2 = var7.dir;
                        var3 = var7.distance;
                        continue;
                     }

                     if (var7.dir == 2) {
                        var2 = var7.dir;
                        var3 = 0.0F;
                     }
                     continue;
                  }
               }
            } else if (var2 == 2) {
               var6 = var1.distance - var3;
               if (!(var4 > var6)) {
                  if (var1.dir != 1 && var1.dir != 3) {
                     return new MinecartLinePos(var1, var1.x1, var1.y1 + var3 + var4, var3 + var4, var2);
                  }

                  return new MinecartLinePos(var1, var1.x1 + var3 + var4, var1.y1, var3 + var4, 3);
               }

               var4 -= var6;
               var3 = var1.distance;
               if (var1.nextPositive != null) {
                  var7 = (MinecartLine)var1.nextPositive.get();
                  if (var7 != null) {
                     var3 = 0.0F;
                     var1 = var7;
                     if (var7.dir == 1) {
                        var2 = var7.dir;
                        var3 = 0.0F;
                        continue;
                     }

                     if (var7.dir == 3) {
                        var2 = var7.dir;
                        var3 = var7.distance;
                     }
                     continue;
                  }
               }
            } else {
               if (!(var4 > var3)) {
                  if (var1.dir != 1 && var1.dir != 3) {
                     return new MinecartLinePos(var1, var1.x1, var1.y1 + var3 - var4, var3 - var4, 2);
                  }

                  return new MinecartLinePos(var1, var1.x1 + var3 - var4, var1.y1, var3 - var4, var2);
               }

               var4 -= var3;
               var3 = 0.0F;
               if (var1.nextNegative != null) {
                  var7 = (MinecartLine)var1.nextNegative.get();
                  if (var7 != null) {
                     var3 = var7.distance;
                     var1 = var7;
                     if (var7.dir == 0) {
                        var2 = var7.dir;
                        var3 = var7.distance;
                        continue;
                     }

                     if (var7.dir == 2) {
                        var2 = var7.dir;
                        var3 = 0.0F;
                     }
                     continue;
                  }
               }
            }
         }

         if (var1.dir == 0) {
            return new MinecartLinePos(var1, var1.x1, var1.y1 + 0.1F, 0.1F, var2, var4);
         }

         if (var1.dir == 1) {
            return new MinecartLinePos(var1, var1.x1 + var1.distance - 0.1F, var1.y1, var1.distance - 0.1F, var2, var4);
         }

         if (var1.dir == 2) {
            return new MinecartLinePos(var1, var1.x1, var1.y1 + var1.distance - 0.1F, var1.distance - 0.1F, var2, var4);
         }

         return new MinecartLinePos(var1, var1.x1 + 0.1F, var1.y1, 0.1F, var2, var4);
      }
   }

   public MinecartLinePos progressLines(MinecartLine var1, boolean var2, float var3, float var4, Consumer<MinecartLine> var5) {
      while(true) {
         if (var5 != null) {
            var5.accept(var1);
         }

         float var6;
         if (var2) {
            var6 = var1.distance - var3;
            if (!(var4 > var6)) {
               if (var1.dir != 1 && var1.dir != 3) {
                  return new MinecartLinePos(var1, var1.x1, var1.y1 + var3 + var4, var3 + var4, 2);
               }

               return new MinecartLinePos(var1, var1.x1 + var3 + var4, var1.y1, var3 + var4, 1);
            }

            var4 -= var6;
         } else {
            var6 = var1.distance;
            if (!(var4 > var6)) {
               if (var1.dir != 1 && var1.dir != 3) {
                  return new MinecartLinePos(var1, var1.x1, var1.y1 + var3 - var4, var3 - var4, 0);
               }

               return new MinecartLinePos(var1, var1.x1 + var3 - var4, var1.y1, var3 - var4, 3);
            }

            var4 -= var6;
         }

         MinecartLine var7;
         if (var2) {
            if (var1.nextPositive != null) {
               var7 = (MinecartLine)var1.nextPositive.get();
               if (var7 != null) {
                  if ((var1.dir != 3 || var7.dir != 0) && (var1.dir != 0 || var7.dir != 3)) {
                     var3 = 0.0F;
                  } else {
                     var2 = false;
                     var3 = var7.distance;
                  }

                  var1 = var7;
                  continue;
               }
            }
         } else if (var1.nextNegative != null) {
            var7 = (MinecartLine)var1.nextNegative.get();
            if (var7 != null) {
               if ((var1.dir != 1 || var7.dir != 2) && (var1.dir != 2 || var7.dir != 1)) {
                  var3 = var7.distance;
               } else {
                  var2 = true;
                  var3 = 0.0F;
               }

               var1 = var7;
               continue;
            }
         }

         if (var2) {
            if (var1.dir != 1 && var1.dir != 3) {
               return new MinecartLinePos(var1, var1.x1, var1.y1 + var1.distance, var1.distance, 2, var4);
            }

            return new MinecartLinePos(var1, var1.x1 + var1.distance, var1.y1, var1.distance, 1, var4);
         }

         if (var1.dir != 1 && var1.dir != 3) {
            return new MinecartLinePos(var1, var1.x1, var1.y1, 0.0F, 0, var4);
         }

         return new MinecartLinePos(var1, var1.x1, var1.y1, 0.0F, 3, var4);
      }
   }
}
