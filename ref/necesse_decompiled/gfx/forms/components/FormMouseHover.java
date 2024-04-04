package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormMouseHover extends FormButton implements FormPositionContainer {
   private FormPosition position;
   public int width;
   public int height;

   public FormMouseHover(int var1, int var2, int var3, int var4, boolean var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.height = var4;
      this.useHoverMoveEvents = var5;
   }

   public FormMouseHover(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isHovering()) {
         GameTooltips var4 = this.getTooltips(var2);
         if (var4 != null) {
            Screen.addTooltip(var4, TooltipLocation.FORM_FOCUS);
         }

         Screen.CURSOR var5 = this.getHoveringCursor(var2);
         if (var5 != null) {
            Screen.setCursor(var5);
         }
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.clickedEvents.hasListeners()) {
         super.addNextControllerFocus(var1, var2, var3, var4, var5, var6);
      }

   }

   public GameTooltips getTooltips(PlayerMob var1) {
      return null;
   }

   public Screen.CURSOR getHoveringCursor(PlayerMob var1) {
      return null;
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
