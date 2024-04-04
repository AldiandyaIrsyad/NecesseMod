package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.Screen;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTexture.GameSprite;

public class TrailTestGameTool extends MouseDebugGameTool {
   public boolean isDown;
   public Trail trail;
   public float thickness = 100.0F;
   public float height = 0.0F;
   public Point lastPoint;

   public TrailTestGameTool(DebugForm var1, String var2) {
      super(var1, var2);
   }

   public void init() {
      this.onLeftEvent((var1) -> {
         if (var1.state) {
            this.isDown = true;
            this.refreshTrail(this.getMouseX(), this.getMouseY());
         } else {
            this.isDown = false;
         }

         return true;
      }, "Create trail");
      this.onMouseMove((var1) -> {
         if (this.isDown) {
            this.refreshTrail(this.getMouseX(), this.getMouseY());
            return true;
         } else {
            return false;
         }
      });
      this.onRightClick((var1) -> {
         if (this.trail != null) {
            this.trail.remove();
            this.trail = null;
         }

         return true;
      }, "Clear trail");
   }

   protected void refreshTrail(int var1, int var2) {
      if (this.lastPoint == null || this.lastPoint.x != var1 || this.lastPoint.y != var2) {
         if (this.trail != null && !this.trail.isRemoved()) {
            this.trail.addPoint(new TrailVector((float)var1, (float)var2, (float)(var1 - this.lastPoint.x), (float)(var2 - this.lastPoint.y), this.thickness, this.height), 0);
         } else {
            this.trail = new Trail(new TrailVector((float)var1, (float)var2, 0.0F, -1.0F, this.thickness, this.height), this.getLevel(), new Color(255, 0, 0, 100), 60000);
            this.trail.smoothCorners = true;
            this.trail.sprite = new GameSprite(Screen.getQuadTexture());
            this.getLevel().entityManager.addTrail(this.trail);
         }

         this.lastPoint = new Point(var1, var2);
      }
   }
}
