package necesse.engine;

import java.awt.Color;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import necesse.engine.control.Input;
import necesse.engine.postProcessing.PostProcessGaussBlur;
import necesse.engine.postProcessing.PostProcessShaderStage;
import necesse.engine.postProcessing.PostProcessStage;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointerList;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameFrameBufferEXT;
import necesse.gfx.gameTexture.GameFrameBufferGL30;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.Pointer;

public class GameWindow {
   public static float[] interfaceSizes = new float[]{1.0F, 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.6F, 1.7F, 1.8F, 1.9F, 2.0F};
   public static int minDynamicInterfaceHeight = 1080;
   public static int minDynamicInterfaceWidth;
   public static int expectedDynamicInterfaceHeight;
   public static int expectedDynamicInterfaceWidth;
   public static int minDynamicSceneHeight;
   public static int minDynamicSceneWidth;
   public static int expectedDynamicSceneHeight;
   public static int expectedDynamicSceneWidth;
   public static float minSceneSize;
   public static float maxSceneSize;
   public FBOType FBO_CAPABILITIES = null;
   private long glfwWindow;
   private Input input;
   private int windowWidth;
   private int windowHeight;
   private int windowFrameWidth;
   private int windowFrameHeight;
   private boolean hasResized;
   private GameFrameBuffer hudBuffer;
   private float hudSize = 1.0F;
   private boolean hudSizeChanged = false;
   private ArrayList<PostProcessStage> scenePostProcessing;
   private GameFrameBuffer sceneBuffer;
   private GameFrameBuffer sceneOverlayBuffer;
   private float sceneSize = 1.0F;
   private boolean sceneSizeChanged = false;
   private GameFrameBuffer windowBuffer = new WindowFrameBuffer(this::setCurrentBuffer);
   private GameFrameBuffer currentBuffer;
   private float sceneRed;
   private float sceneGreen;
   private float sceneBlue;
   private float sceneDarkness;

   public static float getClosestSize(float var0, float[] var1) {
      return (Float)IntStream.range(0, var1.length).mapToObj((var1x) -> {
         return var1[var1x];
      }).min(Comparator.comparingDouble((var1x) -> {
         return (double)Math.abs(var0 - var1x);
      })).orElse(var0);
   }

   public static float getDynamicSize(int var0, int var1, int var2, int var3, int var4, int var5) {
      return var5 > var1 && var4 > var0 ? Math.min((float)var5 / (float)var3, (float)var4 / (float)var2) : 1.0F;
   }

   public GameWindow() {
      this.currentBuffer = this.windowBuffer;
      this.sceneRed = 1.0F;
      this.sceneGreen = 1.0F;
      this.sceneBlue = 1.0F;
      this.glfwWindow = 0L;
      this.scenePostProcessing = new ArrayList();
      this.input = new Input(this);
   }

   public void createWindow(boolean var1) {
      this.createWindow((GameWindow)null, var1);
   }

