package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.HoverStateTextures;

public class FormDropdownButton extends FormComponent implements FormPositionContainer {
   public boolean removingSubmenuRemovesParent;
   protected FormPosition position;
   protected boolean isActive;
   protected int width;
   private GameMessage text;
   protected FormInputSize size;
   protected ButtonColor color;
   protected boolean isHovering;
   public int textAlign;
   public boolean alignLeftIfNotFit;
   public final OptionsList options;
   private SelectionFloatMenu currentMenu;
   protected FormClickHandler clickHandler;

   public FormDropdownButton(int var1, int var2, FormInputSize var3, ButtonColor var4, int var5, GameMessage var6) {
      this.removingSubmenuRemovesParent = true;
      this.isActive = true;
      this.textAlign = 0;
      this.alignLeftIfNotFit = true;
      this.options = new OptionsList(this);
      this.clickHandler = new FormClickHandler((var1x) -> {
         return this.isActive() && this.isMouseOver(var1x) && (this.currentMenu == null || this.currentMenu.isDisposed() && !InputEvent.isFromSameEvent(var1x, this.currentMenu.removeEvent));
      }, -100, (var1x) -> {
         this.playTickSound();
         this.currentMenu = this.options.getMenu(this.width - 4, this.removingSubmenuRemovesParent);
         Rectangle var2 = this.size.getContentRectangle(this.width);
         this.getManager().openFloatMenu(this.currentMenu, this.getX() - var1x.pos.hudX, this.getY() - var1x.pos.hudY + var2.y + var2.height + 2);
      });
      this.position = new FormFixedPosition(var1, var2);
      this.size = var3;
      this.width = var5;
      this.color = var4;
      this.setText(var6);
   }

   public FormDropdownButton(int var1, int var2, FormInputSize var3, ButtonColor var4, int var5) {
      this(var1, var2, var3, var4, var5, new LocalMessage("ui", "selectbutton"));
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      } else {
         this.clickHandler.handleEvent(var1);
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

   public void setText(GameMessage var1) {
      this.text = var1;
   }

   private FairTypeDrawOptions getTextDrawOptions() {
      return (new FairType()).append(this.size.getFontOptions().color(this.getTextColor()), this.getDisplayText()).getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
   }

   public String getDisplayText() {
      return this.text.translate();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      ButtonState var5 = this.getButtonState();
      int var6 = 0;
      boolean var7 = this.clickHandler.isDown() && this.isHovering;
      if (var7) {
         this.size.getButtonDownDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
         var6 = this.size.buttonDownContentDrawOffset;
      } else {
         this.size.getButtonDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      }

      HoverStateTextures var8 = this.size.height < 20 ? Settings.UI.button_select_small : Settings.UI.button_select_big;
      GameTexture var9 = var5 == ButtonState.HIGHLIGHTED ? var8.highlighted : var8.active;
      Rectangle var10 = this.size.getContentRectangle(this.width);
      FormShader.FormShaderState var11 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(var10.x, var10.y, var10.width - var9.getWidth() - 2, var10.height));

      try {
         FairTypeDrawOptions var12 = this.getTextDrawOptions();
         if (this.textAlign == -1) {
            var12.draw(var10.x, var6 + this.size.fontDrawOffset, Color.BLACK);
         } else if (this.textAlign == 1) {
            var12.draw(var10.x + this.width - 5 - var12.getBoundingBox().width, var6 + this.size.fontDrawOffset, Color.BLACK);
         } else if (this.alignLeftIfNotFit && var12.getBoundingBox().width > var10.width) {
            var12.draw(var10.x, var6 + this.size.fontDrawOffset, Color.BLACK);
         } else {
            var12.draw(this.width / 2 - var12.getBoundingBox().width / 2, var6 + this.size.fontDrawOffset, Color.BLACK);
         }
      } finally {
         var11.end();
      }

      FormShader.FormShaderState var21 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(var10.x, var10.y, var10.width, var10.height));

      try {
         int var13 = var9.getHeight();
         var9.initDraw().color(var4).draw(var10.x + var10.width - var9.getWidth() - 2, var10.y + var10.height / 2 - var13 / 2 + var6);
      } finally {
         var21.end();
      }

      if (var7) {
         this.size.getButtonDownEdgeDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      } else {
         this.size.getButtonEdgeDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      super.drawControllerFocus(var1);
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
   }

   protected Color getDrawColor() {
      return this.size.getButtonColor(this.getButtonState());
   }

