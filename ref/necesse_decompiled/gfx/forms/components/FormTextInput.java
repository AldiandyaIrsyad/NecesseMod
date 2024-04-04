package necesse.gfx.forms.components;

import com.codedisaster.steamworks.SteamControllerHandle;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.FormShader;

public class FormTextInput extends FormFairTypeEdit implements FormPositionContainer {
   protected FormPosition position;
   public final FormInputSize size;
   private final int width;
   private boolean active;
   private int scrollX;
   private FormEventsHandler<FormInputEvent<FormTextInput>> submitEvents;
   public GameMessage placeHolder;
   public GameMessage tooltip;
   public FormTypingComponent tabTypingComponent;
   public boolean rightClickToClear;

   public FormTextInput(int var1, int var2, FormInputSize var3, int var4, int var5, int var6) {
      super(var3.getFontOptions(), FairType.TextAlign.CENTER, Color.WHITE, var5, 1, var6);
      this.active = true;
      this.size = var3;
      this.submitEvents = new FormEventsHandler();
      this.position = new FormFixedPosition(var1, var2);
      this.width = var4;
      this.onCaretMove((var1x) -> {
         if (!var1x.causedByMouse) {
            Rectangle var2 = this.getCaretBoundingBox();
            var2.x -= this.getTextX();
            if (this.getDrawOptions().getBoundingBox().width < this.width) {
               this.scrollX = 0;
            } else {
               int var3 = var2.x;
               int var4 = var2.x + var2.width;
               int var5 = (this.width - 16) / 2;
               if (var4 > var5 + this.scrollX) {
                  this.scrollX = Math.max(this.scrollX, var4 - var5);
               }

               if (var3 < -var5 + this.scrollX) {
                  this.scrollX = Math.min(this.scrollX, var3 + var5);
               }
            }

         }
      });
      this.onMouseChangedTyping((var1x) -> {
         if (!this.isTyping()) {
            this.submitEvents.onEvent(new FormInputEvent(this, var1x.event));
         }

      });
   }

   public FormTextInput(int var1, int var2, FormInputSize var3, int var4, int var5) {
      this(var1, var2, var3, var4, -1, var5);
   }

   public FormTextInput onSubmit(FormEventListener<FormInputEvent<FormTextInput>> var1) {
      this.submitEvents.addListener(var1);
      return this;
   }

   public void setActive(boolean var1) {
      this.active = var1;
      if (!this.isActive()) {
         this.clearSelection();
         if (this.isTyping()) {
            this.setTyping(false);
         }
      }

   }

   public boolean isActive() {
      return this.active;
   }

   public boolean submitControllerEnter() {
      InputEvent var1 = InputEvent.ControllerButtonEvent(ControllerEvent.customEvent((SteamControllerHandle)null, ControllerInput.MENU_SELECT), (TickManager)null);
      FormInputEvent var2 = new FormInputEvent(this, var1);
      this.setTyping(false);
      this.submitEvents.onEvent(var2);
      if (var2.hasPreventedDefault()) {
         this.setTyping(true);
         return false;
      } else {
         var1.use();
         return true;
      }
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isActive()) {
         if (this.rightClickToClear && !var1.state && var1.getID() == -99 && this.isMouseOver(var1)) {
            this.setText("");
            this.submitChangeEvent();
            this.setTyping(true);
            var1.use();
         }

         if (this.isTyping()) {
            if (var1.state && this.tabTypingComponent != null && var1.getID() == 258) {
               var1.use();
               this.clearSelection();
               this.tabTypingComponent.setTyping(true);
            } else if (var1.state && (var1.getID() == 257 || var1.getID() == 335 || var1.getID() == 256)) {
               FormInputEvent var4 = new FormInputEvent(this, var1);
               this.clearSelection();
               this.setTyping(false);
               this.submitEvents.onEvent(var4);
               if (var4.hasPreventedDefault()) {
                  this.setTyping(true);
               } else {
                  var1.use();
               }
            }
         }

         super.handleInputEvent(InputEvent.OffsetHudEvent(Screen.input(), var1, -(this.getX() + this.width / 2), -(this.getY() + 5)), var2, var3);
      }

      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
   }

   protected Rectangle getTextBoundingBox() {
      Rectangle var1 = this.size.getContentRectangle(this.width);
      return new Rectangle(var1.x - this.width / 2, var1.y - 5, var1.width, var1.height);
   }

   public GameTooltips getTooltips() {
      return this.tooltip != null ? new StringTooltips(this.tooltip.translate()) : null;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.size.getInputDrawOptions(this.getX(), this.getY(), this.width).draw();
      Rectangle var4 = this.size.getContentRectangle(this.width);
      FormShader.FormShaderState var5 = GameResources.formShader.startState(new Point(this.getX() + this.width / 2, this.getY()), new Rectangle(var4.x - this.width / 2, var4.y, var4.width, var4.height));

      try {
         if (!this.isTyping() && this.getTextLength() == 0 && this.placeHolder != null) {
            FontOptions var6 = (new FontOptions(this.fontOptions)).colorf(1.0F, 1.0F, 1.0F, 0.5F);
            int var7 = -FontManager.bit.getWidthCeil(this.placeHolder.translate(), var6) / 2;
            FontManager.bit.drawString((float)var7, (float)this.size.fontDrawOffset, this.placeHolder.translate(), var6);
         }

         super.draw(var1, var2, var3);
         if (!this.isActive()) {
            Screen.initQuadDraw(var4.width, var4.height).color(0.0F, 0.0F, 0.0F, 0.5F).draw(-this.width / 2, var4.y);
         }
      } finally {
         var5.end();
      }

      if (this.isHovering) {
         GameTooltips var11 = this.getTooltips();
         if (var11 != null) {
            Screen.addTooltip(var11, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   protected int getTextX() {
      return -this.scrollX;
   }

   protected int getTextY() {
      return this.size.fontDrawOffset;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
