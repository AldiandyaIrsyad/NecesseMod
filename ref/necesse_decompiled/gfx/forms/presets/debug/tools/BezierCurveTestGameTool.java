package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.util.List;
import necesse.engine.GameBezierCurve;
import necesse.engine.GameBezierPoint;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.hudManager.HudDrawElement;

public class BezierCurveTestGameTool extends MouseDebugGameTool {
   public Point clickDownPoint;
   public GameBezierCurve curve = new GameBezierCurve();
   public HudDrawElement hudElement;

   public BezierCurveTestGameTool(DebugForm var1, String var2) {
      super(var1, var2);
   }

   public void init() {
      this.onLeftEvent((var1) -> {
         Point var2 = new Point(this.getMouseX(), this.getMouseY());
         if (var1.state) {
            this.clickDownPoint = var2;
         } else {
            if (this.clickDownPoint != null) {
               this.curve.points.add(new GameBezierPoint((float)this.clickDownPoint.x, (float)this.clickDownPoint.y, (float)var2.x, (float)var2.y));
            }

            this.clickDownPoint = null;
         }

         return true;
      }, "Add point");
      this.onRightClick((var1) -> {
         int var2 = -1;
         double var3 = 0.0;
         int var5 = this.getMouseX();
         int var6 = this.getMouseY();

         for(int var7 = 0; var7 < this.curve.points.size(); ++var7) {
            GameBezierPoint var8 = (GameBezierPoint)this.curve.points.get(var7);
            float var9 = GameMath.getExactDistance((float)var5, (float)var6, var8.startX, var8.startY);
            if (var2 < 0 || (double)var9 < var3) {
               var3 = (double)var9;
               var2 = var7;
            }
         }

         if (var2 >= 0) {
            this.curve.points.remove(var2);
         }

         return true;
      }, "Remove point");
      this.onKeyClick(67, (var1) -> {
         this.curve.points.clear();
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
                  BezierCurveTestGameTool.this.curve.draw((float)(-var2.getX()), (float)(-var2.getY()), 5.0F);
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
