package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormCustomButton extends FormButton implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int height;
   private GameMessage[] tooltips;

   public FormCustomButton(int var1, int var2, int var3, int var4, GameMessage... var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.height = var4;
      this.tooltips = var5;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      this.draw(var4, this.getX(), this.getY(), var2);
      if (this.isHovering()) {
         GameTooltips var5 = this.getTooltips();
         if (var5 != null) {
            Screen.addTooltip(var5, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public abstract void draw(Color var1, int var2, int var3, PlayerMob var4);

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   public GameTooltips getTooltips() {
      if (this.tooltips.length == 0) {
         return null;
      } else {
         StringTooltips var1 = new StringTooltips();
         GameMessage[] var2 = this.tooltips;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            GameMessage var5 = var2[var4];
            var1.add(var5.translate());
         }

         return var1;
      }
   }

   public void setTooltips(GameMessage... var1) {
      this.tooltips = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
