package necesse.engine.postProcessing;

import necesse.engine.GameWindow;
import necesse.gfx.gameTexture.GameFrameBuffer;

public abstract class PostProcessStage {
   public final GameWindow window;

   public PostProcessStage(GameWindow var1) {
      this.window = var1;
   }

   public boolean isEnabled() {
      return true;
   }

   public abstract GameFrameBuffer doPostProcessing(GameFrameBuffer var1);

   public abstract void updateFrameBufferSize();

   public abstract void dispose();
}
