package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.MinecartLine;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MinecartTrackObject extends GameObject {
   public GameTexture texture;
   public GameTexture endingTexture;
   public GameTexture supportTexture;
   public GameTexture bridgeTexture;
   protected final GameRandom drawRandom;

   public MinecartTrackObject() {
      this.mapColor = new Color(84, 67, 41);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
      this.canPlaceOnShore = true;
      this.canPlaceOnLiquid = true;
      this.overridesInLiquid = true;
      this.stackSize = 250;
      this.replaceCategories.add("minecarttrack");
      this.canReplaceCategories.add("minecarttrack");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/minecarttrack");
      this.endingTexture = GameTexture.fromFile("objects/minecarttrackending");
      this.supportTexture = GameTexture.fromFile("objects/minecarttracksupport");
      this.bridgeTexture = GameTexture.fromFile("objects/minecarttrackbridge");
   }

   public TrackSprite getSprite(Level var1, int var2, int var3, int var4) {
      TrackSprite var5 = new TrackSprite();
      MinecartLines var6 = this.getMinecartLines(var1, var2, var3, var4, 0.0F, 0.0F, false);
      MinecartLine var7;
      if (var6.up != null) {
         var5.goingUp();
         var7 = var6.up.nextNegative != null ? (MinecartLine)var6.up.nextNegative.get() : null;
         if (var7 == null) {
            var5.connectedUp = false;
         }
      }

      if (var6.right != null) {
         var5.goingRight();
         var7 = var6.right.nextPositive != null ? (MinecartLine)var6.right.nextPositive.get() : null;
         if (var7 == null) {
            var5.connectedRight = false;
         }
      }

      if (var6.down != null) {
         var5.goingDown();
         var7 = var6.down.nextPositive != null ? (MinecartLine)var6.down.nextPositive.get() : null;
         if (var7 == null) {
            var5.connectedDown = false;
         }
      }

      if (var6.left != null) {
         var5.goingLeft();
         var7 = var6.left.nextNegative != null ? (MinecartLine)var6.left.nextNegative.get() : null;
         if (var7 == null) {
            var5.connectedLeft = false;
         }
      }

      switch (var4) {
         case 0:
            if (var5.connectedLeft && var5.connectedRight) {
               return var5.sprite(4, 0);
            } else if (var5.connectedLeft && var5.connectedDown) {
               return var5.sprite(3, 3);
            } else if (var5.connectedRight && var5.connectedDown) {
               return var5.sprite(2, 3);
            } else if (var5.connectedLeft) {
               return var5.sprite(3, 1);
            } else {
               if (var5.connectedRight) {
                  return var5.sprite(2, 1);
               }

               return var5.sprite(1, 0);
            }
         case 1:
            if (var5.connectedUp && var5.connectedDown) {
               return var5.sprite(4, 0);
            } else if (var5.connectedUp && var5.connectedLeft) {
               return var5.sprite(0, 3);
            } else if (var5.connectedDown && var5.connectedLeft) {
               return var5.sprite(0, 2);
            } else if (var5.connectedUp) {
               return var5.sprite(2, 1);
            } else {
               if (var5.connectedDown) {
                  return var5.sprite(2, 0);
               }

               return var5.sprite(0, 0);
            }
         case 2:
            if (var5.connectedLeft && var5.connectedRight) {
               return var5.sprite(4, 0);
            } else if (var5.connectedLeft && var5.connectedUp) {
               return var5.sprite(3, 2);
            } else if (var5.connectedRight && var5.connectedUp) {
               return var5.sprite(2, 2);
            } else if (var5.connectedLeft) {
               return var5.sprite(3, 0);
            } else {
               if (var5.connectedRight) {
                  return var5.sprite(2, 0);
               }

               return var5.sprite(1, 0);
            }
         case 3:
            if (var5.connectedUp && var5.connectedDown) {
               return var5.sprite(4, 0);
            } else if (var5.connectedUp && var5.connectedRight) {
               return var5.sprite(1, 3);
            } else if (var5.connectedDown && var5.connectedRight) {
               return var5.sprite(1, 2);
            } else if (var5.connectedUp) {
               return var5.sprite(3, 1);
            } else {
               if (var5.connectedDown) {
                  return var5.sprite(3, 0);
               }

               return var5.sprite(0, 0);
            }
         default:
            return var5.sprite(0, 0);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      byte var9 = var3.getObjectRotation(var4, var5);
      GameLight var10 = var3.getLightLevel(var4, var5);
      int var11 = var7.getTileDrawX(var4);
      int var12 = var7.getTileDrawY(var5);
      DrawOptionsList var13 = new DrawOptionsList();
      TrackSprite var14 = this.getSprite(var3, var4, var5, var9);
      if (var3.isLiquidTile(var4, var5) || var3.isShore(var4, var5)) {
         if ((var3.isLiquidTile(var4, var5 + 1) || var3.isShore(var4, var5 + 1)) && (!var14.connectedDown || var14.connectedLeft || var14.connectedRight)) {
            TextureDrawOptionsEnd var15 = this.bridgeTexture.initDraw().sprite(var14.x, var14.y, 32).light(var10).pos(var11, var12 + 8);
            var2.add(-100, (var1x) -> {
               var15.draw();
            });
         }

         var13.add(this.supportTexture.initDraw().sprite(var14.x, var14.y, 32).light(var10).pos(var11, var12));
      }

      var13.add(this.texture.initDraw().sprite(var14.x, var14.y, 32).light(var10).pos(var11, var12));
      if (var14.goingUp && !var14.connectedUp) {
         var13.add(this.endingTexture.initDraw().sprite(0, 0, 32).light(var10).pos(var11, var12));
      }

      if (var14.goingRight && !var14.connectedRight) {
         var13.add(this.endingTexture.initDraw().sprite(0, 1, 32).light(var10).pos(var11, var12));
      }

      if (var14.goingDown && !var14.connectedDown) {
         var13.add(this.endingTexture.initDraw().sprite(0, 2, 32).light(var10).pos(var11, var12));
      }

      if (var14.goingLeft && !var14.connectedLeft) {
         var13.add(this.endingTexture.initDraw().sprite(0, 3, 32).light(var10).pos(var11, var12));
      }

      var2.add((var1x) -> {
         var13.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      TrackSprite var10 = this.getSprite(var1, var2, var3, var4);
      if (var1.isLiquidTile(var2, var3) || var1.isShore(var2, var3)) {
         if ((var1.isLiquidTile(var2, var3 + 1) || var1.isShore(var2, var3 + 1)) && (!var10.connectedDown || var10.connectedLeft || var10.connectedRight)) {
            this.bridgeTexture.initDraw().sprite(var10.x, var10.y, 32).alpha(var5).draw(var8, var9 + 8);
         }

         this.supportTexture.initDraw().sprite(var10.x, var10.y, 32).alpha(var5).draw(var8, var9);
      }

      this.texture.initDraw().sprite(var10.x, var10.y, 32).alpha(var5).draw(var8, var9);
      if (var10.goingUp && !var10.connectedUp) {
         this.endingTexture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9);
      }

      if (var10.goingRight && !var10.connectedRight) {
         this.endingTexture.initDraw().sprite(0, 1, 32).alpha(var5).draw(var8, var9);
      }

      if (var10.goingDown && !var10.connectedDown) {
         this.endingTexture.initDraw().sprite(0, 2, 32).alpha(var5).draw(var8, var9);
      }

      if (var10.goingLeft && !var10.connectedLeft) {
         this.endingTexture.initDraw().sprite(0, 3, 32).alpha(var5).draw(var8, var9);
      }

   }

   public MinecartLines getMinecartLines(Level var1, int var2, int var3, float var4, float var5, boolean var6) {
      byte var7 = var1.getObjectRotation(var2, var3);
      return this.getMinecartLines(var1, var2, var3, var7, var4, var5, var6);
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
      boolean var17;
      byte var18;
      if (var4 != 0 && var4 != 2) {
         var14 = false;
         var15 = false;
         if (var9) {
            var16 = var1.getObjectRotation(var2, var3 - 1);
            if (var16 == 0 || var16 == 2) {
               var17 = true;
               if (var10) {
                  var18 = var1.getObjectRotation(var2, var3 + 1);
                  if (var18 == 0 || var18 == 2) {
                     var17 = false;
                  }
               }

               if (var17) {
                  var8.up = MinecartLine.up(var2, var3);
                  var8.up.nextNegative = () -> {
                     return this.getMinecartLines(var1, var2, var3 - 1, var5, var6, false).down;
                  };
                  var15 = true;
               } else {
                  var14 = true;
               }
            }
         }

         if (var10) {
            var16 = var1.getObjectRotation(var2, var3 + 1);
            if (var16 == 0 || var16 == 2) {
               var17 = true;
               if (var9) {
                  var18 = var1.getObjectRotation(var2, var3 - 1);
                  if (var18 == 0 || var18 == 2) {
                     var17 = false;
                  }
               }

               if (var17) {
                  var8.down = MinecartLine.down(var2, var3);
                  var8.down.nextPositive = () -> {
                     return this.getMinecartLines(var1, var2, var3 + 1, var5, var6, false).up;
                  };
                  var15 = true;
               } else {
                  var14 = true;
               }
            }
         }

         if (var11) {
            var8.left = MinecartLine.left(var2, var3);
            var8.left.nextNegative = () -> {
               return this.getMinecartLines(var1, var2 - 1, var3, var5, var6, false).right;
            };
         } else if (var4 == 3 || !var15) {
            var8.left = MinecartLine.leftEnd(var2, var3);
            var8.left.nextNegative = null;
         }

         if (var12) {
            var8.right = MinecartLine.right(var2, var3);
            var8.right.nextPositive = () -> {
               return this.getMinecartLines(var1, var2 + 1, var3, var5, var6, false).left;
            };
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
               if (var8.left == null || var6 > var13) {
                  var8.right.nextNegative = () -> {
                     return var8.down;
                  };
               }

               var8.down.nextNegative = () -> {
                  return var8.right;
               };
            } else {
               if ((var8.right == null || var6 > var13) && var8.left != null) {
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
         if (var11) {
            var16 = var1.getObjectRotation(var2 - 1, var3);
            if (var16 == 1 || var16 == 3) {
               var17 = true;
               if (var12) {
                  var18 = var1.getObjectRotation(var2 + 1, var3);
                  if (var18 == 1 || var18 == 3) {
                     var17 = false;
                  }
               }

               if (var17) {
                  var8.left = MinecartLine.left(var2, var3);
                  var8.left.nextNegative = () -> {
                     return this.getMinecartLines(var1, var2 - 1, var3, var5, var6, false).right;
                  };
                  var15 = true;
               } else {
                  var14 = true;
               }
            }
         }

         if (var12) {
            var16 = var1.getObjectRotation(var2 + 1, var3);
            if (var16 == 1 || var16 == 3) {
               var17 = true;
               if (var11) {
                  var18 = var1.getObjectRotation(var2 - 1, var3);
                  if (var18 == 1 || var18 == 3) {
                     var17 = false;
                  }
               }

               if (var17) {
                  var8.right = MinecartLine.right(var2, var3);
                  var8.right.nextPositive = () -> {
                     return this.getMinecartLines(var1, var2 + 1, var3, var5, var6, false).left;
                  };
                  var15 = true;
               } else {
                  var14 = true;
               }
            }
         }

         if (var9) {
            var8.up = MinecartLine.up(var2, var3);
            var8.up.nextNegative = () -> {
               return this.getMinecartLines(var1, var2, var3 - 1, var5, var6, false).down;
            };
         } else if (var4 == 0 || !var15) {
            var8.up = MinecartLine.upEnd(var2, var3);
            var8.up.nextNegative = null;
         }

         if (var10) {
            var8.down = MinecartLine.down(var2, var3);
            var8.down.nextPositive = () -> {
               return this.getMinecartLines(var1, var2, var3 + 1, var5, var6, false).up;
            };
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
               if (var8.up == null || var5 > var13 && !var7) {
                  var8.down.nextNegative = () -> {
                     return var8.right;
                  };
               }

               var8.right.nextNegative = () -> {
                  return var8.down;
               };
            } else {
               if ((var8.down == null || var5 > var13 && !var7) && var8.up != null) {
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

   protected static class TrackSprite {
      public int x;
      public int y;
      public boolean goingUp;
      public boolean goingRight;
      public boolean goingDown;
      public boolean goingLeft;
      public boolean connectedUp;
      public boolean connectedRight;
      public boolean connectedDown;
      public boolean connectedLeft;

      protected TrackSprite() {
      }

      public void goingUp() {
         this.goingUp = true;
         this.connectedUp = true;
      }

      public void goingRight() {
         this.goingRight = true;
         this.connectedRight = true;
      }

      public void goingDown() {
         this.goingDown = true;
         this.connectedDown = true;
      }

      public void goingLeft() {
         this.goingLeft = true;
         this.connectedLeft = true;
      }

      public TrackSprite sprite(int var1, int var2) {
         this.x = var1;
         this.y = var2;
         return this;
      }
   }
}
