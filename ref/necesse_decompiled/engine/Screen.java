package necesse.engine;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;
import javax.swing.JFrame;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerGlyphTip;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.ControllerState;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.gameTool.GameTool;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModRuntimeException;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.screenHudManager.ScreenHudManager;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.RainSoundEffect;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.state.State;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceTimerAverage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapSet;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.GameMusic;
import necesse.gfx.GameResources;
import necesse.gfx.TableContentDraw;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.forms.components.chat.ChatMessageList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.ContinueForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ScreenTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.GameShader;
import necesse.level.maps.Level;
import necesse.reports.CrashJFrame;
import necesse.reports.CrashReportData;
import necesse.reports.GeneralModdingCrashJFrame;
import necesse.reports.ModCrashJFrame;
import necesse.reports.NoticeJFrame;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

public class Screen {
   public static LinkedList<ContinueComponent> loadingNoticeForms = new LinkedList();
   private static GameWindow window;
   private static GameWindowIcon windowIcon;
   private static String alDeviceName;
   private static long alContext;
   private static long alDevice;
   public static TickManager tickManager;
   public static SoundEmitter listener;
   public static ExecutorService musicStreamer;
   private static final Object soundSynchronized = new Object();
   private static ArrayList<SoundPlayer> playingSounds = new ArrayList();
   private static LinkedList<SoundTime> recentSounds = new LinkedList();
   private static PlayingMusicManager musicManager = new PlayingMusicManager();
   private static SoundPlayer weather;
   private static SoundPlayer fadingWeather;
   private static TicksPerSecond richPresenceUpdater = TicksPerSecond.msPerTick(4000);
   private static float sceneRed = 1.0F;
   private static float sceneGreen = 1.0F;
   private static float sceneBlue = 1.0F;
   private static boolean nextSceneUpdate = false;
   private static float nextSceneRed;
   private static float nextSceneGreen;
   private static float nextSceneBlue;
   private static boolean nextSceneDarknessUpdate = false;
   private static float sceneDarkness;
   private static float nextSceneDarkness;
   private static DarknessFade sceneDarknessFade;
   private static boolean fadedHUD = false;
   private static long fadedHUDStartTime;
   private static AtomicBoolean screenshotCooldown = new AtomicBoolean(false);
   private static long screenshotTime;
   private static final long screenshotEffectTime = 350L;
   private static boolean active;
   public static int cursorSizeOffset = 1;
   public static float[] cursorSizes = new float[]{0.75F, 1.0F, 1.25F, 1.5F, 1.75F};
   private static boolean changedCursor;
   private static boolean forceCursorChange;
   private static CURSOR currentCursor = null;
   private static int currentCursorSize;
   private static CURSOR nextCursor = null;
   private static HashMap<Integer, EventStatusBarData> statusBars;
   private static final Object tooltipsSynchronized = new Object();
   private static LinkedList<ScreenTooltips> tooltips;
   private static Point tooltipsFormFocus;
   private static Point tooltipsInteractFocus;
   private static Point tooltipsPlayer;
   private static Point tooltipFocusOffset;
   private static HashMap<String, ControllerGlyphTip> controllerGlyphs;
   private static MouseDraggingElement mouseDraggingElement;
   private static GameLinkedList<CalledGameTool> gameTools = new GameLinkedList();
   private static boolean firstGameToolControllerIsAimBefore;
   public static ScreenHudManager hudManager = new ScreenHudManager();
   private static LinkedList<GameShader> shaderHistory;
   private static GameExceptionHandler exceptionHandler = new GameExceptionHandler("ticking");
   private static boolean isDisposed = false;
   private static HashMapSet<String, Integer> glErrorPrints = new HashMapSet();

   public static float getCursorSizeZoom(int var0) {
      return cursorSizes[GameMath.limit(var0 + cursorSizeOffset, 0, cursorSizes.length - 1)];
   }

