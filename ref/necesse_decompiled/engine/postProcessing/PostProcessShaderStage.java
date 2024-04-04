package necesse.engine.postProcessing;

import necesse.engine.GameWindow;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.shader.GameShader;

public class PostProcessShaderStage extends PostProcessStage {
   protected GameFrameBuffer frameBuffer;
   public final GameShader shader;

   public PostProcessShaderStage(GameWindow var1, GameShader var2) {
      super(var1);
      this.frameBuffer = var1.getNewFrameBuffer(var1.getSceneWidth(), var1.getSceneHeight());
      this.shader = var2;
   }

   public GameFrameBuffer doPostProcessing(GameFrameBuffer var1) {
      try {
         this.frameBuffer.bind();
         this.frameBuffer.clearColor();
         this.shader.use();
         this.setShaderVariables();
         GameFrameBuffer.draw(var1.getColorBufferTextureID(), 0, 0, this.frameBuffer.getWidth(), this.frameBuffer.getHeight(), (Runnable)null);
      } finally {
         this.shader.stop();
         this.frameBuffer.unbind();
      }

      return this.frameBuffer;
   }

   protected void setShaderVariables() {
   }

   public void updateFrameBufferSize() {
      this.frameBuffer.dispose();
      this.frameBuffer = this.window.getNewFrameBuffer(this.window.getSceneWidth(), this.window.getSceneHeight());
   }

   public void dispose() {
      this.frameBuffer.dispose();
   }
}
