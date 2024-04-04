package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;

public class MainGameMousePanningCamera extends MainGamePanningCamera {
   private final float speedModifier;

   public MainGameMousePanningCamera(int var1, int var2, float var3) {
      super(var1, var2);
      this.speedModifier = var3;
   }

   public void tickCamera(TickManager var1, MainGame var2, Client var3) {
      if (Settings.hideUI || !var2.formManager.isMouseOver()) {
         float var4 = (float)Screen.mousePos().sceneX / (float)Screen.getSceneWidth() * 2.0F - 1.0F;
         float var5 = (float)Screen.mousePos().sceneY / (float)Screen.getSceneHeight() * 2.0F - 1.0F;
         this.setSpeed((float)((new Point2D.Float(var4, var5)).distance(0.0, 0.0) * (double)this.speedModifier));
         this.setDirection(var4, var5);
      }

      super.tickCamera(var1, var2, var3);
   }
}
