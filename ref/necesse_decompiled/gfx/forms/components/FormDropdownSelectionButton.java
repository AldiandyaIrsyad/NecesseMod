package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.MouseDraggingElement;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormDraggingEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.events.FormValueEvent;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.HoverStateTextures;

public class FormDropdownSelectionButton<T> extends FormComponent implements FormPositionContainer {
   public boolean setSelectedText;
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
   private T selected;
   public final OptionsList<T> options;
   private SelectionFloatMenu currentMenu;
   public boolean useHoverMoveEvents;
   public boolean acceptRightClicks;
   public boolean submitControllerPressEvent;
   public double draggingStartDistance;
   protected FormClickHandler clickHandler;
   protected FormEventsHandler<FormValueEvent<FormDropdownSelectionButton<?>, T>> selectedEvents;
   protected FormEventsHandler<FormInputEvent<FormDropdownSelectionButton<T>>> changedHoverEvents;
   protected FormEventsHandler<FormDraggingEvent<FormDropdownSelectionButton<T>>> dragStartedEvents;
   protected InputEvent dragStartDownEvent;

   public FormDropdownSelectionButton(int var1, int var2, FormInputSize var3, ButtonColor var4, int var5, GameMessage var6) {
      this.setSelectedText = true;
      this.removingSubmenuRemovesParent = true;
      this.isActive = true;
      this.textAlign = 0;
      this.alignLeftIfNotFit = true;
      this.selected = null;
      this.options = new OptionsList(this);
      this.useHoverMoveEvents = true;
      this.acceptRightClicks = false;
      this.submitControllerPressEvent = false;
      this.draggingStartDistance = 5.0;
      this.clickHandler = new FormClickHandler((var1x) -> {
         return this.isActive() && this.isMouseOver(var1x) && (this.currentMenu == null || this.currentMenu.isDisposed() && !InputEvent.isFromSameEvent(var1x, this.currentMenu.removeEvent));
      }, (var1x) -> {
         return var1x.getID() == -100 || var1x.getID() == -99 && this.acceptRightClicks;
      }, (var1x) -> {
         this.playTickSound();
         this.currentMenu = this.options.getMenu(this.width - 4, this.removingSubmenuRemovesParent);
         if (var1x.isControllerEvent()) {
            ControllerFocus var2 = this.getManager().getCurrentFocus();
            if (var2 != null) {
               this.getManager().openFloatMenuAt(this.currentMenu, var2.boundingBox.x, var2.boundingBox.y + this.size.textureDrawOffset + this.size.height);
            } else {
               this.getManager().openFloatMenuAt(this.currentMenu, 0, 0);
            }
         } else {
            this.getManager().openFloatMenu(this.currentMenu, this.getX() - var1x.pos.hudX, this.getY() - var1x.pos.hudY + this.size.textureDrawOffset + this.size.height);
         }

      });
      this.selectedEvents = new FormEventsHandler();
      this.changedHoverEvents = new FormEventsHandler();
      this.dragStartedEvents = new FormEventsHandler();
      this.position = new FormFixedPosition(var1, var2);
      this.size = var3;
      this.width = var5;
      this.color = var4;
      this.setSelected((Object)null, var6);
   }

   public FormDropdownSelectionButton(int var1, int var2, FormInputSize var3, ButtonColor var4, int var5) {
      this(var1, var2, var3, var4, var5, new LocalMessage("ui", "selectbutton"));
   }

   public FormDropdownSelectionButton<T> onSelected(FormEventListener<FormValueEvent<FormDropdownSelectionButton<?>, T>> var1) {
      this.selectedEvents.addListener(var1);
      return this;
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
            FormInputEvent var8 = new FormInputEvent(this, var1);
            this.changedHoverEvents.onEvent(var8);
         }

