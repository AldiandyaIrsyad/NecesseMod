package necesse.gfx.forms;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.ComponentFloatMenu;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.inventory.InventoryItem;

public class FormManager implements ComponentListContainer<FormComponent> {
   public static boolean drawControllerFocusBoxes;
   public static boolean drawControllerAreaBoxes;
   private boolean isDisposed;
   private ComponentList<FormComponent> components = new ComponentList<FormComponent>() {
      public InputEvent offsetEvent(InputEvent var1, boolean var2) {
         return var1;
      }

      public FormManager getManager() {
         return FormManager.this;
      }

      public void onChange() {
         FormManager.this.updateMouseOver();
      }
   };
   private boolean isMouseOver;
   private CurrentFloatMenu floatMenu = null;
   private CurrentFloatMenu visibleKeyboard = null;
   private LinkedList<Timeout> timeouts = new LinkedList();
   private ControllerFocus currentControllerFocus;
   private ArrayList<ControllerFocusHandler> nextControllerFocuses;
   private HashSet<ControllerFocusHandler> nextControllerFocusesSet;
   private static HashMap<Integer, Long> lastControllerFocuses = new HashMap();

   public static void cleanUpLastControllerFocuses() {
      HashSet var0 = new HashSet();
      Iterator var1 = lastControllerFocuses.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
         long var3 = System.currentTimeMillis() - (Long)var2.getValue();
         if (var3 > 60000L) {
            var0.add((Integer)var2.getKey());
         }
      }

