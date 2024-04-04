package necesse.level.gameObject;

import necesse.entity.mobs.summon.MinecartLine;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class TrapTrackObject extends MinecartTrackObject {
   public TrapTrackObject() {
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/traptrack");
      this.endingTexture = GameTexture.fromFile("objects/traptrackending");
   }

   public MinecartLines getMinecartLines(Level var1, int var2, int var3, int var4, float var5, float var6, boolean var7) {
      MinecartLines var8 = new MinecartLines(var2, var3);
      boolean var9 = var1.getObjectID(var2, var3 - 1) == this.getID();
      boolean var10 = var1.getObjectID(var2, var3 + 1) == this.getID();
      boolean var11 = var1.getObjectID(var2 - 1, var3) == this.getID();
      boolean var12 = var1.getObjectID(var2 + 1, var3) == this.getID();
      float var13 = 0.2F;
      boolean var14;
      boolean var15;
      byte var16;
      byte var17;
      if (var4 != 0 && var4 != 2) {
         var14 = false;
         var15 = false;
         if (var9 && var10) {
            var16 = var1.getObjectRotation(var2, var3 - 1);
            var17 = var1.getObjectRotation(var2, var3 + 1);
            if (var16 == 2 && var17 == 0) {
               var14 = true;
            }
         }

         if (var9 && !var14) {
            var16 = var1.getObjectRotation(var2, var3 - 1);
            if (var16 == 2) {
               var8.up = MinecartLine.up(var2, var3);
               var8.up.nextNegative = () -> {
                  return this.getMinecartLines(var1, var2, var3 - 1, var5, var6, false).down;
               };
               var15 = true;
            }
         }

         if (var10 && !var14) {
            var16 = var1.getObjectRotation(var2, var3 + 1);
            if (var16 == 0) {
               var8.down = MinecartLine.down(var2, var3);
               var8.down.nextPositive = () -> {
                  return this.getMinecartLines(var1, var2, var3 + 1, var5, var6, false).up;
               };
               var15 = true;
            }
         }

         if (var11) {
            if (var4 != 1 || !var15) {
               var8.left = MinecartLine.left(var2, var3);
               var8.left.nextNegative = () -> {
                  return this.getMinecartLines(var1, var2 - 1, var3, var5, var6, false).right;
               };
            }
         } else if (var4 == 3 || !var15) {
            var8.left = MinecartLine.leftEnd(var2, var3);
            var8.left.nextNegative = null;
         }

         if (var12) {
            if (var4 != 3 || !var15) {
               var8.right = MinecartLine.right(var2, var3);
               var8.right.nextPositive = () -> {
                  return this.getMinecartLines(var1, var2 + 1, var3, var5, var6, false).left;
               };
            }
         } else if (var4 == 1 || !var15) {
            var8.right = MinecartLine.rightEnd(var2, var3);
            var8.right.nextPositive = null;
         }

         if (var8.left != null && var8.right != null) {
            var8.left.nextPositive = () -> {
               return var8.right;
            };
            var8.right.nextNegative = () -> {
               return var8.left;
            };
         }

         if (var14) {
            var8.up = MinecartLine.up(var2, var3);
            var8.up.nextNegative = () -> {
               return this.getMinecartLines(var1, var2, var3 - 1, var5, var6, false).down;
            };
            var8.down = MinecartLine.down(var2, var3);
            var8.down.nextPositive = () -> {
               return this.getMinecartLines(var1, var2, var3 + 1, var5, var6, false).up;
            };
            var8.up.nextPositive = () -> {
               return var8.down;
            };
            var8.down.nextNegative = () -> {
               return var8.up;
            };
         } else if (var8.down != null) {
            var8.down.nextPositive = () -> {
               return this.getMinecartLines(var1, var2, var3 + 1, var5, var6, false).up;
            };
            if (var4 == 1) {
               if (var8.left == null || var6 > var13 && !var7) {
                  var8.right.nextNegative = () -> {
                     return var8.down;
                  };
               }

               var8.down.nextNegative = () -> {
                  return var8.right;
               };
            } else {
               if ((var8.right == null || var6 > var13 && !var7) && var8.left != null) {
                  var8.left.nextPositive = () -> {
                     return var8.down;
                  };
               }

               var8.down.nextNegative = () -> {
                  return var8.left;
               };
            }
         } else if (var8.up != null) {
            var8.up.nextNegative = () -> {
               return this.getMinecartLines(var1, var2, var3 - 1, var5, var6, false).down;
            };
            if (var4 == 1) {
               if (var8.left == null || var6 < -var13) {
                  var8.right.nextNegative = () -> {
                     return var8.up;
                  };
               }

               var8.up.nextPositive = () -> {
                  return var8.right;
               };
            } else {
               if ((var8.right == null || var6 < -var13) && var8.left != null) {
                  var8.left.nextPositive = () -> {
                     return var8.up;
                  };
               }

               var8.up.nextPositive = () -> {
                  return var8.left;
               };
            }
         }
      } else {
         var14 = false;
         var15 = false;
         if (var11 && var12) {
            var16 = var1.getObjectRotation(var2 - 1, var3);
            var17 = var1.getObjectRotation(var2 + 1, var3);
            if (var16 == 1 && var17 == 3) {
               var14 = true;
            }
         }

         if (var11 && !var14) {
            var16 = var1.getObjectRotation(var2 - 1, var3);
            if (var16 == 1) {
               var8.left = MinecartLine.left(var2, var3);
               var8.left.nextNegative = () -> {
                  return this.getMinecartLines(var1, var2 - 1, var3, var5, var6, false).right;
               };
               var15 = true;
            }
         }

         if (var12 && !var14) {
            var16 = var1.getObjectRotation(var2 + 1, var3);
            if (var16 == 3) {
               var8.right = MinecartLine.right(var2, var3);
               var8.right.nextPositive = () -> {
                  return this.getMinecartLines(var1, var2 + 1, var3, var5, var6, false).left;
               };
               var15 = true;
            }
         }

         if (var9) {
            if (var4 != 2 || !var15) {
               var8.up = MinecartLine.up(var2, var3);
               var8.up.nextNegative = () -> {
                  return this.getMinecartLines(var1, var2, var3 - 1, var5, var6, false).down;
               };
            }
         } else if (var4 == 0 || !var15) {
            var8.up = MinecartLine.upEnd(var2, var3);
            var8.up.nextNegative = null;
         }

         if (var10) {
            if (var4 != 0 || !var15) {
               var8.down = MinecartLine.down(var2, var3);
               var8.down.nextPositive = () -> {
                  return this.getMinecartLines(var1, var2, var3 + 1, var5, var6, false).up;
               };
            }
         } else if (var4 == 2 || !var15) {
            var8.down = MinecartLine.downEnd(var2, var3);
            var8.down.nextPositive = null;
         }

         if (var8.up != null && var8.down != null) {
            var8.up.nextPositive = () -> {
               return var8.down;
            };
            var8.down.nextNegative = () -> {
               return var8.up;
            };
         }

         if (var14) {
            var8.left = MinecartLine.left(var2, var3);
            var8.left.nextNegative = () -> {
               return this.getMinecartLines(var1, var2 - 1, var3, var5, var6, false).right;
            };
            var8.right = MinecartLine.right(var2, var3);
            var8.right.nextPositive = () -> {
               return this.getMinecartLines(var1, var2 + 1, var3, var5, var6, false).left;
            };
            var8.left.nextPositive = () -> {
               return var8.right;
            };
            var8.right.nextNegative = () -> {
               return var8.left;
            };
         } else if (var8.right != null) {
            var8.right.nextPositive = () -> {
               return this.getMinecartLines(var1, var2 + 1, var3, var5, var6, false).left;
            };
            if (var4 == 2) {
               if (var8.up == null || var5 > var13) {
                  var8.down.nextNegative = () -> {
                     return var8.right;
                  };
               }

               var8.right.nextNegative = () -> {
                  return var8.down;
               };
            } else {
               if ((var8.down == null || var5 > var13) && var8.up != null) {
                  var8.up.nextPositive = () -> {
                     return var8.right;
                  };
               }

               var8.right.nextNegative = () -> {
                  return var8.up;
               };
            }
         } else if (var8.left != null) {
            var8.left.nextNegative = () -> {
               return this.getMinecartLines(var1, var2 - 1, var3, var5, var6, false).right;
            };
            if (var4 == 2) {
               if (var8.up == null || var5 < -var13) {
                  var8.down.nextNegative = () -> {
                     return var8.left;
                  };
               }

               var8.left.nextPositive = () -> {
                  return var8.down;
               };
            } else {
               if ((var8.down == null || var5 < -var13) && var8.up != null) {
                  var8.up.nextPositive = () -> {
                     return var8.left;
                  };
               }

               var8.left.nextPositive = () -> {
                  return var8.up;
               };
            }
         }
      }

      return var8;
   }
}
