package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.ContentBoxListManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HUD;

public class FormContentBox extends FormComponent implements FormPositionContainer, ComponentListContainer<FormComponent> {
   public boolean drawVerticalOnLeft;
   public boolean drawHorizontalOnTop;
   public boolean alwaysShowVerticalScrollBar;
   public boolean alwaysShowHorizontalScrollBar;
   public boolean drawScrollBarOutsideBox;
   public boolean shouldLimitDrawArea;
   public boolean hitboxFullSize;
   public boolean allowControllerFocusOnScrollbar;
   public int controllerScrollPadding;
   private ControllerFocusHandler scrollXControllerFocus;
   private ControllerFocusHandler scrollYControllerFocus;
   private GameBackground background;
   private ComponentList<FormComponent> components;
   private boolean hidden;
   private FormPosition position;
   private int width;
   private int height;
   private Rectangle contentBox;
   private int scrollX;
   private int scrollY;
   private MouseWheelBuffer wheelBuffer;
   private boolean isHoveringScrollX;
   private boolean isHoveringScrollY;
   private int scrollMouseX;
   private int scrollMouseY;
   private int scrollStartX;
   private int scrollStartY;

   public FormContentBox(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, (GameBackground)null);
   }

   public FormContentBox(int var1, int var2, int var3, int var4, GameBackground var5) {
      this(var1, var2, var3, var4, var5, new Rectangle(var3 - (var5 == null ? 0 : var5.getContentPadding() * 2), var4 - (var5 == null ? 0 : var5.getContentPadding() * 2)));
   }

   public FormContentBox(int var1, int var2, int var3, int var4, GameBackground var5, Rectangle var6) {
      this.drawScrollBarOutsideBox = false;
      this.shouldLimitDrawArea = true;
      this.hitboxFullSize = true;
      this.allowControllerFocusOnScrollbar = true;
      this.controllerScrollPadding = 30;
      this.scrollXControllerFocus = new ScrollbarControllerFocusHandler();
      this.scrollYControllerFocus = new ScrollbarControllerFocusHandler();
      this.hidden = false;
      this.wheelBuffer = new MouseWheelBuffer(true);
      this.scrollMouseX = Integer.MIN_VALUE;
      this.scrollMouseY = Integer.MIN_VALUE;
      this.components = new ComponentList<FormComponent>() {
         public InputEvent offsetEvent(InputEvent var1, boolean var2) {
            int var3;
            int var5;
            int var6;
            if (var2 || var1.pos.hudX >= FormContentBox.this.getX() && var1.pos.hudX <= FormContentBox.this.getX() + FormContentBox.this.getContentWidth()) {
               var5 = FormContentBox.this.background == null ? 0 : FormContentBox.this.background.getContentPadding();
               var6 = !FormContentBox.this.drawScrollBarOutsideBox && FormContentBox.this.hasScrollbarY() ? FormContentBox.this.getScrollBarWidth() : 0;
               var3 = var1.pos.hudX - FormContentBox.this.getX() + FormContentBox.this.contentBox.x + FormContentBox.this.scrollX - (FormContentBox.this.drawVerticalOnLeft ? var6 : 0) - var5;
            } else {
               var3 = Integer.MIN_VALUE;
            }

            int var4;
            if (var2 || var1.pos.hudY >= FormContentBox.this.getY() && var1.pos.hudY <= FormContentBox.this.getY() + FormContentBox.this.getContentHeight()) {
               var5 = FormContentBox.this.background == null ? 0 : FormContentBox.this.background.getContentPadding();
               var6 = !FormContentBox.this.drawScrollBarOutsideBox && FormContentBox.this.hasScrollbarX() ? FormContentBox.this.getScrollBarWidth() : 0;
               var4 = var1.pos.hudY - FormContentBox.this.getY() + FormContentBox.this.contentBox.y + FormContentBox.this.scrollY - (FormContentBox.this.drawHorizontalOnTop ? var6 : 0) - var5;
            } else {
               var4 = Integer.MIN_VALUE;
            }

            return InputEvent.ReplacePosEvent(var1, InputPosition.fromHudPos(Screen.input(), var3, var4));
         }

         public FormManager getManager() {
            return FormContentBox.this.getManager();
         }
      };
      this.setPosition(new FormFixedPosition(var1, var2));
      this.setWidth(var3);
      this.setHeight(var4);
      this.setContentBox(var6);
      this.background = var5;
   }

   protected void init() {
      super.init();
      this.components.init();
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      boolean var4;
      int var5;
      int var6;
      int var7;
      if (var1.getID() == -100) {
         if (var1.state) {
            var4 = this.hasScrollbarY();
            if (var4 && this.scrollMouseX == Integer.MIN_VALUE) {
               var5 = this.getScrollbarSize(this.contentBox.height, this.getHeight());
               var6 = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, var5);
               if (this.isMouseOverScrollbarY(var6, var5, var1)) {
                  this.scrollMouseY = var1.pos.hudY;
                  this.scrollStartY = this.scrollY;
                  var1.use();
               }
            }

            if (this.hasScrollbarX() && this.scrollMouseY == Integer.MIN_VALUE) {
               var5 = !this.drawScrollBarOutsideBox && var4 ? this.getScrollBarWidth() : 0;
               var6 = this.getScrollbarSize(this.contentBox.width, this.getWidth() - var5);
               var7 = this.getScrollbarPos(this.contentBox.width, this.getWidth() - var5, this.getContentWidth() - var5, this.scrollX, var6);
               if (this.isMouseOverScrollbarX(var7, var6, var1)) {
                  this.scrollMouseX = var1.pos.hudX;
                  this.scrollStartX = this.scrollX;
                  var1.use();
               }
            }
         } else if (this.scrollMouseX != Integer.MIN_VALUE || this.scrollMouseY != Integer.MIN_VALUE) {
            this.scrollMouseX = Integer.MIN_VALUE;
            this.scrollMouseY = Integer.MIN_VALUE;
            var1.use();
         }
      } else if (var1.isMouseMoveEvent()) {
         var4 = this.hasScrollbarY();
         var5 = !this.drawScrollBarOutsideBox && var4 ? this.getScrollBarWidth() : 0;
         float var10;
         if (this.scrollMouseY != Integer.MIN_VALUE && var1.pos.hudY != Integer.MIN_VALUE) {
            var6 = var1.pos.hudY - this.scrollMouseY;
            var10 = (float)this.contentBox.height / (float)this.getHeight();
            this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), this.scrollStartY + (int)((float)var6 * var10));
         } else if (this.scrollMouseX != Integer.MIN_VALUE && var1.pos.hudX != Integer.MIN_VALUE) {
            var6 = var1.pos.hudX - this.scrollMouseX;
            var10 = (float)this.contentBox.width / (float)(this.getWidth() - var5);
            this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth() - var5, this.scrollStartX + (int)((float)var6 * var10));
         }

         if (var4) {
            var6 = this.getScrollbarSize(this.contentBox.height, this.getHeight());
            var7 = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, var6);
            this.isHoveringScrollY = this.isMouseOverScrollbarY(var7, var6, var1);
            if (this.isHoveringScrollY) {
               var1.useMove();
            }
         } else {
            this.isHoveringScrollY = false;
         }

         if (this.hasScrollbarX()) {
            var6 = this.getScrollbarSize(this.contentBox.width, this.getWidth() - var5);
            var7 = this.getScrollbarPos(this.contentBox.width, this.getWidth() - var5, this.getContentWidth() - var5, this.scrollX, var6);
            if (this.drawVerticalOnLeft) {
               var7 += var5;
            }

            this.isHoveringScrollX = this.isMouseOverScrollbarX(var7, var6, var1);
            if (this.isHoveringScrollX) {
               var1.useMove();
            }
         } else {
            this.isHoveringScrollX = false;
         }
      }

      this.components.submitInputEvent(var1, var2, var3);
      if (!var1.isUsed()) {
         if (this.isMouseOver(var1) && var1.isMouseWheelEvent()) {
            this.wheelBuffer.add(var1, 20.0);
            int var9 = this.wheelBuffer.useAllScrollY();
            if (var9 != 0 && this.hasScrollbarY()) {
               var5 = this.scrollY;
               this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), this.scrollY - var9);
               if (var5 != this.scrollY) {
                  this.playTickSound();
                  var1.use();
                  this.handleInputEvent(InputEvent.MouseMoveEvent(var1.pos, var2), var2, var3);
               }
            }

            var5 = this.wheelBuffer.useAllScrollX();
            if (var5 != 0 && this.hasScrollbarX()) {
               var6 = this.scrollX;
               boolean var11 = this.hasScrollbarY();
               int var8 = !this.drawScrollBarOutsideBox && var11 ? this.getScrollBarWidth() : 0;
               this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth() - var8, this.scrollX - var5);
               if (var6 != this.scrollX) {
                  this.playTickSound();
                  var1.use();
                  this.handleInputEvent(InputEvent.MouseMoveEvent(var1.pos, var2), var2, var3);
               }
            }
         }

      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (!this.isHidden()) {
         this.scrollYControllerFocus.handleControllerEvent(var1, var2, var3);
         this.scrollXControllerFocus.handleControllerEvent(var1, var2, var3);
         this.components.submitControllerEvent(var1, var2, var3);
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (!this.isHidden()) {
         ScrollAndContentOffset var7 = new ScrollAndContentOffset();
         if (this.allowControllerFocusOnScrollbar) {
            Rectangle var8;
            if (this.hasScrollbarY()) {
               var8 = this.getScrollbarYHitbox();
               var1.add(new ControllerFocus(this.scrollYControllerFocus, var8, var2, var3, 0, (ControllerNavigationHandler)null));
            }

            if (this.hasScrollbarX()) {
               var8 = this.getScrollbarXHitbox();
               var1.add(new ControllerFocus(this.scrollXControllerFocus, var8, var2, var3, 0, (ControllerNavigationHandler)null));
            }
         }

         var5 = var5.intersection(new Rectangle(var2 + this.getX(), var3 + this.getY(), this.width, this.height));
         if (var6) {
            Screen.drawShape(var5, false, 0.0F, 1.0F, 1.0F, 1.0F);
         }

         Iterator var10 = this.components.iterator();

         while(var10.hasNext()) {
            FormComponent var9 = (FormComponent)var10.next();
            if (var9.shouldDraw()) {
               var9.addNextControllerFocus(var1, var2 + var7.xOffset, var3 + var7.yOffset, this.getControllerNavigationHandler(var2, var3), var5, var6);
            }
         }
      }

   }

   protected ControllerNavigationHandler getControllerNavigationHandler(int var1, int var2) {
      return (var3, var4, var5, var6) -> {
         LinkedList var7 = new LinkedList();
         ScrollAndContentOffset var8 = new ScrollAndContentOffset();
         Rectangle var9 = new Rectangle(var1 + var8.xOffset, var2 + var8.yOffset, this.contentBox.width, this.contentBox.height);
         this.components.addNextControllerComponents(var7, var1 + var8.xOffset, var2 + var8.yOffset, this.getControllerNavigationHandler(var1, var2), var9, false);
         ControllerFocus var10 = ControllerFocus.getNext(var3, this.getManager(), var7);
         if (var10 != null) {
            int var11 = this.scrollX;
            int var12 = this.scrollY;
            Rectangle var13 = new Rectangle(var10.boundingBox.x - (var1 + var8.xOffset), var10.boundingBox.y - (var2 + var8.yOffset), var10.boundingBox.width, var10.boundingBox.height);
            int var14 = this.controllerScrollPadding;
            Rectangle var15 = new Rectangle(var13.x - var14, var13.y - var14, var13.width + var14 * 2, var13.height + var14 * 2);
            this.scrollToFit(var15);
            int var16 = this.scrollX - var11;
            int var17 = this.scrollY - var12;
            this.getManager().setControllerFocus(new ControllerFocus(var10, -var16, -var17));
            return true;
         } else {
            return false;
         }
      };
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.background != null) {
         this.background.getDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
      }

      ScrollAndContentOffset var4 = new ScrollAndContentOffset();
      Rectangle var5;
      if (this.shouldLimitDrawArea) {
         var5 = new Rectangle(this.scrollX + this.contentBox.x, this.scrollY + this.contentBox.y, this.getContentWidth(), this.getContentHeight());
      } else {
         var5 = new Rectangle(-var4.xOffset, -var4.yOffset, Screen.getHudWidth(), Screen.getHudHeight());
      }

      FormShader.FormShaderState var6 = GameResources.formShader.startState(new Point(var4.xOffset, var4.yOffset), var5);
      Rectangle var7 = new Rectangle(this.getContentWidth(), this.getContentHeight());
      Rectangle var8;
      if (var3 == null) {
         var8 = null;
      } else {
         var8 = var7.intersection(new Rectangle(var3.x - this.getX(), var3.y - this.getY(), var3.width, var3.height));
      }

      try {
         this.components.drawComponents(var1, var2, var8);
      } finally {
         var6.end();
      }

      Color var9;
      GameTexture var10;
      int var11;
      int var12;
      if (var4.hasScrollbarY) {
         var9 = Settings.UI.activeElementColor;
         var10 = Settings.UI.scrollbar.active;
         var11 = this.getScrollbarSize(this.contentBox.height, this.getHeight());
         var12 = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, var11);
         if (this.isHoveringScrollY) {
            var9 = Settings.UI.highlightElementColor;
            var10 = Settings.UI.scrollbar.highlighted;
         }

         if (this.drawScrollBarOutsideBox) {
            drawWidthComponent(new GameSprite(var10, 0, 0, var10.getHeight()), new GameSprite(var10, 1, 0, var10.getHeight()), this.getX() + (this.drawVerticalOnLeft ? -this.getScrollBarWidth() : this.getWidth()), this.getY() + var12, var11, var9, true);
         } else {
            drawWidthComponent(new GameSprite(var10, 0, 0, var10.getHeight()), new GameSprite(var10, 1, 0, var10.getHeight()), this.getX() + (this.drawVerticalOnLeft ? 0 : this.getWidth() - this.getScrollBarWidth()), this.getY() + var12, var11, var9, true);
         }
      }

      if (var4.hasScrollbarX) {
         var9 = Settings.UI.activeElementColor;
         var10 = Settings.UI.scrollbar.active;
         var11 = this.getScrollbarSize(this.contentBox.width, this.getWidth() - var4.xOffsetByOtherBar);
         var12 = this.getScrollbarPos(this.contentBox.width, this.getWidth() - var4.xOffsetByOtherBar, this.getContentWidth() - var4.xOffsetByOtherBar, this.scrollX, var11);
         if (this.drawVerticalOnLeft) {
            var12 += var4.xOffsetByOtherBar;
         }

         if (this.isHoveringScrollX) {
            var9 = Settings.UI.highlightElementColor;
            var10 = Settings.UI.scrollbar.highlighted;
         }

         if (this.drawScrollBarOutsideBox) {
            drawWidthComponent(new GameSprite(var10, 0, 0, var10.getHeight()), new GameSprite(var10, 1, 0, var10.getHeight()), this.getX() + var12, this.getY() + (this.drawHorizontalOnTop ? -this.getScrollBarWidth() : this.getHeight()), var11, var9);
         } else {
            drawWidthComponent(new GameSprite(var10, 0, 0, var10.getHeight()), new GameSprite(var10, 1, 0, var10.getHeight()), this.getX() + var12, this.getY() + (this.drawHorizontalOnTop ? 0 : this.getHeight() - this.getScrollBarWidth()), var11, var9);
         }
      }

      if (this.background != null) {
         this.background.getEdgeDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
      }

   }

   public List<Rectangle> getHitboxes() {
      if (this.hitboxFullSize) {
         return singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
      } else {
         List var1 = this.components.getHitboxes();
         ScrollAndContentOffset var2 = new ScrollAndContentOffset();
         ArrayList var3 = new ArrayList(var1.size() + 2);
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Rectangle var5 = (Rectangle)var4.next();
            Rectangle var6 = new Rectangle(var5.x + var2.xOffset, var5.y + var2.yOffset, var5.width, var5.height);
            if (this.shouldLimitDrawArea) {
               Rectangle var7 = new Rectangle(var2.xOffset + this.scrollX, var2.yOffset + this.scrollY, this.getContentWidth(), this.getContentHeight());
               var3.add(var7.intersection(var6));
            } else {
               var3.add(var6);
            }
         }

         if (this.hasScrollbarX()) {
            var3.add(this.getScrollbarXHitbox());
         } else if (this.hasScrollbarY()) {
            var3.add(this.getScrollbarYHitbox());
         }

         return var3;
      }
   }

   public boolean isMouseOver(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight())).contains(var1.pos.hudX, var1.pos.hudY);
   }

   protected boolean hasScrollbarX() {
      if (this.alwaysShowHorizontalScrollBar) {
         return true;
      } else {
         int var1 = this.contentBox.width;
         if (this.background != null) {
            var1 += this.background.getContentPadding() * 2;
         }

         return var1 > this.getWidth();
      }
   }

   protected boolean hasScrollbarY() {
      if (this.alwaysShowVerticalScrollBar) {
         return true;
      } else {
         int var1 = this.contentBox.height;
         if (this.background != null) {
            var1 += this.background.getContentPadding() * 2;
         }

         return var1 > this.getHeight();
      }
   }

   protected boolean isMouseOverScrollbarX(InputEvent var1) {
      return var1.isMoveUsed() ? false : this.getScrollbarXHitbox().contains(var1.pos.hudX, var1.pos.hudY);
   }

   protected boolean isMouseOverScrollbarX(int var1, int var2, InputEvent var3) {
      return var3.isMoveUsed() ? false : this.getScrollbarXHitbox(var1, var2).contains(var3.pos.hudX, var3.pos.hudY);
   }

   protected Rectangle getScrollbarXHitbox() {
      int var1 = !this.drawScrollBarOutsideBox && this.hasScrollbarY() ? this.getScrollBarWidth() : 0;
      int var2 = this.getScrollbarSize(this.contentBox.width, this.getWidth() - var1);
      int var3 = this.getScrollbarPos(this.contentBox.width, this.getWidth() - var1, this.getContentWidth() - var1, this.scrollX, var2);
      if (this.drawVerticalOnLeft) {
         var3 += var1;
      }

      return this.getScrollbarXHitbox(var3, var2);
   }

   protected Rectangle getScrollbarXHitbox(int var1, int var2) {
      if (this.drawScrollBarOutsideBox) {
         return this.drawHorizontalOnTop ? new Rectangle(this.getX() + var1, this.getY() - this.getScrollBarWidth(), var2, this.getScrollBarWidth()) : new Rectangle(this.getX() + var1, this.getY() + this.getHeight(), var2, this.getScrollBarWidth());
      } else {
         return this.drawHorizontalOnTop ? new Rectangle(this.getX() + var1, this.getY(), var2, this.getScrollBarWidth()) : new Rectangle(this.getX() + var1, this.getY() + this.getHeight() - this.getScrollBarWidth(), var2, this.getScrollBarWidth());
      }
   }

   protected boolean isMouseOverScrollbarY(InputEvent var1) {
      return var1.isMoveUsed() ? false : this.getScrollbarYHitbox().contains(var1.pos.hudX, var1.pos.hudY);
   }

   protected boolean isMouseOverScrollbarY(int var1, int var2, InputEvent var3) {
      return var3.isMoveUsed() ? false : this.getScrollbarYHitbox(var1, var2).contains(var3.pos.hudX, var3.pos.hudY);
   }

   protected Rectangle getScrollbarYHitbox() {
      int var1 = this.getScrollbarSize(this.contentBox.height, this.getHeight());
      int var2 = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, var1);
      return this.getScrollbarYHitbox(var2, var1);
   }

   protected Rectangle getScrollbarYHitbox(int var1, int var2) {
      if (this.drawScrollBarOutsideBox) {
         return this.drawVerticalOnLeft ? new Rectangle(this.getX() - this.getScrollBarWidth(), this.getY() + var1, this.getScrollBarWidth(), var2) : new Rectangle(this.getX() + this.getWidth(), this.getY() + var1, this.getScrollBarWidth(), var2);
      } else {
         return this.drawVerticalOnLeft ? new Rectangle(this.getX(), this.getY() + var1, this.getScrollBarWidth(), var2) : new Rectangle(this.getX() + this.getWidth() - this.getScrollBarWidth(), this.getY() + var1, this.getScrollBarWidth(), var2);
      }
   }

   protected int getScrollbarSize(int var1, int var2) {
      return var1 <= var2 ? var2 : (int)Math.max((float)var2 / (float)var1 * (float)var2, (float)Math.min(20, var2));
   }

   protected int getScrollbarPos(int var1, int var2, int var3, int var4, int var5) {
      if (var1 <= var2) {
         return 0;
      } else {
         float var6 = (float)var4 / (float)(var1 - var3);
         return (int)(var6 * (float)(var2 - var5));
      }
   }

   protected int limitScroll(int var1, int var2, int var3) {
      return Math.max(0, Math.min(var1 - var2, var3));
   }

   public void scrollToFitForced(Rectangle var1) {
      int var2 = var1.x;
      int var3 = var1.y;
      int var4 = var1.x + var1.width - this.getContentWidth();
      int var5 = var1.y + var1.height - this.getContentHeight();
      this.setScrollX(GameMath.limit(this.scrollX, var4, var2));
      this.setScrollY(GameMath.limit(this.scrollY, var5, var3));
   }

   public void scrollToFit(Rectangle var1) {
      int var2 = var1.x;
      int var3 = var1.y;
      int var4 = var1.x + var1.width - this.getContentWidth();
      int var5 = var1.y + var1.height - this.getContentHeight();
      if (this.hasScrollbarX()) {
         int var6 = !this.drawScrollBarOutsideBox && this.hasScrollbarY() ? this.getScrollBarWidth() : 0;
         this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth() - var6, GameMath.limit(this.scrollX, var4, var2));
      }

      if (this.hasScrollbarY()) {
         this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), GameMath.limit(this.scrollY, var5, var3));
      }

   }

   public void setScrollX(int var1) {
      this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth(), var1);
   }

   public int getScrollX() {
      return this.scrollX;
   }

   public void scrollX(int var1) {
      this.setScrollX(this.getScrollX() + var1);
   }

   public void setScrollY(int var1) {
      this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), var1);
   }

   public void scrollY(int var1) {
      this.setScrollY(this.getScrollY() + var1);
   }

   public int getScrollY() {
      return this.scrollY;
   }

   public void setScroll(int var1, int var2) {
      this.setScrollX(var1);
      this.setScrollY(var2);
   }

   public ComponentList<FormComponent> getComponentList() {
      return this.components;
   }

   public void setContentBox(Rectangle var1) {
      this.contentBox = var1;
      this.scrollX = this.limitScroll(var1.width, this.getContentWidth(), this.scrollX);
      this.scrollY = this.limitScroll(var1.height, this.getContentHeight(), this.scrollY);
   }

   public Rectangle getContentBox() {
      return new Rectangle(this.contentBox);
   }

   public void fitContentBoxToComponents(int var1, int var2, int var3, int var4) {
      Rectangle var5 = this.getContentBoxToFitComponents();
      var5.x -= var1;
      var5.y -= var3;
      var5.width += var2 * 2;
      var5.height += var4 * 2;
      this.setContentBox(var5);
   }

   public void fitContentBoxToComponents(int var1) {
      this.fitContentBoxToComponents(var1, var1, var1, var1);
   }

   public void fitContentBoxToComponents() {
      this.fitContentBoxToComponents(0);
   }

   public void centerContentHorizontal() {
      Rectangle var1 = new Rectangle(this.contentBox);
      if (var1.width < this.width) {
         int var2 = (this.width - var1.width) / 2;
         var1.x -= var2;
         var1.width += var2;
      }

      this.setContentBox(var1);
   }

   public void centerContentVertical() {
      Rectangle var1 = new Rectangle(this.contentBox);
      if (var1.height < this.height) {
         int var2 = (this.height - var1.height) / 2;
         var1.y -= var2;
         var1.height += var2;
      }

      this.setContentBox(var1);
   }

   public Rectangle getContentBoxToFitComponents() {
      int var1 = Integer.MAX_VALUE;
      int var2 = Integer.MAX_VALUE;
      int var3 = Integer.MIN_VALUE;
      int var4 = Integer.MIN_VALUE;
      if (this.components.getComponents().isEmpty()) {
         var1 = 0;
         var2 = 0;
         var3 = 0;
         var4 = 0;
      } else {
         Iterator var5 = this.components.getComponents().iterator();

         while(var5.hasNext()) {
            FormComponent var6 = (FormComponent)var5.next();
            Rectangle var7 = var6.getBoundingBox();
            if (var7.x < var1) {
               var1 = var7.x;
            }

            if (var7.y < var2) {
               var2 = var7.y;
            }

            if (var7.x + var7.width > var3) {
               var3 = var7.x + var7.width;
            }

            if (var7.y + var7.height > var4) {
               var4 = var7.y + var7.height;
            }
         }
      }

      return new Rectangle(var1, var2, var3 - var1, var4 - var2);
   }

   public ContentBoxListManager listManager() {
      return new ContentBoxListManager(this);
   }

   public int getScrollBarWidth() {
      return Settings.UI.scrollbar.active.getHeight();
   }

   public int getMinContentWidth() {
      int var1 = this.getWidth();
      int var2 = this.drawScrollBarOutsideBox ? 0 : this.getScrollBarWidth();
      if (this.background != null) {
         var1 -= Math.max(this.background.getContentPadding() * 2, var2);
      } else {
         var1 -= var2;
      }

      return var1;
   }

   public int getMinContentHeight() {
      int var1 = this.getHeight();
      int var2 = this.drawScrollBarOutsideBox ? 0 : this.getScrollBarWidth();
      if (this.background != null) {
         var1 -= Math.max(this.background.getContentPadding() * 2, var2);
      } else {
         var1 -= var2;
      }

      return var1;
   }

   public int getWidth() {
      return this.width;
   }

   public int getContentWidth() {
      int var1 = this.getWidth();
      if (!this.drawScrollBarOutsideBox && this.hasScrollbarY()) {
         if (this.background != null) {
            var1 -= Math.max(this.background.getContentPadding() * 2, this.getScrollBarWidth());
         } else {
            var1 -= this.getScrollBarWidth();
         }
      } else if (this.background != null) {
         var1 -= this.background.getContentPadding() * 2;
      }

      return var1;
   }

   public void setWidth(int var1) {
      this.width = var1;
      if (this.contentBox != null) {
         this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth(), this.scrollX);
      }

   }

   public int getHeight() {
      return this.height;
   }

   public int getContentHeight() {
      int var1 = this.getHeight();
      if (!this.drawScrollBarOutsideBox && this.hasScrollbarX()) {
         if (this.background != null) {
            var1 -= Math.max(this.background.getContentPadding() * 2, this.getScrollBarWidth());
         } else {
            var1 -= this.getScrollBarWidth();
         }
      } else if (this.background != null) {
         var1 -= this.background.getContentPadding() * 2;
      }

      return var1;
   }

   public void setHeight(int var1) {
      this.height = var1;
      if (this.contentBox != null) {
         this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), this.scrollY);
      }

   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public boolean shouldDraw() {
      return !this.isHidden();
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      if (this.hidden != var1) {
         this.hidden = var1;
         Screen.submitNextMoveEvent();
      }

   }

   public void dispose() {
      super.dispose();
      this.components.disposeComponents();
   }

   private class ScrollbarControllerFocusHandler implements ControllerFocusHandler {
      private boolean isSelected;

      public ScrollbarControllerFocusHandler() {
      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
         if (var1.getState() == ControllerInput.MENU_SELECT && FormContentBox.this.isControllerFocus(this)) {
            this.isSelected = true;
            var1.use();
         } else if (this.isSelected && var1.getState() == ControllerInput.MENU_BACK && FormContentBox.this.isControllerFocus(this)) {
            this.isSelected = false;
            var1.use();
         }

      }

      public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
         if (!this.isSelected) {
            return false;
         } else {
            int var5;
            int var6;
            if (!FormContentBox.this.hasScrollbarY() || var1 != 0 && var1 != 2) {
               if (FormContentBox.this.hasScrollbarX() && (var1 == 1 || var1 == 3)) {
                  var5 = var1 == 1 ? 20 : -20;
                  var6 = FormContentBox.this.scrollX;
                  boolean var7 = FormContentBox.this.hasScrollbarY();
                  int var8 = !FormContentBox.this.drawScrollBarOutsideBox && var7 ? FormContentBox.this.getScrollBarWidth() : 0;
                  FormContentBox.this.scrollX = FormContentBox.this.limitScroll(FormContentBox.this.contentBox.width, FormContentBox.this.getContentWidth() - var8, FormContentBox.this.scrollX + var5);
                  if (var6 != FormContentBox.this.scrollX) {
                     FormContentBox.this.playTickSound();
                     var2.use();
                     Screen.submitNextMoveEvent();
                  }
               }
            } else {
               var5 = var1 == 0 ? -20 : 20;
               var6 = FormContentBox.this.scrollY;
               FormContentBox.this.scrollY = FormContentBox.this.limitScroll(FormContentBox.this.contentBox.height, FormContentBox.this.getContentHeight(), FormContentBox.this.scrollY + var5);
               if (var6 != FormContentBox.this.scrollY) {
                  FormContentBox.this.playTickSound();
                  var2.use();
                  Screen.submitNextMoveEvent();
               }
            }

            return true;
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         if (this.isSelected) {
            Rectangle var2 = var1.boundingBox;
            byte var3 = 5;
            var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
            HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var2).draw();
         } else {
            ControllerFocusHandler.super.drawControllerFocus(var1);
         }

      }
   }

   protected class ScrollAndContentOffset {
      public boolean hasScrollbarY = FormContentBox.this.hasScrollbarY();
      public boolean hasScrollbarX = FormContentBox.this.hasScrollbarX();
      public int xOffsetByOtherBar;
      public int yOffsetByOtherBar;
      public int contentOffset;
      public int xOffset;
      public int yOffset;

      protected ScrollAndContentOffset() {
         this.xOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && this.hasScrollbarY ? FormContentBox.this.getScrollBarWidth() : 0;
         this.yOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && this.hasScrollbarX ? FormContentBox.this.getScrollBarWidth() : 0;
         this.contentOffset = FormContentBox.this.background == null ? 0 : FormContentBox.this.background.getContentPadding();
         this.xOffset = FormContentBox.this.getX() - FormContentBox.this.scrollX - FormContentBox.this.contentBox.x + (FormContentBox.this.drawVerticalOnLeft ? this.xOffsetByOtherBar : 0) + this.contentOffset;
         this.yOffset = FormContentBox.this.getY() - FormContentBox.this.scrollY - FormContentBox.this.contentBox.y + (FormContentBox.this.drawHorizontalOnTop ? this.yOffsetByOtherBar : 0) + this.contentOffset;
      }
   }
}
