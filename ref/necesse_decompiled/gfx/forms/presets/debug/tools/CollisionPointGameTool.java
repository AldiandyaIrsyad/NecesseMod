package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;

public class CollisionPointGameTool extends MouseDebugGameTool {
   private boolean m1Down;
   private boolean m2Down;
   public Point p1;
   public Point p2;
   public HudDrawElement hudElement;

   public CollisionPointGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.onLeftEvent((var1x) -> {
         this.m1Down = var1x.state;
         this.p1 = new Point(this.getMouseX(), this.getMouseY());
         return true;
      }, "Select point 1");
      this.onRightEvent((var1x) -> {
         this.m2Down = var1x.state;
         this.p2 = new Point(this.getMouseX(), this.getMouseY());
         return true;
      }, "Select point 2");
      this.onMouseMove((var1x) -> {
         if (this.m1Down) {
            this.p1 = new Point(this.getMouseX(), this.getMouseY());
         }

         if (this.m2Down) {
            this.p2 = new Point(this.getMouseX(), this.getMouseY());
         }

         var1x.useMove();
         return false;
      });
   }

   public void init() {
      this.p1 = null;
      this.p2 = null;
      final CollisionFilter var1 = (new CollisionFilter()).mobCollision();
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1x, final GameCamera var2, PlayerMob var3) {
            var1x.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1x) {
                  if (CollisionPointGameTool.this.p1 != null && CollisionPointGameTool.this.p2 != null) {
                     FontManager.bit.drawString((float)var2.getDrawX(CollisionPointGameTool.this.p1.x), (float)var2.getDrawY(CollisionPointGameTool.this.p1.y), "Distance: " + GameMath.toDecimals(CollisionPointGameTool.this.p1.distance(CollisionPointGameTool.this.p2), 2), (new FontOptions(12)).outline());
                     Screen.drawLineRGBA(var2.getDrawX(CollisionPointGameTool.this.p1.x), var2.getDrawY(CollisionPointGameTool.this.p1.y), var2.getDrawX(CollisionPointGameTool.this.p2.x), var2.getDrawY(CollisionPointGameTool.this.p2.y), 1.0F, 1.0F, 0.0F, 1.0F);
                     Line2D.Float var2x = new Line2D.Float((float)CollisionPointGameTool.this.p1.x, (float)CollisionPointGameTool.this.p1.y, (float)CollisionPointGameTool.this.p2.x, (float)CollisionPointGameTool.this.p2.y);
                     ArrayList var3 = getLevel().getCollisions(var2x, var1);
                     Iterator var4 = var3.iterator();

                     while(var4.hasNext()) {
                        LevelObjectHit var5 = (LevelObjectHit)var4.next();
                        Screen.drawRectangleLines(var5, var2, 1.0F, 0.0F, 0.0F, 1.0F);
                     }

                     IntersectionPoint var6 = getLevel().getCollisionPoint(var3, var2x, true);
                     if (var6 != null) {
                        Screen.drawCircle(var2.getDrawX((int)var6.getX()), var2.getDrawY((int)var6.getY()), 5, 10, 1.0F, 0.0F, 0.0F, 1.0F, true);
                        FontManager.bit.drawString((float)var2.getDrawX((int)var6.getX()), (float)(var2.getDrawY((int)var6.getY()) + 5), "Dir: " + var6.dir, (new FontOptions(16)).colorf(1.0F, 0.0F, 0.0F).outline());
                     }
                  }

               }
            });
         }
      });
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
