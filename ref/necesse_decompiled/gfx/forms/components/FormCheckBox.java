package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormDraggingEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.HoverStateTextures;

public class FormCheckBox extends FormComponent implements FormPositionContainer {
   private boolean active;
   private boolean isHovering;
   private FormPosition position;
   private String text;
   private ArrayList<String> lines;
   private FontOptions fontOptions;
   public boolean checked;
   private int maxWidth;
   private int width;
   private FormClickHandler clickHandler;
   public boolean useHoverMoveEvents;
   public boolean acceptMouseRepeatEvents;
   public boolean acceptRightClicks;
   public boolean handleClicksIfNoEventHandlers;
   public boolean submitControllerPressEvent;
   public double draggingStartDistance;
   private boolean useButtonTexture;
   private ButtonColor buttonColor;
   private ButtonIcon buttonCheckedIcon;
   private ButtonIcon buttonUncheckedIcon;
   protected FormEventsHandler<FormInputEvent<FormCheckBox>> clickedEvents;
   protected FormEventsHandler<FormInputEvent<FormCheckBox>> changedHoverEvents;
   protected FormEventsHandler<FormDraggingEvent<FormCheckBox>> dragStartedEvents;
   protected InputEvent dragStartDownEvent;

   public FormCheckBox(String var1, int var2, int var3) {
      this(var1, var2, var3, false);
   }

