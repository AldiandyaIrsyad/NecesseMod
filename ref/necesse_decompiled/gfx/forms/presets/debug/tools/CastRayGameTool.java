package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CollisionPoint;
import necesse.level.maps.hudManager.HudDrawElement;

public class CastRayGameTool extends MouseDebugGameTool {
   private boolean m1Down;
   private boolean m2Down;
   public Point p1;
   public Point p2;
   public int maxBounces = 0;
   public int distanceMultiplier = 1;
   public boolean hitMobs = false;
   public HudDrawElement hudElement;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

   public CastRayGameTool(DebugForm var1, String var2) {
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
      this.onKeyClick(77, (var1x) -> {
         this.hitMobs = !this.hitMobs;
         this.setKeyUsage(77, "Hit mobs: " + this.hitMobs);
         return true;
      }, "Hit mobs: " + this.hitMobs);
      this.onScroll((var1x) -> {
         this.wheelBuffer.add(var1x);
         this.wheelBuffer.useScrollY((var1) -> {
            if (var1) {
               if (Screen.isKeyDown(340)) {
                  ++this.distanceMultiplier;
               } else {
                  ++this.maxBounces;
               }
            } else if (Screen.isKeyDown(340)) {
               this.distanceMultiplier = Math.max(--this.distanceMultiplier, 1);
            } else {
               this.maxBounces = Math.max(--this.maxBounces, 0);
            }

         });
         this.scrollUsage = "Max bounces: " + this.maxBounces + ", Distance mod: " + this.distanceMultiplier;
         return true;
      }, "");
      this.scrollUsage = "Max bounces: " + this.maxBounces + ", Distance mod: " + this.distanceMultiplier;
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
                  if (CastRayGameTool.this.p1 != null && CastRayGameTool.this.p2 != null) {
                     Screen.drawLineRGBA(var2.getDrawX(CastRayGameTool.this.p1.x), var2.getDrawY(CastRayGameTool.this.p1.y), var2.getDrawX(CastRayGameTool.this.p2.x), var2.getDrawY(CastRayGameTool.this.p2.y), 1.0F, 1.0F, 0.0F, 1.0F);
                     double var2x = CastRayGameTool.this.p1.distance(CastRayGameTool.this.p2) * (double)CastRayGameTool.this.distanceMultiplier;
                     RayLinkedList var4 = GameUtils.castRay((double)CastRayGameTool.this.p1.x, (double)CastRayGameTool.this.p1.y, (double)(CastRayGameTool.this.p2.x - CastRayGameTool.this.p1.x), (double)(CastRayGameTool.this.p2.y - CastRayGameTool.this.p1.y), var2x, 100.0, CastRayGameTool.this.maxBounces, (var2xx) -> {
                        Stream var3 = getLevel().getCollisions(var2xx, var1).stream().map((var0) -> {
                           return var0;
                        });
                        if (CastRayGameTool.this.hitMobs) {
                           Stream var4 = getLevel().entityManager.mobs.streamInRegionsShape(var2xx, 1);
                           Stream var5 = getLevel().entityManager.players.streamInRegionsShape(var2xx, 1);
                           Stream var10000 = Stream.concat(var4, var5).map(Mob::getCollision);
                           Objects.requireNonNull(var2xx);
                           Stream var6 = var10000.filter(var2xx::intersects);
                           var3 = Stream.concat(var3, var6);
                        }

                        return CollisionPoint.getClosestCollision(var3, var2xx, false);
                     });
                     FontManager.bit.drawString((float)var2.getDrawX(CastRayGameTool.this.p1.x), (float)var2.getDrawY(CastRayGameTool.this.p1.y), "Distance: " + GameMath.toDecimals(var2x, 2), (new FontOptions(12)).outline());
                     FontManager.bit.drawString((float)var2.getDrawX(CastRayGameTool.this.p1.x), (float)(var2.getDrawY(CastRayGameTool.this.p1.y) + 16), "Bounces: " + var4.size(), (new FontOptions(12)).outline());
                     Iterator var5 = var4.iterator();

                     while(var5.hasNext()) {
                        Ray var6 = (Ray)var5.next();
                        int var7 = var2.getDrawX((float)var6.getX1());
                        int var8 = var2.getDrawY((float)var6.getY1());
                        int var9 = var2.getDrawX((float)var6.getX2());
                        int var10 = var2.getDrawY((float)var6.getY2());
                        Screen.drawLineRGBA(var7, var8, var9, var10, 1.0F, 0.0F, 0.0F, 1.0F);
                        if (var6.targetHit != null) {
                           Screen.drawRectangleLines((Rectangle)var6.targetHit, var2, 1.0F, 0.0F, 1.0F, 1.0F);
                        }
                     }

                     FontManager.bit.drawString((float)var2.getDrawX(CastRayGameTool.this.p1.x), (float)(var2.getDrawY(CastRayGameTool.this.p1.y) + 32), "Ray distance: " + GameMath.toDecimals(var4.totalDist, 2), (new FontOptions(12)).outline());
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