         if (var7 && this.useHoverMoveEvents) {
            var1.useMove();
         }
      } else {
         this.clickHandler.handleEvent(var1);
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isActive() && (var1.getState() == ControllerInput.MENU_SELECT || this.acceptRightClicks && var1.getState() == ControllerInput.MENU_BACK)) {
         InputEvent var4;
         if (this.isControllerFocus() && this.submitControllerPressEvent) {
            if (var1.buttonState) {
               var4 = InputEvent.ControllerButtonEvent(var1, var2);
               this.clickHandler.forceHandleEvent(var4);
               var1.use();
               this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(ControllerEvent.buttonEvent(var1.controller, var1.getState(), false), var2));
               var1.use();
            }
         } else if (this.isControllerFocus() && var1.buttonState) {
            var4 = InputEvent.ControllerButtonEvent(var1, var2);
            this.clickHandler.forceHandleEvent(var4);
            var1.use();
         } else if (!var1.buttonState && this.clickHandler.isDown()) {
            this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(var1, var2));
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

   public FormDropdownSelectionButton<T> onChangedHover(FormEventListener<FormInputEvent<FormDropdownSelectionButton<T>>> var1) {
      this.changedHoverEvents.addListener(var1);
      return this;
   }

   public FormDropdownSelectionButton<T> onDragStarted(FormEventListener<FormDraggingEvent<FormDropdownSelectionButton<T>>> var1) {
      this.dragStartedEvents.addListener(var1);
      return this;
   }

   public void setupDragToOtherButtons(Object var1, boolean var2, Predicate<T> var3) {
      this.onDragStarted((var2x) -> {
         final int var3 = var2x.draggingStartedEvent.isUsed() ? var2x.draggingStartedEvent.getLastID() : var2x.draggingStartedEvent.getID();
         Screen.setMouseDraggingElement(new FormDropdownSelectionDraggingElement(this, var1) {
            public boolean isKeyDown(Input var1) {
               return var1.isKeyDown(var3);
            }
         });
      });
      this.onChangedHover((var4) -> {
         if (this.isHovering() && this.isActive()) {
            MouseDraggingElement var5 = Screen.getMouseDraggingElement();
            if (var5 instanceof FormDropdownSelectionDraggingElement) {
               FormDropdownSelectionDraggingElement var6 = (FormDropdownSelectionDraggingElement)var5;
               if (var6.component != this && !Objects.equals(this.selected, var6.component.selected) && Objects.equals(var6.sameObject, var1)) {
                  try {
                     if (var3 == null || var3.test(var6.component.selected)) {
                        this.selected = var6.component.selected;
                        if (var2) {
                           this.selectedEvents.onEvent(new FormValueEvent(this, this.selected));
                        }

                        if (this.setSelectedText) {
                           this.text = var6.component.text;
                        }

                        this.playTickSound();
                     }
                  } catch (ClassCastException var8) {
                  }
               }
            }
         }

      });
   }

   public void setupDragToOtherButtons(Object var1, Predicate<T> var2) {
      this.setupDragToOtherButtons(var1, true, var2);
   }

   protected boolean isDraggingThis() {
      MouseDraggingElement var1 = Screen.getMouseDraggingElement();
      if (var1 instanceof FormDropdownSelectionDraggingElement) {
         FormDropdownSelectionDraggingElement var2 = (FormDropdownSelectionDraggingElement)var1;
         return var2.component == this;
      } else {
         return false;
      }
   }

   private void selectedOption(Option<T> var1) {
      FormValueEvent var2 = new FormValueEvent(this, var1.value);
      Object var3 = this.selected;
      this.selected = var1.value;
      this.selectedEvents.onEvent(var2);
      if (var2.hasPreventedDefault()) {
         this.selected = var3;
      } else if (this.currentMenu != null && !this.currentMenu.isDisposed()) {
         this.currentMenu.remove();
      }

      if (this.setSelectedText) {
         this.text = var1.text;
      }

   }

   public void setSelected(T var1, GameMessage var2) {
      this.selected = var1;
      this.text = var2;
   }

   public T getSelected() {
      return this.selected;
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
      boolean var7 = this.clickHandler.isDown() && this.isHovering();
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
            var12.draw(var10.x + 5, var6 + this.size.fontDrawOffset, Color.BLACK);
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

   public ButtonState getButtonState() {
      if (!this.isActive()) {
         return ButtonState.INACTIVE;
      } else {
         return this.isHovering() ? ButtonState.HIGHLIGHTED : ButtonState.ACTIVE;
      }
   }

   public Color getTextColor() {
      return this.size.getTextColor(this.getButtonState());
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

   public boolean isHovering() {
      return this.isHovering || this.isControllerFocus();
   }

   public void drawDraggingElement(int var1, int var2) {
      FontManager.bit.drawString((float)var1, (float)(var2 - 20), this.text.translate(), (new FontOptions(16)).outline().alphaf(0.8F));
   }

   public static class Option<T> {
      public final T value;
      public final GameMessage text;
      public final Supplier<Boolean> isActive;
      public final Color textColor;
      public final Supplier<GameTooltips> hoverTooltips;

      public Option(T var1, GameMessage var2, Supplier<Boolean> var3, Color var4, Supplier<GameTooltips> var5) {
         this.value = var1;
         this.text = var2;
         this.isActive = var3;
         this.textColor = var4;
         this.hoverTooltips = var5;
      }

      public Option(T var1, GameMessage var2) {
         this(var1, var2, (Supplier)null, (Color)null, (Supplier)null);
      }
   }

   public static class OptionsList<T> {
      private final FormDropdownSelectionButton<T> button;
      private final LinkedList<OptionContainer> options;

      private OptionsList(FormDropdownSelectionButton<T> var1) {
         this.options = new LinkedList();
         this.button = var1;
      }

      public OptionsList<T> add(Option<T> var1) {
         this.options.add(new ValueOptionContainer(this.button, var1));
         return this;
      }

      public OptionsList<T> add(T var1, GameMessage var2) {
         return this.add(var1, var2, (Supplier)null);
      }

      public OptionsList<T> add(T var1, GameMessage var2, Supplier<GameMessage> var3) {
         return this.add(var1, var2, var3, (Supplier)null);
      }

      public OptionsList<T> add(T var1, GameMessage var2, Supplier<GameMessage> var3, Supplier<Boolean> var4) {
         return this.add(new Option(var1, var2, var4, (Color)null, var3 == null ? null : () -> {
            GameMessage var1 = (GameMessage)var3.get();
            return var1 != null ? new StringTooltips(var1.translate()) : null;
         }));
      }

      public OptionsList<T> addSub(GameMessage var1) {
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
      OptionsList(FormDropdownSelectionButton var1, Object var2) {
         this(var1);
      }
   }

   protected static class FormDropdownSelectionDraggingElement implements MouseDraggingElement {
      public final FormDropdownSelectionButton<?> component;
      public final Object sameObject;

      public FormDropdownSelectionDraggingElement(FormDropdownSelectionButton<?> var1, Object var2) {
         this.component = var1;
         this.sameObject = var2;
      }

      public boolean draw(int var1, int var2) {
         this.component.drawDraggingElement(var1, var2);
         return true;
      }
   }

   private static class SubMenuOptionContainer<T> extends OptionContainer {
      public final FormDropdownSelectionButton<T> button;
      public final OptionsList<T> subOptions;
      public final GameMessage text;

      public SubMenuOptionContainer(FormDropdownSelectionButton<T> var1, OptionsList<T> var2, GameMessage var3) {
         super(null);
         this.button = var1;
         this.subOptions = var2;
         this.text = var3;
      }

      public void addToMenu(SelectionFloatMenu var1, boolean var2) {
         var1.add(this.text.translate(), this.subOptions.getMenu(0, var2), var2);
      }
   }

   private static class ValueOptionContainer<T> extends OptionContainer {
      public final FormDropdownSelectionButton<T> button;
      public final Option<T> option;

      public ValueOptionContainer(FormDropdownSelectionButton<T> var1, Option<T> var2) {
         super(null);
         this.button = var1;
         this.option = var2;
      }

      public void addToMenu(SelectionFloatMenu var1, boolean var2) {
         var1.add(this.option.text.translate(), this.option.isActive, this.option.textColor, this.option.hoverTooltips, () -> {
            this.button.selectedOption(this.option);
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
