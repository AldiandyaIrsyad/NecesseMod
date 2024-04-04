package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
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

public class ExpandingPolygonGameTool extends MouseDebugGameTool {
   private ArrayList<Point> points;
   private ExpandingPolygon polygon;
   public HudDrawElement hudElement;

   public ExpandingPolygonGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.onLeftClick((var1x) -> {
         Point var2 = new Point(this.getMouseX(), this.getMouseY());
         this.points.add(var2);
         this.polygon.addPoint(var2);
         return true;
      }, "Add point");
      this.onRightClick((var1x) -> {
         this.points.stream().min(Comparator.comparingDouble((var1) -> {
            return var1.distance((double)this.getMouseX(), (double)this.getMouseY());
         })).ifPresent((var1) -> {
            this.points.remove(var1);
         });
         this.polygon = new ExpandingPolygon((Point[])this.points.toArray(new Point[0]));
         return true;
      }, "Remove point");
   }

   public void init() {
      this.points = new ArrayList();
      this.polygon = new ExpandingPolygon();
      final CollisionFilter var1 = (new CollisionFilter()).mobCollision();
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1x, final GameCamera var2, PlayerMob var3) {
            var1x.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1x) {
                  for(int var2x = 0; var2x < ExpandingPolygonGameTool.this.points.size(); ++var2x) {
                     Point var3 = (Point)ExpandingPolygonGameTool.this.points.get(var2x);
                     Screen.drawCircle(var2.getDrawX(var3.x), var2.getDrawY(var3.y), 5, 10, 0.0F, 1.0F, 0.0F, 1.0F, true);
                     FontManager.bit.drawString((float)var2.getDrawX(var3.x), (float)var2.getDrawY(var3.y), "" + var2x, (new FontOptions(12)).outline());
                  }

                  Screen.drawShape(ExpandingPolygonGameTool.this.polygon, var2, false, 1.0F, 1.0F, 0.0F, 1.0F);
                  ArrayList var5 = getLevel().getCollisions(ExpandingPolygonGameTool.this.polygon, var1);
                  Iterator var6 = var5.iterator();

                  while(var6.hasNext()) {
                     LevelObjectHit var4 = (LevelObjectHit)var6.next();
                     Screen.drawRectangleLines(var4, var2, 1.0F, 0.0F, 0.0F, 1.0F);
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
