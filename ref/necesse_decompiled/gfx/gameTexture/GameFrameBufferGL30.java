package necesse.gfx.gameTexture;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.gfx.shader.GameShader;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class GameFrameBufferGL30 extends GameFrameBuffer {
   public final int width;
   public final int height;
   public final int frameBufferObject;
   public final int textureID;
   private final boolean complete;
   private GameFrameBuffer applyDraw;

   public GameFrameBufferGL30(Consumer<GameFrameBuffer> var1, int var2, int var3) {
      this(var1, var2, var3, true);
   }

   protected GameFrameBufferGL30(Consumer<GameFrameBuffer> var1, int var2, int var3, boolean var4) {
      super(var1);
      this.width = var2;
      this.height = var3;
      this.frameBufferObject = GL30.glGenFramebuffers();
      GL30.glBindFramebuffer(36160, this.frameBufferObject);
      this.textureID = GL30.glGenTextures();
      GL30.glBindTexture(3553, this.textureID);
      GL30.glTexParameteri(3553, 10242, 33071);
      GL30.glTexParameteri(3553, 10243, 33071);
      GL30.glTexImage2D(3553, 0, 32856, Math.max(1, var2), Math.max(1, var3), 0, 6408, 5121, (ByteBuffer)null);
      GL30.glTexParameteri(3553, 10241, 9729);
      GL30.glTexParameteri(3553, 10240, 9729);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, this.textureID, 0);
      this.complete = this.checkStatus();
      if (this.complete) {
         GL30.glBindFramebuffer(36160, this.frameBufferObject);
         GL30.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL30.glClearDepth(0.0);
         GL30.glViewport(0, 0, var2, var3);
         GL30.glMatrixMode(5889);
         GL30.glLoadIdentity();
         GL30.glOrtho(0.0, (double)var2, (double)var3, 0.0, 0.0, 1.0);
         GL30.glMatrixMode(5888);
         GL30.glEnable(2929);
         GL30.glDepthFunc(519);
         GL30.glEnable(3553);
         GL30.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
      }

      this.glUnbind();
      if (var4) {
         this.applyDraw = new GameFrameBufferGL30(var1, var2, var3, false);
      }

   }

   private boolean checkStatus() {
      int var1 = GL30.glCheckFramebufferStatus(36160);
      if (var1 != 36053) {
         System.err.println("Could not create frame buffer");
         switch (var1) {
            case 33305:
               System.err.println("GL_FRAMEBUFFER_UNDEFINED");
               break;
            case 36054:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
               break;
            case 36055:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
               break;
            case 36059:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
               break;
            case 36060:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
               break;
            case 36061:
               System.err.println("GL_FRAMEBUFFER_UNSUPPORTED");
               break;
            case 36182:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean isComplete() {
      return this.complete;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getColorBufferTextureID() {
      return this.textureID;
   }

   public int getDepthBufferTextureID() {
      return 0;
   }

   public void applyDraw(Runnable var1, Runnable var2) {
      try {
         this.applyDraw.bind();
         this.applyDraw.clearColor();
         this.applyDraw.clearDepth();
         var1.run();
      } finally {
         try {
            this.bind();
            Screen.useShader((GameShader)null);
            drawSimple(this.applyDraw.getColorBufferTextureID(), 0, 0, this.applyDraw.getWidth(), this.applyDraw.getHeight(), var2);
         } finally {
            Screen.stopShader((GameShader)null);
         }
      }

   }

   protected void glBind() {
      GL30.glBindFramebuffer(36160, this.frameBufferObject);
   }

   protected void glUnbind() {
      GL30.glBindFramebuffer(36160, 0);
   }

   public void dispose() {
      if (this.applyDraw != null) {
         this.applyDraw.dispose();
      }

      GL30.glDeleteTextures(this.textureID);
      GL30.glDeleteFramebuffers(this.frameBufferObject);
   }
}
