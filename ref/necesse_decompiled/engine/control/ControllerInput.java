package necesse.engine.control;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import com.codedisaster.steamworks.SteamUtils;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;

public class ControllerInput {
   public static final ControllerRefreshFocusState REFRESH_FOCUS;
   public static final ControllerState REPEAT_EVENT;
   public static final ControllerState CONTROLLER_CONNECTED_EVENT;
   public static final ControllerState CONTROLLER_DISCONNECTED_EVENT;
   public static final ControllerActionSetState GAME_CONTROLS;
   public static final ControllerActionSetLayerState MENU_SET_LAYER;
   public static final ControllerAnalogState MOVE;
   public static final ControllerAnalogState AIM;
   public static final ControllerAnalogState CURSOR;
   public static final ControllerButtonState ATTACK;
   public static final ControllerButtonState INTERACT;
   public static final ControllerButtonState TOGGLE_AIM;
   public static final ControllerButtonState INVENTORY;
   public static final ControllerButtonState HEALTH_POTION;
   public static final ControllerButtonState MANA_POTION;
   public static final ControllerButtonState BUFF_POTIONS;
   public static final ControllerButtonState EAT_FOOD;
   public static final ControllerButtonState USE_MOUNT;
   public static final ControllerButtonState SET_ABILITY;
   public static final ControllerButtonState TRINKET_ABILITY;
   public static final ControllerButtonState PLACE_TORCH;
   public static final ControllerButtonState OPEN_ADVENTURE_PARTY;
   public static final ControllerButtonState OPEN_SETTLEMENT;
   public static final ControllerButtonState MAIN_MENU;
   public static final ControllerButtonState SMART_MINING;
   public static final ControllerButtonState QUICK_STACK_NEARBY;
   public static final ControllerButtonState SHOW_MAP;
   public static final ControllerButtonState SHOW_WORLD_MAP;
   public static final ControllerButtonState SCOREBOARD;
   public static final ControllerButtonState TOGGLE_UI;
   public static final ControllerButtonState ZOOM_IN;
   public static final ControllerButtonState ZOOM_OUT;
   public static final ControllerButtonState NEXT_HOTBAR;
   public static final ControllerButtonState PREV_HOTBAR;
   public static final ControllerButtonState MENU_UP;
   public static final ControllerButtonState MENU_RIGHT;
   public static final ControllerButtonState MENU_DOWN;
   public static final ControllerButtonState MENU_LEFT;
   public static final ControllerButtonState MENU_SELECT;
   public static final ControllerButtonState MENU_BACK;
   public static final ControllerButtonState MENU_NEXT;
   public static final ControllerButtonState MENU_PREV;
   public static final ControllerButtonState MENU_INTERACT_ITEM;
   public static final ControllerButtonState MENU_ITEM_ACTIONS_MENU;
   public static final ControllerButtonState MENU_QUICK_TRANSFER;
   public static final ControllerButtonState MENU_QUICK_TRASH;
   public static final ControllerButtonState MENU_DROP_ITEM;
   public static final ControllerButtonState MENU_LOCK_ITEM;
   public static final ControllerButtonState MENU_MOVE_ONE_ITEM;
   public static final ControllerButtonState MENU_GET_ONE_ITEM;
   public static final ControllerButtonState MENU_QUICK_STACK;
   public static final ControllerButtonState MENU_LOOT_ALL;
   public static final ControllerButtonState MENU_SORT_INVENTORY;
   private static final ArrayList<ControllerState> states = new ArrayList();
   private static final HashMap<String, GameTexture> buttonTextures;
   private static SteamController controller;
   private static SteamControllerHandle[] handles;
   private static SteamControllerHandle latestHandle;
   private static ControllerOriginsUsedHandler usedHandler;
   private static final Object newEventsLock;
   private static ArrayList<ControllerEvent> newEvents;
   private static List<ControllerEvent> events;
   protected static HashMap<Integer, RepeatEventTracker> repeatEvents;
   private static int currentControllers;
   private static long lastControllerDetect;
   private static boolean lastUsedCursor;
   private static boolean isAimCursor;
   private static float aimX;
   private static float aimY;
   private static float cursorJoystickX;
   private static float cursorJoystickY;
   private static boolean useMoveAsMenuNavigation;
   private static boolean isNavigatingWithMove;

   public ControllerInput() {
   }

