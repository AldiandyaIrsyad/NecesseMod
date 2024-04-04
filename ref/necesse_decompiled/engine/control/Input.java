package necesse.engine.control;

import java.awt.Point;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.GameWindow;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

public class Input {
   public static final int INVALID_EVENT = -1000;
   public static final int REPEAT_EVENT = -105;
   public static final int CONTROLLER_BUTTON = -104;
   public static final int MOUSE_SCROLL_UP = -103;
   public static final int MOUSE_SCROLL_DOWN = -102;
   public static final int MOUSE_MOVE = -101;
   public static final int LEFT_CLICK = -100;
   public static final int RIGHT_CLICK = -99;
   public static final int MOUSE_WHEEL_CLICK = -98;
   /** @deprecated */
   @Deprecated
   public static final int MOUSE_WHEEL = -98;
   public static final int CHARACTER_EVENT = -1;
   public static long lastCursorInputTime;
   public static long lastKeyboardInputTime;
   public static long lastControllerInputTime;
   public static boolean lastInputIsController;
   final GameWindow window;
   private long glfwWindow;
   protected final Object newEventsLock = new Object();
   protected ArrayList<InputEvent> newEvents = new ArrayList();
   protected List<InputEvent> events = new ArrayList();
   protected HashMap<Integer, InputEvent> downEvents = new HashMap();
   protected final Object repeatEventsLock = new Object();
   protected HashMap<Integer, RepeatEventTracker> repeatEvents = new HashMap();
   private boolean clearInput = false;
   private InputPosition mousePos = new InputPosition(0, 0, 0, 0, 0, 0);
   private InputPosition controllerMousePos = new InputPosition(-1000000, -1000000, -1000000, -1000000, -1000000, -1000000);
   private Point setCursorPos;
   private boolean isTyping = false;
   private boolean updateNextMousePos = false;
   private boolean submitNextMoveEvent = false;
   private Callback cursorCallback;
   private Callback keyCallback;
   private Callback charCallback;
   private Callback mouseCallback;
   private Callback scrollCallback;
   private static final HashMap<Integer, String> inputNames = new HashMap();

   public Input(GameWindow var1) {
      this.window = var1;
   }

   public void init(long var1) {
      this.glfwWindow = var1;
      this.freeCallbacks();
      this.cursorCallback = GLFW.glfwSetCursorPosCallback(var1, (var1x, var3, var5) -> {
         this.mousePos = InputPosition.fromWindowPos(this.window, (int)var3, (int)var5);
         synchronized(this.newEventsLock) {
            this.newEvents.add(InputEvent.MouseMoveEvent(this.mousePos(), Screen.tickManager));
         }

         if (this.setCursorPos == null || GameMath.squareDistance((float)this.setCursorPos.x, (float)this.setCursorPos.y, (float)((int)var3), (float)((int)var5)) > 10.0F) {
            lastCursorInputTime = System.currentTimeMillis();
            if (this.setCursorPos != null) {
               System.out.println(GameMath.squareDistance((float)this.setCursorPos.x, (float)this.setCursorPos.y, (float)((int)var3), (float)((int)var5)));
            }
         }

         this.setCursorPos = null;
      });
      this.keyCallback = GLFW.glfwSetKeyCallback(var1, (var1x, var3, var4, var5, var6) -> {
         if (var3 != -1) {
            if (var3 < 0) {
               GameLog.warn.println("Registered invalid keyboard event with key " + var3);
            } else if (this.isTyping || var5 != 2) {
               if (!this.isTyping || isFunctionKey(var3) || this.isKeyDown(getSystemControlKey())) {
                  InputEvent var7 = InputEvent.KeyboardEvent(var3, var5 == 1 || var5 == 2, var4, this.mousePos(), Screen.tickManager);
                  synchronized(this.newEventsLock) {
                     this.newEvents.add(var7);
                  }

                  if (var7.state) {
                     this.downEvents.put(var7.getID(), var7);
                  } else {
                     var7.downStateEvent = (InputEvent)this.downEvents.get(var7.getID());
                     synchronized(this.repeatEventsLock) {
                        this.repeatEvents.remove(var7.getID());
                     }
                  }

                  lastKeyboardInputTime = System.currentTimeMillis();
               }
            }
         }
      });
      this.charCallback = GLFW.glfwSetCharCallback(var1, (var1x, var3) -> {
         if (!this.isKeyDown(getSystemControlKey())) {
            synchronized(this.newEventsLock) {
               this.newEvents.add(InputEvent.CharacterEvent(var3, new String(Character.toChars(var3)), this.mousePos(), Screen.tickManager));
            }

            lastKeyboardInputTime = System.currentTimeMillis();
         }
      });
      this.mouseCallback = GLFW.glfwSetMouseButtonCallback(var1, (var1x, var3, var4, var5) -> {
         InputEvent var6 = InputEvent.MouseButtonEvent(var3, var4 == 1, this.mousePos(), Screen.tickManager);
         synchronized(this.newEventsLock) {
            this.newEvents.add(var6);
         }

         if (var6.state) {
            this.downEvents.put(var6.getID(), var6);
         } else {
            var6.downStateEvent = (InputEvent)this.downEvents.get(var6.getID());
            synchronized(this.repeatEventsLock) {
               this.repeatEvents.remove(var6.getID());
            }
         }

         lastKeyboardInputTime = System.currentTimeMillis();
      });
      this.scrollCallback = GLFW.glfwSetScrollCallback(var1, (var1x, var3, var5) -> {
         synchronized(this.newEventsLock) {
            this.newEvents.add(InputEvent.MouseScrollEvent(var3, var5, this.mousePos(), Screen.tickManager));
         }

         lastKeyboardInputTime = System.currentTimeMillis();
      });
   }

