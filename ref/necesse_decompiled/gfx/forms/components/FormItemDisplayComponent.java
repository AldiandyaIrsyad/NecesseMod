package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public class FormItemDisplayComponent extends FormComponent implements FormPositionContainer {
   public final InventoryItem item;
   private FormPosition position;
   private boolean hovering;

   public FormItemDisplayComponent(int var1, int var2, InventoryItem var3) {
      this.position = new FormFixedPosition(var1, var2);
      this.item = var3;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.hovering = this.isMouseOver(var1);
         if (this.hovering) {
            var1.useMove();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      GameSprite var4 = this.item.item.getItemSprite(this.item, var2);
      Color var5 = this.item.item.getDrawColor(this.item, var2);
      this.changeDrawOptions(var4.initDraw().size(32).color(var5)).draw(this.getX(), this.getY());
      if (this.hovering) {
         GameTooltips var6 = this.getTooltip();
         if (var6 != null) {
            Screen.addTooltip(var6, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public TextureDrawOptionsEnd changeDrawOptions(TextureDrawOptionsEnd var1) {
      return var1;
   }

   public GameTooltips getTooltip() {
      return new StringTooltips(this.item.getItemDisplayName(), this.item.item.getRarityColor(this.item));
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
