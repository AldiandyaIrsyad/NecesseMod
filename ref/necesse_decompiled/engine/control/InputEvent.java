package necesse.engine.control;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;

public class InputEvent {
   private InputEvent parent;
   private LinkedList<InputEvent> children;
   private int lastID;
   private int id;
   public final long tick;
   public final long frame;
   public final boolean state;
   private double mouseWheelX;
   private double mouseWheelY;
   public final InputPosition pos;
   private boolean isMoveUsed;
   private int scancode;
   private Object[] repeatCallers;
   private int repeatCounter;
   private int repeatDelay;
   private int repeatID;
   InputEvent downStateEvent;
   private String key;
   private int codepoint;
   private ControllerEvent controllerEvent;

   public static InputEvent ControllerButtonEvent(ControllerEvent var0, TickManager var1) {
      InputEvent var2 = new InputEvent(-104, var0.buttonState, InputPosition.dummyPos(), var1);
      var2.controllerEvent = var0;
      if (var0.isUsed()) {
         var2.use();
      } else {
         var0.inputEvents.add(var2);
      }

      return var2;
   }

   public static InputEvent KeyboardEvent(int var0, boolean var1, int var2, InputPosition var3, TickManager var4) {
      InputEvent var5 = new InputEvent(var0, var1, var3, var4);
      var5.scancode = var2;
      return var5;
   }

   public static InputEvent CharacterEvent(int var0, String var1, InputPosition var2, TickManager var3) {
      InputEvent var4 = new InputEvent(-1, true, var2, var3);
      var4.codepoint = var0;
      var4.key = var1;
      return var4;
   }

   public static InputEvent MouseButtonEvent(int var0, boolean var1, InputPosition var2, TickManager var3) {
      if (var0 >= 0 && var0 <= 50) {
         return new InputEvent(var0 - 100, var1, var2, var3);
      } else {
         throw new IllegalArgumentException("Mouse button must in range [0-50]");
      }
   }

   public static InputEvent RepeatEvent(Object[] var0, int var1, int var2, InputPosition var3, TickManager var4, int var5) {
      InputEvent var6 = new InputEvent(-105, true, var3, var4);
      var6.repeatCallers = var0;
      var6.repeatCounter = var1;
      var6.repeatDelay = var2;
      var6.repeatID = var5;
      return var6;
   }

   public static InputEvent MouseScrollEvent(double var0, double var2, InputPosition var4, TickManager var5) {
      InputEvent var6 = new InputEvent(var2 < 0.0 ? -102 : -103, true, var4, var5);
      var6.mouseWheelX = var0;
      var6.mouseWheelY = var2;
      return var6;
   }

   public static InputEvent MouseMoveEvent(InputPosition var0, TickManager var1) {
      return new InputEvent(-101, true, var0, var1);
   }

   public static InputEvent ReplacePosEvent(InputEvent var0, InputPosition var1) {
      InputEvent var2 = new InputEvent(var0.getID(), var0.state, var1, var0.tick, var0.frame);
      var2.parent = var0.parent;
      var0.parent.children.add(var2);
      var2.lastID = var0.lastID;
      var2.mouseWheelX = var0.mouseWheelX;
      var2.mouseWheelY = var0.mouseWheelY;
      var2.repeatCallers = var0.repeatCallers;
      var2.repeatCounter = var0.repeatCounter;
      var2.repeatDelay = var0.repeatDelay;
      var2.repeatID = var0.repeatID;
      var2.scancode = var0.scancode;
      var2.key = var0.key;
      var2.codepoint = var0.codepoint;
      var2.isMoveUsed = var0.isMoveUsed;
      return var2;
   }

   public static InputEvent OffsetHudEvent(Input var0, InputEvent var1, int var2, int var3) {
      return ReplacePosEvent(var1, InputPosition.fromHudPos(var0, var1.pos.hudX == Integer.MIN_VALUE ? var1.pos.hudX : var1.pos.hudX + var2, var1.pos.hudY == Integer.MIN_VALUE ? var1.pos.hudY : var1.pos.hudY + var3));
   }

   protected InputEvent(int var1, boolean var2, InputPosition var3, TickManager var4) {
      this(var1, var2, var3, var4 == null ? 0L : var4.getTotalTicks(), var4 == null ? 0L : var4.getTotalFrames());
   }

   protected InputEvent(int var1, boolean var2, InputPosition var3, long var4, long var6) {
      this.children = new LinkedList();
      this.lastID = -1000;
      this.id = var1;
      this.state = var2;
      this.pos = var3;
      this.tick = var4;
      this.frame = var6;
      this.parent = this;
   }

   public int getID() {
      return this.id;
   }

   public int getLastID() {
      return this.lastID;
   }

   public double getMouseWheelX() {
      return this.mouseWheelX;
   }

   public double getMouseWheelY() {
      return this.mouseWheelY;
   }

   public boolean isKeyboardEvent() {
      return isKeyboardEvent(this.getID());
   }