   private void freeCallbacks() {
      if (this.cursorCallback != null) {
         this.cursorCallback.free();
      }

      this.cursorCallback = null;
      if (this.keyCallback != null) {
         this.keyCallback.free();
      }

      this.keyCallback = null;
      if (this.charCallback != null) {
         this.charCallback.free();
      }

      this.charCallback = null;
      if (this.mouseCallback != null) {
         this.mouseCallback.free();
      }

      this.mouseCallback = null;
      if (this.scrollCallback != null) {
         this.scrollCallback.free();
      }

      this.scrollCallback = null;
   }

   public void startRepeatEvents(InputEvent var1, Object... var2) {
      if (var1.getID() != -105) {
         if (!var1.state) {
            throw new IllegalArgumentException("Cannot start from release events");
         } else if (!var1.isMouseClickEvent() && !var1.isKeyboardEvent()) {
            throw new IllegalArgumentException("Event must be mouse click or keyboard");
         } else {
            synchronized(this.repeatEventsLock) {
               if (!this.repeatEvents.containsKey(var1.getID())) {
                  this.repeatEvents.put(var1.getID(), new RepeatEventTracker(var2, System.currentTimeMillis()));
               }

            }
         }
      }
   }

   public void setCursorPosition(int var1, int var2, TickManager var3) {
      if (this.glfwWindow != 0L) {
         this.setCursorPos = new Point(var1, var2);
         this.mousePos = InputPosition.fromWindowPos(this.window, var1, var2);
         GLFW.glfwSetCursorPos(this.glfwWindow, (double)var1, (double)var2);
         synchronized(this.newEventsLock) {
            this.newEvents.add(InputEvent.MouseMoveEvent(this.mousePos(), var3));
         }
      }
   }

   public void tick(boolean var1, TickManager var2) {
      this.isTyping = var1;
      GLFW.glfwPollEvents();
      if (this.updateNextMousePos) {
         MemoryStack var3 = MemoryStack.stackPush();

         try {
            DoubleBuffer var4 = var3.mallocDouble(1);
            DoubleBuffer var5 = var3.mallocDouble(1);
            GLFW.glfwGetCursorPos(this.glfwWindow, var4, var5);
            int var6 = (int)var4.get();
            int var7 = (int)var5.get();
            this.mousePos = InputPosition.fromWindowPos(this.window, var6, var7);
         } catch (Throwable var16) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var16.addSuppressed(var12);
               }
            }

