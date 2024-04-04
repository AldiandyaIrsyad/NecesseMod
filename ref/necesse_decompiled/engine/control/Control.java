package necesse.engine.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.gameFont.CustomGameFont;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;

public class Control {
   private static boolean controlsLoaded;
   private static ArrayList<Control> controls = new ArrayList();
   private static HashMap<String, Integer> controlsIDs = new HashMap();
   private static HashSet<ControlGroup> controlGroups = new HashSet();
   private static HashMap<String, ControlGroup> modGroups = new HashMap();
   private int key;
   private boolean isPressed;
   private boolean isReleased;
   public final GameMessage text;
   public GameMessage tooltip;
   public final String id;
   public final LoadedMod mod;
   private ControlGroup group;
   private int overlapGroup;
   private InputEvent activateEvent;
   public ControllerButtonState controllerState;
   private MouseWheelBuffer wheelBuffer;
   public static Control MOUSE1;
   public static Control MOUSE2;
   public static Control MOVE_UP;
   public static Control MOVE_DOWN;
   public static Control MOVE_LEFT;
   public static Control MOVE_RIGHT;
   public static Control MOVE_TO_MOUSE;
   public static Control INVENTORY;
   public static Control HEALTH_POT;
   public static Control MANA_POT;
   public static Control BUFF_POTS;
   public static Control EAT_FOOD;
   public static Control USE_MOUNT;
   public static Control SET_ABILITY;
   public static Control TRINKET_ABILITY;
   public static Control PLACE_TORCH;
   public static Control OPEN_ADVENTURE_PARTY;
   public static Control OPEN_SETTLEMENT;
   public static Control SMART_MINING;
   public static Control LOOT_ALL;
   public static Control SORT_INVENTORY;
   public static Control QUICK_STACK;
   public static Control NEXT_HOTBAR;
   public static Control PREV_HOTBAR;
   public static Control HOTBAR_SLOT_0;
   public static Control HOTBAR_SLOT_1;
   public static Control HOTBAR_SLOT_2;
   public static Control HOTBAR_SLOT_3;
   public static Control HOTBAR_SLOT_4;
   public static Control HOTBAR_SLOT_5;
   public static Control HOTBAR_SLOT_6;
   public static Control HOTBAR_SLOT_7;
   public static Control HOTBAR_SLOT_8;
   public static Control HOTBAR_SLOT_9;
   public static Control SHOW_MAP;
   public static Control SHOW_WORLD_MAP;
   public static Control SCOREBOARD;
   public static Control HIDE_UI;
   public static Control SCREENSHOT;
   public static Control ZOOM_IN;
   public static Control ZOOM_OUT;
   public static Control PIPETTE;
   public static Control DEBUG_INFO;
   public static Control DEBUG_HUD;
   public static Control INV_LOCK;
   public static Control INV_QUICK_MOVE;
   public static Control INV_QUICK_TRASH;
   public static Control CRAFT_ALL;
   public static Control[] HOTBAR_SLOTS;

   public Control(int var1, String var2) {
      this(var1, var2, new LocalMessage("controls", var2), "default");
   }

   public Control(int var1, String var2, String var3) {
      this(var1, var2, new LocalMessage("controls", var2), var3);
   }

   public Control(int var1, String var2, GameMessage var3) {
      this(var1, var2, var3, "default");
   }

   public Control(int var1, String var2, GameMessage var3, String var4) {
      this.wheelBuffer = new MouseWheelBuffer(false);
      this.key = var1;
      this.id = var2;
      this.text = var3;
      this.overlapGroup = var4.hashCode();
      this.mod = LoadedMod.getRunningMod();
   }

   public Control setTooltip(GameMessage var1) {
      this.tooltip = var1;
      return this;
   }

   public void changeKey(int var1) {
      this.key = var1;
   }

   public void activate(InputEvent var1) {
      if (var1.getID() == -103) {
         this.wheelBuffer.add(var1);
         if (this.wheelBuffer.useAllScrollY() > 0) {
            this.activateEvent = var1;
            this.isPressed = true;
         } else {
            this.activateEvent = var1;
            this.isReleased = true;
         }
      } else if (var1.getID() == -102) {
         this.wheelBuffer.add(var1);
         if (this.wheelBuffer.useAllScrollY() < 0) {
            this.activateEvent = var1;
            this.isPressed = true;
         } else {
            this.activateEvent = var1;
            this.isReleased = true;
         }
      } else {
         this.activateEvent = var1;
         if (var1.state) {
            this.isPressed = true;
         } else {
            this.isReleased = true;
         }
      }

   }