   public FormCheckBox(String var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, -1, var4);
   }

   public FormCheckBox(String var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public FormCheckBox(String var1, int var2, int var3, int var4, boolean var5) {
      this.fontOptions = (new FontOptions(12)).defaultColor(Settings.UI.activeTextColor);
      this.useHoverMoveEvents = true;
      this.acceptMouseRepeatEvents = false;
      this.acceptRightClicks = false;
      this.handleClicksIfNoEventHandlers = false;
      this.submitControllerPressEvent = false;
      this.draggingStartDistance = 5.0;
      this.position = new FormFixedPosition(var2, var3);
      this.setText(var1, var4);
      this.checked = var5;
      this.clickedEvents = new FormEventsHandler();
      this.clickHandler = new FormClickHandler((var1x) -> {
         return this.isActive() && this.isMouseOver(var1x);
      }, (var1x) -> {
         return var1x.getID() == -100 || var1x.getID() == -99 && this.acceptRightClicks;
      }, (var1x) -> {
         if (this.isDraggingThis()) {
            var1x.use();
         } else {
            boolean var2 = this.checked;
            FormInputEvent var3 = new FormInputEvent(this, var1x);
            this.checked = !var2;
            this.clickedEvents.onEvent(var3);
            if (var3.hasPreventedDefault()) {
               this.checked = var2;
               var1x.use();
            } else if (var1x.shouldSubmitSound()) {
               this.playTickSound();
            }

         }
      });
      this.changedHoverEvents = new FormEventsHandler();
      this.dragStartedEvents = new FormEventsHandler();
      this.setActive(true);
   }

   public FormCheckBox useButtonTexture(ButtonColor var1, ButtonIcon var2, ButtonIcon var3) {
      this.useButtonTexture = true;
      this.buttonColor = var1;
      this.buttonCheckedIcon = var2;
      this.buttonUncheckedIcon = var3;
      this.fontOptions = (new FontOptions(16)).defaultColor(Settings.UI.activeTextColor);
      this.setText(this.text, this.maxWidth);
      return this;
   }

   public FormCheckBox useButtonTexture(ButtonColor var1) {
      return this.useButtonTexture(var1, Settings.UI.button_checked_20, (ButtonIcon)null);
   }

   public FormCheckBox useButtonTexture() {
      return this.useButtonTexture(ButtonColor.BASE);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      boolean var7;
      FormInputEvent var8;
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

         var7 = this.isMouseOver(var1);
         if (this.isHovering != var7) {
            this.isHovering = var7;
            var8 = new FormInputEvent(this, var1);
            this.changedHoverEvents.onEvent(var8);
         }

         if (var7 && this.useHoverMoveEvents) {
            var1.useMove();
         }
      }

      if (this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners()) {
         if (this.acceptMouseRepeatEvents) {
            if (var1.state && (Boolean)this.clickHandler.eventAccept.apply(var1) && (var1.getID() == -100 || var1.isRepeatEvent((Object)this))) {
               if (this.isDraggingThis()) {
                  var1.use();
                  return;
               }

               var1.startRepeatEvents(this);
               var7 = this.checked;
               var8 = new FormInputEvent(this, var1);
               this.checked = !var7;
               this.clickedEvents.onEvent(var8);
               if (var8.hasPreventedDefault()) {
                  this.checked = var7;
                  var1.use();
               } else if (var1.shouldSubmitSound()) {
                  this.playTickSound();
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
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      if (this.useButtonTexture) {
         FormInputSize var5 = FormInputSize.SIZE_20;
         int var6 = this.lines.size() * this.fontOptions.getSize() / 2 - 10;
         ButtonState var7;
         if (!this.isActive()) {
            var7 = ButtonState.INACTIVE;
         } else if (this.isHovering()) {
            var7 = ButtonState.HIGHLIGHTED;
         } else {
            var7 = ButtonState.ACTIVE;
         }

         var5.getButtonDrawOptions(this.buttonColor, ButtonState.ACTIVE, this.getX(), this.getY() + var6, 20, var4).draw();
         ButtonIcon var8 = this.checked ? this.buttonCheckedIcon : this.buttonUncheckedIcon;
         if (var8 != null) {
            GameTexture var9 = var8.texture;
            if (var9 != null) {
               var9.initDraw().color((Color)var8.colorGetter.apply(var7)).draw(this.getX() + 10 - var9.getWidth() / 2, this.getY() + var6 + var5.textureDrawOffset + 10 - var9.getHeight() / 2);
            }
         }

         var5.getButtonEdgeDrawOptions(this.buttonColor, ButtonState.ACTIVE, this.getX(), this.getY() + var6, 20, var4).draw();
      } else {
         HoverStateTextures var10 = this.checked ? Settings.UI.checkbox_checked : Settings.UI.checkbox;
         GameTexture var12 = this.isHovering() ? var10.highlighted : var10.active;
         int var14 = this.lines.size() * this.fontOptions.getSize() / 2 - var12.getHeight() / 2;
         var12.initDraw().color(var4).draw(this.getX(), this.getY() + var14);
      }

      for(int var11 = 0; var11 < this.lines.size(); ++var11) {
         FontManager.bit.drawString((float)(this.getX() + (this.useButtonTexture ? 24 : 18)), (float)(this.getY() + this.fontOptions.getSize() * var11), (String)this.lines.get(var11), this.fontOptions);
      }

      if (this.isHovering()) {
         GameTooltips var13 = this.getTooltip();
         if (var13 != null) {
            Screen.addTooltip(var13, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      Rectangle var2 = var1.boundingBox;
      if (this.useButtonTexture) {
         var2 = new Rectangle(var2.x, var2.y + this.lines.size() * this.fontOptions.getSize() / 2 - 10 + 1, 20, 20);
      } else {
         HoverStateTextures var3 = this.checked ? Settings.UI.checkbox_checked : Settings.UI.checkbox;
         GameTexture var4 = this.isHovering() ? var3.highlighted : var3.active;
         var2 = new Rectangle(var2.x, var2.y + this.lines.size() * this.fontOptions.getSize() / 2 - var4.getHeight() / 2 + 1, var4.getWidth(), var4.getHeight());
      }

      byte var5 = 5;
      var2 = new Rectangle(var2.x - var5, var2.y - var5, var2.width + var5 * 2, var2.height + var5 * 2);
      HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsColor, true, var2).draw();
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
   }

   public GameTooltips getTooltip() {
      return null;
   }

   public boolean isHovering() {
      return this.isHovering || this.isControllerFocus();
   }

   public Color getDrawColor() {
      if (!this.isActive()) {
         return Settings.UI.inactiveElementColor;
      } else {
         return this.isHovering() ? Settings.UI.highlightElementColor : Settings.UI.activeElementColor;
      }
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY() - 1, (this.useButtonTexture ? 24 : 18) + this.width, this.lines.size() * this.fontOptions.getSize() + 2));
   }

   public FormCheckBox onClicked(FormEventListener<FormInputEvent<FormCheckBox>> var1) {
      this.clickedEvents.addListener(var1);
      return this;
   }

   public FormCheckBox onChangedHover(FormEventListener<FormInputEvent<FormCheckBox>> var1) {
      this.changedHoverEvents.addListener(var1);
      return this;
   }

   public FormCheckBox onDragStarted(FormEventListener<FormDraggingEvent<FormCheckBox>> var1) {
      this.dragStartedEvents.addListener(var1);
      return this;
   }

   public void setupDragToOtherCheckboxes(Object var1, boolean var2) {
      this.onDragStarted((var3) -> {
         final int var4 = var3.draggingStartedEvent.isUsed() ? var3.draggingStartedEvent.getLastID() : var3.draggingStartedEvent.getID();
         Screen.setMouseDraggingElement(new FormCheckboxDraggingElement(this, var1) {
            public boolean isKeyDown(Input var1) {
               return var1.isKeyDown(var4);
            }
         });
         this.checked = !this.checked;
         if (var2) {
            this.clickedEvents.onEvent(new FormInputEvent(this, var3.event));
            this.playTickSound();
         }

      });
      this.onChangedHover((var3) -> {
         if (this.isHovering() && this.isActive()) {
            MouseDraggingElement var4 = Screen.getMouseDraggingElement();
            if (var4 instanceof FormCheckboxDraggingElement) {
               FormCheckboxDraggingElement var5 = (FormCheckboxDraggingElement)var4;
               if (var5.component != this && this.checked != var5.component.checked && Objects.equals(var5.sameObject, var1)) {
                  this.checked = var5.component.checked;
                  if (var2) {
                     this.clickedEvents.onEvent(new FormInputEvent(this, var3.event));
                  }

                  this.playTickSound();
               }
            }
         }

      });
   }

   public void setupDragToOtherCheckboxes(Object var1) {
      this.setupDragToOtherCheckboxes(var1, true);
   }

   protected boolean isDraggingThis() {
      MouseDraggingElement var1 = Screen.getMouseDraggingElement();
      if (var1 instanceof FormCheckboxDraggingElement) {
         FormCheckboxDraggingElement var2 = (FormCheckboxDraggingElement)var1;
         return var2.component == this;
      } else {
         return false;
      }
   }

   public void setText(String var1, int var2) {
      this.text = var1;
      this.maxWidth = var2;
      this.width = 0;
      this.lines = GameUtils.breakString(var1, this.fontOptions, var2 > 0 ? Math.max(1, var2 - (this.useButtonTexture ? 24 : 18)) : Integer.MAX_VALUE);
      Iterator var3 = this.lines.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         int var5 = FontManager.bit.getWidthCeil(var4, this.fontOptions);
         if (this.width < var5) {
            this.width = var5;
         }
      }

   }

   public void setText(String var1) {
      this.setText(var1, -1);
   }

   public String getText() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.lines.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.append(var3).append("\n");
      }

      return var1.toString().trim();
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean var1) {
      this.active = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public void drawDraggingElement(int var1, int var2) {
   }

   protected static class FormCheckboxDraggingElement implements MouseDraggingElement {
      public final FormCheckBox component;
      public final Object sameObject;

      public FormCheckboxDraggingElement(FormCheckBox var1, Object var2) {
         this.component = var1;
         this.sameObject = var2;
      }

      public boolean draw(int var1, int var2) {
         this.component.drawDraggingElement(var1, var2);
         return true;
      }
   }
}
