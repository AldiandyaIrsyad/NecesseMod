package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormProgressBarText extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private GameMessage tooltip;
   private boolean isHovering;
   private int width;
   public int currentProgress;
   public int totalProgress;
   public Color completeColor;
   public Color incompleteColor;

   public FormProgressBarText(int var1, int var2, int var3, int var4) {
      this.completeColor = Settings.UI.successTextColor;
      this.incompleteColor = Settings.UI.errorTextColor;
      this.position = new FormFixedPosition(var1, var2);
      this.totalProgress = var3;
      this.width = var4;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.totalProgress == 0) {
         Achievement.drawProgressbar(this.getX(), this.getY() + 6, this.width, 5, 1.0F);
      } else {
         float var4 = GameMath.limit(this.totalProgress == 0 ? 1.0F : (float)this.currentProgress / (float)this.totalProgress, 0.0F, 1.0F);
         Achievement.drawProgressbarText(this.getX(), this.getY(), this.width, 5, var4, this.getText(), this.getTextColor());
      }

      if (this.isHovering()) {
         GameTooltips var5 = this.getTooltips();
         if (var5 != null) {
            Screen.addTooltip(var5, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, 16));
   }

   public boolean isHovering() {
      return this.isHovering;
   }

   public String getText() {
      return this.currentProgress + "/" + this.totalProgress;
   }

   public Color getTextColor() {
      return this.currentProgress >= this.totalProgress ? this.completeColor : this.incompleteColor;
   }

   public GameTooltips getTooltips() {
      return this.tooltip != null ? new StringTooltips(this.tooltip.translate()) : null;
   }

   public void setTooltip(GameMessage var1) {
      this.tooltip = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