   public Screen(GameWindow var1) {
      Screen.window = new GameWindow();
      Screen.window.createWindow(var1, true);
      Screen.window.requestFocus();

      try {
         updateWindow();
         Thread.sleep(100L);
         initALDevice();
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

      var1.destroy();
      this.init();
   }

   private static void updateWindow() {
      updateDisplaySize();
      window.setSceneSize(Settings.sceneSize);
      window.setHudSize(Settings.interfaceSize);
      windowIcon = GameWindowIcon.getRandomIcon();
      window.setIcon(windowIcon);
      window.initInput();
      window.show();
   }

   public static void checkForNewDefaultALDevice() {
      if (Settings.outputDevice == null) {
         ALC10.alcGetString(0L, 4115);
         String var0 = ALC10.alcGetString(0L, 4114);
         if (!Objects.equals(alDeviceName, var0)) {
            initALDevice();
         }
      }

   }

   private static void initALDevice() {
      if (Settings.outputDevice == null || !Settings.outputDevice.equals(alDeviceName)) {
         if (weather != null) {
            weather.dispose();
            weather = null;
         }

         if (fadingWeather != null) {
            fadingWeather.dispose();
            fadingWeather = null;
         }

         musicManager.dispose();
         Iterator var0 = playingSounds.iterator();

         while(var0.hasNext()) {
            SoundPlayer var1 = (SoundPlayer)var0.next();
            var1.dispose();
         }

         playingSounds.clear();
         ALC10.alcMakeContextCurrent(0L);
         if (alDevice != 0L) {
            ALC10.alcCloseDevice(alDevice);
         }

         if (alContext != 0L) {
            ALC10.alcDestroyContext(alContext);
         }

         alDevice = 0L;
         alContext = 0L;
         alDeviceName = "";
         String var6 = "";
         long var7 = 0L;
         if (Settings.outputDevice != null) {
            var7 = ALC10.alcOpenDevice(Settings.outputDevice);
            var6 = Settings.outputDevice;
         }

         if (var7 == 0L) {
            String var3 = ALC10.alcGetString(0L, 4114);
            var7 = ALC10.alcOpenDevice(var3);
            var6 = var3;
            if (var7 == 0L) {
               System.err.println("Could initialize OpenAL: No device was found");
               return;
            }
         }

         alDeviceName = var6;
         alDevice = var7;
         int[] var8 = new int[]{0};
         alContext = ALC10.alcCreateContext(alDevice, var8);
         ALC10.alcMakeContextCurrent(alContext);
         if (alContext == 0L) {
            System.err.println("Could initialize OpenAL: Could not create device context for " + var6);
         } else {
            ALCCapabilities var4 = ALC.createCapabilities(alDevice);
            ALCapabilities var5 = AL.createCapabilities(var4);
            if (!var5.OpenAL11) {
               GameLog.warn.println("OpenAL11 is not supported on this device");
            } else if (!var5.OpenAL10) {
               GameLog.warn.println("OpenAL10 is not supported on this device");
            }

            AL10.alListenerfv(4100, BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F}).rewind());
            AL10.alListenerfv(4100, BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F}).rewind());
            AL10.alListenerfv(4102, BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F}).rewind());
            AL10.alListenerfv(4111, BufferUtils.createFloatBuffer(6).put(new float[]{0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F}).rewind());
         }
      }
   }

   public static boolean isCreated() {
      return window != null && window.isCreated();
   }

   public static boolean audioCreated() {
      return alDevice != 0L && alContext != 0L;
   }

   public static void updateDisplayMode() {
      GameWindow var0 = window;
      window = new GameWindow();
      window.createWindow(var0, true);
      var0.destroy();
      updateWindow();
      window.setupPostProcessing();
      forceCursorChange = true;
      if (currentCursor != null) {
         changeCursor(currentCursor);
      }

      window.show();
      tickWindowResize(true);
   }

   public static String getCurrentAudioDeviceName() {
      return alDeviceName;
   }

   public static void updateAudioDevice() {
      initALDevice();
   }

   public static void reloadInterfaceFromSettings(boolean var0) {
      GlobalData.getCurrentState().reloadInterfaceFromSettings(var0);
   }

   public static void updateSceneSize() {
      float var0 = 1.0F;
      if (Settings.dynamicSceneSize) {
         var0 = GameWindow.getDynamicSize(GameWindow.minDynamicSceneWidth, GameWindow.minDynamicSceneHeight, GameWindow.expectedDynamicSceneWidth, GameWindow.expectedDynamicSceneHeight, window.getFrameWidth(), window.getFrameHeight());
      }

      window.setSceneSize(var0 * Settings.sceneSize);
      input().updateNextMousePos();
      input().submitNextMoveEvent();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public static void updateHudSize() {
      float var0 = 1.0F;
      if (Settings.dynamicInterfaceSize) {
         var0 = GameWindow.getDynamicSize(GameWindow.minDynamicInterfaceWidth, GameWindow.minDynamicInterfaceHeight, GameWindow.expectedDynamicInterfaceWidth, GameWindow.expectedDynamicInterfaceHeight, window.getFrameWidth(), window.getFrameHeight());
      }

      window.setHudSize(var0 * Settings.interfaceSize);
      input().updateNextMousePos();
      input().submitNextMoveEvent();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public static void updateDisplaySize() {
      window.updateWindowSize();
   }

   public static int getWindowWidth() {
      return window.getWidth();
   }

   public static int getWindowHeight() {
      return window.getHeight();
   }

   public static int getFrameWidth() {
      return window.getFrameWidth();
   }

   public static int getFrameHeight() {
      return window.getFrameHeight();
   }

   public static int getSceneWidth() {
      return window.getSceneWidth();
   }

   public static int getSceneHeight() {
      return window.getSceneHeight();
   }

   public static int getHudWidth() {
      return window.getHudWidth();
   }

   public static int getHudHeight() {
      return window.getHudHeight();
   }

   public static void applyDraw(Runnable var0, Runnable var1) {
      window.applyDraw(var0, var1);
   }

   public static GameFrameBuffer getCurrentBuffer() {
      return window.getCurrentBuffer();
   }

   public static GameWindow.FBOType getFBOCapabilities() {
      return window.getFBOCapabilities();
   }

   public static boolean isFocused() {
      return window.isFocused();
   }

   public static void setVSync(boolean var0) {
      window.setVSync(var0);
   }

   public static void setCursorMode(int var0) {
      window.setCursorMode(var0);
   }

   public static Input input() {
      return window.getInput();
   }

   public static InputPosition mousePos() {
      Input var0 = input();
      return var0 == null ? InputPosition.dummyPos() : var0.mousePos();
   }

   public static boolean isKeyDown(int var0) {
      Input var1 = input();
      return var1 == null ? false : var1.isKeyDown(var0);
   }

   public static void submitNextMoveEvent() {
      input().submitNextMoveEvent();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public void init() {
      tickManager = new TickManager("main", Settings.maxFPS) {
         public void update() {
            boolean var1 = (Boolean)Performance.record(this, "other", (Supplier)(() -> {
               int var1 = Settings.maxFPS;
               if (Settings.savePerformanceOnFocusLoss && !Screen.active) {
                  var1 = 30;
               }

               if (this.getMaxFPS() != var1) {
                  this.setMaxFPS(var1);
               }

               boolean var2 = Screen.isFocused();
               if (var2 != Screen.active) {
                  Screen.active = var2;
                  Screen.updateVolume();
                  if (var2) {
                     Screen.checkForNewDefaultALDevice();
                  }
               }

               return Screen.tickWindowResize(false);
            }));

            try {
               boolean var2 = Screen.fadedHUD;
               if (this.isGameTick()) {
                  Screen.musicManager.clearNextMusic();
                  Screen.nextSceneUpdate = false;
                  Screen.nextSceneDarknessUpdate = false;
                  if (!Screen.fadedHUD) {
                     Screen.fadedHUDStartTime = 0L;
                  }

                  Screen.fadedHUD = false;
               }

               FormManager var3 = GlobalData.getCurrentState().getFormManager();
               boolean var4 = var3 != null && (FormTypingComponent.isCurrentlyTyping() || var3.isControllerKeyboardOpen());
               Screen.window.getInput().tick(var4, this);
               boolean var5 = !Settings.reduceUIFramerate || this.isGameTick();
               float var6 = 1.0F;
               if (var2) {
                  long var7 = System.currentTimeMillis() - Screen.fadedHUDStartTime;
                  if (var7 > 500L) {
                     long var9 = 1000L;
                     long var11 = Math.min(var7 - 500L, var9);
                     float var13 = (float)var11 / (float)var9;
                     var6 = GameMath.lerp(var13, 1.0F, 0.1F);
                  }
               }

               Performance.record(this, "other", (Runnable)(() -> {
                  Control.tickControlInputs(Screen.input(), var4, this);
                  ControllerInput.tick(this);
                  Map.Entry var3;
                  if (this.isGameTick()) {
                     Iterator var2 = Screen.statusBars.entrySet().iterator();

                     while(var2.hasNext()) {
                        var3 = (Map.Entry)var2.next();
                        ((EventStatusBarData)var3.getValue()).cleanOldData();
                        if (!((EventStatusBarData)var3.getValue()).hasData()) {
                           var2.remove();
                        }
                     }
                  }

                  synchronized(Screen.tooltipsSynchronized) {
                     Screen.tooltips.clear();
                     Screen.tooltipsFormFocus = null;
                     Screen.tooltipsInteractFocus = null;
                     Screen.tooltipsPlayer = null;
                     Screen.tooltipFocusOffset = null;
                     Screen.controllerGlyphs.clear();
                  }

                  if (!Screen.gameTools.isEmpty()) {
                     GameLinkedList.Element var7 = Screen.gameTools.getFirstElement();
                     GameTool var8 = ((CalledGameTool)var7.object).tool;
                     if (this.isGameTick()) {
                        var8.tick();
                     }

                     if (!var7.isRemoved()) {
                        GameTooltips var4x = var8.getTooltips();
                        if (var4x != null) {
                           Screen.addTooltip(var4x, TooltipLocation.PLAYER);
                        }
                     } else {
                        if (!Screen.gameTools.isEmpty()) {
                           ((CalledGameTool)Screen.gameTools.getFirst()).tool.onRenewed();
                           ControllerInput.setAimIsCursor(((CalledGameTool)Screen.gameTools.getFirst()).controllerIsAimBefore);
                        } else {
                           ControllerInput.setAimIsCursor(Screen.firstGameToolControllerIsAimBefore);
                        }

                        var8 = null;
                     }

                     if (var8 != null && Input.lastInputIsController) {
                        if (var8.forceControllerCursor()) {
                           if (!ControllerInput.isCursorVisible()) {
                              if (var8.canCancel()) {
                                 var7.remove();
                                 if (!Screen.gameTools.isEmpty()) {
                                    ((CalledGameTool)Screen.gameTools.getFirst()).tool.onRenewed();
                                    ControllerInput.setAimIsCursor(((CalledGameTool)Screen.gameTools.getFirst()).controllerIsAimBefore);
                                 } else {
                                    ControllerInput.setAimIsCursor(Screen.firstGameToolControllerIsAimBefore);
                                 }

                                 var8 = null;
                              } else {
                                 ControllerInput.setAimIsCursor(true);
                              }
                           }
                        } else if (!var7.hasNext()) {
                           Screen.firstGameToolControllerIsAimBefore = ControllerInput.isCursorVisible();
                        }
                     }

                     Iterator var9;
                     if (var8 != null) {
                        var9 = Screen.input().getEvents().iterator();

                        while(var9.hasNext()) {
                           InputEvent var5 = (InputEvent)var9.next();
                           if (var5.state && var5.getID() == 256 && var8.canCancel()) {
                              var8.isCancelled();
                              var7.remove();
                              if (!Screen.gameTools.isEmpty()) {
                                 ((CalledGameTool)Screen.gameTools.getFirst()).tool.onRenewed();
                                 ControllerInput.setAimIsCursor(((CalledGameTool)Screen.gameTools.getFirst()).controllerIsAimBefore);
                              } else {
                                 ControllerInput.setAimIsCursor(Screen.firstGameToolControllerIsAimBefore);
                              }

                              var5.use();
                              var8 = null;
                              break;
                           }

                           if (var8.inputEvent(var5)) {
                              var5.use();
                           }

                           if (var7.isRemoved()) {
                              if (!Screen.gameTools.isEmpty()) {
                                 ((CalledGameTool)Screen.gameTools.getFirst()).tool.onRenewed();
                                 ControllerInput.setAimIsCursor(((CalledGameTool)Screen.gameTools.getFirst()).controllerIsAimBefore);
                              } else {
                                 ControllerInput.setAimIsCursor(Screen.firstGameToolControllerIsAimBefore);
                              }

                              var8 = null;
                              break;
                           }
                        }
                     }

                     if (var8 != null) {
                        var9 = ControllerInput.getEvents().iterator();

                        while(var9.hasNext()) {
                           ControllerEvent var10 = (ControllerEvent)var9.next();
                           if (var10.buttonState && (var10.getState() == ControllerInput.MENU_BACK || var10.getState() == ControllerInput.MAIN_MENU) && var8.canCancel()) {
                              var8.isCancelled();
                              var7.remove();
                              if (!Screen.gameTools.isEmpty()) {
                                 ((CalledGameTool)Screen.gameTools.getFirst()).tool.onRenewed();
                                 ControllerInput.setAimIsCursor(((CalledGameTool)Screen.gameTools.getFirst()).controllerIsAimBefore);
                              } else {
                                 ControllerInput.setAimIsCursor(Screen.firstGameToolControllerIsAimBefore);
                              }

                              var10.use();
                              var3 = null;
                              break;
                           }

                           if (var8.controllerEvent(var10)) {
                              var10.use();
                           }

                           if (var7.isRemoved()) {
                              if (!Screen.gameTools.isEmpty()) {
                                 ((CalledGameTool)Screen.gameTools.getFirst()).tool.onRenewed();
                                 ControllerInput.setAimIsCursor(((CalledGameTool)Screen.gameTools.getFirst()).controllerIsAimBefore);
                              } else {
                                 ControllerInput.setAimIsCursor(Screen.firstGameToolControllerIsAimBefore);
                              }

                              var3 = null;
                              break;
                           }
                        }
                     }
                  }

                  if (Screen.input().isPressed(300)) {
                     if (Settings.displayMode != DisplayMode.Borderless && Settings.displayMode != DisplayMode.Fullscreen) {
                        Settings.displayMode = DisplayMode.Borderless;
                     } else {
                        Settings.displayMode = DisplayMode.Windowed;
                     }

                     Screen.updateDisplayMode();
                     Settings.saveClientSettings();
                  }

               }));
               if (Screen.isDisposed) {
                  return;
               }

               Performance.recordConstant(this, "tickTime", (Runnable)(() -> {
                  GlobalData.getCurrentState().frameTick(this);
               }));
               if (Screen.isDisposed) {
                  return;
               }

               if (this.isGameTick()) {
                  if (Screen.nextSceneUpdate) {
                     Screen.sceneRed = Screen.nextSceneRed;
                     Screen.sceneGreen = Screen.nextSceneGreen;
                     Screen.sceneBlue = Screen.nextSceneBlue;
                  } else {
                     Screen.sceneRed = 1.0F;
                     Screen.sceneGreen = 1.0F;
                     Screen.sceneBlue = 1.0F;
                  }

                  Screen.window.setSceneShade(Screen.sceneRed, Screen.sceneGreen, Screen.sceneBlue);
                  if (Screen.sceneDarknessFade != null && !Screen.sceneDarknessFade.apply()) {
                     Screen.sceneDarknessFade = null;
                  }

                  if (Screen.nextSceneDarknessUpdate) {
                     Screen.sceneDarkness = Screen.nextSceneDarkness;
                  } else {
                     Screen.sceneDarkness = 0.0F;
                     Screen.nextSceneDarkness = 0.0F;
                  }

                  Screen.window.setSceneDarkness(Screen.sceneDarkness);
               }

               Performance.recordConstant(this, "drawTime", (Runnable)(() -> {
                  if (!this.isBehind() || !TickManager.skipDrawIfBehind) {
                     Screen.clearShaderHistory();
                     GL11.glClear(16640);
                     Performance.record(this, "scene", (Runnable)(() -> {
                        try {
                           Screen.window.startSceneDraw();
                           GlobalData.getCurrentState().drawScene(this, var1);
                        } finally {
                           Screen.window.endSceneDraw();
                        }

                     }));
                     Performance.record(this, "sceneOverlay", (Runnable)(() -> {
                        try {
                           Screen.window.startSceneOverlayDraw();
                           GlobalData.getCurrentState().drawSceneOverlay(this);
                           if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                              ScreenTooltips.drawAt(Screen.tooltips, TooltipLocation.INTERACT_FOCUS, Screen.tooltipsInteractFocus, GameColor.DEFAULT_COLOR);
                              ScreenTooltips.drawAt(Screen.tooltips, TooltipLocation.PLAYER, Screen.tooltipsPlayer, GameColor.DEFAULT_COLOR);
                           }
                        } finally {
                           Screen.window.endSceneOverlayDraw();
                        }

                     }));
                     Performance.record(this, "postProcess", (Runnable)(() -> {
                        Screen.window.renderScene((GameFrameBuffer)null);
                     }));
                     Performance.record(this, "hudDraw", (Runnable)(() -> {
                        if (var5) {
                           try {
                              Screen.window.startHudDraw();
                              if (!Settings.hideUI && (Settings.showDebugInfo || GlobalData.debugActive())) {
                                 Screen.drawDebug();
                              }

                              GlobalData.getCurrentState().drawHud(this);
                              boolean var2 = false;
                              MouseDraggingElement var3 = Screen.mouseDraggingElement;
                              if (var3 != null) {
                                 if (!var3.isKeyDown(Screen.input())) {
                                    Screen.mouseDraggingElement = null;
                                 } else {
                                    var2 = var3.draw(Screen.mousePos().hudX, Screen.mousePos().hudY);
                                 }
                              }

                              Screen.hudManager.cleanUp();
                              if (!Settings.hideUI) {
                                 if (!var2) {
                                    synchronized(Screen.tooltipsSynchronized) {
                                       Point var6;
                                       InputPosition var15;
                                       if (Input.lastInputIsController) {
                                          if (Screen.tooltipsFormFocus != null) {
                                             Point var5x = new Point(Screen.tooltipsFormFocus);
                                             if (Screen.tooltipFocusOffset != null) {
                                                var5x.x += Screen.tooltipFocusOffset.x;
                                                var5x.y -= Screen.tooltipFocusOffset.y;
                                             }

                                             ScreenTooltips.drawAt(Screen.tooltips, TooltipLocation.FORM_FOCUS, var5x, GameColor.DEFAULT_COLOR);
                                          }

                                          if (ControllerInput.isCursorVisible()) {
                                             var15 = Screen.mousePos();
                                             var6 = new Point(var15.hudX, var15.hudY);
                                             if (Screen.tooltipFocusOffset != null) {
                                                var6.x += Screen.tooltipFocusOffset.x;
                                                var6.y -= Screen.tooltipFocusOffset.y;
                                             }

                                             ScreenTooltips.drawAt(Screen.tooltips, var6.x, var6.y, GameColor.DEFAULT_COLOR);
                                          } else {
                                             ScreenTooltips.drawAt(Screen.tooltips, 0, Screen.getHudHeight(), GameColor.DEFAULT_COLOR);
                                          }
                                       } else {
                                          var15 = Screen.mousePos();
                                          var6 = new Point(var15.hudX, var15.hudY);
                                          if (Screen.tooltipFocusOffset != null) {
                                             var6.x += Screen.tooltipFocusOffset.x;
                                             var6.y -= Screen.tooltipFocusOffset.y;
                                          }

                                          ScreenTooltips.drawAt(Screen.tooltips, var6.x, var6.y, GameColor.DEFAULT_COLOR);
                                       }

                                       if (Input.lastInputIsController) {
                                          int var16 = Screen.getHudWidth();
                                          int var17 = Screen.getHudHeight() - ControllerGlyphTip.getHeight();
                                          Iterator var7 = Screen.controllerGlyphs.values().iterator();

                                          while(var7.hasNext()) {
                                             ControllerGlyphTip var8 = (ControllerGlyphTip)var7.next();
                                             var16 -= var8.getWidth();
                                             var8.draw(var16, var17);
                                          }
                                       }
                                    }
                                 }

                                 Screen.hudManager.draw(this);
                              }

                              Screen.drawScreenshotEffect();
                           } finally {
                              Screen.window.endHudDraw();
                           }
                        }

                     }));
                     Screen.window.renderHud(var6);
                     if (var5) {
                        if (!Screen.changedCursor) {
                           Screen.changeCursor(Screen.CURSOR.DEFAULT);
                        } else {
                           Screen.changeCursor(Screen.nextCursor);
                        }

                        Screen.changedCursor = false;
                     }

                     Screen.update();
                  }
               }));
               Performance.record(this, "other", (Runnable)(() -> {
                  if (!Screen.gameTools.isEmpty()) {
                     CURSOR var1 = ((CalledGameTool)Screen.gameTools.getFirst()).tool.getCursor();
                     if (var1 != null) {
                        Screen.setCursor(var1);
                     }
                  }

                  if (this.isGameTick()) {
                     Screen.richPresenceUpdater.gameTick();
                     if (Screen.richPresenceUpdater.shouldTick()) {
                        GlobalData.getCurrentState().updateSteamRichPresence();
                     }

                     Performance.record(this, "steamCallbacks", (Runnable)(() -> {
                        if (SteamData.isCreated()) {
                           try {
                              SteamAPI.runCallbacks();
                              SteamData.tickStatsStore();
                           } catch (Exception var1) {
                              if (!(var1 instanceof SteamException)) {
                                 throw var1;
                              }

                              if (!SteamData.isOnCallbackErrorCooldown()) {
                                 GameLog.warn.println("Error running Steam callbacks");
                                 SteamData.resetCallbackErrorCooldown();
                              }
                           }
                        }

                     }));
                     SteamData.ConnectInfo var7 = SteamData.tickLobbyConnectRequested();
                     if (var7 != null) {
                        System.out.println("Got Steam request to connect to " + var7.toString());
                        State var2 = GlobalData.getCurrentState();
                        if (var2 instanceof MainMenu) {
                           var7.startConnectionClient((MainMenu)var2);
                           Screen.requestWindowAttention();
                        } else if (var2 instanceof MainGame) {
                           ((MainGame)var2).getClient().instantDisconnect("Quit");
                           MainMenu var3;
                           GlobalData.setCurrentState(var3 = new MainMenu((String)null));
                           var7.startConnectionClient(var3);
                           Screen.requestWindowAttention();
                        }
                     }

                     if (Screen.audioCreated()) {
                        synchronized(Screen.soundSynchronized) {
                           Screen.musicManager.tick();
                           if (Screen.fadingWeather != null) {
                              if (Screen.fadingWeather.isDone()) {
                                 Screen.fadingWeather.dispose();
                                 Screen.fadingWeather = null;
                              } else {
                                 Screen.updateWeatherVol(Screen.fadingWeather);
                              }
                           }

                           if (Screen.weather != null) {
                              if (Screen.weather.isDone()) {
                                 Screen.weather.dispose();
                                 Screen.weather = null;
                              } else {
                                 Screen.updateWeatherVol(Screen.weather);
                              }
                           }

                           Screen.listener = Screen.getALListener();
                           Screen.recentSounds.removeIf((var0) -> {
                              return var0.getAge() > var0.player.gameSound.cooldown;
                           });

                           for(int var8 = 0; var8 < Screen.playingSounds.size(); ++var8) {
                              SoundPlayer var4 = (SoundPlayer)Screen.playingSounds.get(var8);
                              var4.update();
                              if (var4.isDone()) {
                                 var4.dispose();
                                 Screen.playingSounds.remove(var8);
                                 --var8;
                              }
                           }
                        }
                     }
                  }

               }));
               Screen.exceptionHandler.clear(this.isGameTick());
            } catch (Exception var14) {
               Screen.exceptionHandler.submitException(this.isGameTick(), var14, () -> {
                  if (GameExceptionHandler.crashAfterConsecutiveExceptions > 1) {
                     System.err.println("Stuck in error loop, exiting game");
                  }

                  GlobalData.getCurrentState().onCrash(Screen.exceptionHandler.getSavedExceptions());
               });
            } catch (Error var15) {
               Screen.exceptionHandler.submitException(this.isGameTick(), new CriticalGameException(var15), () -> {
                  if (GameExceptionHandler.crashAfterConsecutiveExceptions > 1) {
                     System.err.println("Stuck in error loop, exiting game");
                  }

                  GlobalData.getCurrentState().onCrash(Screen.exceptionHandler.getSavedExceptions());
               });
            }

         }

         public void updateSecond() {
            Performance.record(this, "second", (Runnable)(() -> {
               Localization.cleanListeners();
            }));
         }
      };
      int var1 = getWindowWidth();
      int var2 = getWindowHeight();
      SteamData.init();
      GameAuth.loadAuth();
      System.out.println("Started client on version " + GameInfo.getFullVersionStringAndBuild() + " with authentication " + GameAuth.getAuthentication() + ".");
      musicStreamer = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingDeque(), (var0) -> {
         return new Thread((ThreadGroup)null, var0, "music-streamer");
      });
      statusBars = new HashMap();
      tooltips = new LinkedList();
      tooltipsFormFocus = null;
      tooltipsInteractFocus = null;
      tooltipsPlayer = null;
      tooltipFocusOffset = null;
      controllerGlyphs = new HashMap();
      shaderHistory = new LinkedList();
      GameLoadingScreen.initLoadingScreen(() -> {
         tickWindowResize(false);
         window.startHudDraw();
      }, () -> {
         window.endHudDraw();
         GL11.glClear(16640);
         window.renderHud(1.0F);
         GLFW.glfwPollEvents();
         update();
         if (isCloseRequested()) {
            if (GlobalData.getCurrentState() != null) {
               GlobalData.getCurrentState().onClose();
            }

            dispose();
            System.exit(0);
         }

      });
      queryGLError("PreLoadAll");

      try {
         GlobalData.loadAll(false);
      } catch (Error | Exception var12) {
         Exception var3 = var12;

         for(Object var4 = var12; var4 != null; var4 = ((Throwable)var4).getCause()) {
            ModCrashJFrame var6;
            if (var4 instanceof ModLoadException) {
               var12.printStackTrace();
               dispose(false);
               ModLoadException var20 = (ModLoadException)var4;
               var6 = new ModCrashJFrame(Collections.singletonList(var20.mod), new Throwable[]{var20});
               var6.setVisible(true);
               var6.requestFocus();
               return;
            }

            if (var4 instanceof ModRuntimeException) {
               var12.printStackTrace();
               dispose(false);
               ModRuntimeException var18 = (ModRuntimeException)var4;
               var6 = new ModCrashJFrame(Collections.singletonList(var18.mod), new Throwable[]{var18});
               var6.setVisible(true);
               var6.requestFocus();
               return;
            }

            NoticeJFrame var17;
            if (var4 instanceof OutOfMemoryError) {
               var12.printStackTrace();
               dispose();
               var17 = new NoticeJFrame(400, Localization.translate("misc", "outofmemory"));
               var17.setVisible(true);
               var17.requestFocus();
               return;
            }

            if (var4 instanceof DataFormatException) {
               var12.printStackTrace();
               dispose();
               var17 = new NoticeJFrame(400, Localization.translate("misc", "datacorrupted"));
               var17.setVisible(true);
               var17.requestFocus();
               return;
            }

            if (var4 instanceof IllegalAccessError) {
               Object var5;
               if (!ModLoader.getEnabledMods().isEmpty()) {
                  var5 = new GeneralModdingCrashJFrame(new CrashReportData(Collections.singletonList(var4), (Client)null, (Server)null, "Init"));
               } else {
                  var5 = new CrashJFrame(new CrashReportData(Collections.singletonList(var4), (Client)null, (Server)null, "Init"));
               }

               ((JFrame)var5).setVisible(true);
               ((JFrame)var5).requestFocus();
            }
         }

         try {
            throw var3;
         } catch (ModLoadException var10) {
            throw new RuntimeException(var10);
         }
      }

      queryGLError("PostLoadAll");
      window.setupPostProcessing();
      queryGLError("PostPostProcessingSetup");
      GameLoadingScreen.drawLoadingString(Localization.translate("loading", "finishing"));
      SteamData.loadUserStats();
      LinkedList var14 = new LinkedList();
      if (Settings.showEANotice) {
         NoticeForm var15 = new NoticeForm("startupnotice", 400, 400);
         var15.padding = 2;
         var15.setupNotice((var1x) -> {
            FormFlow var2 = new FormFlow(10);
            var1x.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("misc", "eanotice", new FontOptions(20), 0, var15.getWidth() / 2, 10, var15.getWidth() - 20), 10));
            ((FormLocalCheckBox)var1x.addComponent(new FormLocalCheckBox("ui", "dontshowagain", 5, var2.next(20)))).onClicked((var0) -> {
               Settings.showEANotice = !((FormCheckBox)var0.from).checked;
            });
         });
         var15.onContinue(Settings::saveClientSettings);
         var15.setButtonCooldown(0);
         var14.add(var15);
      }

      HashSet var16 = (HashSet)ModLoader.modErrors.getOrDefault(ModEntry.class, new HashSet());
      if (!var16.isEmpty()) {
         Iterator var22 = var16.iterator();

         while(var22.hasNext()) {
            LoadedMod var19 = (LoadedMod)var22.next();
            var19.initError = true;
            NoticeForm var7 = new NoticeForm("moderrornotice", 400, 400);
            var7.setupNotice((GameMessage)(new LocalMessage("ui", "modiniterror", "mod", var19.name)));
            var7.setButtonCooldown(0);
            var14.addFirst(var7);
         }
      }

      if (loadingNoticeForms != null) {
         var14.addAll(loadingNoticeForms);
      }

      MainMenu var25;
      GlobalData.setCurrentState(var25 = new MainMenu((ContinueForm)null));
      Iterator var21 = var14.iterator();

      while(var21.hasNext()) {
         ContinueComponent var23 = (ContinueComponent)var21.next();
         var25.continueForm(var23);
      }

      if (GameLaunch.instantConnect == null) {
         if (GameLaunch.instantLobbyConnect != null) {
            try {
               SteamData.connectLobby(Long.parseLong(GameLaunch.instantLobbyConnect));
               requestWindowAttention();
            } catch (NumberFormatException var11) {
               System.err.println("Invalid lobby connect id");
            }
         }
      } else {
         String[] var24 = GameLaunch.instantConnect.split(":");
         String var26 = GameLaunch.instantConnect;
         int var8 = 14159;
         if (var24.length > 1) {
            var26 = var24[0];

            try {
               var8 = Integer.parseInt(var24[1]);
               if (var8 < 0 || var8 > 65535) {
                  throw new Exception("Port out of range");
               }
            } catch (Exception var13) {
               System.err.println("Invalid instant connect port, using default");
               var8 = 14159;
            }
         }

         var25.connect((String)null, var26, var8, (MainMenu.ConnectFrom)null);
         requestWindowAttention();
      }

      GameLaunch.instantLobbyConnect = null;
      GameLaunch.instantConnect = null;
      listener = getALListener();
      setCursor(Screen.CURSOR.DEFAULT);
      Control.resetControls();
      GameLoadingScreen.markDone();
      updateWindow();
      window.updateResize();
      tickWindowResize(true);
      tickManager.init();

      do {
         if (isCloseRequested()) {
            GlobalData.getCurrentState().onClose();
            dispose();
            return;
         }

         tickManager.tickLogic();
      } while(!isDisposed);

   }

   private static void drawScreenshotEffect() {
      long var0 = System.currentTimeMillis() - screenshotTime;
      if (var0 < 350L) {
         float var2 = (float)var0 / 350.0F;
         float var3;
         if (var2 > 0.5F) {
            var3 = Math.abs(var2 - 1.0F);
         } else {
            var3 = var2;
         }

         int var4 = (int)((float)getHudHeight() * Math.abs(var3 - 0.5F));
         initQuadDraw(getHudWidth(), getHudHeight() / 2).color(Color.BLACK).draw(0, -var4);
         initQuadDraw(getHudWidth(), getHudHeight() / 2).color(Color.BLACK).draw(0, getHudHeight() / 2 + var4);
      }

   }

   private static boolean isDrawingScreenshotEffect() {
      return System.currentTimeMillis() - screenshotTime < 350L;
   }

   private static void drawDebug() {
      FontManager.bit.drawString(10.0F, 10.0F, "FPS, TPS: " + tickManager.getFPS() + " (" + GameMath.toDecimals(tickManager.getFullDelta(), 2) + "), " + tickManager.getTPS() + " (" + tickManager.getTick() + ")", new FontOptions(16));
      PerformanceTimerAverage var0 = tickManager.getPreviousAverage();
      int var1 = getHudHeight() - 10;
      if (var0 != null) {
         Collection var2 = var0.getChildren().values();
         TableContentDraw var3 = new TableContentDraw();
         FontOptions var4 = new FontOptions(16);
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            PerformanceTimerAverage var6 = (PerformanceTimerAverage)var5.next();
            var3.newRow().addTextColumn(var6.name, var4, 10, 0).addTextColumn(GameMath.toDecimals(var6.getAverageTimePercent(), 3) + "%", var4, 20, 0).addTextColumn(GameUtils.getTimeStringNano(var6.getAverageTime()), var4, 20, 0);
         }

         var3.newRow().addTextColumn("Loop", var4).addTextColumn(GameUtils.getTimeStringNano(var0.getAverageTime()), var4);
         var1 -= var3.getHeight();
         var3.draw(10, var1);
      }

      FontOptions var7 = new FontOptions(12);
      int var8 = getHudHeight() - 2 - (Input.lastInputIsController ? ControllerGlyphTip.getHeight() + 4 : 0);
      var8 -= 12;
      String var9 = Platform.get().getName() + ", Early access build " + SteamData.getApps().getAppBuildId();
      FontManager.bit.drawString((float)(getHudWidth() - 4 - FontManager.bit.getWidthCeil(var9, var7)), (float)var8, var9, var7);
      String var10;
      if (ModLoader.getEnabledMods().size() > 0) {
         var8 -= 12;
         var10 = ModLoader.getEnabledMods().size() + " mod(s) loaded";
         FontManager.bit.drawString((float)(getHudWidth() - 4 - FontManager.bit.getWidthCeil(var10, var7)), (float)var8, var10, var7);
      }

      var8 -= 20;
      var10 = "v. 0.24.2";
      FontOptions var11 = new FontOptions(20);
      FontManager.bit.drawString((float)(getHudWidth() - 4 - FontManager.bit.getWidthCeil(var10, var11)), (float)var8, var10, var11);
   }

   public static void update() {
      window.update();
   }

   public static boolean tickWindowResize(boolean var0) {
      if (!window.tickResize() && !var0) {
         return false;
      } else {
         if (Settings.dynamicSceneSize) {
            updateSceneSize();
         }

         if (Settings.dynamicInterfaceSize) {
            updateHudSize();
         }

         State var1 = GlobalData.getCurrentState();
         if (var1 != null) {
            var1.onWindowResized();
         }

         return true;
      }
   }

   public static void dispose() {
      dispose(true);
   }

   public static void dispose(boolean var0) {
      if (!isDisposed) {
         isDisposed = true;
         if (GlobalData.getCurrentState() != null) {
            GlobalData.getCurrentState().dispose();
         }

         if (var0) {
            ModLoader.getAllMods().forEach(LoadedMod::dispose);
         }

         GameSound.deleteSounds();
         playingSounds.forEach(SoundPlayer::dispose);
         musicManager.dispose();

         try {
            if (musicStreamer != null) {
               musicStreamer.shutdownNow();
               musicStreamer.awaitTermination(2L, TimeUnit.SECONDS);
            }
         } catch (InterruptedException var2) {
         }

         if (weather != null) {
            weather.dispose();
         }

         if (fadingWeather != null) {
            fadingWeather.dispose();
         }

         GameResources.deleteShaders();
         GameTexture.deleteGeneratedTextures();
         FontManager.deleteFonts();
         if (alContext != 0L) {
            ALC10.alcDestroyContext(alContext);
         }

         if (alDevice != 0L) {
            ALC10.alcCloseDevice(alDevice);
         }

         window.destroy();
         GL.setCapabilities((GLCapabilities)null);
         if (Settings.hasLoadedClientSettings()) {
            Settings.saveClientSettings();
         }

         if (GlobalData.stats() != null) {
            GlobalData.stats().saveStatsFile();
         }

         if (GlobalData.achievements() != null) {
            GlobalData.achievements().saveAchievementsFileSafe();
         }

         Control.dispose();
         SteamData.dispose();
      }
   }

   public static void requestClose() {
      window.requestClose();
   }

   private static boolean isCloseRequested() {
      return window.isCloseRequested();
   }

   public static NoticeForm addLoadingNotice(String var0, GameMessage var1, int var2, int var3) {
      NoticeForm var4 = new NoticeForm(var0, var2, var3);
      var4.setupNotice(var1);
      var4.setButtonCooldown(0);
      loadingNoticeForms.add(var4);
      return var4;
   }

   public static NoticeForm addLoadingNotice(String var0, GameMessage var1) {
      return addLoadingNotice(var0, var1, 400, 400);
   }

   public static void updateVolume() {
      synchronized(soundSynchronized) {
         if (playingSounds != null) {
            playingSounds.forEach(SoundPlayer::update);
         }

         if (weather != null) {
            weather.update();
         }

         if (fadingWeather != null) {
            fadingWeather.update();
         }

         musicManager.updateVolume();
      }
   }

   public static int getPlayingSoundsCount() {
      int var0 = 0;
      if (playingSounds != null) {
         var0 += playingSounds.size();
      }

      if (weather != null) {
         ++var0;
      }

      if (fadingWeather != null) {
         ++var0;
      }

      var0 += musicManager.getPlayingCount();
      return var0;
   }

   public static SoundEmitter getALListener() {
      State var0 = GlobalData.getCurrentState();
      return var0 == null ? new SoundEmitter() {
         public float getSoundPositionX() {
            return 0.0F;
         }

         public float getSoundPositionY() {
            return 0.0F;
         }
      } : var0.getALListener();
   }

   public static void takeMapshot(Level var0, Client var1) {
      if (screenshotCooldown.get()) {
         if (var1 != null) {
            var1.chat.addMessage("Cannot take mapshot at this time.");
         }

      } else {
         screenshotCooldown.set(true);

         try {
            GameLoadingScreen.clearLog();
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "mapshot"));
            int var2 = var0.width * 32;
            int var3 = var0.height * 32;
            byte var4 = 3;
            GL20.glUseProgram(0);
            GameFrameBuffer var5 = window.getNewFrameBuffer(var2, var3);
            boolean var13 = false;

            try {
               var13 = true;
               var5.bind();
               if (var5.isComplete()) {
                  GameCamera var6 = new GameCamera(0, 0, var2, var3);
                  var0.drawUtils.draw(var6, var1 == null ? null : var1.getPlayer(), (TickManager)null, true);
                  update();
                  ByteBuffer var7 = BufferUtils.createByteBuffer(var2 * var3 * var4);
                  GL11.glBindTexture(3553, var5.getColorBufferTextureID());
                  GL11.glGetTexImage(3553, 0, 6407, 5121, var7);
                  if (var1 != null) {
                     var1.chat.addMessage(Localization.translate("misc", "mapshottip"));
                  }

                  String var8 = GlobalData.appDataPath() + "screenshots/" + (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
                  (new Thread(new ShotSave(var8, var7, var2, var3, var4, "mapshotsave", var1 == null ? null : var1.chat))).start();
                  var13 = false;
               } else {
                  GameLog.err.println("An error occurred creating mapshot frame buffer.");
                  if (var1 != null) {
                     var1.chat.addMessage("There was an error taking mapshot");
                     var13 = false;
                  } else {
                     var13 = false;
                  }
               }
            } finally {
               if (var13) {
                  var5.unbind();
                  var5.dispose();
                  if (!shaderHistory.isEmpty()) {
                     GameShader var10 = (GameShader)shaderHistory.getLast();
                     if (var10 != null) {
                        var10._ScreenUse();
                     } else {
                        GL20.glUseProgram(0);
                     }
                  }

               }
            }

            var5.unbind();
            var5.dispose();
            if (!shaderHistory.isEmpty()) {
               GameShader var16 = (GameShader)shaderHistory.getLast();
               if (var16 != null) {
                  var16._ScreenUse();
               } else {
                  GL20.glUseProgram(0);
               }
            }
         } catch (Exception var15) {
            screenshotCooldown.set(false);
            var15.printStackTrace();
         }

         window.makeCurrent();
         input().clearInput();
         tickWindowResize(true);
      }
   }

   public static void takeSceneAndHudShot(Client var0) {
      if (screenshotCooldown.get()) {
         if (var0 != null) {
            var0.chat.addMessage("Cannot take scene and hudshot at this time.");
         }

      } else {
         screenshotCooldown.set(true);
         boolean var19 = false;

         label682: {
            GameShader var62;
            label681: {
               try {
                  var19 = true;
                  byte var1 = 4;
                  int var2 = getFrameWidth();
                  int var3 = getFrameHeight();
                  GameFrameBuffer var4 = window.getNewFrameBuffer(var2, var3);

                  try {
                     clearShaderHistory();
                     GL11.glClear(16640);
                     if (var4.isComplete()) {
                        try {
                           window.startSceneDraw();
                           GlobalData.getCurrentState().drawScene(tickManager, true);
                        } finally {
                           window.endSceneDraw();
                        }

                        try {
                           window.startSceneOverlayDraw();
                           GlobalData.getCurrentState().drawSceneOverlay(tickManager);
                        } finally {
                           window.endSceneOverlayDraw();
                        }

                        var4.bind();
                        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                        GL11.glClear(16384);
                        window.renderScene(var4);
                        ByteBuffer var5 = BufferUtils.createByteBuffer(var2 * var3 * var1);
                        GL11.glBindTexture(3553, var4.getColorBufferTextureID());
                        GL11.glGetTexImage(3553, 0, 6408, 5121, var5);
                        String var6 = GlobalData.appDataPath() + "screenshots/scene " + (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
                        (new Thread(new ShotSave(var6, var5, var2, var3, var1, "sceneshotsave", var0 == null ? null : var0.chat))).start();
                     } else {
                        GameLog.err.println("An error occurred creating sceneshot frame buffer.");
                        if (var0 != null) {
                           var0.chat.addMessage("There was an error taking sceneshot");
                        }
                     }
                  } finally {
                     var4.unbind();
                     var4.dispose();
                  }

                  GL20.glUseProgram(0);
                  GameFrameBuffer var63 = window.getNewFrameBuffer(var2, var3);

                  try {
                     if (var63.isComplete()) {
                        try {
                           window.startHudDraw();
                           GlobalData.getCurrentState().drawHud(tickManager);
                           hudManager.cleanUp();
                        } finally {
                           window.endHudDraw();
                        }

                        var63.bind();
                        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                        GL11.glClear(16384);
                        window.renderHud(1.0F);
                        ByteBuffer var64 = BufferUtils.createByteBuffer(var2 * var3 * var1);
                        GL11.glBindTexture(3553, var63.getColorBufferTextureID());
                        GL11.glGetTexImage(3553, 0, 6408, 5121, var64);
                        String var7 = GlobalData.appDataPath() + "screenshots/hud " + (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
                        (new Thread(new ShotSave(var7, var64, var2, var3, var1, "hudshotsave", var0 == null ? null : var0.chat))).start();
                     } else {
                        GameLog.err.println("An error occurred creating hudshot frame buffer.");
                        if (var0 != null) {
                           var0.chat.addMessage("There was an error taking hudshot");
                        }
                     }
                  } finally {
                     var63.unbind();
                     var63.dispose();
                  }

                  var19 = false;
                  break label681;
               } catch (Exception var60) {
                  screenshotCooldown.set(false);
                  var60.printStackTrace();
                  var19 = false;
               } finally {
                  if (var19) {
                     if (!shaderHistory.isEmpty()) {
                        GameShader var11 = (GameShader)shaderHistory.getLast();
                        if (var11 != null) {
                           var11._ScreenUse();
                        } else {
                           GL20.glUseProgram(0);
                        }
                     }

                  }
               }

               if (!shaderHistory.isEmpty()) {
                  var62 = (GameShader)shaderHistory.getLast();
                  if (var62 != null) {
                     var62._ScreenUse();
                  } else {
                     GL20.glUseProgram(0);
                  }
               }
               break label682;
            }

            if (!shaderHistory.isEmpty()) {
               var62 = (GameShader)shaderHistory.getLast();
               if (var62 != null) {
                  var62._ScreenUse();
               } else {
                  GL20.glUseProgram(0);
               }
            }
         }

         window.makeCurrent();
         input().clearInput();
         window.updateResize();
      }
   }

   public static void takeScreenshot(ChatMessageList var0) {
      if (!screenshotCooldown.get() && !isDrawingScreenshotEffect()) {
         playSound(GameResources.cameraShutter, SoundEffect.ui());
         screenshotCooldown.set(true);
         screenshotTime = System.currentTimeMillis();

         try {
            GL11.glReadBuffer(1028);
            int var1 = getWindowWidth();
            int var2 = getWindowHeight();
            byte var3 = 4;
            ByteBuffer var4 = BufferUtils.createByteBuffer(var1 * var2 * var3);
            GL11.glReadPixels(0, 0, var1, var2, 6408, 5121, var4);
            String var5 = GlobalData.appDataPath() + "screenshots/" + (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
            (new Thread(new ShotSave(var5, var4, var1, var2, var3, "screenshotsave", var0))).start();
         } catch (Exception var6) {
            screenshotCooldown.set(false);
            var6.printStackTrace();
         }

         input().clearInput();
      } else {
         if (var0 != null) {
            var0.addMessage("Cannot take screenshot at this time.");
         }

      }
   }

   public static void setGameTool(GameTool var0, Object var1) {
      Objects.requireNonNull(var0);
      if (gameTools.stream().noneMatch((var1x) -> {
         return var1x.tool == var0;
      })) {
         if (gameTools.isEmpty()) {
            firstGameToolControllerIsAimBefore = ControllerInput.isCursorVisible();
         } else {
            ((CalledGameTool)gameTools.getFirst()).tool.onPaused();
            ((CalledGameTool)gameTools.getFirst()).controllerIsAimBefore = ControllerInput.isCursorVisible();
         }

         gameTools.addFirst(new CalledGameTool(var1, var0));
         var0.init();
         if (var0.startControllerCursor()) {
            ControllerInput.setAimIsCursor(true);
         }
      }

   }

   public static void setGameTool(GameTool var0) {
      setGameTool(var0, var0);
   }

   public static boolean clearGameTools(Object var0) {
      CalledGameTool var1 = null;
      if (!gameTools.isEmpty()) {
         var1 = (CalledGameTool)gameTools.getFirst();
      }

      boolean var2 = false;
      Iterator var3 = gameTools.elements().iterator();

      while(var3.hasNext()) {
         GameLinkedList.Element var4 = (GameLinkedList.Element)var3.next();
         if (((CalledGameTool)var4.object).caller == var0) {
            ((CalledGameTool)var4.object).tool.isCleared();
            var4.remove();
            var2 = true;
         }
      }

      if (!gameTools.isEmpty() && gameTools.getFirst() != var1) {
         ((CalledGameTool)gameTools.getFirst()).tool.onRenewed();
         ControllerInput.setAimIsCursor(((CalledGameTool)gameTools.getFirst()).controllerIsAimBefore);
      } else if (var1 != null && gameTools.isEmpty()) {
         ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
      }

      return var2;
   }

   public static boolean clearGameTool(GameTool var0) {
      CalledGameTool var1 = null;
      if (!gameTools.isEmpty()) {
         var1 = (CalledGameTool)gameTools.getFirst();
      }

      boolean var2 = false;
      Iterator var3 = gameTools.elements().iterator();

      while(var3.hasNext()) {
         GameLinkedList.Element var4 = (GameLinkedList.Element)var3.next();
         if (((CalledGameTool)var4.object).tool == var0) {
            ((CalledGameTool)var4.object).tool.isCleared();
            var4.remove();
            var2 = true;
         }
      }

      if (!gameTools.isEmpty() && gameTools.getFirst() != var1) {
         ((CalledGameTool)gameTools.getFirst()).tool.onRenewed();
         ControllerInput.setAimIsCursor(((CalledGameTool)gameTools.getFirst()).controllerIsAimBefore);
      } else if (var1 != null && gameTools.isEmpty()) {
         ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
      }

      return var2;
   }

   public static void setCursor(CURSOR var0) {
      nextCursor = var0;
      changedCursor = true;
   }

   private static void changeCursor(CURSOR var0) {
      if (forceCursorChange || currentCursor != var0 || currentCursorSize != Settings.cursorSize) {
         currentCursor = var0;
         currentCursorSize = Settings.cursorSize;
         window.setCursor(var0);
      }
   }

   public static void setCursorColor(Color var0) {
      Settings.cursorColor = var0;
      GameResources.loadCursors();
      setCursor(currentCursor);
      forceCursorChange = true;
   }

   public static void setCursorSize(int var0) {
      Settings.cursorSize = var0;
      GameResources.loadCursors();
      setCursor(currentCursor);
      forceCursorChange = true;
   }

   public static void requestWindowAttention() {
      window.requestAttention();
   }

   public static void putClipboard(String var0) {
      window.putClipboard(var0);
   }

   public static void putClipboardDefault(String var0) {
      StringSelection var1 = new StringSelection(var0);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, var1);
   }

   public static String getClipboard() {
      return window.getClipboard();
   }

   public static String getClipboardDefault() {
      try {
         return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
      } catch (IOException | UnsupportedFlavorException var1) {
         System.err.println("Could not get clipboard string: " + var1.getMessage());
         return "";
      }
   }

   public static void addTooltip(GameTooltips var0, GameBackground var1, TooltipLocation var2) {
      if (var0 != null) {
         synchronized(tooltipsSynchronized) {
            GameUtils.insertSortedList((List)Screen.tooltips, new ScreenTooltips(var0, var1, var2), Comparator.comparing((var0x) -> {
               return var0x.tooltips.getDrawOrder();
            }, Comparator.reverseOrder()));
         }
      }
   }

   public static void addTooltip(GameTooltips var0, TooltipLocation var1) {
      addTooltip(var0, (GameBackground)null, var1);
   }

   public static void setTooltipsFormFocus(int var0, int var1) {
      tooltipsFormFocus = new Point(var0, var1);
   }

   public static void setTooltipsFormFocus(InputPosition var0) {
      setTooltipsFormFocus(var0.hudX, var0.hudY);
   }

   public static void setTooltipFocusOffset(int var0, int var1) {
      if (tooltipFocusOffset == null) {
         tooltipFocusOffset = new Point(var0, var1);
      } else {
         tooltipFocusOffset = new Point(Math.max(tooltipFocusOffset.x, var0), Math.max(tooltipFocusOffset.y, var1));
      }

   }

   public static void setTooltipsInteractFocus(int var0, int var1) {
      tooltipsInteractFocus = new Point(var0, var1);
   }

   public static void setTooltipsInteractFocus(InputPosition var0) {
      setTooltipsInteractFocus(var0.sceneX, var0.sceneY);
   }

   public static void setTooltipsPlayer(int var0, int var1) {
      tooltipsPlayer = new Point(var0, var1);
   }

   public static void setTooltipsPlayer(InputPosition var0) {
      setTooltipsPlayer(var0.sceneX, var0.sceneY);
   }

   public static void addControllerGlyph(String var0, ControllerState... var1) {
      if (Input.lastInputIsController) {
         synchronized(tooltipsSynchronized) {
            controllerGlyphs.compute(var0, (var2, var3) -> {
               if (var3 == null) {
                  return new ControllerGlyphTip(var0, var1);
               } else {
                  var3.addGlyphs(var1);
                  return var3;
               }
            });
         }
      }
   }

   public static void setMouseDraggingElement(MouseDraggingElement var0) {
      mouseDraggingElement = var0;
   }

   public static MouseDraggingElement getMouseDraggingElement() {
      return mouseDraggingElement;
   }

   /** @deprecated */
   @Deprecated
   public static void registerForHealthBar(Mob var0) {
      registerMobHealthStatusBar(var0);
   }

   public static void registerMobHealthStatusBar(Mob var0) {
      registerEventStatusBar(var0.getUniqueID(), var0.getHealthUnlimited(), var0.getMaxHealth(), var0.getLocalization());
   }

   public static void registerEventStatusBar(int var0, int var1, int var2, GameMessage var3) {
      registerEventStatusBar(var0, var1, var2, () -> {
         return new EventStatusBarData(var3);
      });
   }

   public static void registerEventStatusBar(int var0, int var1, int var2, Supplier<EventStatusBarData> var3) {
      statusBars.compute(var0, (var3x, var4) -> {
         if (var4 == null) {
            var4 = (EventStatusBarData)var3.get();
         }

         var4.append(var1, var2);
         return var4;
      });
   }

   public static Iterable<EventStatusBarData> getStatusBars() {
      return statusBars.values();
   }

   public static void stopShader(GameShader var0) {
      boolean var1 = false;
      GameShader var2;
      if (!shaderHistory.isEmpty()) {
         var2 = (GameShader)shaderHistory.getLast();
         if (var2 == var0) {
            shaderHistory.removeLast();
            GameShader var3 = shaderHistory.isEmpty() ? null : (GameShader)shaderHistory.getLast();
            if (var3 != null) {
               var3._ScreenUse();
            } else {
               GL20.glUseProgram(0);
            }
         } else {
            var1 = true;
         }
      } else {
         var1 = true;
      }

      if (var1) {
         System.err.println("Stopped wrong shader. Remember to stop shader again before stopping another.");
         var2 = shaderHistory.isEmpty() ? null : (GameShader)shaderHistory.getLast();
         System.err.println("Current shader in use is " + var2);
      }

   }

   private static void clearShaderHistory() {
      while(!shaderHistory.isEmpty()) {
         stopShader((GameShader)shaderHistory.getLast());
      }

   }

   public static void useShader(GameShader var0) {
      shaderHistory.add(var0);
      if (var0 != null) {
         var0._ScreenUse();
      } else {
         GL20.glUseProgram(0);
      }

   }

   public static void setWeatherSound(GameSound var0, float var1, float var2, float var3, float var4) {
      if (audioCreated()) {
         if (weather != null && weather.getSecondsLeft() < 0.7F) {
            fadingWeather = weather;
            updateWeatherVol(fadingWeather);
            weather = null;
         }

         Point2D.Float var5 = GameMath.normalize(var1, var2);
         RainSoundEffect var6 = new RainSoundEffect(() -> {
            return Settings.masterVolume * Settings.weatherVolume;
         }, var5.x, var5.y, var3, var4);
         if (weather == null) {
            weather = new SoundPlayer(var0, var6);
            weather.playSound();
         } else {
            weather.effect = var6;
         }

         updateWeatherVol(weather);
      }
   }

   private static void updateWeatherVol(SoundPlayer var0) {
      float var1;
      if (var0.getSecondsLeft() < 0.5F) {
         var1 = var0.getSecondsLeft() / 0.5F;
         var0.effect.volume(var1);
      } else if (var0.getPositionSeconds() < 0.5F) {
         var1 = var0.getPositionSeconds() / 0.5F;
         var0.effect.volume(var1);
      } else {
         var0.effect.volume(1.0F);
      }

      var0.update();
   }

   public static void forceChangeMusic() {
      musicManager.forceChangeMusic();
   }

   public static void setMusic(AbstractMusicList var0, ComparableSequence<Integer> var1) {
      musicManager.setNextMusic(var0, var1);
   }

   public static void setMusic(MusicOptions var0, ComparableSequence<Integer> var1) {
      setMusic((AbstractMusicList)(new MusicList()).addMusic(var0), (ComparableSequence)var1);
   }

   public static void setMusic(GameMusic var0, ComparableSequence<Integer> var1, float var2) {
      setMusic((AbstractMusicList)(new MusicList()).addMusic(var0, var2), (ComparableSequence)var1);
   }

   public static void setMusic(GameMusic var0, ComparableSequence<Integer> var1) {
      setMusic((AbstractMusicList)(new MusicList()).addMusic(var0), (ComparableSequence)var1);
   }

   public static void setMusic(AbstractMusicList var0, int var1) {
      setMusic(var0, new ComparableSequence(var1));
   }

   public static void setMusic(MusicOptions var0, int var1) {
      setMusic(var0, new ComparableSequence(var1));
   }

   public static void setMusic(GameMusic var0, int var1, float var2) {
      setMusic(var0, new ComparableSequence(var1), var2);
   }

   public static void setMusic(GameMusic var0, int var1) {
      setMusic(var0, new ComparableSequence(var1));
   }

   public static void setMusic(AbstractMusicList var0, MusicPriority var1) {
      setMusic(var0, var1.priority);
   }

   public static void setMusic(MusicOptions var0, MusicPriority var1) {
      setMusic(var0, var1.priority);
   }

   public static void setMusic(GameMusic var0, MusicPriority var1, float var2) {
      setMusic(var0, var1.priority, var2);
   }

   public static void setMusic(GameMusic var0, MusicPriority var1) {
      setMusic(var0, var1.priority);
   }

   public static GameMusic getCurrentMusic() {
      return musicManager.getCurrentMusic();
   }

   public static void setSceneShade(float var0, float var1, float var2) {
      if (!nextSceneUpdate) {
         nextSceneRed = var0;
         nextSceneGreen = var1;
         nextSceneBlue = var2;
         nextSceneUpdate = true;
      } else if (var0 == sceneRed && var1 == sceneGreen && var2 == sceneBlue) {
         nextSceneRed = var0;
         nextSceneGreen = var1;
         nextSceneBlue = var2;
      }

   }

   public static void setSceneShade(Color var0) {
      setSceneShade((float)var0.getRed() / 255.0F, (float)var0.getGreen() / 255.0F, (float)var0.getBlue() / 255.0F);
   }

   public static void setSceneDarkness(float var0) {
      nextSceneDarkness = var0;
      nextSceneDarknessUpdate = true;
   }

   public static void setSceneDarknessFade(float var0, float var1, int var2, Supplier<Point> var3, Supplier<Boolean> var4) {
      sceneDarknessFade = new DarknessFade(var0, var1, System.currentTimeMillis(), (long)var2, var3, var4);
   }

   public static void fadeHUD() {
      fadedHUD = true;
      if (fadedHUDStartTime == 0L) {
         fadedHUDStartTime = System.currentTimeMillis();
      }

   }

   public static SoundPlayer playSound(GameSound var0, SoundEffect var1) {
      if (!audioCreated()) {
         return null;
      } else {
         SoundPlayer var2 = new SoundPlayer(var0, var1);
         addQueuedSound(var2);
         return var2;
      }
   }

   private static void addQueuedSound(SoundPlayer var0) {
      synchronized(soundSynchronized) {
         Iterator var2 = recentSounds.iterator();

         SoundTime var3;
         do {
            if (!var2.hasNext()) {
               var0.playSound();
               playingSounds.add(var0);
               if (var0.gameSound.cooldown > 0L) {
                  recentSounds.add(new SoundTime(var0));
               }

               return;
            }

            var3 = (SoundTime)var2.next();
         } while(var3.player.gameSound != var0.gameSound || var3.getAge() >= var0.gameSound.cooldown || !var3.isNear(var0, 50));

         var0.dispose();
      }
   }

   public static void drawLineAdv(int var0, int var1, int var2, int var3, float[] var4) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glBegin(1);
      GL11.glColor4f(var4[0], var4[1], var4[2], var4[3]);
      GL11.glVertex2f((float)var0, (float)var1);
      GL11.glColor4f(var4[4], var4[5], var4[6], var4[7]);
      GL11.glVertex2f((float)var2, (float)var3);
      GL11.glEnd();
   }

   public static void drawRectangleLines(Rectangle var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glBegin(2);
      GL11.glColor4f(var3, var4, var5, var6);
      GL11.glVertex2f(var1 + (float)var0.x, var2 + (float)var0.y);
      GL11.glVertex2f(var1 + (float)var0.x + (float)var0.width, var2 + (float)var0.y);
      GL11.glVertex2f(var1 + (float)var0.x + (float)var0.width, var2 + (float)var0.y + (float)var0.height);
      GL11.glVertex2f(var1 + (float)var0.x, var2 + (float)var0.y + (float)var0.height);
      GL11.glEnd();
   }

   public static void drawRectangleLines(Rectangle var0, GameCamera var1, float var2, float var3, float var4, float var5) {
      drawRectangleLines(var0, (float)(-var1.getX()), (float)(-var1.getY()), var2, var3, var4, var5);
   }

   public static void drawRectangleLines(Rectangle var0, float var1, float var2, float var3, float var4) {
      drawRectangleLines(var0, 0.0F, 0.0F, var1, var2, var3, var4);
   }

   public static void drawShape(Shape var0, float var1, float var2, boolean var3, float var4, float var5, float var6, float var7) {
      float[] var8 = new float[6];
      LinkedList var9 = new LinkedList();

      for(PathIterator var10 = var0.getPathIterator((AffineTransform)null); !var10.isDone(); var10.next()) {
         int var11 = var10.currentSegment(var8);
         if (var11 == 4) {
            break;
         }

         if (var11 == 0 || var11 == 1) {
            var9.add(new Point2D.Float(var8[0] + var1, var8[1] + var2));
         }
      }

      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glColor4f(var4, var5, var6, var7);
      if (var9.size() <= 2) {
         GL11.glBegin(1);
      } else {
         GL11.glBegin(var3 ? 9 : 2);
      }

      Iterator var12 = var9.iterator();

      while(var12.hasNext()) {
         Point2D.Float var13 = (Point2D.Float)var12.next();
         GL11.glVertex2f(var13.x, var13.y);
      }

      GL11.glEnd();
   }

   public static void drawShape(Shape var0, boolean var1, float var2, float var3, float var4, float var5) {
      drawShape(var0, 0.0F, 0.0F, var1, var2, var3, var4, var5);
   }

   public static void drawShape(Shape var0, GameCamera var1, boolean var2, float var3, float var4, float var5, float var6) {
      drawShape(var0, (float)(-var1.getX()), (float)(-var1.getY()), var2, var3, var4, var5, var6);
   }

   public static void drawCircle(int var0, int var1, int var2, int var3, Color var4, boolean var5) {
      drawCircle(var0, var1, var2, var3, (float)var4.getRed() / 255.0F, (float)var4.getGreen() / 255.0F, (float)var4.getBlue() / 255.0F, (float)var4.getAlpha() / 255.0F, var5);
   }

   public static void drawCircle(int var0, int var1, int var2, int var3, float var4, float var5, float var6, float var7, boolean var8) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glColor4f(var4, var5, var6, var7);
      GL11.glBegin(var8 ? 9 : 2);

      for(int var9 = 0; var9 < var3; ++var9) {
         double var10 = 6.283185307179586 * (double)var9 / (double)var3;
         double var12 = (double)var2 * Math.cos(var10);
         double var14 = (double)var2 * Math.sin(var10);
         GL11.glVertex2d(var12 + (double)var0, var14 + (double)var1);
      }

      GL11.glEnd();
   }

   public static void drawLine(int var0, int var1, int var2, int var3, Color var4) {
      drawLineRGBA(var0, var1, var2, var3, (float)var4.getRed() / 255.0F, (float)var4.getGreen() / 255.0F, (float)var4.getBlue() / 255.0F, (float)var4.getAlpha() / 255.0F);
   }

   public static void drawLineRGBA(int var0, int var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glColor4f(var4, var5, var6, var7);
      GL11.glBegin(1);
      GL11.glVertex2f((float)var0, (float)var1);
      GL11.glVertex2f((float)var2, (float)var3);
      GL11.glEnd();
   }

   public static GameTexture getQuadTexture() {
      return GameResources.empty;
   }

   public static TextureDrawOptionsEnd initQuadDraw(int var0, int var1) {
      return getQuadTexture().initDraw().size(var0, var1);
   }

   public static void printGLError(String var0, int var1) {
      if (var1 != 0) {
         switch (var1) {
            case 1280:
               if (var0 != null && !((HashSet)glErrorPrints.get(var0)).contains(var1)) {
                  System.err.println("GLError." + var0 + ": GL_INVALID_ENUM");
                  (new Throwable()).printStackTrace(System.err);
                  glErrorPrints.add(var0, var1);
               }
               break;
            case 1281:
               if (var0 != null && !((HashSet)glErrorPrints.get(var0)).contains(var1)) {
                  System.err.println("GLError." + var0 + ": GL_INVALID_VALUE");
                  (new Throwable()).printStackTrace(System.err);
                  glErrorPrints.add(var0, var1);
               }
               break;
            case 1282:
               if (var0 != null && !((HashSet)glErrorPrints.get(var0)).contains(var1)) {
                  System.err.println("GLError." + var0 + ": GL_INVALID_OPERATION");
                  (new Throwable()).printStackTrace(System.err);
                  glErrorPrints.add(var0, var1);
               }
               break;
            case 1283:
               if (var0 != null && !((HashSet)glErrorPrints.get(var0)).contains(var1)) {
                  System.err.println("GLError." + var0 + ": GL_STACK_OVERFLOW");
                  (new Throwable()).printStackTrace(System.err);
                  glErrorPrints.add(var0, var1);
               }
               break;
            case 1284:
               if (var0 != null && !((HashSet)glErrorPrints.get(var0)).contains(var1)) {
                  System.err.println("GLError." + var0 + ": GL_STACK_UNDERFLOW");
                  (new Throwable()).printStackTrace(System.err);
                  glErrorPrints.add(var0, var1);
               }
               break;
            case 1285:
               if (var0 != null && !((HashSet)glErrorPrints.get(var0)).contains(var1)) {
                  System.err.println("GLError." + var0 + ": GL_OUT_OF_MEMORY");
                  (new Throwable()).printStackTrace(System.err);
                  glErrorPrints.add(var0, var1);
               }
         }

      }
   }

   public static int queryGLError(String var0) {
      int var1 = GL11.glGetError();
      if (var0 != null) {
         printGLError(var0, var1);
      }

      return var1;
   }

   public static enum CURSOR {
      DEFAULT(0),
      INVISIBLE(-1),
      INTERACT(1),
      GRAB_OFF(2),
      GRAB_ON(3),
      LOCK(4),
      UNLOCK(5),
      CARET(6, 4, 8),
      TRASH(7),
      ARROWS_DIAGONAL1(8, 7, 7),
      ARROWS_DIAGONAL2(9, 7, 7),
      ARROWS_VERTICAL(10, 4, 8),
      ARROWS_HORIZONTAL(11, 8, 4);

      private long cursor;
      private final int spriteX;
      private final int xOffset;
      private final int yOffset;

      private CURSOR(int var3, int var4, int var5) {
         this.spriteX = var3;
         this.xOffset = var4;
         this.yOffset = var5;
         this.cursor = 0L;
      }

      private CURSOR(int var3) {
         this(var3, 0, 0);
      }

      public long getCursor() {
         return this.cursor;
      }

      public void loadCursor(GameTexture var1, int var2) {
         if (this.cursor != 0L) {
            GLFW.glfwDestroyCursor(this.cursor);
         }

         MemoryStack var3 = MemoryStack.stackPush();

         try {
            GLFWImage var4 = GLFWImage.malloc(var3);
            int var7 = 0;
            int var8 = 0;
            ByteBuffer var5;
            int var6;
            if (this.spriteX < 0) {
               var5 = (new GameTexture("cursorBuffer", 1, 1)).buffer;
               var6 = 1;
            } else {
               var6 = 32;
               var7 = this.xOffset;
               var8 = this.yOffset;
               GameTexture var9 = new GameTexture(var1, this.spriteX, 0, var6);
               float var10 = Screen.getCursorSizeZoom(var2);
               if (var10 != 1.0F) {
                  var6 = (int)((float)var6 * var10);
                  var7 = (int)((float)var7 * var10);
                  var8 = (int)((float)var8 * var10);
                  var9 = var9.resize(var6, var6);
               }

               var5 = var9.buffer;
            }

            var5.position(0);
            var4.width(var6).height(var6).pixels(var5);
            this.cursor = GLFW.glfwCreateCursor(var4, var7, var8);
         } catch (Throwable var12) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (var3 != null) {
            var3.close();
         }

      }

      // $FF: synthetic method
      private static CURSOR[] $values() {
         return new CURSOR[]{DEFAULT, INVISIBLE, INTERACT, GRAB_OFF, GRAB_ON, LOCK, UNLOCK, CARET, TRASH, ARROWS_DIAGONAL1, ARROWS_DIAGONAL2, ARROWS_VERTICAL, ARROWS_HORIZONTAL};
      }
   }

   private static class DarknessFade {
      public float startDarkness;
      public float endDarkness;
      public long startTime;
      public long timeSpan;
      public Supplier<Point> midGetter;
      public Supplier<Boolean> isValid;

      public DarknessFade(float var1, float var2, long var3, long var5, Supplier<Point> var7, Supplier<Boolean> var8) {
         this.startDarkness = var1;
         this.endDarkness = var2;
         this.startTime = var3;
         this.timeSpan = var5;
         this.midGetter = var7;
         this.isValid = var8;
      }

      public boolean apply() {
         if (this.isValid != null && !(Boolean)this.isValid.get()) {
            return false;
         } else {
            long var1 = System.currentTimeMillis() - this.startTime;
            if (var1 > this.timeSpan) {
               return false;
            } else {
               float var3 = (float)var1 / (float)this.timeSpan;
               float var4 = this.startDarkness + (this.endDarkness - this.startDarkness) * var3;
               Point var5 = (Point)this.midGetter.get();
               GameResources.darknessShader.midScreenX = var5.x;
               GameResources.darknessShader.midScreenY = var5.y;
               Screen.setSceneDarkness(GameMath.limit(var4, 0.0F, 1.0F));
               return true;
            }
         }
      }
   }

   private static class ShotSave implements Runnable {
      private ByteBuffer buffer;
      private String filePath;
      private String translateKey;
      private int imageWidth;
      private int imageHeight;
      private int imageBPP;
      private ChatMessageList chat;

      public ShotSave(String var1, ByteBuffer var2, int var3, int var4, int var5, String var6, ChatMessageList var7) {
         this.filePath = var1;
         this.buffer = var2;
         this.imageWidth = var3;
         this.imageHeight = var4;
         this.imageBPP = var5;
         this.translateKey = var6;
         this.chat = var7;
      }

      public void run() {
         try {
            int var1 = this.imageWidth * this.imageHeight * this.imageBPP;
            if (this.buffer.capacity() != var1) {
               throw new IllegalArgumentException("Buffer incorrect size: " + this.buffer.capacity() + ", expected: " + var1);
            }

            File var2 = new File(this.filePath + ".png");
            GameUtils.mkDirs(var2);
            ByteBuffer var3 = BufferUtils.createByteBuffer(var1);

            for(int var4 = this.imageHeight - 1; var4 >= 0; --var4) {
               int var5 = this.imageWidth * var4 * this.imageBPP;
               byte[] var6 = new byte[this.imageWidth * this.imageBPP];
               this.buffer.position(var5);
               this.buffer.get(var6, 0, var6.length);
               var3.put(var6, 0, var6.length);
            }

            var3.position(0);
            STBImageWrite.stbi_write_png(var2.getAbsolutePath(), this.imageWidth, this.imageHeight, this.imageBPP, var3, 0);
            if (this.chat != null) {
               FairType var8 = new FairType();
               var8.append(FairCharacterGlyph.fromStringToOpenFile(ChatMessage.fontOptions, Localization.translate("misc", this.translateKey, "path", var2.getAbsolutePath()), var2));
               this.chat.addMessage(var8);
            }
         } catch (Exception var7) {
            this.chat.addMessage(GameColor.RED.getColorCode() + "Error saving image: " + var7.getMessage());
            var7.printStackTrace();
            Screen.screenshotCooldown.set(false);
         }

         Screen.screenshotCooldown.set(false);
      }
   }

   private static class CalledGameTool {
      public final Object caller;
      public final GameTool tool;
      public boolean controllerIsAimBefore;

      public CalledGameTool(Object var1, GameTool var2) {
         this.caller = var1;
         this.tool = var2;
      }
   }

   public static enum MusicPriority {
      BIOME(0),
      EVENT(100),
      MUSIC_PLAYER(1000),
      PORTABLE_MUSIC_PLAYER(2000);

      public final int priority;

      private MusicPriority(int var3) {
         this.priority = var3;
      }

      public ComparableSequence<Integer> thenBy(int var1) {
         return (new ComparableSequence(this.priority)).thenBy((Comparable)var1);
      }

      // $FF: synthetic method
      private static MusicPriority[] $values() {
         return new MusicPriority[]{BIOME, EVENT, MUSIC_PLAYER, PORTABLE_MUSIC_PLAYER};
      }
   }

   private static class SoundTime {
      public final SoundPlayer player;
      public final long time;

      public SoundTime(SoundPlayer var1) {
         this.player = var1;
         this.time = System.currentTimeMillis();
      }

      public boolean isNear(SoundPlayer var1, int var2) {
         PrimitiveSoundEmitter var3 = this.player.effect.getEmitter();
         PrimitiveSoundEmitter var4 = var1.effect.getEmitter();
         if (var3 instanceof SoundEmitter && var4 instanceof SoundEmitter) {
            SoundEmitter var5 = (SoundEmitter)var3;
            SoundEmitter var6 = (SoundEmitter)var4;
            float var7 = Math.abs(var5.getSoundPositionX() - var6.getSoundPositionX());
            float var8 = Math.abs(var5.getSoundPositionY() - var6.getSoundPositionY());
            return var7 < (float)var2 && var8 < (float)var2;
         } else {
            return var3 == null && var4 == null;
         }
      }

      public long getAge() {
         return System.currentTimeMillis() - this.time;
      }
   }
}
