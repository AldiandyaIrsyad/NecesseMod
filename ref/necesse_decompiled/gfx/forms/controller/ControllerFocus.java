package necesse.gfx.forms.controller;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.List;
import necesse.engine.util.GameMath;
import necesse.gfx.forms.FormManager;

public class ControllerFocus {
   public final ControllerFocusHandler handler;
   public final Rectangle boundingBox;
   public final int initialFocusPriority;
   public final ControllerNavigationHandler customNavigationHandler;
   private Point boundingBoxCenter;

   public static void add(List<ControllerFocus> var0, Rectangle var1, ControllerFocusHandler var2, Rectangle var3, int var4, int var5, int var6, ControllerNavigationHandler var7) {
      if (var1.intersects(new Rectangle(var3.x + var4, var3.y + var5, var3.width, var3.height))) {
         var0.add(new ControllerFocus(var2, var3, var4, var5, var6, var7));
      }

   }

   public ControllerFocus(ControllerFocusHandler var1, Rectangle var2, int var3, int var4, int var5, ControllerNavigationHandler var6) {
      this.handler = var1;
      this.boundingBox = new Rectangle(var3 + var2.x, var4 + var2.y, var2.width, var2.height);
      this.initialFocusPriority = var5;
      this.customNavigationHandler = var6;
   }

   public ControllerFocus(ControllerFocus var1, int var2, int var3) {
      this(var1.handler, var1.boundingBox, var2, var3, var1.initialFocusPriority, var1.customNavigationHandler);
   }

   public Point getTooltipAndFloatMenuPoint() {
      Point var1 = this.handler.getControllerTooltipAndFloatMenuPoint(this);
      if (var1 == null) {
         var1 = new Point(this.boundingBox.x, this.boundingBox.y);
      }

      return var1;
   }

   public Point getBoundingBoxCenter() {
      if (this.boundingBoxCenter != null) {
         return this.boundingBoxCenter;
      } else {
         this.boundingBoxCenter = new Point(this.boundingBox.x + this.boundingBox.width / 2, this.boundingBox.y + this.boundingBox.height / 2);
         return this.boundingBoxCenter;
      }
   }

