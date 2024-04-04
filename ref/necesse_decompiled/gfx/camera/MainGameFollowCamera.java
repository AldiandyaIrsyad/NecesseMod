package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

public class MainGameFollowCamera extends MainGameCamera {
   public MainGameFollowCamera() {
   }

   public void tickCamera(TickManager var1, MainGame var2, Client var3) {
      PlayerMob var4 = var3.getPlayer();
      if (var4 != null) {
         this.centerCamera(var4.getDrawX(), var4.getDrawY());
         if (var2.isRunning() && var4.getSelectedItem() != null) {
            float var5 = var4.getSelectedItem().item.zoomAmount();
            if (var5 != 0.0F) {
               float var6;
               float var7;
               if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                  var6 = ControllerInput.getAimX();
                  var7 = ControllerInput.getAimY();
               } else {
                  var6 = (float)Screen.mousePos().sceneX / (float)Screen.getSceneWidth() * 2.0F - 1.0F;
                  var7 = (float)Screen.mousePos().sceneY / (float)Screen.getSceneHeight() * 2.0F - 1.0F;
               }

               this.setPosition(this.getX() + (int)(var6 * var5), this.getY() + (int)(var7 * var5));
            }
         }
      }

      Point2D.Float var8 = var3.getCurrentCameraShake();
      this.setPosition(this.getX() + (int)var8.x, this.getY() + (int)var8.y);
   }
}
