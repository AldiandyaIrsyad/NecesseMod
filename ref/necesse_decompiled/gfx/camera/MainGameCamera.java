package necesse.gfx.camera;

import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;

public abstract class MainGameCamera extends GameCamera {
   public MainGameCamera(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public MainGameCamera(int var1, int var2) {
      super(var1, var2);
   }

   public MainGameCamera() {
   }

   public abstract void tickCamera(TickManager var1, MainGame var2, Client var3);
}
