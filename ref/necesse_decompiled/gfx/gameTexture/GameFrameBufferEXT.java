package necesse.gfx.gameTexture;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.gfx.shader.GameShader;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class GameFrameBufferEXT extends GameFrameBuffer {
   public final int width;
   public final int height;
   public final int frameBufferObject;
   public final int textureID;
   private final boolean complete;
   private GameFrameBuffer applyDraw;

   public GameFrameBufferEXT(Consumer<GameFrameBuffer> var1, int var2, int var3) {
      this(var1, var2, var3, true);
   }

   protected GameFrameBufferEXT(Consumer<GameFrameBuffer> var1, int var2, int var3, boolean var4) {
      super(var1);
      this.width = var2;
      this.height = var3;
      this.frameBufferObject = EXTFramebufferObject.glGenFramebuffersEXT();
      EXTFramebufferObject.glBindFramebufferEXT(36160, this.frameBufferObject);
      this.textureID = GL11.glGenTextures();
      GL11.glBindTexture(3553, this.textureID);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      GL11.glTexImage2D(3553, 0, 32856, Math.max(1, var2), Math.max(1, var3), 0, 6408, 5121, (ByteBuffer)null);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, this.textureID, 0);
      this.complete = this.checkStatus();
      if (this.complete) {
         EXTFramebufferObject.glBindFramebufferEXT(36160, this.frameBufferObject);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClearDepth(0.0);
         GL11.glViewport(0, 0, var2, var3);
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, (double)var2, (double)var3, 0.0, 0.0, 1.0);
         GL11.glMatrixMode(5888);
         GL11.glEnable(2929);
         GL11.glDepthFunc(519);
         GL11.glEnable(3553);
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
      }

      this.glUnbind();
      if (var4) {
         this.applyDraw = new GameFrameBufferEXT(var1, var2, var3, false);
      }

   }

   private boolean checkStatus() {
      int var1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (var1 != 36053) {
         System.err.println("Could not create frame buffer");
         switch (var1) {
            case 36054:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT");
               break;
            case 36055:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT");
            case 36056:
            default:
               break;
            case 36057:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT");
               break;
            case 36058:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT");
               break;
            case 36059:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT");
               break;
            case 36060:
               System.err.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT");
               break;
            case 36061:
               System.err.println("GL_FRAMEBUFFER_UNSUPPORTED_EXT");
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
      EXTFramebufferObject.glBindFramebufferEXT(36160, this.frameBufferObject);
   }

   protected void glUnbind() {
      EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
   }

   public void dispose() {
      if (this.applyDraw != null) {
         this.applyDraw.dispose();
      }

      GL11.glDeleteTextures(this.textureID);
      EXTFramebufferObject.glDeleteFramebuffersEXT(this.frameBufferObject);
   }
}