            throw var16;
         }

         if (var3 != null) {
            var3.close();
         }

         this.updateNextMousePos = false;
      }

      synchronized(this.repeatEventsLock) {
         Iterator var18 = this.repeatEvents.keySet().iterator();

         while(var18.hasNext()) {
            int var19 = (Integer)var18.next();
            RepeatEventTracker var20 = (RepeatEventTracker)this.repeatEvents.get(var19);

            while(var20.lastPressTime + (long)var20.nextPressCooldown <= System.currentTimeMillis()) {
               var20.lastPressTime = System.currentTimeMillis();
               if (var20.repeatPresses < 5) {
                  var20.nextPressCooldown = 75;
               } else if (var20.repeatPresses < 10) {
                  var20.nextPressCooldown = 50;
               } else if (var20.repeatPresses < 25) {
                  var20.nextPressCooldown = 25;
               } else if (var20.repeatPresses < 50) {
                  var20.nextPressCooldown = 10;
               } else if (var20.repeatPresses < 100) {
                  var20.nextPressCooldown = 5;
               } else {
                  var20.nextPressCooldown = 1;
               }

               ++var20.repeatPresses;
               var20.lastPressTime += (long)var20.nextPressCooldown;
               synchronized(this.newEventsLock) {
                  this.newEvents.add(InputEvent.RepeatEvent(var20.callers, var20.repeatPresses, var20.nextPressCooldown, this.mousePos(), var2, var19));
               }
            }
         }
      }

      if (this.submitNextMoveEvent) {
         synchronized(this.newEventsLock) {
            this.newEvents.add(InputEvent.MouseMoveEvent(this.mousePos(), var2));
         }

         this.submitNextMoveEvent = false;
      }

      if (this.clearInput) {
         this.newEvents.clear();
         this.clearInput = false;
      }

      synchronized(this.newEventsLock) {
         this.newEvents.removeIf(Objects::isNull);
         this.events = Collections.unmodifiableList(this.newEvents);
         this.newEvents = new ArrayList();
      }

      lastInputIsController = lastControllerInputTime > lastKeyboardInputTime && lastControllerInputTime > lastCursorInputTime - 200L;
   }

   public void clearInput() {
      this.clearInput = true;
   }

   public List<InputEvent> getEvents() {
      return this.events;
   }

   public InputEvent getEvent(int var1) {
      return (InputEvent)this.events.stream().filter((var1x) -> {
         return var1x.getID() == var1;
      }).findFirst().orElse((Object)null);
   }

   public boolean isPressed(int var1) {
      return this.events.stream().anyMatch((var1x) -> {
         return var1x.getID() == var1 && var1x.state;
      });
   }

   public boolean isReleased(int var1) {
      return this.events.stream().anyMatch((var1x) -> {
         return var1x.getID() == var1 && !var1x.state;
      });
   }

   public boolean hasChanged(int var1) {
      return this.events.stream().anyMatch((var1x) -> {
         return var1x.getID() == var1;
      });
   }

   public boolean isKeyDown(int var1) {
      if (this.glfwWindow == 0L) {
         return false;
      } else if (var1 < 0) {
         return GLFW.glfwGetMouseButton(this.glfwWindow, var1 + 100) == 1;
      } else {
         return GLFW.glfwGetKey(this.glfwWindow, var1) == 1;
      }
   }

   public InputPosition mousePos() {
      return lastInputIsController && !ControllerInput.isCursorVisible() ? this.controllerMousePos : this.mousePos;
   }

   public void updateNextMousePos() {
      this.updateNextMousePos = true;
   }

   public void submitNextMoveEvent() {
      this.submitNextMoveEvent = true;
   }

   public void submitInputEvent(InputEvent var1) {
      synchronized(this.newEventsLock) {
         this.newEvents.add(var1);
      }
   }

   public void dispose() {
      this.freeCallbacks();
   }

   public static boolean isFunctionKey(int var0) {
      return var0 >= 0 && inputNames.containsKey(var0);
   }

   public static int getSystemControlKey() {
      switch (Platform.get()) {
         case WINDOWS:
            return 341;
         case MACOSX:
            return 341;
         default:
            return 341;
      }
   }

   public static String getName(int var0) {
      if (var0 < -1 && var0 >= -100) {
         return "MOUSE" + (var0 + 101);
      } else {
         String var1 = (String)inputNames.getOrDefault(var0, (Object)null);
         if (var1 != null) {
            return var1;
         } else {
            try {
               switch (var0) {
                  case -1:
                     return "NOT SET";
                  case 32:
                     return "SPACE";
                  default:
                     return GLFW.glfwGetKeyName(var0, 0).toUpperCase();
               }
            } catch (Exception var3) {
               var1 = "N/A";
               return var1;
            }
         }
      }
   }

   public static String getName(InputEvent var0) {
      return getName(var0.getID());
   }

   static {
      inputNames.put(-102, "SCROLLDOWN");
      inputNames.put(-103, "SCROLLUP");
      inputNames.put(-101, "MOUSEMOVE");
      inputNames.put(-100, "LEFT-CLICK");
      inputNames.put(-99, "RIGHT-CLICK");
      inputNames.put(256, "ESCAPE");
      inputNames.put(257, "ENTER");
      inputNames.put(258, "TAB");
      inputNames.put(259, "BACKSPACE");
      inputNames.put(260, "INSERT");
      inputNames.put(261, "DELETE");
      inputNames.put(262, "RIGHT");
      inputNames.put(263, "LEFT");
      inputNames.put(264, "DOWN");
      inputNames.put(265, "UP");
      inputNames.put(266, "PAGE_UP");
      inputNames.put(267, "PAGE_DOWN");
      inputNames.put(268, "HOME");
      inputNames.put(269, "END");
      inputNames.put(280, "CAPS_LOCK");
      inputNames.put(281, "SCROLL_LOCK");
      inputNames.put(282, "NUM_LOCK");
      inputNames.put(283, "PRINT_SCREEN");
      inputNames.put(284, "PAUSE");
      inputNames.put(290, "F1");
      inputNames.put(291, "F2");
      inputNames.put(292, "F3");
      inputNames.put(293, "F4");
      inputNames.put(294, "F5");
      inputNames.put(295, "F6");
      inputNames.put(296, "F7");
      inputNames.put(297, "F8");
      inputNames.put(298, "F9");
      inputNames.put(299, "F10");
      inputNames.put(300, "F11");
      inputNames.put(301, "F12");
      inputNames.put(302, "F13");
      inputNames.put(303, "F14");
      inputNames.put(304, "F15");
      inputNames.put(305, "F16");
      inputNames.put(306, "F17");
      inputNames.put(307, "F18");
      inputNames.put(308, "F19");
      inputNames.put(309, "F20");
      inputNames.put(310, "F21");
      inputNames.put(311, "F22");
      inputNames.put(312, "F23");
      inputNames.put(313, "F24");
      inputNames.put(314, "F25");
      inputNames.put(320, "KP_0");
      inputNames.put(321, "KP_1");
      inputNames.put(322, "KP_2");
      inputNames.put(323, "KP_3");
      inputNames.put(324, "KP_4");
      inputNames.put(325, "KP_5");
      inputNames.put(326, "KP_6");
      inputNames.put(327, "KP_7");
      inputNames.put(328, "KP_8");
      inputNames.put(329, "KP_9");
      inputNames.put(330, "KP_DECIMAL");
      inputNames.put(331, "KP_DIVIDE");
      inputNames.put(332, "KP_MULTIPLY");
      inputNames.put(333, "KP_SUBTRACT");
      inputNames.put(334, "KP_ADD");
      inputNames.put(335, "KP_ENTER");
      inputNames.put(336, "KP_EQUAL");
      inputNames.put(340, "LSHIFT");
      inputNames.put(341, "LCTRL");
      inputNames.put(342, "LALT");
      inputNames.put(343, "LEFT_SUPER");
      inputNames.put(344, "RSHIFT");
      inputNames.put(345, "RCTRL");
      inputNames.put(346, "RALT");
      inputNames.put(347, "RIGHT_SUPER");
      inputNames.put(348, "MENU");
   }

   protected static class RepeatEventTracker {
      public final Object[] callers;
      public long lastPressTime;
      public int nextPressCooldown;
      public int repeatPresses;

      public RepeatEventTracker(Object[] var1, long var2) {
         this.callers = var1;
         this.lastPressTime = var2;
         this.nextPressCooldown = 250;
      }
   }
}
