package necesse.gfx.forms.floatMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerGlyphTip;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackgroundTextures;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class SelectionFloatMenu extends FloatMenu {
   private LinkedList<SelectionBoxList> boxes;
   private int totalWidth;
   private int totalHeight;
   private SelectionFloatMenu subMenu;
   public boolean subMenuRemovesThis;
   private final SelectionFloatMenuStyle style;
   private final int minWidth;
   private SelectionBoxList currentControllerFocus;
   public InputEvent removeEvent;

   public static SelectionFloatMenuStyle Solid(final FontOptions var0) {
      return new SelectionFloatMenuStyle() {
         public int getPadding() {
            return 4;
         }

         public int getListMargin() {
            return 2;
         }

         public void drawListBackground(SelectionBoxList var1, int var2, int var3) {
            Settings.UI.selectionBox.getDrawOptions(var2, var3, var1.width, var1.height + 2).draw();
         }

         public void drawListBackgroundEdge(SelectionBoxList var1, int var2, int var3) {
            Settings.UI.selectionBox.getEdgeDrawOptions(var2, var3, var1.width, var1.height + 2).draw();
         }

         public void drawButtonBackground(int var1, int var2, Dimension var3, boolean var4, boolean var5) {
            GameBackgroundTextures var6 = Settings.UI.selectionBox;
            if (!var4) {
               var6 = Settings.UI.selectionBox_inactive;
            } else if (var5) {
               var6 = Settings.UI.selectionBox_highlighted;
            }

            var6.getCenterDrawOptions(var1 - 2, var2 - 2, var3.width + 4, var3.height + 6).draw();
         }

         public void drawButtonBackgroundEdge(int var1, int var2, Dimension var3, boolean var4, boolean var5) {
            GameBackgroundTextures var6 = Settings.UI.selectionBox;
            if (!var4) {
               var6 = Settings.UI.selectionBox_inactive;
            } else if (var5) {
               var6 = Settings.UI.selectionBox_highlighted;
            }

            var6.getCenterEdgeDrawOptions(var1 - 2, var2 - 2, var3.width + 4, var3.height + 6).draw();
         }

         public FontOptions getFontOptions() {
            return var0;
         }

         public Color getFontColor(boolean var1, boolean var2) {
            if (!var1) {
               return Settings.UI.selectionBoxInactiveTextColor;
            } else {
               return var2 ? Settings.UI.selectionBoxHighlightedTextColor : Settings.UI.selectionBoxActiveTextColor;
            }
         }
      };
   }

   public static SelectionFloatMenuStyle Transparent(final FontOptions var0) {
      return new SelectionFloatMenuStyle() {
         public int getPadding() {
            return 4;
         }

         public int getListMargin() {
            return 0;
         }

         public void drawListBackground(SelectionBoxList var1, int var2, int var3) {
         }

         public void drawListBackgroundEdge(SelectionBoxList var1, int var2, int var3) {
         }

         public void drawButtonBackground(int var1, int var2, Dimension var3, boolean var4, boolean var5) {
            float var6 = var5 ? 0.3F : 0.0F;
            Screen.initQuadDraw(var3.width, var3.height).color(var6, var6, var6, 0.8F).draw(var1, var2);
         }

         public void drawButtonBackgroundEdge(int var1, int var2, Dimension var3, boolean var4, boolean var5) {
         }

         public FontOptions getFontOptions() {
            return var0;
         }

         public Color getFontColor(boolean var1, boolean var2) {
            return var1 ? Color.WHITE : Color.GRAY;
         }
      };
   }

   public SelectionFloatMenu(FormComponent var1, SelectionFloatMenuStyle var2, int var3) {
      super(var1);
      this.boxes = new LinkedList();
      this.subMenu = null;
      this.subMenuRemovesThis = true;
      this.style = var2;
      this.minWidth = var3;
      this.boxes.add(new SelectionBoxList(var2.getListMargin()));
   }

   public SelectionFloatMenu(FormComponent var1, SelectionFloatMenuStyle var2) {
      this(var1, var2, 0);
   }

   public SelectionFloatMenu(FormComponent var1) {
      this(var1, Solid(new FontOptions(12)));
   }

   public void init() {
      super.init();
      SelectionBoxList.SelectionBox var1 = (SelectionBoxList.SelectionBox)((SelectionBoxList)this.boxes.getFirst()).buttons.getFirst();
      if (var1 != null) {
         this.parent.prioritizeControllerFocus(var1);
      }

   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      boolean var4 = var1.isMouseClickEvent();
      boolean var5 = var1.wasMouseClickEvent() && var1.state;
      var5 = this.menuInputEvent(var1, var4) || var5;
      if (var5) {
         var1.use();
         this.remove();
         this.removeEvent = var1;
      }

   }

   private boolean menuInputEvent(InputEvent var1, boolean var2) {
      boolean var3 = false;
      if (this.subMenu != null) {
         if (this.subMenu.menuInputEvent(var1, var2)) {
            this.subMenu.remove();
         } else if (var2) {
            var3 = true;
         }
      }

      Iterator var4 = this.boxes.iterator();

      while(var4.hasNext()) {
         SelectionBoxList var5 = (SelectionBoxList)var4.next();
         if (var5.handleInputEvent(var1)) {
            var3 = true;
         }
      }

      return var2 && !var3;
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (this.subMenu != null) {
         this.subMenu.handleControllerEvent(var1, var2, var3);
      }

      Iterator var4 = this.boxes.iterator();

      while(var4.hasNext()) {
         SelectionBoxList var5 = (SelectionBoxList)var4.next();
         var5.handleControllerEvent(var1, var2, var3);
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      Iterator var7;
      SelectionBoxList var8;
      Iterator var9;
      SelectionBoxList.SelectionBox var10;
      Dimension var11;
      Rectangle var12;
      if (this.subMenu != null) {
         var7 = this.subMenu.boxes.iterator();

         while(var7.hasNext()) {
            var8 = (SelectionBoxList)var7.next();
            var9 = var8.buttons.iterator();

            while(var9.hasNext()) {
               var10 = (SelectionBoxList.SelectionBox)var9.next();
               var11 = var10.getDimensions();
               var12 = new Rectangle(this.getDrawX() + var8.xOffset, this.getDrawY() + var10.yOffset, var11.width, var11.height);
               var1.add(new ControllerFocus(var10, var12, 0, 0, 0, var4));
            }
         }
      }

      var7 = this.boxes.iterator();

      while(var7.hasNext()) {
         var8 = (SelectionBoxList)var7.next();
         var9 = var8.buttons.iterator();

         while(var9.hasNext()) {
            var10 = (SelectionBoxList.SelectionBox)var9.next();
            var11 = var10.getDimensions();
            var12 = new Rectangle(this.getDrawX() + var8.xOffset, this.getDrawY() + var10.yOffset, var11.width, var11.height);
            var1.add(new ControllerFocus(var10, var12, 0, 0, 0, var4));
         }
      }

   }

   public int getDrawX() {
      return Math.min(Screen.getHudWidth() - this.totalWidth, super.getDrawX());
   }

   public int getDrawY() {
      return Math.min(this.getHudHeight() - this.totalHeight, super.getDrawY());
   }

   public int getHudHeight() {
      return Screen.getHudHeight() - (Input.lastInputIsController ? ControllerGlyphTip.getHeight() + 2 : 0);
   }

   public void draw(TickManager var1, PlayerMob var2) {
      Iterator var3 = this.boxes.iterator();

      while(var3.hasNext()) {
         SelectionBoxList var4 = (SelectionBoxList)var3.next();
         var4.draw(this.getDrawX(), this.getDrawY() - this.style.getListMargin());
      }

      if (this.subMenu != null) {
         this.subMenu.draw(var1, var2);
      }

   }

   public boolean isMouseOver(InputEvent var1) {
      return this.boxes.stream().anyMatch((var2) -> {
         return (new Rectangle(this.getDrawX(), this.getDrawY() + var2.xOffset, var2.width, var2.height)).contains(var1.pos.hudX, var1.pos.hudY);
      });
   }

   private void add(FairTypeDrawOptions var1, FairTypeDrawOptions var2, Supplier<Boolean> var3, Color var4, Supplier<GameTooltips> var5, BiConsumer<SelectionBoxList.SelectionBox, InputEvent> var6, BiConsumer<SelectionBoxList.SelectionBox, ControllerEvent> var7) {
      SelectionBoxList var8 = (SelectionBoxList)this.boxes.getLast();
      Dimension var9 = this.getButtonDimensions(var1, var2);
      if (var8.height + this.style.getListMargin() * 2 + var9.height > this.getHudHeight()) {
         this.boxes.addLast(new SelectionBoxList(var8.xOffset + this.style.getListMargin() + var8.width));
         ((SelectionBoxList)this.boxes.getLast()).add(var1, var2, var3, var4, var5, var6, var7);
      } else {
         var8.add(var1, var2, var3, var4, var5, var6, var7);
      }

      this.totalHeight = Math.max(this.totalHeight, ((SelectionBoxList)this.boxes.getLast()).height);
      this.totalWidth = ((SelectionBoxList)this.boxes.getLast()).xOffset + ((SelectionBoxList)this.boxes.getLast()).width;
   }

   private TypeParser[] getParsers() {
      return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(this.style.getFontOptions().getSize()), TypeParsers.InputIcon(this.style.getFontOptions())};
   }

   public SelectionFloatMenu add(String var1, Supplier<Boolean> var2, Color var3, Supplier<GameTooltips> var4, Runnable var5) {
      FairType var6 = (new FairType()).append(this.style.getFontOptions(), var1).applyParsers(this.getParsers());
      FairTypeDrawOptions var7 = var6.getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
      BiConsumer var8 = (var2x, var3x) -> {
         if (var2x.isActive != null && !(Boolean)var2x.isActive.get()) {
            var2x.isDown = false;
         }

         if (var3x.isMouseClickEvent()) {
            if (var2x.isInputOver(var3x)) {
               if ((var2x.isActive == null || (Boolean)var2x.isActive.get()) && var3x.getID() == -100) {
                  if (var3x.state) {
                     var2x.isDown = true;
                  } else if (var2x.isDown) {
                     var5.run();
                     this.parent.playTickSound();
                     var2x.isDown = false;
                  }
               }

               var3x.use();
            } else {
               var2x.isDown = false;
            }
         }

      };
      BiConsumer var9 = (var2x, var3x) -> {
         if (var2x.isActive != null && !(Boolean)var2x.isActive.get()) {
            var2x.isDown = false;
         }

         if (var3x.getState() == ControllerInput.MENU_SELECT) {
            if (var2x.isCurrentControllerFocus()) {
               if (var2x.isActive == null || (Boolean)var2x.isActive.get()) {
                  if (var3x.buttonState) {
                     var2x.isDown = true;
                  } else if (var2x.isDown) {
                     var5.run();
                     this.parent.playTickSound();
                     var2x.isDown = false;
                  }
               }

               var3x.use();
            } else {
               var2x.isDown = false;
            }
         }

      };
      this.add(var7, (FairTypeDrawOptions)null, var2, var3, var4, var8, var9);
      return this;
   }

   public SelectionFloatMenu add(String var1, Runnable var2) {
      return this.add(var1, (Supplier)null, (Color)null, (Supplier)null, var2);
   }

   public SelectionFloatMenu add(String var1, Supplier<Boolean> var2, Color var3, Supplier<GameTooltips> var4, SelectionFloatMenu var5, boolean var6) {
      FairType var7 = (new FairType()).append(this.style.getFontOptions(), var1).applyParsers(this.getParsers());
      FairTypeDrawOptions var8 = var7.getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
      FairType var9 = (new FairType()).append(this.style.getFontOptions(), ">");
      FairTypeDrawOptions var10 = var9.getDrawOptions(FairType.TextAlign.RIGHT);
      BiConsumer var11 = (var3x, var4x) -> {
         if (var3x.isActive != null && !(Boolean)var3x.isActive.get()) {
            var3x.isDown = false;
         }

         if (var4x.isMouseClickEvent()) {
            if (var3x.isInputOver(var4x)) {
               if ((var3x.isActive == null || (Boolean)var3x.isActive.get()) && var4x.getID() == -100) {
                  if (var4x.state) {
                     var3x.isDown = true;
                  } else if (var3x.isDown) {
                     var4x.use();
                     if (this.subMenu == var5) {
                        this.subMenu.remove();
                     } else {
                        this.subMenu = var5;
                        Point var5x = var3x.getDrawPos();
                        this.subMenu.init(var5x.x + var3x.getDimensions().width, var5x.y, () -> {
                           if (this.subMenu == var5) {
                              var5.dispose();
                              this.subMenu = null;
                              if (var6) {
                                 this.remove();
                              }
                           }

                        });
                     }

                     this.parent.playTickSound();
                     var3x.isDown = false;
                  }
               }

               var4x.use();
            } else {
               var3x.isDown = false;
            }
         }

      };
      BiConsumer var12 = (var3x, var4x) -> {
         if (var3x.isActive != null && !(Boolean)var3x.isActive.get()) {
            var3x.isDown = false;
         }

         if (var4x.getState() == ControllerInput.MENU_SELECT) {
            if (var3x.isCurrentControllerFocus()) {
               if (var3x.isActive == null || (Boolean)var3x.isActive.get()) {
                  if (var4x.buttonState) {
                     var3x.isDown = true;
                  } else if (var3x.isDown) {
                     var4x.use();
                     if (this.subMenu == var5) {
                        this.subMenu.remove();
                     } else {
                        this.subMenu = var5;
                        Point var5x = var3x.getDrawPos();
                        this.subMenu.init(var5x.x + var3x.getDimensions().width, var5x.y, () -> {
                           if (this.subMenu == var5) {
                              var5.dispose();
                              this.subMenu = null;
                              if (var6) {
                                 this.remove();
                              }
                           }

                        });
                     }

                     this.parent.playTickSound();
                     var3x.isDown = false;
                  }
               }

               var4x.use();
            } else {
               var3x.isDown = false;
            }
         }

      };
      this.add(var8, var10, var2, var3, var4, var11, var12);
      return this;
   }

   public SelectionFloatMenu add(String var1, SelectionFloatMenu var2, boolean var3) {
      return this.add(var1, (Supplier)null, (Color)null, (Supplier)null, var2, var3);
   }

   public boolean isEmpty() {
      Iterator var1 = this.boxes.iterator();

      SelectionBoxList var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (SelectionBoxList)var1.next();
      } while(var2.buttons.isEmpty());

      return false;
   }

   private Dimension getButtonDimensions(FairTypeDrawOptions var1, FairTypeDrawOptions var2) {
      Rectangle var3 = var1.getBoundingBox();
      Dimension var4 = new Dimension(var3.width + this.style.getPadding() * 2, var3.height + this.style.getPadding() * 2);
      if (var2 != null) {
         Rectangle var5 = var2.getBoundingBox();
         var4.width += var5.width + this.style.getPadding() * 2;
         var4.height = Math.max(var4.height, var5.height + this.style.getPadding() * 2);
      }

      return var4;
   }

   public abstract static class SelectionFloatMenuStyle {
      public SelectionFloatMenuStyle() {
      }

      public abstract int getPadding();

      public abstract int getListMargin();

      public abstract void drawListBackground(SelectionBoxList var1, int var2, int var3);

      public abstract void drawListBackgroundEdge(SelectionBoxList var1, int var2, int var3);

      public abstract void drawButtonBackground(int var1, int var2, Dimension var3, boolean var4, boolean var5);

      public abstract void drawButtonBackgroundEdge(int var1, int var2, Dimension var3, boolean var4, boolean var5);

      public abstract FontOptions getFontOptions();

      public abstract Color getFontColor(boolean var1, boolean var2);
   }

   private class SelectionBoxList {
      private int xOffset;
      private LinkedList<SelectionBox> buttons = new LinkedList();
      private int width = 0;
      private int height = 0;

      public SelectionBoxList(int var2) {
         this.xOffset = var2;
         this.width = SelectionFloatMenu.this.minWidth;
      }

      private SelectionBox add(FairTypeDrawOptions var1, FairTypeDrawOptions var2, Supplier<Boolean> var3, Color var4, Supplier<GameTooltips> var5, BiConsumer<SelectionBox, InputEvent> var6, BiConsumer<SelectionBox, ControllerEvent> var7) {
         SelectionBox var8;
         this.buttons.add(var8 = new SelectionBox(var1, var2, var3, var4, var5, var6, var7, this.height));
         Dimension var9 = var8.getDimensions();
         this.width = Math.max(this.width, var9.width);
         this.height += var9.height;
         return var8;
      }

      public boolean handleInputEvent(InputEvent var1) {
         boolean var2 = false;
         Iterator var3 = this.buttons.iterator();

         while(var3.hasNext()) {
            SelectionBox var4 = (SelectionBox)var3.next();
            if (var4.handleInputEvent(var1)) {
               var2 = true;
            }
         }

         return var2;
      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
         Iterator var4 = this.buttons.iterator();

         while(var4.hasNext()) {
            SelectionBox var5 = (SelectionBox)var4.next();
            var5.handleControllerEvent(var1, var2, var3);
         }

      }

      public void draw(int var1, int var2) {
         SelectionFloatMenu.this.style.drawListBackground(this, var1 + this.xOffset, var2);
         Iterator var3 = this.buttons.iterator();

         while(var3.hasNext()) {
            SelectionBox var4 = (SelectionBox)var3.next();
            var4.draw(var1 + this.xOffset, var2);
         }

         SelectionFloatMenu.this.style.drawListBackgroundEdge(this, var1 + this.xOffset, var2);
      }

      private class SelectionBox implements ControllerFocusHandler {
         public final FairTypeDrawOptions textDrawOptions;
         public final FairTypeDrawOptions endingDrawOptions;
         public final Supplier<Boolean> isActive;
         public final Color textColor;
         public final Supplier<GameTooltips> hoverTooltips;
         public final BiConsumer<SelectionBox, InputEvent> eventHandler;
         public final BiConsumer<SelectionBox, ControllerEvent> controllerHandler;
         public final int yOffset;
         public boolean isDown;

         public SelectionBox(FairTypeDrawOptions var2, FairTypeDrawOptions var3, Supplier<Boolean> var4, Color var5, Supplier<GameTooltips> var6, BiConsumer<SelectionBox, InputEvent> var7, BiConsumer<SelectionBox, ControllerEvent> var8, int var9) {
            this.textDrawOptions = var2;
            this.endingDrawOptions = var3;
            this.isActive = var4;
            this.textColor = var5;
            this.hoverTooltips = var6;
            this.eventHandler = var7;
            this.controllerHandler = var8;
            this.yOffset = var9;
         }

         protected Point getDrawPos() {
            return new Point(SelectionFloatMenu.this.getDrawX() + SelectionBoxList.this.xOffset, SelectionFloatMenu.this.getDrawY() + this.yOffset);
         }

         public boolean handleInputEvent(InputEvent var1) {
            this.eventHandler.accept(this, var1);
            return !var1.isMouseClickEvent() || this.isInputOver(var1);
         }

         public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
            this.controllerHandler.accept(this, var1);
         }

         public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
            return false;
         }

         public boolean isCurrentControllerFocus() {
            return SelectionFloatMenu.this.parent.getManager().isControllerFocus(this);
         }

         protected boolean isInputOver(InputEvent var1) {
            Point var2 = this.getDrawPos();
            return (new Rectangle(this.getDimensions())).contains(var1.pos.hudX - var2.x, var1.pos.hudY - var2.y + SelectionFloatMenu.this.style.getListMargin());
         }

         protected Dimension getDimensions() {
            Dimension var1 = SelectionFloatMenu.this.getButtonDimensions(this.textDrawOptions, this.endingDrawOptions);
            return new Dimension(Math.max(SelectionBoxList.this.width, var1.width), var1.height);
         }

         public void draw(int var1, int var2) {
            var2 += this.yOffset;
            boolean var3 = this.isActive == null || (Boolean)this.isActive.get();
            boolean var4 = this.isDown || this.isInputOver(InputEvent.MouseMoveEvent(Screen.mousePos(), Screen.tickManager)) || SelectionFloatMenu.this.parent.isControllerFocus(this);
            SelectionFloatMenu.this.style.drawButtonBackground(var1, var2, this.getDimensions(), var3, var4);
            Rectangle var5 = this.textDrawOptions.getBoundingBox();
            this.textDrawOptions.draw(var1 + SelectionFloatMenu.this.style.getPadding() - var5.x, var2 + SelectionFloatMenu.this.style.getPadding() - var5.y, this.textColor != null ? this.textColor : SelectionFloatMenu.this.style.getFontColor(var3, var4));
            if (this.endingDrawOptions != null) {
               Rectangle var6 = this.endingDrawOptions.getBoundingBox();
               this.endingDrawOptions.draw(var1 + SelectionBoxList.this.width - var6.width - var6.x - SelectionFloatMenu.this.style.getPadding(), var2 + SelectionFloatMenu.this.style.getPadding() - var6.y, this.textColor != null ? this.textColor : SelectionFloatMenu.this.style.getFontColor(var3, var4));
            }

            SelectionFloatMenu.this.style.drawButtonBackgroundEdge(var1, var2, this.getDimensions(), var3, var4);
            if (var4 && this.hoverTooltips != null) {
               GameTooltips var7 = (GameTooltips)this.hoverTooltips.get();
               if (var7 != null) {
                  Screen.addTooltip(var7, TooltipLocation.FORM_FOCUS);
               }
            }

         }

         public void drawControllerFocus(ControllerFocus var1) {
            ControllerFocusHandler.super.drawControllerFocus(var1);
            Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
         }
      }
   }
}