   public boolean isMouseClickEvent() {
      return isMouseClickEvent(this.getID());
   }

   public boolean isCharacterEvent() {
      return isCharacterEvent(this.getID());
   }

   public boolean isMouseMoveEvent() {
      return isMouseMoveEvent(this.getID());
   }

   public boolean isMouseWheelEvent() {
      return isMouseWheelEvent(this.getID());
   }

   public boolean isControllerEvent() {
      return isControllerEvent(this.getID());
   }

   public ControllerEvent getControllerEvent() {
      return this.controllerEvent;
   }

   public boolean wasKeyboardEvent() {
      return isKeyboardEvent(this.getLastID());
   }

   public boolean wasMouseClickEvent() {
      return isMouseClickEvent(this.getLastID());
   }

   public boolean wasCharacterEvent() {
      return isCharacterEvent(this.getLastID());
   }

   public boolean wasMouseMoveEvent() {
      return isMouseMoveEvent(this.getLastID());
   }

   public boolean wasMouseWheelEvent() {
      return isMouseWheelEvent(this.getLastID());
   }

   public boolean wasControllerEvent() {
      return isControllerEvent(this.getLastID());
   }

   public boolean isRepeatEvent(Function<Object, Boolean> var1) {
      return this.isRepeatEvent(0, (Function)var1);
   }

   public boolean isRepeatEvent(int var1, Function<Object, Boolean> var2) {
      if (this.getID() == -105) {
         return this.repeatCallers != null && var1 < this.repeatCallers.length ? (Boolean)var2.apply(this.repeatCallers[var1]) : (Boolean)var2.apply((Object)null);
      } else {
         return false;
      }
   }

   public boolean isRepeatEvent(Object... var1) {
      return this.getID() == -105 ? Arrays.equals(this.repeatCallers, var1) : false;
   }

   public InputEvent getDownStateEvent() {
      return this.downStateEvent;
   }

   public boolean isSameFrame(TickManager var1) {
      return this.frame == var1.getTotalFrames();
   }

   public boolean isSameFrame(InputEvent var1) {
      return var1.frame == this.frame;
   }

   public boolean shouldSubmitSound() {
      if (this.controllerEvent != null) {
         return this.controllerEvent.shouldSubmitSound();
      } else if (this.getID() != -105) {
         return true;
      } else {
         byte var1 = 11;
         if (this.repeatDelay >= var1) {
            return true;
         } else {
            return this.repeatCounter % (var1 / this.repeatDelay) == 0;
         }
      }
   }

   public int getRepeatID() {
      return this.repeatID;
   }

   public boolean isRepeatEvent(Object var1) {
      return this.isRepeatEvent((var1x) -> {
         return Objects.equals(var1x, var1);
      });
   }

   public boolean isRepeatEvent(int var1, Object var2) {
      return this.isRepeatEvent(var1, (var1x) -> {
         return Objects.equals(var1x, var2);
      });
   }

   public String getChar() {
      return this.key;
   }

   public void useMove() {
      if (!this.isMoveUsed()) {
         this.isMoveUsed = true;
         this.children.stream().filter((var1) -> {
            return var1 != this;
         }).forEach(InputEvent::useMove);
         if (this.parent != this) {
            this.parent.useMove();
         }
      }

   }

   public boolean isMoveUsed() {
      return this.isMoveUsed;
   }

   public void use() {
      if (!this.isUsed()) {
         this.lastID = this.id;
         this.id = -1000;
         if (this.parent != this) {
            this.parent.use();
            this.parent.children.stream().filter((var1) -> {
               return var1 != this;
            }).forEach(InputEvent::use);
         }

         this.children.stream().filter((var1) -> {
            return var1 != this;
         }).forEach(InputEvent::use);
         if (this.controllerEvent != null) {
            this.controllerEvent.use();
         }
      }

   }

   public boolean isSameEvent(InputEvent var1) {
      if (var1 == this) {
         return true;
      } else {
         return this.parent != null && this.parent != this ? this.parent.isSameEvent(var1) : this.children.contains(var1);
      }
   }

   public boolean isUsed() {
      return this.getID() == -1000;
   }

   public void startRepeatEvents(Object... var1) {
      Screen.input().startRepeatEvents(this, var1);
   }

   public static boolean isKeyboardEvent(int var0) {
      return var0 >= 0;
   }

   public static boolean isMouseClickEvent(int var0) {
      return var0 <= -50 && var0 >= -100;
   }

   public static boolean isCharacterEvent(int var0) {
      return var0 == -1;
   }

   public static boolean isMouseMoveEvent(int var0) {
      return var0 == -101;
   }

   public static boolean isMouseWheelEvent(int var0) {
      return var0 == -102 || var0 == -103;
   }

   public static boolean isControllerEvent(int var0) {
      return var0 == -104;
   }

   public static boolean isFromSameEvent(InputEvent var0, InputEvent var1) {
      return var0 != null && var1 != null ? Objects.equals(var0.parent, var1.parent) : false;
   }
}
