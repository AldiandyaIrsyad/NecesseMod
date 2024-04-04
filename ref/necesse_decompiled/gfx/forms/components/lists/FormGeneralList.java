package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HoverStateTextures;

public abstract class FormGeneralList<E extends FormListElement> extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   protected int width;
   protected int height;
   protected final int ELEMENT_PADDING = 16;
   protected List<E> elements;
   public final int elementHeight;
   protected int scroll;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
   private int mouseDown;
   private long mouseDownTime;
   private float scrollBuffer;
   protected boolean isHoveringBot;
   protected boolean isHoveringTop;
   protected boolean isHoveringSpace;
   protected boolean acceptMouseRepeatEvents = false;
   protected boolean limitListElementDraw = true;

   public FormGeneralList(int var1, int var2, int var3, int var4, int var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.height = var4;
      this.elementHeight = var5;
      this.reset();
   }

   public void reset() {
      this.elements = new ArrayList();
      this.resetScroll();
   }

   public void resetScroll() {
      this.scroll = 0;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      MouseOverObject var4;
      if (var1.isMouseMoveEvent()) {
         this.isHoveringTop = this.isMouseOverTop(var1);
         this.isHoveringBot = this.isMouseOverBot(var1);
         this.isHoveringSpace = this.isMouseOverElementSpace(var1);
         this.elements.forEach((var0) -> {
            var0.setMoveEvent((InputEvent)null);
         });
         var4 = this.getMouseOverObj(var1);
         if (var4 != null) {
            ((FormListElement)this.elements.get(var4.elementIndex)).setMoveEvent(InputEvent.OffsetHudEvent(Screen.input(), var1, -var4.xOffset, -var4.yOffset));
            var1.useMove();
         }

         if (this.isHoveringTop || this.isHoveringBot || this.isHoveringSpace) {
            var1.useMove();
         }
      } else if (var1.isMouseWheelEvent()) {
         if (var1.state && this.isMouseOverElementSpace(var1)) {
            this.wheelBuffer.add(var1, (double)this.getScrollAmount());
            int var5 = this.wheelBuffer.useAllScrollY();
            if (this.scroll(-var5)) {
               this.playTickSound();
               var1.use();
               this.handleInputEvent(InputEvent.MouseMoveEvent(var1.pos, var2), var2, var3);
            }
         }
      } else if (var1.isMouseClickEvent() || this.acceptMouseRepeatEvents && var1.getID() == -105) {
         if (var1.isMouseClickEvent()) {
            if (var1.state) {
               this.mouseDownTime = System.currentTimeMillis() + 250L;
               if (this.isMouseOverTop(var1)) {
                  if (this.scrollUp()) {
                     this.playTickSound();
                  }

                  var1.use();
                  this.mouseDown = 1;
               } else if (this.isMouseOverBot(var1)) {
                  if (this.scrollDown()) {
                     this.playTickSound();
                  }

                  var1.use();
                  this.mouseDown = -1;
               }
            } else {
               this.mouseDown = 0;
            }
         }

         if (!var1.isUsed()) {
            var4 = this.getMouseOverObj(var1);
            if (var4 != null) {
               if (var1.state) {
                  ((FormListElement)this.elements.get(var4.elementIndex)).onClick(this, var4.elementIndex, InputEvent.OffsetHudEvent(Screen.input(), var1, -var4.xOffset, -var4.yOffset), var3);
                  var1.use();
               } else {
                  var1.use();
               }
            }
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isButton && var1.buttonState || this.acceptMouseRepeatEvents && var1.getState() == ControllerInput.REPEAT_EVENT) {
         ControllerFocus var4 = this.getManager().getCurrentFocus();
         if (var4 != null && var4.handler instanceof FormListElement) {
            int var5 = Math.max(0, (int)Math.ceil((double)((float)this.scroll / (float)this.elementHeight)));
            int var6 = this.height - 32;
            int var7 = Math.min(this.elements.size(), (int)Math.floor((double)((float)(this.scroll + var6) / (float)this.elementHeight)));

            for(int var8 = var5; var8 < var7; ++var8) {
               FormListElement var9 = (FormListElement)this.elements.get(var8);
               if (var9 == var4.handler) {
                  var9.onControllerEvent(this, var8, var1, var2, var3);
               }
            }
         }
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      var5 = var5.intersection(new Rectangle(var2 + this.getX(), var3 + this.getY(), this.width, this.height));
      if (var6) {
         Screen.drawShape(var5, false, 0.0F, 1.0F, 1.0F, 1.0F);
      }

      int var7 = Math.max(0, (int)Math.ceil((double)((float)this.scroll / (float)this.elementHeight)));
      int var8 = this.height - 32;
      int var9 = Math.min(this.elements.size(), (int)Math.floor((double)((float)(this.scroll + var8) / (float)this.elementHeight)));

      for(int var10 = var7; var10 < var9; ++var10) {
         FormListElement var11 = (FormListElement)this.elements.get(var10);
         int var12 = var10 * this.elementHeight - this.scroll + 16;
         ControllerFocus.add(var1, var5, var11, new Rectangle(this.width, this.elementHeight), var2 + this.getX(), var3 + this.getY() + var12, 0, this.getControllerNavigationHandler(var2, var3));
      }

   }

   protected ControllerNavigationHandler getControllerNavigationHandler(int var1, int var2) {
      return (var3, var4, var5, var6) -> {
         LinkedList var7 = new LinkedList();
         Rectangle var8 = new Rectangle(var1 + this.getX(), var2 + this.getY() - this.elementHeight, this.width, this.height + this.elementHeight * 2);
         int var9 = Math.max(0, (int)Math.ceil((double)((float)this.scroll / (float)this.elementHeight)) - 1);
         int var10 = this.height - 32;
         int var11 = Math.min(this.elements.size(), (int)Math.floor((double)((float)(this.scroll + var10) / (float)this.elementHeight)) + 1);

         for(int var12 = var9; var12 < var11; ++var12) {
            FormListElement var13 = (FormListElement)this.elements.get(var12);
            int var14 = var12 * this.elementHeight - this.scroll + 16;
            ControllerFocus.add(var7, var8, var13, new Rectangle(this.width, this.elementHeight), var1 + this.getX(), var2 + this.getY() + var14, 0, this.getControllerNavigationHandler(var1, var2));
         }

         ControllerFocus var19 = ControllerFocus.getNext(var3, this.getManager(), var7);
         if (var19 != null) {
            int var20 = this.scroll;
            Rectangle var21 = new Rectangle(var19.boundingBox.x - (var1 + this.getX()), var19.boundingBox.y - (var2 + this.getY() - this.scroll), var19.boundingBox.width, var19.boundingBox.height);
            int var15 = this.elementHeight;
            int var16 = var21.y - var15;
            int var17 = var21.y + var21.height + var15 - this.height;
            this.scroll = Math.max(0, GameMath.limit(this.scroll, var17, var16));
            this.limitMaxScroll();
            int var18 = this.scroll - var20;
            this.getManager().setControllerFocus(new ControllerFocus(var19, 0, -var18));
            return true;
         } else {
            return false;
         }
      };
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.handleDrawScroll(var1);
      if (this.elements.size() == 0) {
         this.drawEmptyMessage(var1);
      } else {
         int var4 = Math.max(0, this.scroll / this.elementHeight);
         int var5 = this.height - 32;
         int var6 = var5 / this.elementHeight + (var5 % this.elementHeight == 0 ? 0 : 1);
         int var7 = Math.min(this.elements.size(), var4 + var6 + (this.scroll % this.elementHeight == 0 ? 0 : 1));

         for(int var8 = var4; var8 < var7; ++var8) {
            int var9 = var8 * this.elementHeight - this.scroll + 16;
            int var10 = this.getX();
            int var11 = this.getY() + var9;
            FormShader.FormShaderState var12;
            int var13;
            int var14;
            if (this.limitListElementDraw) {
               var13 = Math.max(0, 16 - var9);
               var14 = Math.min(this.elementHeight, this.height - var9 - 16) - var13;
               var12 = GameResources.formShader.startState(new Point(var10, var11), new Rectangle(0, var13, this.width, var14));
            } else {
               var13 = Math.max(0, 16 - var9);
               var14 = this.height - var9 - 16 - var13;
               var12 = GameResources.formShader.startState(new Point(var10, var11), new Rectangle(0, var13, this.width, var14));
            }

            try {
               ((FormListElement)this.elements.get(var8)).draw(this, var1, var2, var8);
            } finally {
               var12.end();
            }
         }
      }

      this.drawScrollButtons(var1);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   protected void handleDrawScroll(TickManager var1) {
      if (this.mouseDown > 0 && this.mouseDownTime < System.currentTimeMillis() && this.isHoveringTop) {
         this.scrollBuffer -= var1.getDelta() * 0.5F;
      } else if (this.mouseDown < 0 && this.mouseDownTime < System.currentTimeMillis() && this.isHoveringBot) {
         this.scrollBuffer += var1.getDelta() * 0.5F;
      }

      int var2 = (int)this.scrollBuffer;
      if (var2 != 0) {
         this.scroll(var2);
         this.scrollBuffer -= (float)var2;
      }

      this.limitMaxScroll();
   }

   protected void drawEmptyMessage(TickManager var1) {
      GameMessage var2 = this.getEmptyMessage();
      if (var2 != null) {
         FormShader.FormShaderState var3 = GameResources.formShader.startState((Point)null, new Rectangle(0, 16, this.width, this.height - 32));

         try {
            FontOptions var4 = (new FontOptions(this.getEmptyMessageFontOptions())).color(Settings.UI.activeTextColor);
            String var5 = var2.translate();
            ArrayList var6 = GameUtils.breakString(var5, var4, this.width - 20);

            for(int var7 = 0; var7 < var6.size(); ++var7) {
               String var8 = (String)var6.get(var7);
               int var9 = FontManager.bit.getWidthCeil(var8, var4);
               FontManager.bit.drawString((float)(this.getX() + this.width / 2 - var9 / 2), (float)(this.getY() + 16 + var7 * var4.getSize() + 4), var8, var4);
            }
         } finally {
            var3.end();
         }
      }

   }

   protected void drawScrollButtons(TickManager var1) {
      HoverStateTextures var2 = Settings.UI.button_navigate_vertical;
      GameTexture var3 = this.isHoveringTop ? var2.highlighted : var2.active;
      Color var4 = this.isHoveringTop ? Settings.UI.highlightElementColor : Settings.UI.activeElementColor;
      GameTexture var5 = this.isHoveringBot ? var2.highlighted : var2.active;
      Color var6 = this.isHoveringBot ? Settings.UI.highlightElementColor : Settings.UI.activeElementColor;
      var3.initDraw().color(var4).draw(this.getX() + this.width / 2 - var3.getWidth() / 2, this.getY() + 3);
      var5.initDraw().color(var6).mirrorY().draw(this.getX() + this.width / 2 - var5.getWidth() / 2, this.getY() + this.height - 3 - var5.getHeight());
   }

   protected FormGeneralList<E>.MouseOverObject getMouseOverObj(InputEvent var1) {
      if (!this.isMouseOverElementSpace(var1)) {
         return null;
      } else {
         int var2 = Math.max(0, this.scroll / this.elementHeight);
         int var3 = this.height - 32;
         int var4 = var3 / this.elementHeight + (var3 % this.elementHeight == 0 ? 0 : 1);
         int var5 = Math.min(this.elements.size(), var2 + var4 + (this.scroll % this.elementHeight == 0 ? 0 : 1));

         for(int var6 = var2; var6 < var5; ++var6) {
            MouseOverObject var7 = this.getMouseOffset(var6, var1);
            if (var7.xOffset != -1 && var7.yOffset != -1) {
               return var7;
            }
         }

         return null;
      }
   }

   protected E getMouseOverElement(InputEvent var1) {
      MouseOverObject var2 = this.getMouseOverObj(var1);
      return var2 != null ? (FormListElement)this.elements.get(var2.elementIndex) : null;
   }

   protected FormGeneralList<E>.MouseOverObject getMouseOffset(int var1, InputEvent var2) {
      int var3 = var1 * this.elementHeight - this.scroll + 16;
      int var4 = this.getX();
      int var5 = this.getY() + var3;
      return this.getMouseOffset(var1, var2, var4, var5);
   }

   private FormGeneralList<E>.MouseOverObject getMouseOffset(int var1, InputEvent var2, int var3, int var4) {
      int var5 = var2.pos.hudX - var3;
      int var6 = var2.pos.hudY - var4;
      if (var5 < 0 || var5 >= this.width) {
         var3 = -1;
      }

      if (var6 < 0 || var6 >= this.elementHeight) {
         var4 = -1;
      }

      return new MouseOverObject(var1, var3, var4);
   }

   public boolean isMouseOverElementSpace(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY() + 16, this.width, this.height - 32)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public boolean isMouseOverTop(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX() + this.width / 2 - 16, this.getY() + 3, 32, 10)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public boolean isMouseOverBot(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX() + this.width / 2 - 16, this.getY() + this.height - 13, 32, 10)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public int getScrollAmount() {
      return 20;
   }

   public boolean scrollUp() {
      return this.scroll(-this.getScrollAmount());
   }

   public boolean scrollDown() {
      return this.scroll(this.getScrollAmount());
   }

   public boolean scroll(int var1) {
      int var2 = this.scroll;
      this.scroll += var1;
      if (this.scroll < 0) {
         this.scroll = 0;
      }

      this.limitMaxScroll();
      if (var2 != this.scroll) {
         Screen.submitNextMoveEvent();
         return true;
      } else {
         return false;
      }
   }

   public void limitMaxScroll() {
      int var1 = Math.max(0, (this.elements.size() - 1) * this.elementHeight - (this.height - 32 - this.elementHeight));
      if (this.scroll > var1) {
         this.scroll = var1;
      }

   }

   public GameMessage getEmptyMessage() {
      return null;
   }

   public FontOptions getEmptyMessageFontOptions() {
      return new FontOptions(12);
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int var1) {
      this.height = var1;
   }

   protected class MouseOverObject {
      public int elementIndex;
      public int xOffset;
      public int yOffset;

      public MouseOverObject(int var2, int var3, int var4) {
         this.elementIndex = var2;
         this.xOffset = var3;
         this.yOffset = var4;
      }
   }
}
