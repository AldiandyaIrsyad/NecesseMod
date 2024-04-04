package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.FormShader;

public class FormLabelEdit extends FormFairTypeEdit implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int scrollX;
   private FormEventsHandler<FormInputEvent<FormLabelEdit>> submitEvents = new FormEventsHandler();
   public Color color;
   public boolean addControllerHitbox;

   public FormLabelEdit(String var1, FontOptions var2, Color var3, int var4, int var5, int var6, int var7) {
      super(var2, FairType.TextAlign.LEFT, var3, -1, 1, var7);
      this.color = Settings.UI.activeTextColor;
      this.addControllerHitbox = false;
      this.position = new FormFixedPosition(var4, var5);
      this.width = var6;
      this.setText(var1);
      this.onCaretMove((var1x) -> {
         if (!var1x.causedByMouse) {
            this.fitScroll();
         }

      });
      this.onMouseChangedTyping((var1x) -> {
         if (!this.isTyping()) {
            this.submitEvents.onEvent(new FormInputEvent(this, var1x.event));
         }

      });
   }

   public FormLabelEdit onSubmit(FormEventListener<FormInputEvent<FormLabelEdit>> var1) {
      this.submitEvents.addListener(var1);
      return this;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public void fitScroll() {
      Rectangle var1 = this.getCaretBoundingBox();
      var1.x -= this.getTextX();
      if (this.getDrawOptions().getBoundingBox().width < this.width) {
         this.scrollX = 0;
      } else {
         int var2 = var1.x;
         int var3 = var1.x + var1.width;
         if (var3 > this.width - 32) {
            this.scrollX = Math.max(this.scrollX, var3 - (this.width - 32));
         }

         if (var2 < this.scrollX) {
            this.scrollX = Math.max(0, Math.min(this.scrollX, var2 - 16));
         }
      }

   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isTyping() && var1.state && (var1.getID() == 257 || var1.getID() == 256)) {
         FormInputEvent var4 = new FormInputEvent(this, var1);
         this.setTyping(false);
         this.submitEvents.onEvent(var4);
         if (var4.hasPreventedDefault()) {
            this.setTyping(true);
         } else {
            var1.use();
         }
      }

      super.handleInputEvent(InputEvent.OffsetHudEvent(Screen.input(), var1, -this.getX(), -this.getY()), var2, var3);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.addControllerHitbox) {
         ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.fontOptions.getSize()));
   }

   protected Rectangle getTextBoundingBox() {
      return new Rectangle(0, 0, this.width, this.fontOptions.getSize());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      FormShader.FormShaderState var4 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(0, 0, this.width, this.fontOptions.getSize() + 4));

      try {
         super.draw(var1, var2, var3);
      } finally {
         var4.end();
      }

   }

   protected int getTextX() {
      return -this.scrollX;
   }

   protected int getTextY() {
      return 0;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
