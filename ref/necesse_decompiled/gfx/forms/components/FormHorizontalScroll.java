package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
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
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.HoverStateTextures;

public class FormHorizontalScroll<T> extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private boolean active;
   protected int scroll;
   protected ScrollElement<T>[] elements;
   protected ScrollElement<T> customElement;
   public DrawOption drawOption;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
   private boolean isHovering;
   private boolean isHoveringLeft;
   private boolean isHoveringRight;
   private boolean isControllerSelected;
   private FormEventsHandler<FormInputEvent<FormHorizontalScroll<T>>> changedEvents = new FormEventsHandler();

   @SafeVarargs
   public FormHorizontalScroll(int var1, int var2, int var3, DrawOption var4, int var5, ScrollElement<T>... var6) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.drawOption = var4;
      this.scroll = var5;
      this.elements = var6;
      this.setActive(true);
   }

   public FormHorizontalScroll<T> onChanged(FormEventListener<FormInputEvent<FormHorizontalScroll<T>>> var1) {
      this.changedEvents.addListener(var1);
      return this;
   }

   public ScrollElement<T> getCurrent() {
      return this.scroll == -1 ? this.customElement : this.elements[this.scroll];
   }

   public T getValue() {
      return this.scroll == -1 ? this.customElement.value : this.elements[this.scroll].value;
   }

   public void setValue(T var1) {
      for(int var2 = 0; var2 < this.elements.length; ++var2) {
         if (Objects.equals(this.elements[var2].value, var1)) {
            this.scroll = var2;
            return;
         }
      }

      throw new NoSuchElementException("Cannot find scroll element with value " + var1);
   }

   public void setElement(ScrollElement<T> var1) {
      Objects.requireNonNull(var1);

      try {
         this.setValue(var1.value);
      } catch (NoSuchElementException var3) {
         this.customElement = var1;
         this.scroll = -1;
      }

   }

   public boolean hasValue(T var1) {
      ScrollElement[] var2 = this.elements;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ScrollElement var5 = var2[var4];
         if (Objects.equals(var5.value, var1)) {
            return true;
         }
      }

      return false;
   }

   public void setData(ScrollElement<T>[] var1) {
      this.elements = var1;
      if (this.scroll >= var1.length) {
         this.scroll = var1.length - 1;
      }

   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         this.isHoveringLeft = this.isMouseOverLeft(var1);
         this.isHoveringRight = this.isMouseOverRight(var1);
         if (this.isHovering || this.isHoveringLeft || this.isHoveringRight) {
            var1.useMove();
         }
      }

      if (this.isActive()) {
         if (var1.isMouseWheelEvent() && !var1.state && this.isMouseOver(var1)) {
            this.wheelBuffer.add(var1);
            this.wheelBuffer.useScrollY((var2x) -> {
               if (var2x) {
                  this.increase(var1);
               } else {
                  this.decrease(var1);
               }

            });
         }

         if (var1.state && !var1.isKeyboardEvent()) {
            if (var1.getID() == -100) {
               if (this.isMouseOverLeft(var1)) {
                  this.decrease(var1);
               }

               if (this.isMouseOverRight(var1)) {
                  this.increase(var1);
               }
            }

         }
      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isActive()) {
         if (var1.getState() == ControllerInput.MENU_SELECT) {
            if (this.isControllerFocus() && var1.buttonState) {
               this.isControllerSelected = true;
               var1.use();
            }
         } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && this.isControllerSelected && var1.buttonState) {
            this.isControllerSelected = false;
            var1.use();
         }
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      super.onControllerUnfocused(var1);
      this.isControllerSelected = false;
   }

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      if (this.isControllerSelected && this.isActive()) {
         switch (var1) {
            case 1:
               this.increase(InputEvent.ControllerButtonEvent(var2, var3));
               var2.use();
               break;
            case 3:
               this.decrease(InputEvent.ControllerButtonEvent(var2, var3));
               var2.use();
         }

         return true;
      } else {
         return super.handleControllerNavigate(var1, var2, var3, var4);
      }
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public void increase(InputEvent var1) {
      int var2 = this.scroll;
      FormInputEvent var3 = new FormInputEvent(this, var1);
      ++this.scroll;
      if (this.scroll >= this.elements.length) {
         this.scroll = 0;
      }

      this.changedEvents.onEvent(var3);
      if (var3.hasPreventedDefault()) {
         this.scroll = var2;
      } else {
         this.playTickSound();
      }

      if (var1 != null) {
         var1.use();
      }

   }

   public void decrease(InputEvent var1) {
      int var2 = this.scroll;
      FormInputEvent var3 = new FormInputEvent(this, var1);
      --this.scroll;
      if (this.scroll < 0) {
         this.scroll = this.elements.length - 1;
      }

      this.changedEvents.onEvent(var3);
      if (var3.hasPreventedDefault()) {
         this.scroll = var2;
      } else {
         this.playTickSound();
      }

      if (var1 != null) {
         var1.use();
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = Settings.UI.activeTextColor;
      if (!this.isActive()) {
         var4 = Settings.UI.inactiveTextColor;
      }

      ScrollElement var5 = this.getDrawText();
      var5.draw(this.getX() + this.width / 2, this.getY() + 1, var4, this.isHovering || this.isControllerFocus() && this.isControllerSelected, this.drawOption);
      HoverStateTextures var6 = Settings.UI.button_navigate_horizontal;
      if (this.isActive()) {
         GameTexture var7 = !this.isHoveringLeft && !this.isControllerSelected ? var6.active : var6.highlighted;
         Color var8 = !this.isHoveringLeft && !this.isControllerSelected ? Settings.UI.activeElementColor : Settings.UI.highlightElementColor;
         GameTexture var9 = !this.isHoveringRight && !this.isControllerSelected ? var6.active : var6.highlighted;
         Color var10 = !this.isHoveringRight && !this.isControllerSelected ? Settings.UI.activeElementColor : Settings.UI.highlightElementColor;
         var7.initDraw().color(var8).draw(this.getX(), this.getY() - 1);
         var9.initDraw().color(var10).mirrorX().draw(this.getX() + this.width - var9.getWidth() - 1, this.getY() - 1);
      } else {
         var6.active.initDraw().color(Settings.UI.inactiveElementColor).draw(this.getX(), this.getY() - 1);
         var6.active.initDraw().mirrorX().color(Settings.UI.inactiveElementColor).draw(this.getX() + this.width - var6.active.getWidth() - 1, this.getY() - 1);
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      if (this.isControllerSelected) {
         Rectangle var2 = var1.boundingBox;
         byte var3 = 5;
         var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var2).draw();
      } else {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

   }

   public ScrollElement getDrawText() {
      return this.scroll == -1 ? this.getCustomElement() : this.elements[this.scroll];
   }

   public ScrollElement getCustomElement() {
      return this.customElement;
   }

   public boolean isMouseOverLeft(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY(), 10, 14)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public boolean isMouseOverRight(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX() + this.width - 10, this.getY(), 10, 14)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY() - 2, this.width, 16));
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

   public static enum DrawOption {
      string,
      value,
      valueOnHover;

      private DrawOption() {
      }

      // $FF: synthetic method
      private static DrawOption[] $values() {
         return new DrawOption[]{string, value, valueOnHover};
      }
   }

   public static class ScrollElement<T> {
      public T value;
      public FontOptions fontOptions;
      public GameMessage str;
      public GameTooltips tooltips;

      public ScrollElement(T var1, GameMessage var2, FontOptions var3, GameTooltips var4) {
         this.value = var1;
         this.str = var2;
         this.fontOptions = var3;
         this.tooltips = var4;
      }

      public ScrollElement(T var1, String var2, FontOptions var3, GameTooltips var4) {
         this(var1, (GameMessage)(new StaticMessage(var2)), var3, var4);
      }

      public ScrollElement(T var1, GameMessage var2, FontOptions var3) {
         this(var1, (GameMessage)var2, var3, (GameTooltips)null);
      }

      public ScrollElement(T var1, String var2, FontOptions var3) {
         this(var1, (GameMessage)(new StaticMessage(var2)), var3);
      }

      public ScrollElement(T var1, GameMessage var2) {
         this(var1, var2, new FontOptions(12));
      }

      public ScrollElement(T var1, String var2) {
         this(var1, (GameMessage)(new StaticMessage(var2)));
      }

      public void draw(int var1, int var2, Color var3, boolean var4, DrawOption var5) {
         String var6 = "N/A";
         switch (var5) {
            case value:
               var6 = String.valueOf(this.value);
               break;
            case string:
               var6 = this.str == null ? "null" : this.str.translate();
               break;
            case valueOnHover:
               if (var4) {
                  var6 = String.valueOf(this.value);
               } else {
                  var6 = this.str == null ? "null" : this.str.translate();
               }
         }

         FontOptions var7 = (new FontOptions(this.fontOptions)).defaultColor(var3);
         FontManager.bit.drawString((float)(var1 - FontManager.bit.getWidthCeil(var6, var7) / 2), (float)(var2 + 6 - FontManager.bit.getHeightCeil(var6, var7) / 2), var6, var7);
         if (var4 && this.tooltips != null) {
            Screen.addTooltip(this.tooltips, TooltipLocation.FORM_FOCUS);
         }

      }
   }
}
