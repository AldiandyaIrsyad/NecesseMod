package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;

public class MainGamePanningCamera extends MainGameCamera {
   private float xBuffer;
   private float yBuffer;
   private float dx;
   private float dy;
   private float speed;

   public MainGamePanningCamera(int var1, int var2) {
      super(var1, var2);
   }

   public void tickCamera(TickManager var1, MainGame var2, Client var3) {
      this.xBuffer += this.dx * this.speed * var1.getDelta() / 250.0F;

      float var4;
      for(this.yBuffer += this.dy * this.speed * var1.getDelta() / 250.0F; this.xBuffer >= 1.0F || this.xBuffer <= -1.0F; this.xBuffer -= var4) {
         var4 = Math.signum(this.xBuffer);
         this.x = (int)((float)this.x + var4);
      }

      while(this.yBuffer >= 1.0F || this.yBuffer <= -1.0F) {
         var4 = Math.signum(this.yBuffer);
         this.y = (int)((float)this.y + var4);
         this.yBuffer -= var4;
      }

   }

   public void setDirection(float var1, float var2) {
      Point2D.Float var3 = GameMath.normalize(var1, var2);
      this.dx = var3.x;
      this.dy = var3.y;
   }

   public float getSpeed() {
      return this.speed;
   }

   public void setSpeed(float var1) {
      this.speed = var1;
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