   public static ControllerFocus getNext(int var0, FormManager var1, List<ControllerFocus> var2) {
      ControllerFocus var3 = var1.getCurrentFocus();
      Comparator var4;
      if (var3 == null) {
         var4 = Comparator.comparingInt((var0x) -> {
            return -var0x.initialFocusPriority;
         });
      } else {
         var4 = Comparator.comparingInt((var2x) -> {
            if (var3.handler == var2x.handler) {
               return Integer.MAX_VALUE;
            } else {
               int var5 = 0;
               int var3x;
               int var4;
               switch (var0) {
                  case 0:
                     if (var2x.boundingBox.y < var3.boundingBox.y && var2x.boundingBox.y + var2x.boundingBox.height < var3.boundingBox.y + var3.boundingBox.height) {
                        var3x = var3.boundingBox.y - (var2x.boundingBox.y + var2x.boundingBox.height);
                        if (var3.boundingBox.x + var3.boundingBox.width < var2x.boundingBox.x) {
                           var4 = var2x.boundingBox.x - (var3.boundingBox.x + var3.boundingBox.width);
                        } else if (var3.boundingBox.x > var2x.boundingBox.x + var2x.boundingBox.width) {
                           var4 = var3.boundingBox.x - (var2x.boundingBox.x + var2x.boundingBox.width);
                        } else {
                           var4 = 0;
                           if (var3.boundingBox.x < var2x.boundingBox.x) {
                              var5 = var3.boundingBox.x + var3.boundingBox.width - var2x.boundingBox.x;
                           } else {
                              var5 = var2x.boundingBox.x + var2x.boundingBox.width - var3.boundingBox.x;
                           }

                           var5 = GameMath.min(var2x.boundingBox.width, var3.boundingBox.width, var5);
                        }
                        break;
                     }

                     return Integer.MAX_VALUE;
                  case 1:
                     if (var2x.boundingBox.x > var3.boundingBox.x && var2x.boundingBox.x + var2x.boundingBox.width > var3.boundingBox.x + var3.boundingBox.width) {
                        var3x = var2x.boundingBox.x - (var3.boundingBox.x + var3.boundingBox.width);
                        if (var3.boundingBox.y + var3.boundingBox.height < var2x.boundingBox.y) {
                           var4 = var2x.boundingBox.y - (var3.boundingBox.y + var3.boundingBox.height);
                        } else if (var3.boundingBox.y > var2x.boundingBox.y + var2x.boundingBox.height) {
                           var4 = var3.boundingBox.y - (var2x.boundingBox.y + var2x.boundingBox.height);
                        } else {
                           var4 = 0;
                           if (var3.boundingBox.y < var2x.boundingBox.y) {
                              var5 = var3.boundingBox.y + var3.boundingBox.height - var2x.boundingBox.y;
                           } else {
                              var5 = var2x.boundingBox.y + var2x.boundingBox.height - var3.boundingBox.y;
                           }

                           var5 = GameMath.min(var2x.boundingBox.height, var3.boundingBox.height, var5);
                        }
                        break;
                     }

                     return Integer.MAX_VALUE;
                  case 2:
                     if (var2x.boundingBox.y > var3.boundingBox.y && var2x.boundingBox.y + var2x.boundingBox.height > var3.boundingBox.y + var3.boundingBox.height) {
                        var3x = var2x.boundingBox.y - (var3.boundingBox.y + var3.boundingBox.height);
                        if (var3.boundingBox.x + var3.boundingBox.width < var2x.boundingBox.x) {
                           var4 = var2x.boundingBox.x - (var3.boundingBox.x + var3.boundingBox.width);
                        } else if (var3.boundingBox.x > var2x.boundingBox.x + var2x.boundingBox.width) {
                           var4 = var3.boundingBox.x - (var2x.boundingBox.x + var2x.boundingBox.width);
                        } else {
                           var4 = 0;
                           if (var3.boundingBox.x < var2x.boundingBox.x) {
                              var5 = var3.boundingBox.x + var3.boundingBox.width - var2x.boundingBox.x;
                           } else {
                              var5 = var2x.boundingBox.x + var2x.boundingBox.width - var3.boundingBox.x;
                           }

                           var5 = GameMath.min(var2x.boundingBox.width, var3.boundingBox.width, var5);
                        }
                        break;
                     }

                     return Integer.MAX_VALUE;
                  case 3:
                     if (var2x.boundingBox.x < var3.boundingBox.x && var2x.boundingBox.x + var2x.boundingBox.width < var3.boundingBox.x + var3.boundingBox.width) {
                        var3x = var3.boundingBox.x - (var2x.boundingBox.x + var2x.boundingBox.width);
                        if (var3.boundingBox.y + var3.boundingBox.height < var2x.boundingBox.y) {
                           var4 = var2x.boundingBox.y - (var3.boundingBox.y + var3.boundingBox.height);
                        } else if (var3.boundingBox.y > var2x.boundingBox.y + var2x.boundingBox.height) {
                           var4 = var3.boundingBox.y - (var2x.boundingBox.y + var2x.boundingBox.height);
                        } else {
                           var4 = 0;
                           if (var3.boundingBox.y < var2x.boundingBox.y) {
                              var5 = var3.boundingBox.y + var3.boundingBox.height - var2x.boundingBox.y;
                           } else {
                              var5 = var2x.boundingBox.y + var2x.boundingBox.height - var3.boundingBox.y;
                           }

                           var5 = GameMath.min(var2x.boundingBox.height, var3.boundingBox.height, var5);
                        }
                        break;
                     }

                     return Integer.MAX_VALUE;
                  default:
                     return Integer.MAX_VALUE;
               }

               return Math.max(var3x, 0) + Math.max(var4, 0) - var5 / 4;
            }
         });
      }

      ControllerFocus var5 = (ControllerFocus)var2.stream().min(var4).orElse((Object)null);
      if (var5 != null && var3 != null) {
         switch (var0) {
            case 0:
               if (var5.boundingBox.y >= var3.boundingBox.y || var5.boundingBox.y + var5.boundingBox.height >= var3.boundingBox.y + var3.boundingBox.height) {
                  var5 = null;
               }
               break;
            case 1:
               if (var5.boundingBox.x <= var3.boundingBox.x || var5.boundingBox.x + var5.boundingBox.width <= var3.boundingBox.x + var3.boundingBox.width) {
                  var5 = null;
               }
               break;
            case 2:
               if (var5.boundingBox.y <= var3.boundingBox.y || var5.boundingBox.y + var5.boundingBox.height <= var3.boundingBox.y + var3.boundingBox.height) {
                  var5 = null;
               }
               break;
            case 3:
               if (var5.boundingBox.x >= var3.boundingBox.x || var5.boundingBox.x + var5.boundingBox.width >= var3.boundingBox.x + var3.boundingBox.width) {
                  var5 = null;
               }
         }
      }

      return var5;
   }
}
