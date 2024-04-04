package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormGrabbedEvent;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;

public class FormSlider extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private int width;
   protected String text;
   public boolean drawValue;
   public boolean drawValueInPercent;
   public boolean allowScroll;
   private int value;
   private float percentage;
   private int minValue;
   private int maxValue;
   private boolean mouseDown;
   private boolean isControllerSelected;
   private int mouseDownGlobalX;
   private int mouseDownBarX;
   private FontOptions fontOptions;
   private MouseWheelBuffer wheelBuffer;
   private boolean isHoveringSlider;
   private boolean isHoveringBar;
   private boolean isHoveringText;
   private FormEventsHandler<FormInputEvent<FormSlider>> changedEvents;
   private FormEventsHandler<FormGrabbedEvent<FormSlider>> grabEvents;
   private FormEventsHandler<FormInputEvent<FormSlider>> scrollEvents;

   public FormSlider(String var1, int var2, int var3, int var4, int var5, int var6, int var7, FontOptions var8) {
      this.allowScroll = true;
      this.wheelBuffer = new MouseWheelBuffer(false);
      this.changedEvents = new FormEventsHandler();
      this.grabEvents = new FormEventsHandler();
      this.scrollEvents = new FormEventsHandler();
      this.text = var1;
      this.position = new FormFixedPosition(var2, var3);
      this.minValue = var5;
      this.maxValue = var6;
      this.width = var7;
      this.fontOptions = var8.defaultColor(Settings.UI.activeTextColor);
      this.setValue(var4);
      this.drawValue = true;
      this.drawValueInPercent = true;
   }

   public FormSlider(String var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, new FontOptions(16));
   }

   public FormSlider setValue(int var1) {
      this.value = Math.max(this.minValue, Math.min(this.maxValue, var1));
      this.percentage = (float)(this.value - this.minValue) / (float)(this.maxValue - this.minValue);
      return this;
   }

   public int getValue() {
      return this.value;
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   public int getMinValue() {
      return this.minValue;
   }

   public FormSlider setPercentage(float var1) {
      this.percentage = Math.max(0.0F, Math.min(var1, 1.0F));
      this.value = (int)(this.percentage * (float)(this.maxValue - this.minValue)) + this.minValue;
      return this;
   }

   public float getPercentage() {
      return this.percentage;
   }

   public FormSlider setRange(int var1, int var2) {
      this.minValue = var1;
      this.maxValue = var2;
      this.setValue(this.getValue());
      return this;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.allowScroll && var1.state && var1.isMouseWheelEvent() && this.isMouseOverBar(var1)) {
         this.wheelBuffer.add(var1);
         this.wheelBuffer.useScrollY((var2x) -> {
            FormInputEvent var3;
            if (var2x) {
               if (this.getValue() < this.maxValue) {
                  var3 = new FormInputEvent(this, var1);
                  this.setValue(this.getValue() + 1);
                  this.changedEvents.onEvent(var3);
                  this.scrollEvents.onEvent(var3);
                  if (!var3.hasPreventedDefault()) {
                     this.playTickSound();
                  }
               }
            } else if (this.getValue() > this.minValue) {
               var3 = new FormInputEvent(this, var1);
               this.setValue(this.getValue() - 1);
               this.changedEvents.onEvent(var3);
               this.scrollEvents.onEvent(var3);
               if (!var3.hasPreventedDefault()) {
                  this.playTickSound();
               }
            }

         });
         var1.use();
      }

      if (!var1.isKeyboardEvent()) {
         FormGrabbedEvent var4;
         FormInputEvent var5;
         if (var1.state) {
            if (var1.getID() == -100 & this.isMouseOverBar(var1)) {
               var4 = new FormGrabbedEvent(this, var1, true);
               this.mouseDown = true;
               this.mouseDownGlobalX = Screen.mousePos().hudX;
               this.mouseDownBarX = var1.pos.hudX - this.getX() - 5;
               this.grabEvents.onEvent(var4);
               if (!var4.hasPreventedDefault()) {
                  this.playTickSound();
               }

               var5 = new FormInputEvent(this, var1);
               this.setPercentage((float)this.mouseDownBarX / ((float)this.width - 10.0F));
               this.changedEvents.onEvent(var5);
               var1.use();
            }
         } else if (var1.getID() == -100 & this.mouseDown) {
            var4 = new FormGrabbedEvent(this, var1, false);
            this.mouseDown = false;
            this.grabEvents.onEvent(var4);
            var1.use();
         }

         if (var1.isMouseMoveEvent()) {
            if (this.mouseDown) {
               int var6 = Screen.mousePos().hudX - this.mouseDownGlobalX;
               var5 = new FormInputEvent(this, var1);
               this.setPercentage((float)(this.mouseDownBarX + var6) / ((float)this.width - 10.0F));
               this.changedEvents.onEvent(var5);
            }

            this.isHoveringBar = this.isMouseOverBar(var1);
            this.isHoveringSlider = this.isMouseOverSlider(var1);
            this.isHoveringText = this.isMouseOverText(var1);
            if (this.isHoveringBar || this.isHoveringSlider || this.isHoveringText) {
               var1.useMove();
            }
         }

      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      FormGrabbedEvent var4;
      if (var1.getState() == ControllerInput.MENU_SELECT) {
         if (this.isControllerFocus() && var1.buttonState) {
            var4 = new FormGrabbedEvent(this, InputEvent.ControllerButtonEvent(var1, var2), true);
            this.isControllerSelected = true;
            this.grabEvents.onEvent(var4);
            if (!var4.hasPreventedDefault()) {
               this.playTickSound();
            }

            var1.use();
         }
      } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && this.isControllerSelected && var1.buttonState) {
         var4 = new FormGrabbedEvent(this, InputEvent.ControllerButtonEvent(var1, var2), false);
         this.isControllerSelected = false;
         this.grabEvents.onEvent(var4);
         if (!var4.hasPreventedDefault()) {
            this.playTickSound();
         }

         var1.use();
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      super.onControllerUnfocused(var1);
      this.isControllerSelected = false;
   }

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      if (this.isControllerSelected) {
         FormInputEvent var5;
         switch (var1) {
            case 1:
               if (this.getValue() < this.maxValue) {
                  var5 = new FormInputEvent(this, InputEvent.ControllerButtonEvent(var2, var3));
                  this.setValue(this.getValue() + 1);
                  this.changedEvents.onEvent(var5);
                  this.scrollEvents.onEvent(var5);
                  if (!var5.hasPreventedDefault() && var2.shouldSubmitSound()) {
                     this.playTickSound();
                  }
               }

               var2.use();
               break;
            case 3:
               if (this.getValue() > this.minValue) {
                  var5 = new FormInputEvent(this, InputEvent.ControllerButtonEvent(var2, var3));
                  this.setValue(this.getValue() - 1);
                  this.changedEvents.onEvent(var5);
                  this.scrollEvents.onEvent(var5);
                  if (!var5.hasPreventedDefault() && var2.shouldSubmitSound()) {
                     this.playTickSound();
                  }
               }

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

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = Settings.UI.activeElementColor;
      GameTexture var5 = Settings.UI.slider.active;
      if (this.isHoveringSlider || this.isGrabbed()) {
         var4 = Settings.UI.highlightElementColor;
         var5 = Settings.UI.slider.highlighted;
      }

      String var6 = this.getValueText();
      int var7 = FontManager.bit.getWidthCeil(var6, this.fontOptions);
      FontManager.bit.drawString((float)(this.getX() + this.width - var7), (float)this.getY(), var6, this.fontOptions);
      int var8 = this.width - var7;
      String var9 = GameUtils.maxString(this.text, this.fontOptions, var8 - 10);
      if (!var9.equals(this.text)) {
         var9 = var9 + "...";
         if (this.isHoveringText || this.isControllerSelected || this.isControllerFocus(this)) {
            Screen.addTooltip(new StringTooltips(this.text), TooltipLocation.FORM_FOCUS);
         }
      }

      FontManager.bit.drawString((float)this.getX(), (float)this.getY(), var9, this.fontOptions);
      int var10 = this.getTextHeight();
      drawWidthComponent(new GameSprite(var5, 0, 0, var5.getHeight()), new GameSprite(var5, 1, 0, var5.getHeight()), this.getX(), this.getY() + var10, this.width);
      var5.initDraw().section(var5.getHeight() * 2, var5.getWidth(), 0, var5.getHeight()).color(var4).draw(this.getX() + this.getSliderPixelProgress(var5), this.getY() + this.getTextHeight());
   }

   public void drawControllerFocus(ControllerFocus var1) {
      if (this.isControllerSelected) {
         Rectangle var2 = var1.boundingBox;
         int var3 = this.getTextHeight();
         GameTexture var4 = Settings.UI.slider.active;
         int var5 = this.getSliderPixelProgress(var4);
         var2 = new Rectangle(var2.x + var5, var2.y + var3, var4.getWidth() - var4.getHeight() * 2 + 1, var4.getHeight() + 1);
         byte var6 = 5;
         var2 = new Rectangle(var2.x - var6, var2.y - var6, var2.width + var6 * 2, var2.height + var6 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var2).draw();
      } else {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

   }

   public boolean isGrabbed() {
      return this.mouseDown || this.isControllerSelected;
   }

   public FormSlider onChanged(FormEventListener<FormInputEvent<FormSlider>> var1) {
      this.changedEvents.addListener(var1);
      return this;
   }

   public FormSlider onGrab(FormEventListener<FormGrabbedEvent<FormSlider>> var1) {
      this.grabEvents.addListener(var1);
      return this;
   }

   public FormSlider onScroll(FormEventListener<FormInputEvent<FormSlider>> var1) {
      this.scrollEvents.addListener(var1);
      return this;
   }

   public String getValueText() {
      if (!this.drawValue) {
         return "";
      } else {
         return this.drawValueInPercent ? (int)(this.getPercentage() * 100.0F) + "%" : String.valueOf(this.getValue());
      }
   }

   protected int getTextHeight() {
      return !this.drawValue && this.text.isEmpty() ? 0 : this.fontOptions.getSize() + 4;
   }

   public int getTotalHeight() {
      return this.getTextHeight() + Settings.UI.slider.active.getHeight();
   }

   public List<Rectangle> getHitboxes() {
      int var1 = this.getTextHeight();
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, var1 + Settings.UI.slider.active.getHeight()));
   }

   public int getSliderPixelProgress(GameTexture var1) {
      int var2 = var1.getWidth() - var1.getHeight() * 2;
      return (int)(this.getPercentage() * (float)(this.width - var2));
   }

   public boolean isMouseOverSlider(InputEvent var1) {
      GameTexture var2 = Settings.UI.slider.active;
      int var3 = var2.getWidth() - var2.getHeight() * 2;
      return (new Rectangle(this.getX() + this.getSliderPixelProgress(var2), this.getY() + this.getTextHeight(), var3, var2.getHeight())).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public boolean isMouseOverBar(InputEvent var1) {
      return (new Rectangle(this.getX(), this.getY() + this.getTextHeight(), this.width, 13)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public boolean isMouseOverText(InputEvent var1) {
      int var2 = this.getTextHeight();
      return var2 > 0 ? (new Rectangle(this.getX(), this.getY(), this.width, var2)).contains(var1.pos.hudX, var1.pos.hudY) : false;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
