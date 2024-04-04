package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;

public class FormHorizontalToggle extends FormButtonToggle implements FormPositionContainer {
   private FormPosition position;

   public FormHorizontalToggle(int var1, int var2) {
      this.position = new FormFixedPosition(var1, var2);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      GameTexture var5;
      if (this.isToggled()) {
         var5 = this.isHovering() ? Settings.UI.toggle_on.active : Settings.UI.toggle_on.highlighted;
         var5.initDraw().color(var4).draw(this.getX(), this.getY());
      } else {
         var5 = this.isHovering() ? Settings.UI.toggle_off.active : Settings.UI.toggle_off.highlighted;
         var5.initDraw().color(var4).draw(this.getX(), this.getY());
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), 32, 16));
   }

   public boolean isMouseOver(InputEvent var1) {
      Point var2 = new Point(var1.pos.hudX, var1.pos.hudY);
      return (new Rectangle(this.getX(), this.getY(), 32, 16)).contains(var2);
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
