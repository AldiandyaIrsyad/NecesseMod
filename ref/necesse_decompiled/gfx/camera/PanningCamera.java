package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;

public class PanningCamera extends GameCamera {
   private float xBuffer;
   private float yBuffer;
   private float dx;
   private float dy;
   private float speed;

   public PanningCamera(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public PanningCamera(int var1, int var2) {
      super(var1, var2);
   }

   public PanningCamera() {
   }

   public void tickMovement(TickManager var1) {
      this.xBuffer += this.dx * this.speed * var1.getDelta() / 250.0F;

      float var2;
      for(this.yBuffer += this.dy * this.speed * var1.getDelta() / 250.0F; this.xBuffer >= 1.0F || this.xBuffer <= -1.0F; this.xBuffer -= var2) {
         var2 = Math.signum(this.xBuffer);
         this.x = (int)((float)this.x + var2);
      }

      while(this.yBuffer >= 1.0F || this.yBuffer <= -1.0F) {
         var2 = Math.signum(this.yBuffer);
         this.y = (int)((float)this.y + var2);
         this.yBuffer -= var2;
      }

   }

   public void setDirection(float var1, float var2) {
      Point2D.Float var3 = GameMath.normalize(var1, var2);
      this.dx = var3.x;
      this.dy = var3.y;
   }

   public void setSpeed(float var1) {
      this.speed = var1;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getXDir() {
      return this.dx;
   }

   public float getYDir() {
      return this.dy;
   }

   public void invertXDir() {
      this.dx = -this.dx;
   }

   public void invertYDir() {
      this.dy = -this.dy;
   }
}