   public static void init() {
      if (SteamAPI.isSteamRunning()) {
         controller = new SteamController();
         controller.init();
         handles = new SteamControllerHandle[16];

         for(int var0 = 0; var0 < states.size(); ++var0) {
            ((ControllerState)states.get(var0)).init(var0, controller);
         }
      }

   }

   public static GameTexture getButtonGlyph(ControllerState var0) {
      if (controller != null && latestHandle != null) {
         SteamController.ActionOrigin[] var1 = var0.getSteamOriginsOut(controller, latestHandle);
         if (var1 != null && var1[0] != null) {
            String var2 = controller.getGlyphForActionOrigin(var1[0]);
            if (var2 == null) {
               return null;
            } else {
               String var3 = GameUtils.getFileExtension(var2);
               if (var3 != null && var2.substring(0, var2.length() - var3.length() - 1).endsWith("_md")) {
                  String var4 = var2.substring(0, var2.length() - var3.length() - 4) + "_sm." + var3;
                  if ((new File(var4)).exists()) {
                     var2 = var4;
                  }
               }

               GameTexture var7 = (GameTexture)buttonTextures.getOrDefault(var2, (Object)null);
               if (var7 == null) {
                  try {
                     var7 = GameTexture.fromFileRawOutside(var2);
                  } catch (FileNotFoundException var6) {
                     var7 = GameResources.error;
                     var6.printStackTrace();
                  }

                  buttonTextures.put(var2, var7);
               }

               return var7;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void tick(TickManager var0) {
      if (controller != null) {
         controller.runFrame();
         boolean var1 = false;
         usedHandler.clearUsed();
         int var2;
         if (lastControllerDetect < System.currentTimeMillis()) {
            lastControllerDetect = System.currentTimeMillis() + 1000L;
            var2 = currentControllers;
            currentControllers = controller.getConnectedControllers(handles);
            if (var2 != currentControllers) {
               synchronized(newEventsLock) {
                  if (var2 > currentControllers) {
                     newEvents.add(ControllerEvent.customEvent((SteamControllerHandle)null, CONTROLLER_DISCONNECTED_EVENT));
                  } else {
                     newEvents.add(ControllerEvent.customEvent((SteamControllerHandle)null, CONTROLLER_CONNECTED_EVENT));
                  }
               }

               Iterator var3 = states.iterator();

               while(var3.hasNext()) {
                  ControllerState var4 = (ControllerState)var3.next();
                  var4.init(controller);
               }

               var1 = true;
               System.out.println("Detected " + currentControllers + " controllers");
            }
         }

         Iterator var24 = states.iterator();

         while(var24.hasNext()) {
            ControllerState var26 = (ControllerState)var24.next();
            synchronized(newEventsLock) {
               SteamControllerHandle var5 = var26.updateSteamData(controller, handles, currentControllers, newEvents, usedHandler, var1, var0);
               if (var5 != null) {
                  latestHandle = var5;
                  Input.lastControllerInputTime = System.currentTimeMillis();
               }
            }
         }

         var24 = repeatEvents.keySet().iterator();

         int var28;
         while(var24.hasNext()) {
            var28 = (Integer)var24.next();
            RepeatEventTracker var30 = (RepeatEventTracker)repeatEvents.get(var28);

            while(var30.lastPressTime + (long)var30.nextPressCooldown <= System.currentTimeMillis()) {
               var30.lastPressTime = System.currentTimeMillis();
               if (var30.repeatPresses < 5) {
                  var30.nextPressCooldown = 75;
               } else if (var30.repeatPresses < 10) {
                  var30.nextPressCooldown = 50;
               } else if (var30.repeatPresses < 25) {
                  var30.nextPressCooldown = 25;
               } else if (var30.repeatPresses < 50) {
                  var30.nextPressCooldown = 10;
               } else if (var30.repeatPresses < 100) {
                  var30.nextPressCooldown = 5;
               } else {
                  var30.nextPressCooldown = 1;
               }

               ++var30.repeatPresses;
               var30.lastPressTime += (long)var30.nextPressCooldown;
               synchronized(newEventsLock) {
                  newEvents.add(ControllerEvent.repeatEvent(var30.controller, var30.callers, var30.repeatPresses, var30.nextPressCooldown, (ControllerState)states.get(var28)));
               }
            }
         }

         if (useMoveAsMenuNavigation) {
            float var25 = MOVE.getX();
            float var29 = MOVE.getY();
            double var31 = (new Point2D.Float(var25, var29)).distance(0.0, 0.0);
            if (var31 < 0.5) {
               if (isNavigatingWithMove) {
                  repeatEvents.remove(MENU_UP.getID());
                  repeatEvents.remove(MENU_RIGHT.getID());
                  repeatEvents.remove(MENU_DOWN.getID());
                  repeatEvents.remove(MENU_LEFT.getID());
               }

               isNavigatingWithMove = false;
            } else {
               isNavigatingWithMove = true;
               Point2D.Float var6 = GameMath.normalize(var25, var29);
               float var7 = GameMath.getAngle(new Point2D.Float(var6.x, var6.y));
               int var8 = (int)(GameMath.fixAngle(var7 + 90.0F + 45.0F) * 4.0F / 360.0F);
               ControllerEvent var9;
               if (var8 == 0) {
                  if (!repeatEvents.containsKey(MENU_UP.getID())) {
                     var9 = ControllerEvent.buttonEvent(latestHandle, MENU_UP, true);
                     synchronized(newEventsLock) {
                        newEvents.add(var9);
                     }

                     startRepeatEvents(var9, MENU_UP);
                  }
               } else {
                  repeatEvents.remove(MENU_UP.getID());
               }

               if (var8 == 1) {
                  if (!repeatEvents.containsKey(MENU_RIGHT.getID())) {
                     var9 = ControllerEvent.buttonEvent(latestHandle, MENU_RIGHT, true);
                     synchronized(newEventsLock) {
                        newEvents.add(var9);
                     }

                     startRepeatEvents(var9, MENU_RIGHT);
                  }
               } else {
                  repeatEvents.remove(MENU_RIGHT.getID());
               }

               if (var8 == 2) {
                  if (!repeatEvents.containsKey(MENU_DOWN.getID())) {
                     var9 = ControllerEvent.buttonEvent(latestHandle, MENU_DOWN, true);
                     synchronized(newEventsLock) {
                        newEvents.add(var9);
                     }

                     startRepeatEvents(var9, MENU_DOWN);
                  }
               } else {
                  repeatEvents.remove(MENU_DOWN.getID());
               }

               if (var8 == 3) {
                  if (!repeatEvents.containsKey(MENU_LEFT.getID())) {
                     var9 = ControllerEvent.buttonEvent(latestHandle, MENU_LEFT, true);
                     synchronized(newEventsLock) {
                        newEvents.add(var9);
                     }

                     startRepeatEvents(var9, MENU_LEFT);
                  }
               } else {
                  repeatEvents.remove(MENU_LEFT.getID());
               }
            }
         } else {
            if (isNavigatingWithMove) {
               repeatEvents.remove(MENU_UP.getID());
               repeatEvents.remove(MENU_RIGHT.getID());
               repeatEvents.remove(MENU_DOWN.getID());
               repeatEvents.remove(MENU_LEFT.getID());
            }

            isNavigatingWithMove = false;
         }

         if ((isNavigatingWithMove || MENU_UP.isDown() || MENU_RIGHT.isDown() || MENU_DOWN.isDown() || MENU_LEFT.isDown()) && lastUsedCursor) {
            lastUsedCursor = false;
            Screen.submitNextMoveEvent();
         }

         synchronized(newEventsLock) {
            newEvents.removeIf(Objects::isNull);
            events = Collections.unmodifiableList(newEvents);
            newEvents = new ArrayList();
         }

         if (CURSOR.getX() != 0.0F || CURSOR.getY() != 0.0F) {
            if (!isCursorVisible()) {
               Screen.setCursorMode(212993);
            }

            lastUsedCursor = true;
            Screen.submitNextMoveEvent();
            InputPosition var27 = Screen.input().mousePos();
            var28 = GameMath.limit((int)((float)var27.windowX + CURSOR.getX()), 1, Screen.getWindowWidth() - 1);
            int var32 = GameMath.limit((int)((float)var27.windowY - CURSOR.getY()), 1, Screen.getWindowHeight() - 1);
            Screen.input().setCursorPosition(var28, var32, var0);
         }

         if (AIM.getX() == 0.0F && AIM.getY() == 0.0F) {
            aimX = 0.0F;
            aimY = 0.0F;
         } else {
            if (!isAimCursor) {
               aimX = AIM.getX();
               aimY = AIM.getY();
            } else {
               cursorJoystickX += AIM.getX() * var0.getDelta() * Settings.cursorJoystickSensitivity;
               cursorJoystickY += AIM.getY() * var0.getDelta() * Settings.cursorJoystickSensitivity;
               if (Math.abs(cursorJoystickX) > 1.0F || Math.abs(cursorJoystickY) > 1.0F) {
                  var2 = (int)cursorJoystickX;
                  var28 = (int)cursorJoystickY;
                  cursorJoystickX -= (float)var2;
                  cursorJoystickY -= (float)var28;
                  InputPosition var33 = Screen.input().mousePos();
                  int var34 = GameMath.limit(var33.windowX + var2, 1, Screen.getWindowWidth() - 1);
                  int var35 = GameMath.limit(var33.windowY + var28, 1, Screen.getWindowHeight() - 1);
                  Screen.input().setCursorPosition(var34, var35, var0);
               }

               aimX = 0.0F;
               aimY = 0.0F;
            }

            if (lastUsedCursor) {
               lastUsedCursor = false;
               Screen.submitNextMoveEvent();
            }
         }

         if (TOGGLE_AIM.isPressed()) {
            setAimIsCursor(!isAimCursor);
         }

         if (Input.lastInputIsController && !isCursorVisible()) {
            Screen.setCursorMode(212994);
         } else {
            Screen.setCursorMode(212993);
         }

      }
   }

   public static List<ControllerEvent> getEvents() {
      return events;
   }

   public static ControllerEvent getEvent(ControllerState var0) {
      return (ControllerEvent)events.stream().filter((var1) -> {
         return var1.getState() == var0;
      }).findFirst().orElse((Object)null);
   }

   public static boolean isPressed(ControllerState var0) {
      return events.stream().anyMatch((var1) -> {
         return var1.getState() == var0 && var1.buttonState;
      });
   }

   public static boolean isReleased(ControllerState var0) {
      return events.stream().anyMatch((var1) -> {
         return var1.getState() == var0 && !var1.buttonState;
      });
   }

   public static boolean hasChanged(ControllerState var0) {
      return events.stream().anyMatch((var1) -> {
         return var1.getState() == var0;
      });
   }

   public static void startRepeatEvents(ControllerEvent var0, Object... var1) {
      if (var0.getState() != REPEAT_EVENT && var0.getUsedState() != REPEAT_EVENT) {
         if (!var0.buttonState) {
            throw new IllegalArgumentException("Cannot start from release events");
         } else if (!var0.isButton) {
            throw new IllegalArgumentException("Event must be button");
         } else {
            ControllerState var2;
            if (var0.isUsed()) {
               var2 = var0.getUsedState();
            } else {
               var2 = var0.getState();
            }

            if (!repeatEvents.containsKey(var2.getID())) {
               repeatEvents.put(var2.getID(), new RepeatEventTracker(var0.controller, var1, System.currentTimeMillis()));
            }

         }
      }
   }

   public static void setAimIsCursor(boolean var0) {
      boolean var1 = isAimCursor;
      isAimCursor = var0;
      if (!var1 && isAimCursor && Input.lastInputIsController) {
         InputPosition var2 = Screen.mousePos();
         if (var2.windowX <= 0 || var2.windowY <= 0 || var2.windowX >= Screen.getWindowWidth() || var2.windowY >= Screen.getWindowHeight()) {
            Screen.input().setCursorPosition(Screen.getWindowWidth() / 2, Screen.getWindowHeight() / 2, Screen.tickManager);
         }

         Screen.setCursorMode(212993);
      }

   }

   public static boolean isCursorVisible() {
      return isAimCursor || lastUsedCursor;
   }

   public static float getAimX() {
      return aimX;
   }

   public static float getAimY() {
      return aimY;
   }

   public static void submitNextRefreshFocusEvent() {
      REFRESH_FOCUS.submitNextEvent();
   }

   public static void submitControllerEvent(ControllerEvent var0) {
      synchronized(newEventsLock) {
         newEvents.add(var0);
      }
   }

   public static void setMoveAsMenuNavigation(boolean var0) {
      useMoveAsMenuNavigation = var0;
   }

   public static void showFloatingTextInput(SteamUtils.FloatingGamepadTextInputMode var0, int var1, int var2, int var3, int var4) {
      SteamData.getUtils().showFloatingGamepadTextInput(var0, var1, var2, var3, var4);
   }

   public static void dismissFloatingTextInput() {
      SteamData.getUtils().dismissFloatingGamepadTextInput();
   }

   public static boolean showControllerPanel() {
      if (latestHandle != null) {
         controller.showBindingPanel(latestHandle);
         return true;
      } else {
         return false;
      }
   }

   public static void vibrate(float var0, float var1) {
      if (latestHandle != null) {
         short var2 = (short)((int)(var0 * 65535.0F));
         short var3 = (short)((int)(var1 * 65535.0F));
         controller.triggerVibration(latestHandle, var2, var3);
      }

   }

   public static void dispose() {
      if (controller != null) {
         controller.shutdown();
         controller = null;
      }

   }

   static {
      states.add(REFRESH_FOCUS = new ControllerRefreshFocusState());
      states.add(REPEAT_EVENT = new ControllerEmptyState());
      states.add(CONTROLLER_CONNECTED_EVENT = new ControllerEmptyState());
      states.add(CONTROLLER_DISCONNECTED_EVENT = new ControllerEmptyState());
      states.add(GAME_CONTROLS = new ControllerActionSetState("GameControls"));
      states.add(MENU_SET_LAYER = new ControllerActionSetLayerState("MenuControls"));
      states.add(MOVE = new ControllerAnalogState(GAME_CONTROLS, "move"));
      states.add(AIM = new ControllerAnalogState(GAME_CONTROLS, "aim"));
      states.add(CURSOR = new ControllerAnalogState(GAME_CONTROLS, "cursor"));
      states.add(MENU_UP = new ControllerButtonState(MENU_SET_LAYER, "menuup", (Supplier)null));
      states.add(MENU_RIGHT = new ControllerButtonState(MENU_SET_LAYER, "menuright", (Supplier)null));
      states.add(MENU_DOWN = new ControllerButtonState(MENU_SET_LAYER, "menudown", (Supplier)null));
      states.add(MENU_LEFT = new ControllerButtonState(MENU_SET_LAYER, "menuleft", (Supplier)null));
      states.add(MENU_SELECT = new ControllerButtonState(MENU_SET_LAYER, "menuselect", (Supplier)null));
      states.add(MENU_BACK = new ControllerButtonState(MENU_SET_LAYER, "menuback", (Supplier)null));
      states.add(MENU_NEXT = new ControllerButtonState(MENU_SET_LAYER, "menunext", (Supplier)null));
      states.add(MENU_PREV = new ControllerButtonState(MENU_SET_LAYER, "menuprevious", (Supplier)null));
      states.add(MENU_INTERACT_ITEM = new ControllerButtonState(MENU_SET_LAYER, "menuinteractitem", (Supplier)null));
      states.add(MENU_ITEM_ACTIONS_MENU = new ControllerButtonState(MENU_SET_LAYER, "menuitemactionsmenu", (Supplier)null));
      states.add(MENU_QUICK_TRANSFER = new ControllerButtonState(MENU_SET_LAYER, "menuquicktransfer", (Supplier)null));
      states.add(MENU_QUICK_TRASH = new ControllerButtonState(MENU_SET_LAYER, "menuquicktrash", (Supplier)null));
      states.add(MENU_DROP_ITEM = new ControllerButtonState(MENU_SET_LAYER, "menudropitem", (Supplier)null));
      states.add(MENU_LOCK_ITEM = new ControllerButtonState(MENU_SET_LAYER, "menulockitem", (Supplier)null));
      states.add(MENU_MOVE_ONE_ITEM = new ControllerButtonState(MENU_SET_LAYER, "menumoveoneitem", (Supplier)null));
      states.add(MENU_GET_ONE_ITEM = new ControllerButtonState(MENU_SET_LAYER, "menugetoneitem", (Supplier)null));
      states.add(MENU_QUICK_STACK = new ControllerButtonState(MENU_SET_LAYER, "menuquickstack", () -> {
         return Control.QUICK_STACK;
      }));
      states.add(MENU_LOOT_ALL = new ControllerButtonState(MENU_SET_LAYER, "menulootall", () -> {
         return Control.LOOT_ALL;
      }));
      states.add(MENU_SORT_INVENTORY = new ControllerButtonState(MENU_SET_LAYER, "menusortinventory", () -> {
         return Control.SORT_INVENTORY;
      }));
      states.add(ATTACK = new ControllerButtonState(GAME_CONTROLS, "attack", () -> {
         return Control.MOUSE1;
      }));
      states.add(INTERACT = new ControllerButtonState(GAME_CONTROLS, "interact", () -> {
         return Control.MOUSE2;
      }));
      states.add(TOGGLE_AIM = new ControllerButtonState(GAME_CONTROLS, "toggleaim", (Supplier)null));
      states.add(INVENTORY = new ControllerButtonState(GAME_CONTROLS, "inventory", () -> {
         return Control.INVENTORY;
      }));
      states.add(HEALTH_POTION = new ControllerButtonState(GAME_CONTROLS, "healthpotion", () -> {
         return Control.HEALTH_POT;
      }));
      states.add(MANA_POTION = new ControllerButtonState(GAME_CONTROLS, "manapotion", () -> {
         return Control.MANA_POT;
      }));
      states.add(BUFF_POTIONS = new ControllerButtonState(GAME_CONTROLS, "buffpotions", () -> {
         return Control.BUFF_POTS;
      }));
      states.add(EAT_FOOD = new ControllerButtonState(GAME_CONTROLS, "eatfood", () -> {
         return Control.EAT_FOOD;
      }));
      states.add(USE_MOUNT = new ControllerButtonState(GAME_CONTROLS, "usemount", () -> {
         return Control.USE_MOUNT;
      }));
      states.add(SET_ABILITY = new ControllerButtonState(GAME_CONTROLS, "setability", () -> {
         return Control.SET_ABILITY;
      }));
      states.add(TRINKET_ABILITY = new ControllerButtonState(GAME_CONTROLS, "trinketability", () -> {
         return Control.TRINKET_ABILITY;
      }));
      states.add(PLACE_TORCH = new ControllerButtonState(GAME_CONTROLS, "placetorch", () -> {
         return Control.PLACE_TORCH;
      }));
      states.add(OPEN_ADVENTURE_PARTY = new ControllerButtonState(GAME_CONTROLS, "openadventureparty", () -> {
         return Control.OPEN_ADVENTURE_PARTY;
      }));
      states.add(OPEN_SETTLEMENT = new ControllerButtonState(GAME_CONTROLS, "opensettlement", () -> {
         return Control.OPEN_SETTLEMENT;
      }));
      states.add(MAIN_MENU = new ControllerButtonState(GAME_CONTROLS, "mainmenu", (Supplier)null));
      states.add(SMART_MINING = new ControllerButtonState(GAME_CONTROLS, "smartmining", () -> {
         return Control.SMART_MINING;
      }));
      states.add(QUICK_STACK_NEARBY = new ControllerButtonState(GAME_CONTROLS, "quickstacknearby", () -> {
         return Control.QUICK_STACK;
      }));
      states.add(SHOW_MAP = new ControllerButtonState(GAME_CONTROLS, "showmap", () -> {
         return Control.SHOW_MAP;
      }));
      states.add(SHOW_WORLD_MAP = new ControllerButtonState(GAME_CONTROLS, "showworldmap", () -> {
         return Control.SHOW_WORLD_MAP;
      }));
      states.add(SCOREBOARD = new ControllerButtonState(GAME_CONTROLS, "scoreboard", () -> {
         return Control.SCOREBOARD;
      }));
      states.add(TOGGLE_UI = new ControllerButtonState(GAME_CONTROLS, "toggleui", () -> {
         return Control.HIDE_UI;
      }));
      states.add(ZOOM_IN = new ControllerButtonState(GAME_CONTROLS, "zoomin", () -> {
         return Control.ZOOM_IN;
      }));
      states.add(ZOOM_OUT = new ControllerButtonState(GAME_CONTROLS, "zoomout", () -> {
         return Control.ZOOM_OUT;
      }));
      states.add(NEXT_HOTBAR = new ControllerButtonState(GAME_CONTROLS, "nexthotbar", () -> {
         return Control.NEXT_HOTBAR;
      }));
      states.add(PREV_HOTBAR = new ControllerButtonState(GAME_CONTROLS, "prevhotbar", () -> {
         return Control.PREV_HOTBAR;
      }));
      buttonTextures = new HashMap();
      usedHandler = new ControllerOriginsUsedHandler();
      newEventsLock = new Object();
      newEvents = new ArrayList();
      events = new ArrayList();
      repeatEvents = new HashMap();
   }

   protected static class RepeatEventTracker {
      public final SteamControllerHandle controller;
      public final Object[] callers;
      public long lastPressTime;
      public int nextPressCooldown;
      public int repeatPresses;

      public RepeatEventTracker(SteamControllerHandle var1, Object[] var2, long var3) {
         this.controller = var1;
         this.callers = var2;
         this.lastPressTime = var3;
         this.nextPressCooldown = 250;
      }
   }
}