   protected ButtonState getButtonState() {
      if (!this.isActive()) {
         return ButtonState.INACTIVE;
      } else {
         return this.isHovering ? ButtonState.HIGHLIGHTED : ButtonState.ACTIVE;
      }
   }

   public Color getTextColor() {
      return (Color)this.getButtonState().textColorGetter.apply(Settings.UI);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public void setActive(boolean var1) {
      this.isActive = var1;
   }

   public static class OptionsList {
      private final FormDropdownButton button;
      private final LinkedList<OptionContainer> options;

      private OptionsList(FormDropdownButton var1) {
         this.options = new LinkedList();
         this.button = var1;
      }

      public OptionsList add(Option var1) {
         this.options.add(new ValueOptionContainer(this.button, var1));
         return this;
      }

      public OptionsList add(GameMessage var1, Runnable var2) {
         return this.add(var1, (Supplier)null, var2);
      }

      public OptionsList add(GameMessage var1, Supplier<GameMessage> var2, Runnable var3) {
         return this.add(var1, var2, (Supplier)null, var3);
      }

      public OptionsList add(GameMessage var1, Supplier<GameMessage> var2, Supplier<Boolean> var3, Runnable var4) {
         return this.add(new Option(var1, var3, (Color)null, var2 == null ? null : () -> {
            GameMessage var1 = (GameMessage)var2.get();
            return var1 != null ? new StringTooltips(var1.translate()) : null;
         }, var4));
      }

      public OptionsList addSub(GameMessage var1) {
         OptionsList var2 = new OptionsList(this.button);
         this.options.add(new SubMenuOptionContainer(this.button, var2, var1));
         return var2;
      }

      public void clear() {
         this.options.clear();
      }

      public int size() {
         return this.options.size();
      }

      public boolean isEmpty() {
         return this.options.isEmpty();
      }

      private SelectionFloatMenu getMenu(int var1, boolean var2) {
         SelectionFloatMenu var3 = new SelectionFloatMenu(this.button, SelectionFloatMenu.Solid(new FontOptions(12)), var1);
         this.options.forEach((var2x) -> {
            var2x.addToMenu(var3, var2);
         });
         return var3;
      }

      // $FF: synthetic method
      OptionsList(FormDropdownButton var1, Object var2) {
         this(var1);
      }
   }

   public static class Option {
      public final GameMessage text;
      public final Supplier<Boolean> isActive;
      public final Color textColor;
      public final Supplier<GameTooltips> hoverTooltips;
      public final Runnable action;

      public Option(GameMessage var1, Supplier<Boolean> var2, Color var3, Supplier<GameTooltips> var4, Runnable var5) {
         this.text = var1;
         this.isActive = var2;
         this.textColor = var3;
         this.hoverTooltips = var4;
         this.action = var5;
      }

      public Option(GameMessage var1, Runnable var2) {
         this(var1, (Supplier)null, (Color)null, (Supplier)null, var2);
      }
   }

   private static class SubMenuOptionContainer extends OptionContainer {
      public final FormDropdownButton button;
      public final OptionsList subOptions;
      public final GameMessage text;

      public SubMenuOptionContainer(FormDropdownButton var1, OptionsList var2, GameMessage var3) {
         super(null);
         this.button = var1;
         this.subOptions = var2;
         this.text = var3;
      }

      public void addToMenu(SelectionFloatMenu var1, boolean var2) {
         var1.add(this.text.translate(), this.subOptions.getMenu(0, var2), var2);
      }
   }

   private static class ValueOptionContainer extends OptionContainer {
      public final FormDropdownButton button;
      public final Option option;

      public ValueOptionContainer(FormDropdownButton var1, Option var2) {
         super(null);
         this.button = var1;
         this.option = var2;
      }

      public void addToMenu(SelectionFloatMenu var1, boolean var2) {
         var1.add(this.option.text.translate(), this.option.isActive, this.option.textColor, this.option.hoverTooltips, () -> {
            this.option.action.run();
            if (this.button.currentMenu != null && !this.button.currentMenu.isDisposed()) {
               this.button.currentMenu.remove();
            }

         });
      }
   }

   private abstract static class OptionContainer {
      private OptionContainer() {
      }

      public abstract void addToMenu(SelectionFloatMenu var1, boolean var2);

      // $FF: synthetic method
      OptionContainer(Object var1) {
         this();
      }
   }
}
