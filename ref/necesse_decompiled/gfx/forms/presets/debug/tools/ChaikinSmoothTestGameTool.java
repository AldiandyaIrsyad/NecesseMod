package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.hudManager.HudDrawElement;

public class ChaikinSmoothTestGameTool extends MouseDebugGameTool {
   public Point lastPoint;
   public ArrayList<TrailVector> points = new ArrayList();
   public HudDrawElement hudElement;

   public ChaikinSmoothTestGameTool(DebugForm var1, String var2) {
      super(var1, var2);
   }

   public void init() {
      this.onLeftEvent((var1) -> {
         Point var2 = new Point(this.getMouseX(), this.getMouseY());
         if (var1.state) {
            this.lastPoint = var2;
         } else {
            if (this.lastPoint != null) {
               this.points.add(new TrailVector((float)this.lastPoint.x, (float)this.lastPoint.y, (float)(var2.x - this.lastPoint.x), (float)(var2.y - this.lastPoint.y), 0.0F, 0.0F));
            }

            this.lastPoint = null;
         }

         return true;
      }, "Add point");
      this.onRightClick((var1) -> {
         if (!this.points.isEmpty()) {
            this.points.remove(this.points.size() - 1);
         }

         return true;
      }, "Remove point");
      this.onKeyClick(71, (var1) -> {
         this.points = Trail.smooth(this.points);
         return true;
      }, "Smooth");
      this.onKeyClick(67, (var1) -> {
         this.points.clear();
         return true;
      }, "Clear points");
      this.setupHudElement();
   }

   public void setupHudElement() {
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return Integer.MAX_VALUE;
               }

               public void draw(TickManager var1) {
                  if (ChaikinSmoothTestGameTool.this.lastPoint != null) {
                     Screen.drawLineRGBA(var2.getDrawX(ChaikinSmoothTestGameTool.this.lastPoint.x), var2.getDrawY(ChaikinSmoothTestGameTool.this.lastPoint.y), var2.getDrawX(ChaikinSmoothTestGameTool.this.getMouseX()), var2.getDrawY(ChaikinSmoothTestGameTool.this.getMouseY()), 0.0F, 0.0F, 1.0F, 1.0F);
                  }

                  TrailVector var3;
                  for(int var2x = 0; var2x < ChaikinSmoothTestGameTool.this.points.size(); ++var2x) {
                     var3 = (TrailVector)ChaikinSmoothTestGameTool.this.points.get(var2x);
                     if (var2x > 0) {
                        TrailVector var4 = (TrailVector)ChaikinSmoothTestGameTool.this.points.get(var2x - 1);
                        Screen.drawLineRGBA(var2.getDrawX(var4.pos.x), var2.getDrawY(var4.pos.y), var2.getDrawX(var3.pos.x), var2.getDrawY(var3.pos.y), 1.0F, 0.0F, 0.0F, 1.0F);
                     }
                  }

                  Iterator var5 = ChaikinSmoothTestGameTool.this.points.iterator();

                  while(var5.hasNext()) {
                     var3 = (TrailVector)var5.next();
                     Screen.drawCircle(var2.getDrawX(var3.pos.x), var2.getDrawY(var3.pos.y), 2, 15, 0.0F, 1.0F, 0.0F, 1.0F, true);
                     Screen.drawLineRGBA(var2.getDrawX(var3.pos.x), var2.getDrawY(var3.pos.y), var2.getDrawX(var3.pos.x + var3.dx * 20.0F), var2.getDrawY(var3.pos.y + var3.dy * 20.0F), 0.0F, 1.0F, 0.0F, 1.0F);
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