   public void createWindow(GameWindow var1, boolean var2) {
      GLFW.glfwDefaultWindowHints();
      this.setupWindowHints();
      GLFW.glfwWindowHint(131076, 0);
      this.glfwWindow = GLFW.glfwCreateWindow(1280, 720, "Necesse v. 0.24.2", 0L, var1 != null ? var1.glfwWindow : 0L);
      if (this.glfwWindow == 0L) {
         Screen.queryGLError("windowCreation");
         throw new GameWindowCreationException("Failed to create the GLFW window");
      } else {
         if (var1 != null) {
            this.hudSize = var1.hudSize;
            this.sceneSize = var1.sceneSize;
         }

         MemoryStack var3 = MemoryStack.stackPush();

         try {
            IntBuffer var4 = var3.mallocInt(1);
            IntBuffer var5 = var3.mallocInt(1);
            GLFW.glfwGetWindowSize(this.glfwWindow, var4, var5);
            this.windowWidth = Math.max(var4.get(0), 1);
            this.windowHeight = Math.max(var5.get(0), 1);
            IntBuffer var6 = var3.mallocInt(1);
            IntBuffer var7 = var3.mallocInt(1);
            GLFW.glfwGetFramebufferSize(this.glfwWindow, var6, var7);
            this.windowFrameWidth = Math.max(var6.get(0), 1);
            this.windowFrameHeight = Math.max(var7.get(0), 1);
         } catch (Throwable var9) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var3 != null) {
            var3.close();
         }

         GLFW.glfwSetWindowSizeCallback(this.glfwWindow, (var1x, var3x, var4x) -> {
            this.windowWidth = Math.max(var3x, 1);
            this.windowHeight = Math.max(var4x, 1);
            this.hasResized = true;
         });
         GLFW.glfwSetFramebufferSizeCallback(this.glfwWindow, (var1x, var3x, var4x) -> {
            this.windowFrameWidth = Math.max(var3x, 1);
            this.windowFrameHeight = Math.max(var4x, 1);
            this.hasResized = true;
         });
         this.makeCurrent();
         GL.createCapabilities();
         if (GL.getCapabilities().OpenGL30) {
            this.FBO_CAPABILITIES = GameWindow.FBOType.GL30;
         } else if (GL.getCapabilities().GL_EXT_framebuffer_object) {
            this.FBO_CAPABILITIES = GameWindow.FBOType.EXT;
         } else {
            GameLog.warn.println("Could not find framebuffer capabilities, some functions may not work.");
         }

         Screen.queryGLError("PostWindowGL");
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClearDepth(0.0);
         if (var2) {
            this.updateWindowSize();
         } else {
            this.updateResize();
         }

         GL11.glEnable(3553);
         GL11.glEnable(2929);
         GL11.glDepthFunc(519);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         this.setVSync(Settings.vSyncEnabled);
      }
   }

   public void initInput() {
      this.input.init(this.glfwWindow);
   }

   public GameFrameBuffer getNewFrameBuffer(int var1, int var2) {
      if (var1 != 0 && var2 != 0) {
         if (this.FBO_CAPABILITIES == GameWindow.FBOType.EXT) {
            return new GameFrameBufferEXT(this::setCurrentBuffer, var1, var2);
         } else {
            Screen.queryGLError("PostNewEXTFrameBuffer");
            GameFrameBufferGL30 var3 = new GameFrameBufferGL30(this::setCurrentBuffer, var1, var2);
            int var4 = Screen.queryGLError((String)null);
            if (var4 != 0) {
               System.out.println("Detected error trying to create GL30 frame buffers, trying EXT.");
               Screen.printGLError((String)null, var4);

               try {
                  GameFrameBufferEXT var5 = new GameFrameBufferEXT(this::setCurrentBuffer, var1, var2);
                  var4 = Screen.queryGLError((String)null);
                  if (var4 != 0) {
                     System.out.println("Could not create EXT frame buffer either, some things may not work correctly :(");
                     Screen.printGLError((String)null, var4);
                     return var3;
                  }

                  this.FBO_CAPABILITIES = GameWindow.FBOType.EXT;
                  return var5;
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }

            return var3;
         }
      } else {
         return new WindowFrameBuffer(this::setCurrentBuffer);
      }
   }

   private void setCurrentBuffer(GameFrameBuffer var1) {
      boolean var2 = this.currentBuffer != var1;
      if (var1 == null) {
         this.currentBuffer = this.windowBuffer;
      } else {
         this.currentBuffer = var1;
      }

      if (var2) {
         this.updateView();
      }

   }

   public void setupWindowHints() {
      Platform var1 = Platform.get();
      if (var1 != Platform.MACOSX) {
         switch (Settings.displayMode) {
            case Borderless:
               GLFW.glfwWindowHint(131077, 0);
               GLFW.glfwWindowHint(131075, 0);
               break;
            case Fullscreen:
               GLFW.glfwWindowHint(131077, 1);
               GLFW.glfwWindowHint(131075, 0);
               break;
            case Windowed:
               GLFW.glfwWindowHint(131077, 1);
               GLFW.glfwWindowHint(131075, 1);
         }
      } else {
         GLFW.glfwWindowHint(131075, 1);
         GLFW.glfwWindowHint(131077, 1);
      }

   }

   public void updateWindowSize() {
      MemoryStack var1 = MemoryStack.stackPush();

      try {
         long var2 = WindowUtils.getMonitor(Settings.monitor);
         if (var2 == 0L) {
            System.out.println("Could not find monitor from settings, falling back to primary monitor");
            var2 = GLFW.glfwGetPrimaryMonitor();
         }

         if (var2 == 0L) {
            throw new NullPointerException("Could not find monitor");
         }

         GLFWVidMode var4 = GLFW.glfwGetVideoMode(var2);
         if (var4 == null) {
            throw new NullPointerException("Could not find monitor video mode");
         }

         int var5 = Settings.displaySize == null ? var4.width() : Settings.displaySize.width;
         int var6 = Settings.displaySize == null ? var4.height() : Settings.displaySize.height;
         IntBuffer var7 = var1.mallocInt(1);
         IntBuffer var8 = var1.mallocInt(1);
         GLFW.glfwGetMonitorPos(var2, var7, var8);
         if (Platform.get() != Platform.MACOSX) {
            switch (Settings.displayMode) {
               case Borderless:
                  GLFW.glfwSetWindowMonitor(this.glfwWindow, 0L, var7.get(0), var8.get(0), var4.width(), var4.height(), var4.refreshRate());
                  break;
               case Fullscreen:
                  GLFW.glfwSetWindowMonitor(this.glfwWindow, var2, 0, 0, var5, var6, var4.refreshRate());
                  break;
               case Windowed:
                  GLFW.glfwSetWindowMonitor(this.glfwWindow, 0L, var7.get(0) + (var4.width() - var5) / 2, var8.get(0) + (var4.height() - var6) / 2, var5, var6, var4.refreshRate());
            }
         } else {
            GLFW.glfwSetWindowMonitor(this.glfwWindow, 0L, var7.get(0) + (var4.width() - var5) / 2, var8.get(0) + (var4.height() - var6) / 2, var5, var6, var4.refreshRate());
         }
      } catch (Throwable var10) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (var1 != null) {
         var1.close();
      }

      this.updateResize();
   }

   public void setupPostProcessing() {
      this.scenePostProcessing.forEach(PostProcessStage::dispose);
      this.scenePostProcessing.clear();
      this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.debugColorShader));
      this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.colorSettingShader) {
         public boolean isEnabled() {
            return Settings.sceneColors != SceneColorSetting.Normal;
         }

         protected void setShaderVariables() {
            super.setShaderVariables();
            Settings.sceneColors.shaderSetup.accept(GameResources.colorSettingShader);
         }
      });
      this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.brightnessShader) {
         protected void setShaderVariables() {
            super.setShaderVariables();
            GameResources.brightnessShader.pass1f("brightness", Settings.brightness > 1.0F ? GameMath.lerp(Settings.brightness - 1.0F, 1.0F, 1.5F) : Settings.brightness);
         }
      });
      this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.windowColorShader) {
         protected void setShaderVariables() {
            GameResources.windowColorShader.pass1f("red", GameWindow.this.sceneRed);
            GameResources.windowColorShader.pass1f("green", GameWindow.this.sceneGreen);
            GameResources.windowColorShader.pass1f("blue", GameWindow.this.sceneBlue);
         }
      });
      this.scenePostProcessing.add(new PostProcessGaussBlur(this, 1.0F));
      this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.darknessShader) {
         public boolean isEnabled() {
            return GameWindow.this.sceneDarkness > 0.0F;
         }

         protected void setShaderVariables() {
            GameResources.darknessShader.pass1f("intensity", GameWindow.this.sceneDarkness);
         }
      });
   }

   public boolean isCreated() {
      return this.glfwWindow != 0L;
   }

   public Input getInput() {
      return this.input;
   }

   public boolean tickResize() {
      boolean var1 = false;
      if (this.sceneSizeChanged) {
         this.updateSceneResize();
         this.sceneSizeChanged = false;
         var1 = true;
      }

      if (this.hudSizeChanged) {
         this.updateHudResize();
         this.hudSizeChanged = false;
         var1 = true;
      }

      if (this.hasResized) {
         this.hasResized = false;
         if (this.windowWidth > 1 && this.windowHeight > 1) {
            this.updateResize();
         }

         var1 = true;
      }

      return var1;
   }

   private void updateSceneResize() {
      float var1 = 1.0F / this.sceneSize;
      if (this.sceneBuffer != null) {
         this.sceneBuffer.dispose();
      }

      this.sceneBuffer = this.getNewFrameBuffer((int)((float)this.getWidth() * var1), (int)((float)this.getHeight() * var1));
      this.scenePostProcessing.forEach(PostProcessStage::updateFrameBufferSize);
      if (this.sceneOverlayBuffer != null) {
         this.sceneOverlayBuffer.dispose();
      }

      this.sceneOverlayBuffer = this.getNewFrameBuffer(this.sceneBuffer.getWidth(), this.sceneBuffer.getHeight());
      this.updateView();
      this.hasResized = false;
   }

   private void updateHudResize() {
      float var1 = 1.0F / this.hudSize;
      if (this.hudBuffer != null) {
         this.hudBuffer.dispose();
      }

      this.hudBuffer = this.getNewFrameBuffer((int)((float)this.getWidth() * var1), (int)((float)this.getHeight() * var1));
      this.updateView();
      this.hasResized = false;
   }

   public void updateResize() {
      this.updateSceneResize();
      this.updateHudResize();
   }

   public void updateView() {
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0, (double)this.currentBuffer.getWidth(), (double)this.currentBuffer.getHeight(), 0.0, 0.0, 1.0);
      GL11.glMatrixMode(5888);
      GL11.glViewport(0, 0, this.currentBuffer.getWidth(), this.currentBuffer.getHeight());
   }

   public void makeCurrent() {
      GLFW.glfwMakeContextCurrent(this.glfwWindow);
   }

   public void setVSync(boolean var1) {
      GLFW.glfwSwapInterval(var1 ? 1 : 0);
   }

   public int getWidth() {
      return this.windowWidth;
   }

   public int getHeight() {
      return this.windowHeight;
   }

   public int getFrameWidth() {
      return this.windowFrameWidth;
   }

   public int getFrameHeight() {
      return this.windowFrameHeight;
   }

   public int getSceneWidth() {
      return this.sceneBuffer == null ? 1 : this.sceneBuffer.getWidth();
   }

   public int getSceneHeight() {
      return this.sceneBuffer == null ? 1 : this.sceneBuffer.getHeight();
   }

   public int getHudWidth() {
      return this.hudBuffer == null ? 1 : this.hudBuffer.getWidth();
   }

   public int getHudHeight() {
      return this.hudBuffer == null ? 1 : this.hudBuffer.getHeight();
   }

   public void setSceneSize(float var1) {
      var1 = GameMath.toDecimals(var1, 2);
      if (var1 != this.sceneSize) {
         this.sceneSize = var1;
         this.sceneSizeChanged = true;
      }

   }

   public void setHudSize(float var1) {
      var1 = GameMath.toDecimals(var1, 2);
      if (var1 != this.hudSize) {
         this.hudSize = var1;
         this.hudSizeChanged = true;
      }

   }

   public void startHudDraw() {
      this.hudBuffer.bind();
      this.hudBuffer.clearColor();
      this.hudBuffer.clearDepth();
   }

   public void endHudDraw() {
      this.hudBuffer.unbind();
   }

   public GameFrameBuffer getCurrentBuffer() {
      return this.currentBuffer;
   }

   public void renderHud(float var1) {
      if (GameResources.sharpenShader != null && Settings.sharpenInterface) {
         try {
            GameResources.sharpenShader.use(this.getFrameWidth(), this.getFrameHeight(), this.hudSize - 1.0F);
            GameFrameBuffer.draw(this.hudBuffer.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), () -> {
               GL11.glColor4f(var1, var1, var1, var1);
            });
         } catch (Exception var3) {
            GameResources.sharpenShader.stop();
         }
      } else {
         GameFrameBuffer.draw(this.hudBuffer.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), () -> {
            GL11.glColor4f(var1, var1, var1, var1);
         });
      }

   }

   public void setSceneShade(float var1, float var2, float var3) {
      this.sceneRed = var1;
      this.sceneGreen = var2;
      this.sceneBlue = var3;
   }

   public void setSceneDarkness(float var1) {
      this.sceneDarkness = var1;
   }

   public void setSceneShade(Color var1) {
      this.setSceneShade((float)var1.getRed() / 255.0F, (float)var1.getGreen() / 255.0F, (float)var1.getBlue() / 255.0F);
   }

   public void startSceneDraw() {
      this.sceneBuffer.bind();
      this.sceneBuffer.clearColor();
      this.sceneBuffer.clearDepth();
   }

   public void endSceneDraw() {
      this.sceneBuffer.unbind();
   }

   public void applyDraw(Runnable var1, Runnable var2) {
      this.currentBuffer.applyDraw(var1, var2);
   }

   public void startSceneOverlayDraw() {
      this.sceneOverlayBuffer.bind();
      this.sceneOverlayBuffer.clearColor();
      this.sceneOverlayBuffer.clearDepth();
   }

   public void endSceneOverlayDraw() {
      this.sceneOverlayBuffer.unbind();
   }

   public void renderScene(GameFrameBuffer var1) {
      GameFrameBuffer var2 = this.sceneBuffer;
      Iterator var3 = this.scenePostProcessing.iterator();

      while(var3.hasNext()) {
         PostProcessStage var4 = (PostProcessStage)var3.next();
         if (var4.isEnabled()) {
            var2 = var4.doPostProcessing(var2);
         }
      }

      if (var1 != null) {
         var1.bind();
      }

      GameFrameBuffer.draw(var2.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), (Runnable)null);
      GameFrameBuffer.draw(this.sceneOverlayBuffer.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), (Runnable)null);
   }

   public void show() {
      GLFW.glfwShowWindow(this.glfwWindow);
   }

   public boolean isFocused() {
      return GLFW.glfwGetWindowAttrib(this.glfwWindow, 131073) == 1;
   }

   public void requestAttention() {
      GLFW.glfwRequestWindowAttention(this.glfwWindow);
   }

   public void setIcon(GameWindowIcon var1) {
      if (Platform.get() != Platform.MACOSX) {
         GLFWImage.Buffer var2 = GLFWImage.malloc(4);

         try {
            PointerList var3 = new PointerList(new Pointer[0]);
            var3.add((Buffer)GameWindowIcon.bufferAndScaleIcon(var2, 0, var1, 16));
            var3.add((Buffer)GameWindowIcon.bufferAndScaleIcon(var2, 1, var1, 32));
            var3.add((Buffer)GameWindowIcon.bufferAndScaleIcon(var2, 2, var1, 64));
            var3.add((Buffer)GameWindowIcon.bufferAndScaleIcon(var2, 3, var1, 128));
            GLFW.glfwSetWindowIcon(this.glfwWindow, var2);
            var3.freeAll();
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

      }
   }

   public void update() {
      GLFW.glfwSwapBuffers(this.glfwWindow);
   }

   public void requestClose() {
      GLFW.glfwSetWindowShouldClose(this.glfwWindow, true);
   }

   public boolean isCloseRequested() {
      return GLFW.glfwWindowShouldClose(this.glfwWindow);
   }

   public void destroy() {
      if (this.glfwWindow != 0L) {
         this.sceneBuffer.dispose();
         this.scenePostProcessing.forEach(PostProcessStage::dispose);
         this.sceneOverlayBuffer.dispose();
         this.hudBuffer.dispose();
         this.input.dispose();
         Callbacks.glfwFreeCallbacks(this.glfwWindow);
         GLFW.glfwDestroyWindow(this.glfwWindow);
         this.glfwWindow = 0L;
      }

   }

   public void setCursor(Screen.CURSOR var1) {
      long var2 = var1.getCursor();
      GLFW.glfwSetCursor(this.glfwWindow, var2);
   }

   public void printCapabilities(PrintStream var1) {
      GLCapabilities var2 = GL.getCapabilities();
      Class var3 = var2.getClass();
      int var4 = 0;
      Field[] var5 = var3.getFields();
      int var6 = var5.length;

      int var7;
      Field var8;
      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         var4 = Math.max(var4, var8.getName().length());
      }

      var5 = var3.getFields();
      var6 = var5.length;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         StringBuilder var9 = (new StringBuilder()).append(var8.getName());
         int var10 = var4 - var8.getName().length();

         for(int var11 = 0; var11 < var10; ++var11) {
            var9.append(".");
         }

         var9.append(": ");

         Object var14;
         try {
            var14 = var8.get(var2);
            if (var14 instanceof Long) {
               var14 = Long.toHexString((Long)var14);
            } else if (var14 instanceof Integer) {
               var14 = Integer.toHexString((Integer)var14);
            } else if (var14 instanceof Short) {
               var14 = Integer.toHexString((Short)var14);
            } else if (var14 instanceof Byte) {
               var14 = Integer.toHexString((Byte)var14);
            }
         } catch (IllegalAccessException var13) {
            var14 = "ERR";
         }

         var9.append(var14);
         var1.println(var9);
      }

   }

   public void setCursorMode(int var1) {
      GLFW.glfwSetInputMode(this.glfwWindow, 208897, var1);
   }

   public void putClipboard(String var1) {
      GLFW.glfwSetClipboardString(this.glfwWindow, var1);
   }

   public String getClipboard() {
      return (String)GLFWGameError.tryGLFWError(new GLFWGameError.Supplier<String>(new int[]{65545}) {
         public String run() {
            return GLFW.glfwGetClipboardString(GameWindow.this.glfwWindow);
         }

         public String onCatch(GLFWGameError var1) {
            return var1.errorName;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object onCatch(GLFWGameError var1) {
            return this.onCatch(var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object run() {
            return this.run();
         }
      });
   }

   public void requestFocus() {
      GLFW.glfwFocusWindow(this.glfwWindow);
   }

   public FBOType getFBOCapabilities() {
      return this.FBO_CAPABILITIES;
   }

   static {
      minDynamicInterfaceWidth = minDynamicInterfaceHeight * 16 / 9;
      expectedDynamicInterfaceHeight = 1080;
      expectedDynamicInterfaceWidth = expectedDynamicInterfaceHeight * 16 / 9;
      minDynamicSceneHeight = 1080;
      minDynamicSceneWidth = minDynamicSceneHeight * 16 / 9;
      expectedDynamicSceneHeight = 1080;
      expectedDynamicSceneWidth = expectedDynamicSceneHeight * 16 / 9;
      minSceneSize = 1.0F;
      maxSceneSize = 4.0F;
   }

   public static enum FBOType {
      GL30,
      EXT;

      private FBOType() {
      }

      // $FF: synthetic method
      private static FBOType[] $values() {
         return new FBOType[]{GL30, EXT};
      }
   }

   private class WindowFrameBuffer extends GameFrameBuffer {
      public WindowFrameBuffer(Consumer<GameFrameBuffer> var2) {
         super(var2);
      }

      public boolean isComplete() {
         return true;
      }

      public int getWidth() {
         return GameWindow.this.getFrameWidth();
      }

      public int getHeight() {
         return GameWindow.this.getFrameHeight();
      }

      public int getColorBufferTextureID() {
         return 0;
      }

      public int getDepthBufferTextureID() {
         return 0;
      }

      public void applyDraw(Runnable var1, Runnable var2) {
         var1.run();
      }

      public void glBind() {
         if (GameWindow.this.FBO_CAPABILITIES == GameWindow.FBOType.EXT) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
         } else {
            GL30.glBindFramebuffer(36160, 0);
         }

      }

      public void glUnbind() {
         this.glBind();
      }

      public void dispose() {
      }
   }
}
