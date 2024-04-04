package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ExpandingPolygon;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;

public class CollisionRectangleGameTool extends MouseDebugGameTool {
   private boolean m1Down;
   private boolean m2Down;
   public Point r1p1;
   public Point r1p2;
   public Point r2p1;
   public Point r2p2;
   public HudDrawElement hudElement;

   public CollisionRectangleGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.onLeftEvent((var1x) -> {
         this.m1Down = var1x.state;
         if (var1x.state) {
            this.r1p1 = new Point(this.getMouseX(), this.getMouseY());
         }

         this.r1p2 = new Point(this.getMouseX(), this.getMouseY());
         return true;
      }, "Select rectangle 1");
      this.onRightEvent((var1x) -> {
         this.m2Down = var1x.state;
         if (var1x.state) {
            this.r2p1 = new Point(this.getMouseX(), this.getMouseY());
         }

         this.r2p2 = new Point(this.getMouseX(), this.getMouseY());
         return true;
      }, "Select rectangle 2");
      this.onMouseMove((var1x) -> {
         if (this.m1Down) {
            this.r1p2 = new Point(this.getMouseX(), this.getMouseY());
         }

         if (this.m2Down) {
            this.r2p2 = new Point(this.getMouseX(), this.getMouseY());
         }

         var1x.useMove();
         return false;
      });
   }

   public void init() {
      this.r1p1 = null;
      this.r1p2 = null;
      this.r2p1 = null;
      this.r2p2 = null;
      final CollisionFilter var1 = (new CollisionFilter()).mobCollision();
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1x, final GameCamera var2, PlayerMob var3) {
            var1x.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1x) {
                  Rectangle var2x = CollisionRectangleGameTool.toRectangle(CollisionRectangleGameTool.this.r1p1, CollisionRectangleGameTool.this.r1p2);
                  Rectangle var3 = CollisionRectangleGameTool.toRectangle(CollisionRectangleGameTool.this.r2p1, CollisionRectangleGameTool.this.r2p2);
                  Object var4 = null;
                  if (var2x != null) {
                     var4 = var2x;
                     Screen.drawShape(var2x, var2, true, 0.0F, 1.0F, 0.0F, 0.5F);
                  }

                  if (var3 != null) {
                     var4 = var3;
                     Screen.drawShape(var3, var2, true, 0.0F, 0.0F, 1.0F, 0.5F);
                  }

                  if (var2x != null && var3 != null) {
                     ExpandingPolygon var5 = new ExpandingPolygon(new Shape[]{var2x, var3});
                     var4 = var5;
                     Rectangle var6 = var5.getBounds();
                     int var7 = (int)var6.getCenterX();
                     int var8 = (int)var6.getCenterY();
                     FontManager.bit.drawString((float)var2.getDrawX(var7), (float)var2.getDrawY(var8), "" + var5.npoints, (new FontOptions(16)).outline());
                  }

                  if (var4 != null) {
                     Screen.drawShape((Shape)var4, var2, false, 1.0F, 1.0F, 0.0F, 1.0F);
                     ArrayList var9 = getLevel().getCollisions((Shape)var4, var1);
                     Iterator var10 = var9.iterator();

                     while(var10.hasNext()) {
                        LevelObjectHit var11 = (LevelObjectHit)var10.next();
                        Screen.drawRectangleLines(var11, var2, 1.0F, 0.0F, 0.0F, 1.0F);
                     }
                  }

               }
            });
         }
      });
   }

   private static Rectangle toRectangle(Point var0, Point var1) {
      return var0 != null && var1 != null ? new Rectangle(Math.min(var0.x, var1.x), Math.min(var0.y, var1.y), Math.abs(var0.x - var1.x), Math.abs(var0.y - var1.y)) : null;
   }

   public void isCancelled() {
      super.isCancelled();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }
}