   public void reset() {
      this.isPressed = false;
      this.isReleased = false;
   }

   public boolean isDown() {
      if (!Screen.isFocused()) {
         return false;
      } else if (this.activateEvent != null && !this.activateEvent.isUsed()) {
         return this.activateEvent.isMouseWheelEvent() ? this.isPressed : this.activateEvent.state;
      } else {
         return false;
      }
   }

   public boolean isPressed() {
      return this.activateEvent != null && !this.activateEvent.isUsed() ? this.isPressed : false;
   }

   public boolean isReleased() {
      return this.activateEvent != null && !this.activateEvent.isUsed() ? this.isReleased : false;
   }

   public InputEvent getEvent() {
      return this.activateEvent;
   }

   public boolean isMouseWheel() {
      return InputEvent.isMouseWheelEvent(this.getKey());
   }

   public int getKey() {
      return this.key;
   }

   public String getKeyName() {
      return Input.getName(this.getKey());
   }

   public boolean overlaps(Control var1) {
      return var1 != this && this.getKey() != -1 && var1.overlapGroup == this.overlapGroup && this.getKey() == var1.getKey();
   }

   public static void loadControls() {
      if (!controlsLoaded) {
         ControlGroup var0 = new ControlGroup(1, new LocalMessage("controls", "groupgeneral"));
         ControlGroup var1 = new ControlGroup(2, new LocalMessage("controls", "groupmovement"));
         ControlGroup var2 = new ControlGroup(3, new LocalMessage("controls", "groupmisc"));
         ControlGroup var3 = new ControlGroup(4, new LocalMessage("controls", "grouphotbar"));
         ControlGroup var4 = new ControlGroup(5, new LocalMessage("controls", "groupkeymods"));
         ControlGroup var5 = new ControlGroup(6, new LocalMessage("controls", "groupdebug"));
         MOUSE1 = addControl(new Control(-100, "mouse1"), var0);
         MOUSE2 = addControl(new Control(-99, "mouse2"), var0);
         MOVE_UP = addControl(new Control(87, "moveup"), var1);
         MOVE_LEFT = addControl(new Control(65, "moveleft"), var1);
         MOVE_DOWN = addControl(new Control(83, "movedown"), var1);
         MOVE_RIGHT = addControl(new Control(68, "moveright"), var1);
         MOVE_TO_MOUSE = addControl(new Control(-1, "movetomouse"), var1);
         INVENTORY = addControl(new Control(69, "inventory"), var0);
         HEALTH_POT = addControl(new Control(81, "healthpotion"), var0);
         MANA_POT = addControl(new Control(90, "manapotion"), var0);
         EAT_FOOD = addControl(new Control(-1, "eatfood"), var0);
         PIPETTE = addControl((new Control(-98, "pipette")).setTooltip(new LocalMessage("controls", "pipettetip")), var2);
         SCOREBOARD = addControl(new Control(258, "scoreboard"), var2);
         SHOW_MAP = addControl(new Control(77, "showmap"), var2);
         SHOW_WORLD_MAP = addControl(new Control(78, "showworldmap"), var2);
         ZOOM_IN = addControl(new Control(334, "zoomin"), var2);
         ZOOM_OUT = addControl(new Control(333, "zoomout"), var2);
         HIDE_UI = addControl(new Control(293, "hideui"), var2);
         SCREENSHOT = addControl(new Control(294, "screenshot"), var2);
         DEBUG_INFO = addControl(new Control(290, "debuginfo"), var5);
         DEBUG_HUD = addControl(new Control(291, "debughud"), var5);
         BUFF_POTS = addControl(new Control(66, "buffpotions"), var0);
         PLACE_TORCH = addControl(new Control(82, "placetorch"), var0);
         USE_MOUNT = addControl(new Control(70, "usemount"), var0);
         SET_ABILITY = addControl(new Control(86, "setability"), var0);
         TRINKET_ABILITY = addControl(new Control(32, "trinketability"), var0);
         OPEN_ADVENTURE_PARTY = addControl(new Control(88, "openadventureparty"), var0);
         OPEN_SETTLEMENT = addControl(new Control(67, "opensettlement"), var0);
         SMART_MINING = addControl(new Control(341, "smartmining"), var0);
         LOOT_ALL = addControl(new Control(-1, "lootall"), var0);
         SORT_INVENTORY = addControl(new Control(-1, "sortinventory"), var0);
         QUICK_STACK = addControl(new Control(-1, "quickstack"), var0);
         NEXT_HOTBAR = addControl(new Control(-102, "nexthotbar"), var3);
         PREV_HOTBAR = addControl(new Control(-103, "prevhotbar"), var3);
         HOTBAR_SLOT_0 = addControl(new Control(49, "hotbarslot0", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "1")), var3);
         HOTBAR_SLOT_1 = addControl(new Control(50, "hotbarslot1", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "2")), var3);
         HOTBAR_SLOT_2 = addControl(new Control(51, "hotbarslot2", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "3")), var3);
         HOTBAR_SLOT_3 = addControl(new Control(52, "hotbarslot3", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "4")), var3);
         HOTBAR_SLOT_4 = addControl(new Control(53, "hotbarslot4", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "5")), var3);
         HOTBAR_SLOT_5 = addControl(new Control(54, "hotbarslot5", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "6")), var3);
         HOTBAR_SLOT_6 = addControl(new Control(55, "hotbarslot6", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "7")), var3);
         HOTBAR_SLOT_7 = addControl(new Control(56, "hotbarslot7", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "8")), var3);
         HOTBAR_SLOT_8 = addControl(new Control(57, "hotbarslot8", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "9")), var3);
         HOTBAR_SLOT_9 = addControl(new Control(48, "hotbarslot9", (new LocalMessage("controls", "hotbarslot")).addReplacement("number", "10")), var3);
         HOTBAR_SLOTS = new Control[]{HOTBAR_SLOT_0, HOTBAR_SLOT_1, HOTBAR_SLOT_2, HOTBAR_SLOT_3, HOTBAR_SLOT_4, HOTBAR_SLOT_5, HOTBAR_SLOT_6, HOTBAR_SLOT_7, HOTBAR_SLOT_8, HOTBAR_SLOT_9};
         INV_LOCK = addControl(new Control(342, "invlock", "invmods"), var4);
         INV_QUICK_MOVE = addControl(new Control(340, "invquickmove", "invmods"), var4);
         INV_QUICK_TRASH = addControl(new Control(341, "invtrash", "invmods"), var4);
         CRAFT_ALL = addControl(new Control(340, "craftall", "craftmods"), var4);
         ControllerInput.init();
         controlsLoaded = true;
      }
   }

   public static void tickControlInputs(Input var0, boolean var1, TickManager var2) {
      Iterator var3 = controls.iterator();

      while(true) {
         Control var4;
         do {
            if (!var3.hasNext()) {
               return;
            }

            var4 = (Control)var3.next();
         } while(var4 == null);

         boolean var5 = true;
         if (var4.getKey() != -1 && (!InputEvent.isKeyboardEvent(var4.key) || !var1)) {
            Iterator var6 = var0.getEvents().iterator();

            while(var6.hasNext()) {
               InputEvent var7 = (InputEvent)var6.next();
               if (var7.getID() == var4.getKey()) {
                  var4.activate(var7);
                  var5 = false;
               }
            }
         }

         if (var5) {
            var4.reset();
         }
      }
   }

   public static void resetControls() {
      Control var1;
      for(Iterator var0 = controls.iterator(); var0.hasNext(); var1.activateEvent = null) {
         var1 = (Control)var0.next();
         var1.reset();
      }

   }

   public static void dispose() {
      ControllerInput.dispose();
   }

   public static Iterable<Control> getControls() {
      return controls;
   }

   public static Stream<Control> streamControls() {
      return controls.stream();
   }

   public static Iterable<ControlGroup> getGroups() {
      return controlGroups;
   }

   public static Stream<ControlGroup> streamGroups() {
      return controlGroups.stream();
   }

   public static boolean isControlsLoaded() {
      return controlsLoaded;
   }

   private static <T extends Control> T addControl(T var0, ControlGroup var1) {
      if (controlsIDs.containsKey(var0.id)) {
         throw new NullPointerException("ERROR: Conflicted control name: Could not add " + var0.id);
      } else {
         controlsIDs.put(var0.id, controls.size());
         controls.add(var0);
         controlGroups.add(var1);
         var1.controls.add(var0);
         return var0;
      }
   }

   public static <T extends Control> T addModControl(T var0) {
      LoadedMod var1 = LoadedMod.getRunningMod();
      if (var1 == null) {
         throw new IllegalStateException("Cannot add controls when outside mod loading");
      } else {
         ControlGroup var2 = (ControlGroup)modGroups.compute(var1.id, (var1x, var2x) -> {
            if (var2x == null) {
               var2x = new ControlGroup(Integer.MAX_VALUE, new StaticMessage(var1.name));
            }

            return var2x;
         });
         return addControl(var0, var2);
      }
   }

   public static Control getControl(String var0) {
      return controlsIDs.containsKey(var0) ? (Control)controls.get((Integer)controlsIDs.get(var0)) : null;
   }

   public static int getControlIconWidth(FontOptions var0, String var1, Control var2, String var3, String var4) {
      return getDrawControlLogic(var0, 0, 0, var1, var2, var3, var4).width;
   }

   public static void drawControlIcon(FontOptions var0, int var1, int var2, String var3, Control var4, String var5, String var6) {
      getDrawControlLogic(var0, var1, var2, var3, var4, var5, var6).draw();
   }

   public static int getControlIconWidth(FontOptions var0, String var1, int var2, String var3, String var4) {
      return getDrawControlLogic(var0, 0, 0, var1, var2, var3, var4).width;
   }

   public static void drawControlIcon(FontOptions var0, int var1, int var2, String var3, int var4, String var5, String var6) {
      getDrawControlLogic(var0, var1, var2, var3, var4, var5, var6).draw();
   }

   public static DrawFlow getDrawControlLogic(FontOptions var0, int var1, int var2, String var3, Control var4, String var5, String var6) {
      return getDrawControlLogic(var0, var1, var2, var3, var4 == null ? -1 : var4.getKey(), var5, var6);
   }

   public static DrawFlow getDrawControlLogic(FontOptions var0, int var1, int var2, String var3, int var4, String var5, String var6) {
      int var7 = 0;
      ArrayList var8 = new ArrayList();
      String var9 = "";
      int var10 = var2 + var0.getSize() / 2 - 8;
      int var11;
      if (var3 != null && !var3.isEmpty()) {
         var7 += drawKey(var1 + var7, var10, var8, var3, var0.getAlpha(), var0.isPixelFont());
         var11 = var1 + var7;
         var8.add(() -> {
            FontManager.bit.drawString((float)var11, (float)(var2 + 2), "+", var0);
         });
         var7 += FontManager.bit.getWidthCeil("+", var0) + 2;
      }

      if (var4 < -1 && var4 >= -100) {
         switch (var4) {
            case -100:
               var7 += drawMouse(var1 + var7, var10, var8, 0, var0.getAlpha());
               break;
            case -99:
               var7 += drawMouse(var1 + var7, var10, var8, 1, var0.getAlpha());
               break;
            case -98:
               var7 += drawMouse(var1 + var7, var10, var8, 2, var0.getAlpha());
               break;
            default:
               var7 += drawMouse(var1 + var7, var10, var8, -1, var0.getAlpha());
               var9 = " - ";
               var11 = var1 + var7;
               var8.add(() -> {
                  FontManager.bit.drawString((float)var11, (float)(var2 + 2), var5, var0);
               });
               var7 += FontManager.bit.getWidthCeil(var5, var0);
         }
      } else {
         var7 += drawKey(var1 + var7, var10, var8, var5, var0.getAlpha(), var0.isPixelFont());
      }

      if (var6 != null && var6.length() > 0) {
         String var13 = var9 + var6;
         int var12 = var1 + var7;
         var8.add(() -> {
            FontManager.bit.drawString((float)(var12 + 2), (float)(var2 + 2), var13, var0);
         });
         var7 += FontManager.bit.getWidthCeil(var13, var0) + 4;
      }

      return new DrawFlow(var7, var8);
   }

   private static int drawMouse(int var0, int var1, ArrayList<Runnable> var2, int var3, float var4) {
      GameTexture var5 = Settings.UI.input;
      int var6 = var0 + 2;
      int var7 = var1 + 2;
      var2.add(() -> {
         var5.initDraw().sprite(0, 0, 16).alpha(var4).draw(var6, var7);
      });
      if (var3 == 0) {
         var2.add(() -> {
            var5.initDraw().sprite(1, 0, 16).color(1.0F, 0.2F, 0.2F, var4).draw(var6, var7);
         });
      } else {
         var2.add(() -> {
            var5.initDraw().sprite(1, 0, 16).alpha(var4).draw(var6, var7);
         });
      }

      if (var3 == 1) {
         var2.add(() -> {
            var5.initDraw().sprite(2, 0, 16).color(1.0F, 0.2F, 0.2F, var4).draw(var6, var7);
         });
      } else {
         var2.add(() -> {
            var5.initDraw().sprite(2, 0, 16).alpha(var4).draw(var6, var7);
         });
      }

      if (var3 == 2) {
         var2.add(() -> {
            var5.initDraw().sprite(3, 0, 16).color(1.0F, 0.2F, 0.2F, var4).draw(var6, var7);
         });
      } else {
         var2.add(() -> {
            var5.initDraw().sprite(3, 0, 16).alpha(var4).draw(var6, var7);
         });
      }

      return 16;
   }

   private static int drawKey(int var0, int var1, ArrayList<Runnable> var2, String var3, float var4, boolean var5) {
      int var6 = var0 + 2;
      int var7 = var1 + 2;
      byte var8;
      byte var9;
      if (var5 && !var3.chars().anyMatch((var0x) -> {
         return !CustomGameFont.fontTextureContains((char)var0x);
      })) {
         var9 = 16;
         var8 = 0;
      } else {
         var9 = 15;
         var8 = 1;
      }

      FontOptions var10 = (new FontOptions(var9)).colorf(0.15F, 0.15F, 0.15F, var4);
      if (var5) {
         var10.forcePixelFont();
      } else {
         var10.forceNonPixelFont();
      }

      int var11 = FontManager.bit.getWidthCeil(var3, var10);
      int var12 = var3.length() <= 1 ? Math.max(var11, 12) : var11;
      GameTexture var13 = Settings.UI.input;

      int var14;
      for(var14 = 0; var14 < var12; var14 += 16) {
         if (var14 > var12 - 16) {
            var2.add(() -> {
               var13.initDraw().spriteSection(1, 1, 16, 0, var14 == 0 ? var12 : var12 % var14, 0, 16).color(1.0F, 1.0F, 1.0F, var4).draw(var6 + var14, var7);
            });
         } else {
            var2.add(() -> {
               var13.initDraw().sprite(1, 1, 16).color(1.0F, 1.0F, 1.0F, var4).draw(var6 + var14, var7);
            });
         }
      }

      var2.add(() -> {
         var13.initDraw().sprite(0, 1, 16).color(1.0F, 1.0F, 1.0F, var4).draw(var6 - 16, var7);
      });
      var2.add(() -> {
         var13.initDraw().sprite(2, 1, 16).color(1.0F, 1.0F, 1.0F, var4).draw(var6 + var12, var7);
      });
      var14 = var6 + var12 / 2 - var11 / 2;
      var2.add(() -> {
         FontManager.bit.drawString((float)var14, (float)(var7 + var8), var3, var10);
      });
      return var12 + 8;
   }

   public static class ControlGroup {
      public final int sort;
      public final GameMessage displayName;
      private ArrayList<Control> controls = new ArrayList();

      public ControlGroup(int var1, GameMessage var2) {
         this.sort = var1;
         this.displayName = var2;
      }

      public Iterable<Control> getControls() {
         return this.controls;
      }
   }

   public static class DrawFlow {
      public final int width;
      public final ArrayList<Runnable> drawLogic;

      public DrawFlow(int var1, ArrayList<Runnable> var2) {
         this.width = var1;
         this.drawLogic = var2;
      }

      public void draw() {
         this.drawLogic.forEach(Runnable::run);
      }
   }
}
