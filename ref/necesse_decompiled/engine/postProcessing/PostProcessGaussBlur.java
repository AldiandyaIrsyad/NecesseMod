package necesse.engine.postProcessing;

import necesse.engine.GameWindow;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameFrameBuffer;

public class PostProcessGaussBlur extends PostProcessStage {
   public static boolean enabled = false;
   private float size;
   private boolean sizeChanged;
   private GameFrameBuffer horiBuffer;
   private GameFrameBuffer vertBuffer;

   public PostProcessGaussBlur(GameWindow var1, float var2) {
      super(var1);
      this.size = var2;
      this.horiBuffer = var1.getNewFrameBuffer((int)((float)var1.getSceneWidth() * var2), (int)((float)var1.getSceneHeight() * var2));
      this.vertBuffer = var1.getNewFrameBuffer((int)((float)var1.getSceneWidth() * var2), (int)((float)var1.getSceneHeight() * var2));
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setSize(float var1) {
      if (this.size != var1) {
         this.size = var1;
         this.sizeChanged = true;
      }
   }

   public GameFrameBuffer doPostProcessing(GameFrameBuffer var1) {
      if (enabled) {
         if (this.sizeChanged) {
            this.horiBuffer.dispose();
            this.horiBuffer = this.window.getNewFrameBuffer((int)((float)this.window.getSceneWidth() * this.size), (int)((float)this.window.getSceneHeight() * this.size));
            this.vertBuffer.dispose();
            this.vertBuffer = this.window.getNewFrameBuffer((int)((float)this.window.getSceneWidth() * this.size), (int)((float)this.window.getSceneHeight() * this.size));
            this.sizeChanged = false;
         }

         try {
            this.horiBuffer.bind();
            this.horiBuffer.clearColor();
            GameResources.horizontalGaussShader.use();
            GameResources.horizontalGaussShader.pass1f("pixelSize", 1.0F / (float)this.horiBuffer.getWidth());
            GameFrameBuffer.draw(var1.getColorBufferTextureID(), 0, 0, this.horiBuffer.getWidth(), this.horiBuffer.getHeight(), (Runnable)null);
         } finally {
            GameResources.horizontalGaussShader.stop();
            this.horiBuffer.unbind();
         }

         try {
            this.vertBuffer.bind();
            this.vertBuffer.clearColor();
            GameResources.verticalGaussShader.use();
            GameResources.verticalGaussShader.pass1f("pixelSize", 1.0F / (float)this.vertBuffer.getHeight());
            GameFrameBuffer.draw(this.horiBuffer.getColorBufferTextureID(), 0, 0, this.vertBuffer.getWidth(), this.vertBuffer.getHeight(), (Runnable)null);
         } finally {
            GameResources.verticalGaussShader.stop();
            this.vertBuffer.unbind();
         }

         return this.vertBuffer;
      } else {
         return var1;
      }
   }

   public void updateFrameBufferSize() {
      this.sizeChanged = true;
   }

   public void dispose() {
      this.horiBuffer.dispose();
      this.vertBuffer.dispose();
   }
}