      HashMap var10001 = lastControllerFocuses;
      Objects.requireNonNull(var10001);
      var0.forEach(var10001::remove);
   }

   public FormManager() {
   }

   public ComponentList<FormComponent> getComponentList() {
      return this.components;
   }

   public void openFloatMenu(FloatMenu var1) {
      this.openFloatMenu(var1, 0, 0);
   }

   public void openFloatMenu(FloatMenu var1, FormComponent var2, InputEvent var3) {
      this.openFloatMenu(var1, var2, var3, 0, 0);
   }

   public void openFloatMenu(FloatMenu var1, FormComponent var2, InputEvent var3, int var4, int var5) {
      if (var3.isControllerEvent()) {
         ControllerFocus var6 = this.getCurrentFocus();
         if (var6 != null) {
            Point var7 = var6.getTooltipAndFloatMenuPoint();
            this.openFloatMenuAt(var1, var7.x + var4, var7.y + var5);
            return;
         }
      }

      Rectangle var8 = var2.getBoundingBox();
      this.openFloatMenu(var1, var8.x - var3.pos.hudX + var4, var8.y - var3.pos.hudY + var5);
   }

   public void openFloatMenu(FloatMenu var1, int var2, int var3) {
      ControllerFocus var4 = this.getCurrentFocus();
      if (var4 != null) {
         Point var5 = var4.getTooltipAndFloatMenuPoint();
         this.openFloatMenuAt(var1, var5.x + var2, var5.y + var3);
      } else {
         this.openFloatMenuAt(var1, Screen.mousePos().hudX + var2, Screen.mousePos().hudY + var3);
      }

   }

   public void openFloatMenuAt(FloatMenu var1, int var2, int var3) {
      Objects.requireNonNull(var1);
      if (this.floatMenu != null) {
         this.floatMenu.menu.dispose();
      }

      this.floatMenu = new CurrentFloatMenu(var1, var2, var3, Screen.tickManager);
      var1.init(this.floatMenu.drawX, this.floatMenu.drawY, () -> {
         if (this.floatMenu != null && this.floatMenu.menu == var1) {
            this.floatMenu.menu.dispose();
            if (this.floatMenu.menu.disposeFocus != null) {
               this.setNextControllerFocus(this.floatMenu.menu.disposeFocus);
            }

            this.floatMenu = null;
            ControllerInput.submitNextRefreshFocusEvent();
         }

      });
      this.updateMouseOver();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public boolean hasFloatMenu() {
      return this.floatMenu != null;
   }

   public void openControllerKeyboard(final FormTypingComponent var1) {
      if (this.visibleKeyboard != null) {
         this.visibleKeyboard.menu.dispose();
      }

      final AtomicReference var2 = new AtomicReference();
      ComponentFloatMenu var3 = new ComponentFloatMenu(var1, new ControllerKeyboardForm(var1) {
         public void submitEnter() {
            var1.submitControllerEnter();
            ((FloatMenu)var2.get()).remove();
         }
      });
      var2.set(var3);
      this.visibleKeyboard = new CurrentFloatMenu(var3, 0, 0, Screen.tickManager);
      var3.init(this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, () -> {
         if (this.visibleKeyboard != null && this.visibleKeyboard.menu == var3) {
            this.visibleKeyboard.menu.dispose();
            if (this.visibleKeyboard.menu.disposeFocus != null) {
               this.setNextControllerFocus(this.visibleKeyboard.menu.disposeFocus);
            }

            this.visibleKeyboard = null;
            ControllerInput.submitNextRefreshFocusEvent();
         }

      });
      this.updateMouseOver();
      ControllerInput.submitNextRefreshFocusEvent();
   }

   public boolean isControllerKeyboardOpen() {
      return this.visibleKeyboard != null;
   }

   public void updateMouseOver(InputEvent var1) {
      this.isMouseOver = this.visibleKeyboard != null && this.visibleKeyboard.menu.isMouseOver(var1);
      this.isMouseOver = this.floatMenu != null && this.floatMenu.menu.isMouseOver(var1);
      Iterator var2 = this.components.iterator();

      while(var2.hasNext()) {
         FormComponent var3 = (FormComponent)var2.next();
         if (var3.shouldDraw() && var3.isMouseOver(var1)) {
            this.isMouseOver = true;
            break;
         }
      }

   }

   public final void updateMouseOver() {
      this.updateMouseOver(InputEvent.MouseMoveEvent(Screen.mousePos(), Screen.tickManager));
   }

   public void frameTick(TickManager var1) {
      long var2 = System.currentTimeMillis();

      while(!this.timeouts.isEmpty()) {
         Timeout var4 = (Timeout)this.timeouts.getFirst();
         if (var4.time >= var2) {
            break;
         }

         this.timeouts.removeFirst();
         var4.runnable.run();
      }

      if (this.currentControllerFocus != null) {
         this.currentControllerFocus.handler.frameTickControllerFocus(var1, this.currentControllerFocus);
      }

   }

   public void submitInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.updateMouseOver(var1);
      }

      if (this.visibleKeyboard != null && !this.visibleKeyboard.isSameStartTime(var1)) {
         this.visibleKeyboard.menu.handleInputEvent(var1, var2, var3);
         if (var1.isMouseMoveEvent() && this.visibleKeyboard.menu.isMouseOver(var1)) {
            var1.useMove();
         }

         if (var1.isUsed()) {
            return;
         }

         if (var1.state && var1.getID() == 256) {
            this.visibleKeyboard.menu.remove();
            var1.use();
         }
      }

      if (this.floatMenu != null && !this.floatMenu.isSameStartTime(var1)) {
         this.floatMenu.menu.handleInputEvent(var1, var2, var3);
         if (var1.isMouseMoveEvent() && this.floatMenu.menu.isMouseOver(var1)) {
            var1.useMove();
         }

         if (var1.isUsed()) {
            return;
         }

         if (var1.state && var1.getID() == 256) {
            this.floatMenu.menu.remove();
            var1.use();
         }
      }

      this.components.submitInputEvent(var1, var2, var3);
      if ((var1.isMouseWheelEvent() || var1.isMouseClickEvent()) && this.isMouseOver(var1)) {
         var1.use();
      }

      if (var1.wasMouseClickEvent() && FormTypingComponent.isCurrentlyTyping()) {
         FormTypingComponent.getCurrentTypingComponent().submitUsedInputEvent(var1);
      }

   }

   public void submitControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      LinkedList var7;
      if (var1.getState() == ControllerInput.REFRESH_FOCUS) {
         if (ControllerInput.MENU_SET_LAYER.isActive() && Input.lastInputIsController) {
            var7 = new LinkedList();
            if (this.visibleKeyboard != null) {
               this.visibleKeyboard.menu.addNextControllerFocus(var7, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
            } else if (this.floatMenu != null) {
               this.floatMenu.menu.addNextControllerFocus(var7, this.floatMenu.drawX, this.floatMenu.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
            } else {
               this.components.addNextControllerComponents(var7, 0, 0, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
            }

            this.refreshFocus(var7);
         }

      } else {
         ControllerFocus var6;
         if ((var1.getState() == ControllerInput.AIM || var1.getState() == ControllerInput.CURSOR) && ControllerInput.isCursorVisible()) {
            var7 = new LinkedList();
            if (this.visibleKeyboard != null) {
               this.visibleKeyboard.menu.addNextControllerFocus(var7, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
            } else if (this.floatMenu != null) {
               this.floatMenu.menu.addNextControllerFocus(var7, this.floatMenu.drawX, this.floatMenu.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
            } else {
               this.components.addNextControllerComponents(var7, 0, 0, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
            }

            InputPosition var8 = Screen.mousePos();
            var6 = (ControllerFocus)var7.stream().filter((var1x) -> {
               return var1x.boundingBox.contains(var8.hudX, var8.hudY);
            }).findFirst().orElse((Object)null);
            if (var6 != null && !this.isControllerFocus(var6.handler)) {
               this.setControllerFocus(var6);
            }

         } else if (!var1.isUsed()) {
            if (this.visibleKeyboard != null) {
               this.visibleKeyboard.menu.handleControllerEvent(var1, var2, var3);
               if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && var1.buttonState) {
                  this.visibleKeyboard.menu.remove();
                  var1.use();
               }
            } else if (this.floatMenu != null) {
               this.floatMenu.menu.handleControllerEvent(var1, var2, var3);
               if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && var1.buttonState) {
                  this.floatMenu.menu.remove();
                  var1.use();
               }
            } else {
               this.components.submitControllerEvent(var1, var2, var3);
            }

            if (!var1.isUsed()) {
               byte var4;
               if ((var1.getState() != ControllerInput.MENU_UP || !var1.buttonState) && !var1.isRepeatEvent((Object)ControllerInput.MENU_UP)) {
                  if ((var1.getState() != ControllerInput.MENU_RIGHT || !var1.buttonState) && !var1.isRepeatEvent((Object)ControllerInput.MENU_RIGHT)) {
                     if ((var1.getState() != ControllerInput.MENU_DOWN || !var1.buttonState) && !var1.isRepeatEvent((Object)ControllerInput.MENU_DOWN)) {
                        if ((var1.getState() != ControllerInput.MENU_LEFT || !var1.buttonState) && !var1.isRepeatEvent((Object)ControllerInput.MENU_LEFT)) {
                           var4 = -1;
                        } else {
                           var1.startRepeatEvents(ControllerInput.MENU_LEFT);
                           var4 = 3;
                        }
                     } else {
                        var1.startRepeatEvents(ControllerInput.MENU_DOWN);
                        var4 = 2;
                     }
                  } else {
                     var1.startRepeatEvents(ControllerInput.MENU_RIGHT);
                     var4 = 1;
                  }
               } else {
                  var1.startRepeatEvents(ControllerInput.MENU_UP);
                  var4 = 0;
               }

               if (var4 != -1) {
                  var1.use();
                  LinkedList var5 = null;
                  if (this.currentControllerFocus != null) {
                     var5 = new LinkedList();
                     if (this.visibleKeyboard != null) {
                        this.visibleKeyboard.menu.addNextControllerFocus(var5, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
                     } else if (this.floatMenu != null) {
                        this.floatMenu.menu.addNextControllerFocus(var5, this.floatMenu.drawX, this.floatMenu.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
                     } else {
                        this.components.addNextControllerComponents(var5, 0, 0, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
                     }

                     if (var5.stream().anyMatch((var1x) -> {
                        return var1x.handler == this.currentControllerFocus.handler;
                     })) {
                        if (this.currentControllerFocus.handler.handleControllerNavigate(var4, var1, var2, var3)) {
                           return;
                        }

                        if (this.currentControllerFocus.customNavigationHandler != null && this.currentControllerFocus.customNavigationHandler.handleNavigate(var4, var1, var2, var3)) {
                           return;
                        }
                     }
                  }

                  if (var5 == null) {
                     var5 = new LinkedList();
                     if (this.visibleKeyboard != null) {
                        this.visibleKeyboard.menu.addNextControllerFocus(var5, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
                     } else if (this.floatMenu != null) {
                        this.floatMenu.menu.addNextControllerFocus(var5, this.floatMenu.drawX, this.floatMenu.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
                     } else {
                        this.components.addNextControllerComponents(var5, 0, 0, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), false);
                     }
                  }

                  var6 = ControllerFocus.getNext(var4, this, var5);
                  if (var6 != null) {
                     this.setControllerFocus(var6);
                  }
               }

            }
         }
      }
   }

   public void refreshFocus(List<ControllerFocus> var1) {
      boolean var2 = true;
      ControllerFocus var3;
      if (this.nextControllerFocuses != null) {
         var3 = null;
         Iterator var4 = this.nextControllerFocuses.iterator();

         while(var4.hasNext()) {
            ControllerFocusHandler var5 = (ControllerFocusHandler)var4.next();
            ControllerFocus var6 = (ControllerFocus)var1.stream().filter((var1x) -> {
               return var1x.handler == var5;
            }).findFirst().orElse((Object)null);
            long var7 = (Long)lastControllerFocuses.getOrDefault(var5.getControllerFocusHashcode(), -1L);
            if (var3 == null) {
               var3 = var6;
            } else {
               long var9 = (Long)lastControllerFocuses.getOrDefault(var3.handler.getControllerFocusHashcode(), -1L);
               if (var9 < var7) {
                  var3 = var6;
               }
            }
         }

         this.nextControllerFocuses = null;
         this.nextControllerFocusesSet = null;
         if (var3 != null) {
            this.setControllerFocus(var3);
            return;
         }
      }

      if (this.currentControllerFocus != null) {
         var3 = (ControllerFocus)var1.stream().filter((var1x) -> {
            return var1x.handler == this.currentControllerFocus.handler;
         }).findFirst().orElse((Object)null);
         if (var3 != null) {
            this.currentControllerFocus = var3;
            var2 = false;
         }
      }

      if (var2) {
         var3 = (ControllerFocus)var1.stream().map((var0) -> {
            long var1 = (Long)lastControllerFocuses.getOrDefault(var0.handler.getControllerFocusHashcode(), -1L);
            return new ObjectValue(var0, var1);
         }).filter((var0) -> {
            return (Long)var0.value != -1L;
         }).max(Comparator.comparingLong((var0) -> {
            return (Long)var0.value;
         })).map((var0) -> {
            return (ControllerFocus)var0.object;
         }).orElse((Object)null);
         if (this.currentControllerFocus != null && var3 == null) {
            lastControllerFocuses.put(this.currentControllerFocus.handler.getControllerFocusHashcode(), System.currentTimeMillis());
            Comparator var11 = Comparator.comparingDouble((var1x) -> {
               Rectangle var2 = var1x.boundingBox.intersection(this.currentControllerFocus.boundingBox);
               if (var2.isEmpty()) {
                  return 0.0;
               } else {
                  double var3 = (double)var1x.boundingBox.width * (double)var1x.boundingBox.height;
                  double var5 = (double)var2.width * (double)var2.height;
                  return var5 / var3;
               }
            });
            var11 = var11.thenComparingDouble((var1x) -> {
               return -(new Point2D.Double(var1x.boundingBox.getCenterX(), var1x.boundingBox.getCenterY())).distance(this.currentControllerFocus.boundingBox.getCenterX(), this.currentControllerFocus.boundingBox.getCenterY());
            });
            var3 = (ControllerFocus)var1.stream().max(var11).orElse((Object)null);
         }

         if (var3 == null) {
            var3 = (ControllerFocus)var1.stream().max(Comparator.comparingInt((var0) -> {
               return var0.initialFocusPriority;
            })).orElse((Object)null);
         }

         this.setControllerFocus(var3);
      }

   }

   public void prioritizeControllerFocus(ControllerFocusHandler... var1) {
      if (var1 != null) {
         long var2 = System.currentTimeMillis();
         ControllerFocusHandler[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ControllerFocusHandler var7 = var4[var6];
            if (var7 != null) {
               lastControllerFocuses.put(var7.getControllerFocusHashcode(), var2--);
            }
         }

      }
   }

   public void tryPrioritizeControllerFocus(ControllerFocusHandler... var1) {
      if (var1 != null) {
         long var2 = System.currentTimeMillis();
         ControllerFocusHandler[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ControllerFocusHandler var7 = var4[var6];
            if (var7 != null && !lastControllerFocuses.containsKey(var7.getControllerFocusHashcode())) {
               lastControllerFocuses.put(var7.getControllerFocusHashcode(), var2--);
            }
         }

      }
   }

   public void setControllerFocus(ControllerFocus var1) {
      if (this.currentControllerFocus != null) {
         this.currentControllerFocus.handler.onControllerUnfocused(this.currentControllerFocus);
      }

      this.currentControllerFocus = var1;
      if (this.currentControllerFocus != null) {
         lastControllerFocuses.put(this.currentControllerFocus.handler.getControllerFocusHashcode(), System.currentTimeMillis());
         this.currentControllerFocus.handler.onControllerFocused(this.currentControllerFocus);
      }

   }

   public void setNextControllerFocus(ControllerFocusHandler... var1) {
      boolean var2 = false;
      if (this.nextControllerFocuses == null) {
         this.nextControllerFocuses = new ArrayList(var1.length);
         this.nextControllerFocusesSet = new HashSet();
      } else {
         this.nextControllerFocuses.ensureCapacity(this.nextControllerFocuses.size() + var1.length);
      }

      ControllerFocusHandler[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ControllerFocusHandler var6 = var3[var5];
         if (this.nextControllerFocusesSet.add(var6)) {
            this.nextControllerFocuses.add(var6);
            var2 = true;
         }
      }

      if (var2) {
         ControllerInput.submitNextRefreshFocusEvent();
      }

   }

   public boolean isControllerFocus(ControllerFocusHandler var1) {
      if (Input.lastInputIsController && ControllerInput.MENU_SET_LAYER.isActive()) {
         if (var1 == null) {
            return this.currentControllerFocus == null;
         } else {
            return this.currentControllerFocus != null && this.currentControllerFocus.handler == var1;
         }
      } else {
         return false;
      }
   }

   public ControllerFocus getCurrentFocus() {
      return Input.lastInputIsController && ControllerInput.MENU_SET_LAYER.isActive() ? this.currentControllerFocus : null;
   }

   public boolean isControllerTyping() {
      return this.visibleKeyboard != null;
   }

   public boolean isControllerTyping(ControllerFocusHandler var1) {
      if (this.visibleKeyboard != null) {
         return this.visibleKeyboard.menu.disposeFocus == var1;
      } else {
         return false;
      }
   }

   public boolean isMouseOver() {
      return this.isMouseOver;
   }

   public boolean isMouseOver(InputEvent var1) {
      if (this.visibleKeyboard != null && this.visibleKeyboard.menu.isMouseOver(var1)) {
         return true;
      } else if (this.floatMenu != null && this.floatMenu.menu.isMouseOver(var1)) {
         return true;
      } else {
         Iterator var2 = this.components.iterator();

         FormComponent var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (FormComponent)var2.next();
         } while(!var3.shouldDraw() || !var3.isMouseOver(var1));

         return true;
      }
   }

   public boolean isMouseOver(InputPosition var1) {
      return this.isMouseOver(InputEvent.MouseMoveEvent(var1, (TickManager)null));
   }

   public void draw(TickManager var1, PlayerMob var2) {
      GameResources.formShader.use();

      try {
         this.components.drawComponents(var1, var2, (Rectangle)null);
         if (this.floatMenu != null) {
            this.floatMenu.menu.draw(var1, var2);
         }

         if (this.visibleKeyboard != null) {
            Screen.initQuadDraw(Screen.getHudWidth(), Screen.getHudHeight()).color(new Color(0, 0, 0, 70)).draw(0, 0);
            this.visibleKeyboard.menu.draw(var1, var2);
         }
      } finally {
         GameResources.formShader.stop();
      }

      ControllerFocus var5;
      if (drawControllerFocusBoxes || drawControllerAreaBoxes) {
         LinkedList var3 = new LinkedList();
         if (this.floatMenu != null) {
            this.floatMenu.menu.addNextControllerFocus(var3, this.floatMenu.drawX, this.floatMenu.drawY, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), drawControllerAreaBoxes);
         } else {
            this.components.addNextControllerComponents(var3, 0, 0, (ControllerNavigationHandler)null, new Rectangle(Screen.getHudWidth(), Screen.getHudHeight()), drawControllerAreaBoxes);
         }

         if (drawControllerFocusBoxes) {
            Iterator var4 = var3.iterator();

            label175:
            while(true) {
               do {
                  if (!var4.hasNext()) {
                     break label175;
                  }

                  var5 = (ControllerFocus)var4.next();
               } while(this.currentControllerFocus != null && var5.handler == this.currentControllerFocus.handler);

               Rectangle var6 = var5.boundingBox;
               Screen.drawShape(var6, false, 1.0F, 1.0F, 0.0F, 1.0F);
            }
         }
      }

      Runnable var9 = null;
      if (GlobalData.getCurrentState().isRunning() && var2 != null && var2.isInventoryExtended()) {
         InventoryItem var10 = var2.getDraggingItem();
         if (var10 != null) {
            var5 = this.getCurrentFocus();
            if (var5 != null) {
               Point var12 = var5.getBoundingBoxCenter();
               var9 = () -> {
                  var10.draw(var2, var12.x - 16, var12.y - 28, false, true);
               };
            } else {
               InputPosition var13 = Screen.mousePos();
               var9 = () -> {
                  var10.draw(var2, var13.hudX - 16, var13.hudY - 16, false, true);
               };
            }
         }
      }

      if (Input.lastInputIsController && ControllerInput.MENU_SET_LAYER.isActive() && this.currentControllerFocus != null) {
         Point var11 = this.currentControllerFocus.getTooltipAndFloatMenuPoint();
         if (var11 == null) {
            var11 = this.currentControllerFocus.boundingBox.getLocation();
         }

         Screen.setTooltipsFormFocus(var11.x, var11.y - (var9 != null ? 4 : 0));
         if (var9 != null) {
            Screen.setTooltipFocusOffset(0, 4);
         }

         this.currentControllerFocus.handler.drawControllerFocus(this.currentControllerFocus);
      } else if (var9 != null) {
         Screen.setTooltipFocusOffset(0, 12);
      }

      if (var9 != null) {
         var9.run();
      }

   }

   public Timeout setTimeout(Runnable var1, long var2) {
      Timeout var4 = new Timeout(var1, var2);
      this.timeouts.add(var4);
      this.timeouts.sort(Comparator.comparingLong((var0) -> {
         return var0.time;
      }));
      return var4;
   }

   public boolean clearTimeout(Timeout var1) {
      return this.timeouts.remove(var1);
   }

   public void onWindowResized() {
      this.components.onWindowResized();
   }

   public void dispose() {
      this.isDisposed = true;
      if (this.floatMenu != null) {
         this.floatMenu.menu.dispose();
      }

      if (this.visibleKeyboard != null) {
         this.visibleKeyboard.menu.dispose();
      }

      this.components.disposeComponents();
   }

   public boolean isDisposed() {
      return this.isDisposed;
   }

   public void onComponentDispose(FormComponent var1) {
      if (this.floatMenu != null && this.floatMenu.menu.parent == var1) {
         this.floatMenu.menu.remove();
      }

      if (this.visibleKeyboard != null && this.visibleKeyboard.menu.parent == var1) {
         this.visibleKeyboard.menu.remove();
      }

   }

   private static class CurrentFloatMenu {
      public final long tick;
      public final long frame;
      public final FloatMenu menu;
      public final int drawX;
      public final int drawY;

      public CurrentFloatMenu(FloatMenu var1, int var2, int var3, TickManager var4) {
         this.menu = var1;
         this.drawX = var2;
         this.drawY = var3;
         this.tick = var4.getTotalTicks();
         this.frame = var4.getTotalFrames();
      }

      public boolean isSameStartTime(InputEvent var1) {
         return this.frame == var1.frame;
      }
   }

   public static class Timeout {
      public final Runnable runnable;
      public final long time;

      public Timeout(Runnable var1, long var2) {
         this.runnable = var1;
         this.time = System.currentTimeMillis() + var2;
      }
   }
}
