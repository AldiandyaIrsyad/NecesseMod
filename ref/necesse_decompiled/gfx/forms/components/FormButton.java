package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.MouseDraggingElement;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormDraggingEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.ui.ButtonState;

public abstract class FormButton extends FormComponent {
   private long onCooldown;
   private int cooldownTime;
   private FormClickHandler clickHandler = new FormClickHandler((var1) -> {
      return this.acceptsEvents() && !this.isOnCooldown() && this.isMouseOver(var1);
   }, (var1) -> {
      return var1.getID() == -100 || var1.getID() == -99 && this.acceptRightClicks;
   }, (var1) -> {
      if (this.isPressDraggingThis()) {
         var1.use();
      } else {
         FormInputEvent var2 = new FormInputEvent(this, var1);
         this.clickedEvents.onEvent(var2);
         if (!var2.hasPreventedDefault()) {
            this.pressed(var1);
            if (this.cooldownTime > 0) {
               this.startCooldown();
            }

            var1.use();
         }

      }
   });
   private boolean active;
   public boolean useHoverMoveEvents = true;
   private boolean isHovering;
   public boolean acceptMouseRepeatEvents = false;
   public boolean acceptRightClicks = false;
   public boolean handleClicksIfNoEventHandlers = false;
   public boolean submitControllerPressEvent = false;
   public double draggingStartDistance = 5.0;
   protected FormEventsHandler<FormInputEvent<FormButton>> clickedEvents = new FormEventsHandler();
   protected FormEventsHandler<FormInputEvent<FormButton>> changedHoverEvents = new FormEventsHandler();
   protected FormEventsHandler<FormDraggingEvent<FormButton>> dragStartedEvents = new FormEventsHandler();
   protected InputEvent dragStartDownEvent;

   public FormButton() {
      this.setActive(true);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         if (this.clickHandler.isDown()) {
            InputEvent var4 = this.clickHandler.getDownEvent();
            if (var4 != null && var4 != this.dragStartDownEvent) {
               double var5 = GameMath.diagonalMoveDistance(var4.pos.windowX, var4.pos.windowY, var1.pos.windowX, var1.pos.windowY);
               if (var5 >= this.draggingStartDistance) {
                  this.dragStartDownEvent = var4;
                  this.dragStartedEvents.onEvent(new FormDraggingEvent(this, var1, this.dragStartDownEvent));
               }
            }
         }

         boolean var7 = this.isMouseOver(var1);
         if (this.isHovering != var7) {
            this.isHovering = var7;
            FormInputEvent var9 = new FormInputEvent(this, var1);
            this.changedHoverEvents.onEvent(var9);
         }

         if (var7 && this.useHoverMoveEvents) {
            var1.useMove();
         }
      }

