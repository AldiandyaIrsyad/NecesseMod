package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public class FormItemIcon extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private boolean isHovering;
   public InventoryItem item;
   public boolean showNameAsTooltip;

   public FormItemIcon(int var1, int var2, InventoryItem var3, boolean var4) {
      this.position = new FormFixedPosition(var1, var2);
      this.item = var3;
      this.showNameAsTooltip = var4;
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
      this.item.draw(var2, this.getX(), this.getY(), false);
      if (this.isHovering()) {
         if (this.showNameAsTooltip) {
            Screen.addTooltip(new StringTooltips(this.item.getItemDisplayName()), TooltipLocation.FORM_FOCUS);
         } else {
            Screen.addTooltip(this.item.getTooltip(var2, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
   }

   public boolean isHovering() {
      return this.isHovering;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
