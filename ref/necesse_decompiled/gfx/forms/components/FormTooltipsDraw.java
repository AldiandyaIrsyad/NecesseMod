package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class FormTooltipsDraw extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private GameTooltips tooltips;
   private int width;
   private int height;

   public FormTooltipsDraw(int var1, int var2, ListGameTooltips var3) {
      this.position = new FormFixedPosition(var1, var2);
      this.setTooltips(var3);
   }

   public void setTooltips(GameTooltips var1) {
      this.tooltips = var1;
      if (var1 != null) {
         this.width = var1.getWidth();
         this.height = var1.getHeight();
      } else {
         this.width = 0;
         this.height = 0;
      }

   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.tooltips != null) {
         this.tooltips.draw(this.getX(), this.getY(), GameColor.DEFAULT_COLOR);
      }

   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