      if (this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners()) {
         if (this.acceptMouseRepeatEvents) {
            if (var1.state && (Boolean)this.clickHandler.eventAccept.apply(var1) && (var1.getID() == -100 || var1.isRepeatEvent((Object)this))) {
               if (this.isPressDraggingThis()) {
                  var1.use();
                  return;
               }

               var1.startRepeatEvents(this);
               FormInputEvent var8 = new FormInputEvent(this, var1);
               this.clickedEvents.onEvent(var8);
               if (!var8.hasPreventedDefault()) {
                  this.pressed(var1);
                  if (this.cooldownTime > 0) {
                     this.startCooldown();
                  }

                  var1.use();
               }
            }
         } else {
            this.clickHandler.handleEvent(var1);
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isActive()) {
         if (var1.getState() == ControllerInput.MENU_SELECT || this.acceptRightClicks && var1.getState() == ControllerInput.MENU_BACK) {
            InputEvent var4;
            if (this.isControllerFocus() && this.submitControllerPressEvent) {
               if (var1.buttonState) {
                  if (this.acceptMouseRepeatEvents) {
                     var1.startRepeatEvents(this);
                  }

                  var4 = InputEvent.ControllerButtonEvent(var1, var2);
                  this.clickHandler.forceHandleEvent(var4);
                  var1.use();
                  this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(ControllerEvent.buttonEvent(var1.controller, var1.getState(), false), var2));
                  var1.use();
               }
            } else if (this.isControllerFocus() && var1.buttonState) {
               if (this.acceptMouseRepeatEvents) {
                  var1.startRepeatEvents(this);
               }

               var4 = InputEvent.ControllerButtonEvent(var1, var2);
               this.clickHandler.forceHandleEvent(var4);
               var1.use();
            } else if (!var1.buttonState && this.clickHandler.isDown()) {
               this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(var1, var2));
               var1.use();
            }
         } else if (this.acceptMouseRepeatEvents && var1.isRepeatEvent((Object)this) && (this.clickHandler.isDown() || this.submitControllerPressEvent)) {
            this.clickHandler.forceClick(InputEvent.ControllerButtonEvent(var1, var2));
            var1.use();
         }
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      this.clickHandler.reset();
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners()) {
         ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      super.drawControllerFocus(var1);
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
   }

   public FormButton onClicked(FormEventListener<FormInputEvent<FormButton>> var1) {
      this.clickedEvents.addListener(var1);
      return this;
   }

   public FormButton onChangedHover(FormEventListener<FormInputEvent<FormButton>> var1) {
      this.changedHoverEvents.addListener(var1);
      return this;
   }

   public FormButton onDragStarted(FormEventListener<FormDraggingEvent<FormButton>> var1) {
      this.dragStartedEvents.addListener(var1);
      return this;
   }

   public void setupDragPressOtherButtons(Object var1, Consumer<FormInputEvent<FormButton>> var2, Predicate<FormButton> var3, Consumer<FormInputEvent<FormButton>> var4) {
      this.onDragStarted((var3x) -> {
         final int var4 = var3x.draggingStartedEvent.isUsed() ? var3x.draggingStartedEvent.getLastID() : var3x.draggingStartedEvent.getID();
         FormButtonDraggingElement var5 = new FormButtonDraggingElement(this, var1) {
            public boolean isKeyDown(Input var1) {
               return var1.isKeyDown(var4);
            }
         };
         var5.pressedButtons.add(this);
         Screen.setMouseDraggingElement(var5);
         FormInputEvent var6 = new FormInputEvent(this, var3x.draggingStartedEvent);
         this.clickedEvents.onEvent(var6);
         if (!var6.hasPreventedDefault()) {
            this.pressed(var3x.event);
            if (this.cooldownTime > 0) {
               this.startCooldown();
            }

            var3x.event.use();
         }

         if (var2 != null) {
            var2.accept(var6);
         }

      });
      this.onChangedHover((var4x) -> {
         if (this.isHovering() && this.isActive() && !this.isOnCooldown()) {
            MouseDraggingElement var5 = Screen.getMouseDraggingElement();
            if (var5 instanceof FormButtonDraggingElement) {
               FormButtonDraggingElement var6 = (FormButtonDraggingElement)var5;
               if (var6.component != this && !var6.pressedButtons.contains(this) && (var3 == null || var3.test(var6.component)) && Objects.equals(var6.sameObject, var1)) {
                  var6.pressedButtons.add(this);
                  if (var4 != null) {
                     var4.accept(new FormInputEvent(this, var4x.event));
                  } else {
                     FormInputEvent var7 = new FormInputEvent(this, var4x.event);
                     this.clickedEvents.onEvent(var7);
                     if (!var7.hasPreventedDefault()) {
                        this.pressed(var4x.event);
                        if (this.cooldownTime > 0) {
                           this.startCooldown();
                        }

                        var4x.event.use();
                     }
                  }
               }
            }
         }

      });
   }

   public void setupDragPressOtherButtons(Object var1) {
      this.setupDragPressOtherButtons(var1, (Consumer)null, (Predicate)null, (Consumer)null);
   }

   protected boolean isPressDraggingThis() {
      MouseDraggingElement var1 = Screen.getMouseDraggingElement();
      if (var1 instanceof FormButtonToggle.FormButtonToggleDraggingElement) {
         FormButtonToggle.FormButtonToggleDraggingElement var2 = (FormButtonToggle.FormButtonToggleDraggingElement)var1;
         return var2.component == this;
      } else {
         return false;
      }
   }

   protected void pressed(InputEvent var1) {
      if (var1.shouldSubmitSound()) {
         this.playTickSound();
      }

   }

   public void setCooldown(int var1) {
      this.cooldownTime = var1;
   }

   public void startCooldown(int var1) {
      this.onCooldown = System.currentTimeMillis() + (long)var1;
   }

   public void startCooldown() {
      this.onCooldown = System.currentTimeMillis() + (long)this.cooldownTime;
   }

   public void stopCooldown() {
      this.onCooldown = 0L;
   }

   public boolean isOnCooldown() {
      return this.onCooldown > System.currentTimeMillis();
   }

   protected boolean acceptsEvents() {
      return this.isActive();
   }

   public boolean isHovering() {
      return this.isHovering || this.isControllerFocus();
   }

   protected boolean isDown() {
      return this.clickHandler.isDown();
   }

   public Color getDrawColor() {
      return (Color)this.getButtonState().elementColorGetter.apply(Settings.UI);
   }

   public ButtonState getButtonState() {
      if (this.isActive() && !this.isOnCooldown()) {
         return this.isHovering() ? ButtonState.HIGHLIGHTED : ButtonState.ACTIVE;
      } else {
         return ButtonState.INACTIVE;
      }
   }

   public Color getTextColor() {
      return (Color)this.getButtonState().textColorGetter.apply(Settings.UI);
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean var1) {
      this.active = var1;
   }

   public boolean shouldUseMouseEvents() {
      return this.useHoverMoveEvents;
   }

   public void drawDraggingElement(int var1, int var2) {
   }

   protected static class FormButtonDraggingElement implements MouseDraggingElement {
      public final FormButton component;
      public final Object sameObject;
      public final HashSet<FormButton> pressedButtons = new HashSet();

      public FormButtonDraggingElement(FormButton var1, Object var2) {
         this.component = var1;
         this.sameObject = var2;
      }

      public boolean draw(int var1, int var2) {
         this.component.drawDraggingElement(var1, var2);
         return true;
      }
   }
}
