package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.FormClickHandler;
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
import necesse.gfx.gameFont.GameFontHandler;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormDialogueOption extends FormComponent implements FormPositionContainer {
   private static final int[] toolbarHotkeys = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 48};
   private GameFontHandler font;
   private int optionNumber;
   private FormPosition position;
   private GameMessage text;
   private int numberWidth;
   private int maxWidth;
   private FontOptions fontOptions;
   private ArrayList<Line> lines;
   private int linesWidth;
   private boolean isHovering;
   private FormClickHandler clickHandler;
   private boolean active;
   public boolean mouseOverCoversEntireWidth;
   protected FormEventsHandler<FormInputEvent<FormDialogueOption>> clickedEvents;
   public Supplier<GameTooltips> tooltipsSupplier;

   public FormDialogueOption(int var1, GameMessage var2, FontOptions var3, int var4, int var5, int var6) {
      this.font = FontManager.bit;
      this.lines = new ArrayList();
      this.active = true;
      this.mouseOverCoversEntireWidth = true;
      this.optionNumber = var1;
      this.fontOptions = var3.defaultColor(Settings.UI.activeTextColor);
      this.position = new FormFixedPosition(var4, var5);
      this.numberWidth = var1 > 0 ? this.font.getWidthCeil(var1 + ". ", var3) : 0;
      this.setText(var2, var6);
      this.clickedEvents = new FormEventsHandler();
      this.clickHandler = new FormClickHandler((var1x) -> {
         return this.acceptsEvents() && this.isMouseOver(var1x);
      }, -100, (var1x) -> {
         FormInputEvent var2 = new FormInputEvent(this, var1x);
         this.clickedEvents.onEvent(var2);
         if (!var2.hasPreventedDefault()) {
            this.pressed(var1x);
            var1x.use();
         }

      });
   }

   public FormDialogueOption onClicked(FormEventListener<FormInputEvent<FormDialogueOption>> var1) {
      this.clickedEvents.addListener(var1);
      return this;
   }

   protected void pressed(InputEvent var1) {
      this.playTickSound();
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      }

      this.clickHandler.handleEvent(var1);
      if (this.isActive() && var1.isKeyboardEvent() && var1.state && this.optionNumber >= 1 && this.optionNumber <= 10 && var1.getID() == toolbarHotkeys[this.optionNumber - 1]) {
         this.clickHandler.forceClick(var1);
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isActive() && var1.getState() == ControllerInput.MENU_SELECT) {
         if (this.isControllerFocus() && var1.buttonState) {
            InputEvent var4 = InputEvent.ControllerButtonEvent(var1, var2);
            this.clickHandler.forceHandleEvent(var4);
            var1.use();
         } else if (!var1.buttonState && this.clickHandler.isDown()) {
            this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(var1, var2));
            var1.use();
         }
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.text.hasUpdated()) {
         this.updateLines();
      }

      int var4 = this.getX();
      int var5 = this.getY();
      FontOptions var6 = (new FontOptions(this.fontOptions)).color(this.getDrawColor());
      if (this.optionNumber > 0) {
         this.font.drawString((float)var4, (float)var5, this.optionNumber + ". ", var6);
      }

      for(int var7 = 0; var7 < this.lines.size(); ++var7) {
         this.font.drawString((float)(var4 + this.numberWidth), (float)(var5 + var7 * var6.getSize()), ((Line)this.lines.get(var7)).str, var6);
      }

      if (this.isHovering() && this.tooltipsSupplier != null) {
         GameTooltips var8 = (GameTooltips)this.tooltipsSupplier.get();
         if (var8 != null) {
            Screen.addTooltip(var8, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      super.drawControllerFocus(var1);
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.numberWidth + this.linesWidth + 2, this.lines.size() * this.fontOptions.getSize()));
   }

   public boolean isMouseOver(InputEvent var1) {
      if (var1.isMoveUsed()) {
         return false;
      } else {
         int var2 = this.getX();
         int var3 = this.getY();
         if (this.mouseOverCoversEntireWidth) {
            return (new Rectangle(var2, var3, this.maxWidth, this.lines.size() * this.fontOptions.getSize())).contains(var1.pos.hudX, var1.pos.hudY);
         } else if ((new Rectangle(var2, var3, this.numberWidth, this.fontOptions.getSize())).contains(var1.pos.hudX, var1.pos.hudY)) {
            return true;
         } else {
            for(int var4 = 0; var4 < this.lines.size(); ++var4) {
               if ((new Rectangle(var2, var3 + var4 * this.fontOptions.getSize(), ((Line)this.lines.get(var4)).width + this.numberWidth, this.fontOptions.getSize())).contains(var1.pos.hudX, var1.pos.hudY)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public void setMaxWidth(int var1) {
      this.maxWidth = var1;
      this.updateLines();
   }

   public void setText(GameMessage var1) {
      this.text = var1;
      this.updateLines();
   }

   public void setText(GameMessage var1, int var2) {
      this.text = var1;
      this.maxWidth = var2;
      this.updateLines();
   }

   public GameMessage getText() {
      return this.text;
   }

   protected void updateLines() {
      String var1 = this.text.translate();
      ArrayList var2 = GameUtils.breakString(var1, this.fontOptions, this.maxWidth - this.numberWidth);
      this.lines = new ArrayList(var2.size());
      this.linesWidth = 0;

      int var5;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); this.linesWidth = Math.max(this.linesWidth, var5)) {
         String var4 = (String)var3.next();
         var5 = this.font.getWidthCeil(var4, this.fontOptions);
         this.lines.add(new Line(var4, var5));
      }

   }

   protected boolean acceptsEvents() {
      return this.isActive();
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

   protected boolean isHovering() {
      return this.isHovering;
   }

   public Color getDrawColor() {
      if (!this.isActive()) {
         return Settings.UI.inactiveTextColor;
      } else {
         return this.isHovering() ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
      }
   }

   protected static class Line {
      public final String str;
      public final int width;

      public Line(String var1, int var2) {
         this.str = var1;
         this.width = var2;
      }
   }
}
